package com.crazyostudio.ecommercegrocery.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.crazyostudio.ecommercegrocery.Adapter.DialogSliderAdapter;
import com.crazyostudio.ecommercegrocery.Adapter.OrderProductAdapter;
import com.crazyostudio.ecommercegrocery.Adapter.ProductAdapter;
import com.crazyostudio.ecommercegrocery.Adapter.SliderAdapter;
import com.crazyostudio.ecommercegrocery.Adapter.VariantsAdapter;
import com.crazyostudio.ecommercegrocery.HelperClass.ShoppingCartHelper;
import com.crazyostudio.ecommercegrocery.HelperClass.ValuesHelper;
import com.crazyostudio.ecommercegrocery.Manager.ProductManager;
import com.crazyostudio.ecommercegrocery.Model.BrandModel;
import com.crazyostudio.ecommercegrocery.Model.OrderModel;
import com.crazyostudio.ecommercegrocery.Model.ProductModel;
import com.crazyostudio.ecommercegrocery.Model.ShoppingCartFirebaseModel;
import com.crazyostudio.ecommercegrocery.Model.ShoppingCartsProductModel;
import com.crazyostudio.ecommercegrocery.Model.Variations;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.Services.BrandService;
import com.crazyostudio.ecommercegrocery.Services.DatabaseService;
import com.crazyostudio.ecommercegrocery.databinding.ActivityOrderDetailsBinding;
import com.crazyostudio.ecommercegrocery.databinding.DialogFullscreenImageBinding;
import com.crazyostudio.ecommercegrocery.databinding.ProductViewDialogBinding;
import com.crazyostudio.ecommercegrocery.databinding.RemoveProductBoxAlertBinding;
import com.crazyostudio.ecommercegrocery.interfaceClass.OrderProductInterface;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderDetailsActivity extends AppCompatActivity implements OrderProductInterface {
    private ActivityOrderDetailsBinding binding;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private OrderModel orderModel;
    private DatabaseService databaseService;
    private ProductManager productManager;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupActionBar();
        initializeFirebase();

        binding.orderDetailsViewBack.setOnClickListener(view -> onBackPressed());
        binding.download.setOnClickListener(v -> downloadBill());
        binding.ContinueShopping.setOnClickListener(v -> finish());

        String orderID = getIntent().getStringExtra("orderID");
        if (orderID != null && !orderID.isEmpty()) {
            fetchOrderDetails(orderID);
        }
        else {
            Toast.makeText(this, "Order ID is missing", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    private void initializeFirebase() {
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        databaseService = new DatabaseService();
        productManager = new ProductManager(this);
        userId = auth.getUid();
    }

    private void fetchOrderDetails(String orderID) {
        database.getReference().child("Order").child(userId).child(orderID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            orderModel = snapshot.getValue(OrderModel.class);
                            binding.progressCircular.setVisibility(View.GONE);
                            if (orderModel != null) {
                                displayOrderDetails();
                            } else {
                                Toast.makeText(OrderDetailsActivity.this, "Failed to retrieve order details", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(OrderDetailsActivity.this, "Order not found", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(OrderDetailsActivity.this, "Failed to retrieve order details", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private void downloadBill() {
        // Implement your download bill logic here
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    private void displayOrderDetails() {
        if (orderModel.getCustomer() != null) {
            binding.orderContactName.setText(orderModel.getCustomer().getFullName());
            binding.orderContactPhone.setText(orderModel.getShipping().getShippingAddress().getMobileNumber());
            binding.orderShippingAddress.setText(orderModel.getShipping().getShippingAddress().getFlatHouse() + " " + orderModel.getShipping().getShippingAddress().getAddress());
            binding.orderShippingMethod.setText(orderModel.getShipping().getShippingMethod());
            binding.orderPaymentMethod.setText(orderModel.getPayment().getPaymentMethod());

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String formattedDateTime = dateFormat.format(orderModel.getOrderDate());
            binding.orderDate.setText(formattedDateTime);
            binding.orderDeliveryStatus.setText(orderModel.getOrderStatus());

            if (ValuesHelper.DELIVERED.equals(orderModel.getOrderStatus())) {
                binding.orderDeliveryOnBox.setVisibility(View.VISIBLE);
                String formattedDateOfDeliveredOrder = dateFormat.format(orderModel.getShipping().getDeliveredData());
                binding.orderDeliveryDate.setText(formattedDateOfDeliveredOrder);
//                binding.orderDeliveryStatus.setText(formattedDateOfDeliveredOrder);
                binding.download.setVisibility(View.VISIBLE);
//                order_delivery_on_box
            }

            OrderProductAdapter orderProductAdapter = new OrderProductAdapter(orderModel.getOrderItems(), this, this::onOrder);
            binding.orderItems.setAdapter(orderProductAdapter);
            binding.orderItems.setLayoutManager(new LinearLayoutManager(this));
            orderProductAdapter.notifyDataSetChanged();

            binding.discount.setText("₹"+orderModel.getCouponCodeValue());
            binding.subtotal.setText("₹" + ShoppingCartHelper.calculateTotalPrices(orderModel.getOrderItems()));
            binding.shippingFee.setText("₹" + orderModel.getShipping().getShippingFee());
//            binding.save.setText("₹" + ShoppingCartHelper.calculateTotalSavings(orderModel.getOrderItems()));
            binding.grandTotal.setText("₹" + orderModel.getOrderTotalPrice());

        }
        else {
            Toast.makeText(this, "Customer information is missing", Toast.LENGTH_SHORT).show();
//            binding.download.setVisibility(View.INVISIBLE);
            this.finish();
        }
    }

    @Override
    public void onOrder(ShoppingCartsProductModel model) {
//        ProductModel productModel = new ProductModel(model.isAvailable(), model.getProductId(), model.getProductName(), model.getProductDescription(), model.getBrand(), model.getCategory(), model.getSubCategory(), model.getPrice(), model.getMrp(), model.getDiscount(), model.getStockCount(), model.getMinSelectableQuantity(), model.getMaxSelectableQuantity(), model.getWeight(), model.getWeightSIUnit(), model.getProductLife(), model.getProductType(), model.getProductIsFoodItem(), model.getKeywords(), model.getProductImage(), model.getVariations());
//        showProductViewDialog(productModel, this);
    }

//    private void showProductViewDialog(ProductModel productModel, Activity context) {
//        List<String> imageUrls = productModel.getProductImage();
//        Dialog bottomSheetDialog = new Dialog(context);
//        ProductViewDialogBinding productViewDialogBinding = ProductViewDialogBinding.inflate(getLayoutInflater());
//        ViewPager2 viewPager = productViewDialogBinding.viewPager;
//        SliderAdapter sliderAdapter = new SliderAdapter(context, imageUrls, position -> showImageInDialog(position, imageUrls, context));
//        viewPager.setAdapter(sliderAdapter);
//
//        ArrayList<ProductModel> sameProducts = new ArrayList<>();
//        databaseService.getAllProductsByCategoryOnly(productModel.getCategory(), new DatabaseService.GetAllProductsCallback() {
//            @Override
//            public void onSuccess(ArrayList<ProductModel> products) {
//                sameProducts.addAll(products);
//                sameProducts.remove(productModel);
//                LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
//                productViewDialogBinding.similarProductsRecyclerView.setLayoutManager(layoutManager);
//                productViewDialogBinding.similarProductsRecyclerView.setAdapter(new ProductAdapter(sameProducts, (subProductModel, SubsameProducts) -> showProductViewDialog(subProductModel, context), context, "Main"));
//            }
//
//            @Override
//            public void onError(String errorMessage) {
//                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        productViewDialogBinding.closeButton.setOnClickListener(v -> bottomSheetDialog.dismiss());
//        productViewDialogBinding.quantity.setText(String.valueOf(productModel.getSelectableQuantity()));
//
//        List<Variations> variationsList = new ArrayList<>();
//        ArrayList<ProductModel> productModels = new ArrayList<>();
//        VariantsAdapter variantsAdapter = new VariantsAdapter(context, variationsList, productModel.getProductId(), id -> {
//            for (ProductModel model : productModels) {
//                if (id.equals(model.getProductId())) {
//                    showProductViewDialog(model, context);
//                    break;
//                }
//            }
//        });
//        RecyclerView variantsRecyclerView = productViewDialogBinding.variantsList;
//        variantsRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
//        variantsRecyclerView.setAdapter(variantsAdapter);
//        variationsList.add(new Variations(productModel.getProductId(), productModel.getMinSelectableQuantity() + " * " + productModel.getWeight() + productModel.getWeightSIUnit(), String.valueOf(productModel.getPrice())));
//        variantsAdapter.notifyDataSetChanged();
//
//        if (productModel.getVariations() != null) {
//            for (int i = 0; i < productModel.getVariations().size(); i++) {
//                int finalI = i;
////                databaseService.getAllProductById(productModel.getVariations().get(i).getId(), new DatabaseService.GetAllProductsModelCallback() {
////                    @Override
////                    public void onSuccess(ProductModel oneProduct) {
////                        variationsList.add(new Variations(oneProduct.getProductId(), productModel.getVariations().get(finalI).getWeightWithSIUnit(), String.valueOf(oneProduct.getPrice())));
////                        productModels.add(oneProduct);
////                        variantsAdapter.notifyDataSetChanged();
////                    }
////
////                    @Override
////                    public void onError(String errorMessage) {
////                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
////                    }
////                });
//            }
//        }
//
//        productViewDialogBinding.categoryName.setText(productModel.getCategory());
//        productViewDialogBinding.ProductName.setText(productModel.getProductName());
//        productViewDialogBinding.brandNameInBox.setText(productModel.getBrand());
//        BrandService brandService = new BrandService(context);
//        brandService.getAllBrandWithIconsById(productModel.getBrand(), new BrandService.addBrandsByIdListener() {
//            @Override
//            public void onFailure(Exception error) {
//                Toast.makeText(context, "Failed to load brand icon", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onSuccess(BrandModel brandModel) {
//                Glide.with(context)
//                        .load(brandModel.getBrandIcon())
//                        .placeholder(R.drawable.product_image_shimmee_effect)
//                        .error(R.drawable.product_image_shimmee_effect)
//                        .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
//                        .centerCrop()
//                        .into(productViewDialogBinding.brandIcon);
//            }
//        });
//
//        productViewDialogBinding.Price.setText("₹" + productModel.getPrice());
//        productViewDialogBinding.size.setText(productModel.getWeight() + " " + productModel.getWeightSIUnit());
//        productViewDialogBinding.MRP.setText(":" + productModel.getMrp());
//        productViewDialogBinding.MRP.setPaintFlags(productViewDialogBinding.MRP.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//
//        if (!productModel.getProductDescription().isEmpty()) {
//            productViewDialogBinding.Description.setVisibility(View.VISIBLE);
//            productViewDialogBinding.DescriptionTextView.setVisibility(View.VISIBLE);
//            productViewDialogBinding.Description.setText(productModel.getProductDescription());
//        }
//
//        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//            productManager.isProductInCart(productModel.getProductId(), new ProductManager.addListenerForIsProductInCart() {
//                @Override
//                public void FoundProduct(ShoppingCartFirebaseModel shoppingCartFirebaseModel) {
//                    productViewDialogBinding.quantityBox.setVisibility(View.VISIBLE);
//                    productViewDialogBinding.AddTOCart.setVisibility(View.GONE);
//                    productViewDialogBinding.quantity.setText(String.valueOf(shoppingCartFirebaseModel.getProductSelectQuantity()));
//                }
//
//                @Override
//                public void notFoundInCart() {
//                    productViewDialogBinding.AddTOCart.setVisibility(View.VISIBLE);
//                    productViewDialogBinding.quantityBox.setVisibility(View.GONE);
//                }
//            });
//        }
//
//        if (productModel.getStockCount() == 0) {
//            productViewDialogBinding.OutOfStockBuyOptions.setVisibility(View.VISIBLE);
//            productViewDialogBinding.quantity.setText("0");
//            productViewDialogBinding.quantityBox.setVisibility(View.GONE);
//            productViewDialogBinding.AddTOCart.setVisibility(View.GONE);
//        }
//
//        String diet;
//        if ("FoodVeg".equals(productModel.getProductIsFoodItem())) {
//            diet = "Veg";
//            Glide.with(context)
//                    .load(R.drawable.food_green)
//                    .placeholder(R.drawable.product_image_shimmee_effect)
//                    .error(R.drawable.product_image_shimmee_effect)
//                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
//                    .centerCrop()
//                    .into(productViewDialogBinding.FoodTypeIcon);
//        } else if ("FoodNonVeg".equals(productModel.getProductIsFoodItem())) {
//            diet = "NonVeg";
//            Glide.with(context)
//                    .load(R.drawable.food_brown)
//                    .placeholder(R.drawable.product_image_shimmee_effect)
//                    .error(R.drawable.product_image_shimmee_effect)
//                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
//                    .centerCrop()
//                    .into(productViewDialogBinding.FoodTypeIcon);
//        } else if ("VegetableAndFruit".equals(productModel.getProductIsFoodItem())) {
//            diet = "VegetableAndFruit";
//            Glide.with(context)
//                    .load(R.drawable.food_green)
//                    .placeholder(R.drawable.product_image_shimmee_effect)
//                    .error(R.drawable.product_image_shimmee_effect)
//                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
//                    .centerCrop()
//                    .into(productViewDialogBinding.FoodTypeIcon);
//        } else {
//            diet = "not food item";
//            productViewDialogBinding.FoodTypeIcon.setVisibility(View.GONE);
//        }
//
//        productViewDialogBinding.AddTOCart.setOnClickListener(v -> {
//            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//                productManager.addToBothDatabase(new ShoppingCartFirebaseModel(productModel.getProductId(), productModel.getMinSelectableQuantity()), new ProductManager.AddListenerForAddToBothInDatabase() {
//                    @Override
//                    public void added(ShoppingCartFirebaseModel shoppingCartFirebaseModel) {
//                        productViewDialogBinding.AddTOCart.setVisibility(View.GONE);
//                        productViewDialogBinding.quantityBox.setVisibility(View.VISIBLE);
//                    }
//
//                    @Override
//                    public void failure(Exception e) {
//                        productViewDialogBinding.AddTOCart.setVisibility(View.VISIBLE);
//                        productViewDialogBinding.quantityBox.setVisibility(View.GONE);
//                        Toast.makeText(context, "Check your network connection and try again", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            } else {
//                context.startActivity(new Intent(context, AuthMangerActivity.class));
//            }
//        });
//
//        productViewDialogBinding.plusBtn.setOnClickListener(view -> {
//            int quantity = productModel.getSelectableQuantity();
//            quantity++;
//            if (quantity > productModel.getStockCount()) {
//                Toast.makeText(context, "Max stock available: " + productModel.getStockCount(), Toast.LENGTH_SHORT).show();
//            } else {
//                productModel.setSelectableQuantity(quantity);
//                productViewDialogBinding.quantity.setText(String.valueOf(productModel.getSelectableQuantity()));
//                productManager.UpdateCartQuantityById(userId, productModel.getProductId(), productModel.getSelectableQuantity());
//            }
//        });
//
//        productViewDialogBinding.minusBtn.setOnClickListener(view -> {
//            int quantity = productModel.getSelectableQuantity();
//            if (quantity > 1) {
//                quantity--;
//                productModel.setSelectableQuantity(quantity);
//                productViewDialogBinding.quantity.setText(String.valueOf(productModel.getSelectableQuantity()));
//                productManager.UpdateCartQuantityById(userId, productModel.getProductId(), productModel.getSelectableQuantity());
//            } else {
//                showRemoveProductDialog(productModel, context, productViewDialogBinding);
//            }
//        });
//
//        bottomSheetDialog.setContentView(productViewDialogBinding.getRoot());
//        bottomSheetDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        bottomSheetDialog.getWindow().getAttributes().windowAnimations = R.style.bottom_sheet_dialogAnimation;
//        bottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
//        bottomSheetDialog.show();
//    }
//
//    private void showRemoveProductDialog(ProductModel productModel, Activity context, ProductViewDialogBinding productViewDialogBinding) {
//        Dialog removeBottomSheetDialog = new Dialog(context);
//        RemoveProductBoxAlertBinding boxAlertBinding = RemoveProductBoxAlertBinding.inflate(getLayoutInflater());
//        removeBottomSheetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        removeBottomSheetDialog.setContentView(boxAlertBinding.getRoot());
//        Window window = removeBottomSheetDialog.getWindow();
//        if (window != null) {
//            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            WindowManager.LayoutParams layoutParams = window.getAttributes();
//            layoutParams.windowAnimations = R.style.bottom_sheet_dialogAnimation;
//            layoutParams.gravity = Gravity.CENTER_VERTICAL;
//            window.setAttributes(layoutParams);
//        }
//
//        boxAlertBinding.confirmRemoveButton.setOnClickListener(remove -> {
//            productManager.RemoveCartProductById(userId, productModel.getProductId());
//            productViewDialogBinding.AddTOCart.setVisibility(View.VISIBLE);
//            productViewDialogBinding.quantityBox.setVisibility(View.GONE);
//            removeBottomSheetDialog.dismiss();
//        });
//
//        boxAlertBinding.cancelRemoveButton.setOnClickListener(remove -> removeBottomSheetDialog.dismiss());
//
//        if (!removeBottomSheetDialog.isShowing()) {
//            removeBottomSheetDialog.show();
//        }
//    }
//
//    private void showImageInDialog(int position, List<String> imageUrls, Activity context) {
//        Dialog dialog = new Dialog(context);
//        DialogFullscreenImageBinding dialogBinding = DialogFullscreenImageBinding.inflate(LayoutInflater.from(context));
//        dialog.setContentView(dialogBinding.getRoot());
//        dialogBinding.getRoot().setOnClickListener(v -> dialog.dismiss());
//        dialogBinding.close.setOnClickListener(v -> dialog.dismiss());
//
//        DialogSliderAdapter dialogAdapter = new DialogSliderAdapter(context, imageUrls);
//        dialogBinding.dialogViewPager.setAdapter(dialogAdapter);
//        dialogBinding.dialogViewPager.setCurrentItem(position);
//
//        if (dialog.getWindow() != null) {
//            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            dialog.getWindow().setBackgroundDrawableResource(android.R.color.white);
//        }
//
//        Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_up);
//        dialogBinding.getRoot().startAnimation(animation);
//
//        dialog.show();
//    }
}
