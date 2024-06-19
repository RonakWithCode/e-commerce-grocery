package com.crazyostudio.ecommercegrocery.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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
import com.crazyostudio.ecommercegrocery.Dialog.DialogUtils;
import com.crazyostudio.ecommercegrocery.HelperClass.ShoppingCartHelper;
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
import com.crazyostudio.ecommercegrocery.interfaceClass.onClickProductAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class OrderDetailsActivity extends AppCompatActivity implements OrderProductInterface {
    ActivityOrderDetailsBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    OrderModel orderModel;
    DatabaseService databaseService;
    ProductManager productManager;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        databaseService = new DatabaseService();
        productManager = new ProductManager(this);
        userId = auth.getUid();
//        if (getIntent().getBooleanExtra("DialogUtils", false)){
//            DialogUtils.showConfirmationDialog(this, "Do you want to remove all items from the cart?", (dialog, which) -> {
//
//        }
        binding.orderDetailsViewBack.setOnClickListener(view-> this.onBackPressed());
        binding.download.setOnClickListener(v -> downloadBill());
        binding.ContinueShopping.setOnClickListener(v->{
            this.finish();
        });
         String orderID = getIntent().getStringExtra("orderID");
        database.getReference().child("Order").child(Objects.requireNonNull(auth.getUid())).child(orderID).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    orderModel = snapshot.getValue(OrderModel.class);
                    binding.progressCircular.setVisibility(View.GONE);
                    getData();

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
   }

    private void downloadBill() {
        DialogUtils.showConfirmationDialog(this, "Do you want to remove all items from the cart?", (dialog, which) -> {
            Toast.makeText(this, "wait", Toast.LENGTH_SHORT).show();
        });

    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    private void getData() {
        if (orderModel.getCustomer() != null) {
            binding.orderContactName.setText(orderModel.getCustomer().getFullName());
            binding.orderContactPhone.setText(orderModel.getShipping().getShippingAddress().getMobileNumber());
            binding.orderShippingAddress.setText(orderModel.getShipping().getShippingAddress().getFlatHouse()+orderModel.getShipping().getShippingAddress().getAddress());
            binding.orderShippingMethod.setText(orderModel.getShipping().getShippingMethod());
            binding.orderPaymentMethod.setText(orderModel.getPayment().getPaymentMethod());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            // Format the current date and time as a string
            String formattedDateTime = dateFormat.format(orderModel.getOrderDate());
            binding.orderDate.setText(formattedDateTime);
            binding.orderDeliveryStatus.setText(orderModel.getOrderStatus());
            if (orderModel.getOrderStatus().equals("delivered")) {
                binding.orderDeliveryOnBox.setVisibility(View.VISIBLE);
                String formattedDateOfDeliveredOrder = dateFormat.format(orderModel.getShipping().getDeliveredData());
                binding.orderDeliveryStatus.setText(formattedDateOfDeliveredOrder);
            }
            OrderProductAdapter orderProductAdapter  = new OrderProductAdapter(orderModel.getOrderItems(),this,this::onOrder);
            binding.orderItems.setAdapter(orderProductAdapter);
            binding.orderItems.setLayoutManager(new LinearLayoutManager(this));
            orderProductAdapter.notifyDataSetChanged();

            binding.shippingFee.setText("₹"+orderModel.getShipping().getShippingFee());
            binding.save.setText("₹"+ ShoppingCartHelper.calculateTotalSavings(orderModel.getOrderItems()));
            binding.grandTotal.setText("₹"+orderModel.getOrderTotalPrice());
            if (orderModel.getOrderStatus().equals("successfully"))
            {
                binding.download.setVisibility(View.VISIBLE);
            }
        } else {
            Toast.makeText(this, "Customer information is missing", Toast.LENGTH_SHORT).show();
            binding.download.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onOrder(ShoppingCartsProductModel model) {
//        Intent intent = new Intent(this,FragmentLoader.class);
//        intent.putExtra("LoadID","Details");
//        intent.putExtra("productId",model.getProductId());
//        startActivity(intent);
//    public ShoppingCartsProductModel(boolean isAvailable, String productId, String productName, String productDescription, String brand, String category, String subCategory, double price, double mrp, double discount, int stockCount, int minSelectableQuantity, int maxSelectableQuantity, int selectableQuantity, String weight, String weightSIUnit, String productLife, String productType, String productIsFoodItem, ArrayList<String> keywords, ArrayList<String> productImage, @Nullable ArrayList< Variations > variations) {

        ProductModel productModel = new ProductModel(model.isAvailable(),model.getProductId(),model.getProductName(),model.getProductDescription(),model.getBrand(),model.getCategory(),model.getSubCategory(),model.getPrice(),model.getMrp(),model.getDiscount(),model.getStockCount(),model.getMinSelectableQuantity(),model.getMaxSelectableQuantity(),model.getWeight(),model.getWeightSIUnit(),model.getProductLife(),model.getProductType(),model.getProductIsFoodItem(),model.getKeywords(),model.getProductImage(),model.getVariations(),null);
//    public ProductModel(boolean isAvailable, String productId, String productName, String productDescription, String brand, String category, String subCategory, double price, double mrp, double discount, int stockCount, int minSelectableQuantity, int maxSelectableQuantity, String weight, String weightSIUnit, String productLife, String productType, String productIsFoodItem, ArrayList<String> keywords, ArrayList<String> productImage, @Nullable ArrayList<Variations> variations, @Nullable SponsorTypeModel sponsorTypeModel) {

//        showProductViewDialog(productModel);
        showProductViewDialog(productModel,this);



    }



    private void showProductViewDialog(ProductModel productModel, Activity context) {
        List<String> imageUrls = productModel.getProductImage(); // Add your image URLs here
        Dialog bottomSheetDialog = new Dialog(context);
        ProductViewDialogBinding productViewDialogBinding = ProductViewDialogBinding.inflate(getLayoutInflater());
        ViewPager2 viewPager = productViewDialogBinding.viewPager;
        SliderAdapter sliderAdapter = new SliderAdapter(context, imageUrls, position -> {
            showImageInDialog(position, imageUrls,context);
        });
        viewPager.setAdapter(sliderAdapter);

        ArrayList<ProductModel> sameProducts = new ArrayList<>();
        databaseService.getAllProductsByCategoryOnly(productModel.getCategory(), new DatabaseService.GetAllProductsCallback() {
            @Override
            public void onSuccess(ArrayList<ProductModel> products) {
                sameProducts.addAll(products);
                sameProducts.remove(productModel);
                LinearLayoutManager LayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                productViewDialogBinding.similarProductsRecyclerView.setLayoutManager(LayoutManager);
                productViewDialogBinding.similarProductsRecyclerView.setAdapter(new ProductAdapter(sameProducts, new onClickProductAdapter() {
                    @Override
                    public void onClick(ProductModel subProductModel, ArrayList<ProductModel> SubsameProducts) {
                        showProductViewDialog(subProductModel,context);
                    }
                }, context, "Main"));

            }

            @Override
            public void onError(String errorMessage) {

            }
        });

        productViewDialogBinding.closeButton.setOnClickListener(v -> bottomSheetDialog.dismiss());

        productViewDialogBinding.quantity.setText(""+productModel.getSelectableQuantity());

        List<Variations> variationsList = new ArrayList<>();
        ArrayList<ProductModel> productModels = new ArrayList<>();
        VariantsAdapter variantsAdapter = new VariantsAdapter(context, variationsList, productModel.getProductId(), new VariantsAdapter.VariantsCallback() {
            @Override
            public void Product(String id) {
                for (int i = 0; i < productModels.size(); i++) {
                    if (id.equals(productModels.get(i).getProductId())) {
                        showProductViewDialog(productModels.get(i),context);

                    }
                }

//                databaseService.getAllProductById(id, new DatabaseService.GetAllProductsModelCallback() {
//                    @Override
//                    public void onSuccess(ProductModel products) {
//                        if (sameProducts != null) {
//                            showProductViewDialog(products);
//                        }
//                    }
//
//                    @Override
//                    public void onError(String errorMessage) {
//
//                    }
//                });
            }
        });
        RecyclerView variantsRecyclerView = productViewDialogBinding.variantsList;
        variantsRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        variantsRecyclerView.setAdapter(variantsAdapter);
        variationsList.add(new Variations(productModel.getProductId(),productModel.getMinSelectableQuantity()+" * "+productModel.getWeight()+productModel.getWeightSIUnit(),productModel.getPrice()+""));
        variantsAdapter.notifyDataSetChanged();
//        Product Variations

        if (productModel.getVariations() != null) {
//            List<Variations> finalVariationsList = new ArrayList<>();
            for (int i = 0; i < productModel.getVariations().size(); i++) {
                int finalI = i;
                databaseService.getAllProductById(productModel.getVariations().get(i).getId(), new DatabaseService.GetAllProductsModelCallback() {
                    @Override
                    public void onSuccess(ProductModel oneProduct) {
                        variationsList.add(new Variations(oneProduct.getProductId(),productModel.getVariations().get(finalI).getWeightWithSIUnit(), ""+oneProduct.getPrice()));
                        productModels.add(oneProduct);
                        variantsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
//            variationsList.addAll(finalVariationsList);

        }

//        End Product Variations

//        Set up
        productViewDialogBinding.categoryName.setText(productModel.getCategory());
        productViewDialogBinding.ProductName.setText(productModel.getProductName());


//        Set Brand with logo
        productViewDialogBinding.brandNameInBox.setText(productModel.getBrand());
        BrandService brandService = new BrandService(context);
        brandService.getAllBrandWithIconsById(productModel.getBrand(), new BrandService.addBrandsByIdListener() {
            @Override
            public void onFailure(Exception error) {

            }

            @Override
            public void onSuccess(BrandModel brandModel) {
                Glide.with(context)
                        .load(brandModel.getBrandIcon())
                        .placeholder(R.drawable.product_image_shimmee_effect) // Placeholder image while loading
                        .error(R.drawable.product_image_shimmee_effect) // Error image if loading fails
                        .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) // Resize the image
                        .centerCrop() // Scale type for resizing
                        .into(productViewDialogBinding.brandIcon);
            }
        });

        productViewDialogBinding.Price.setText("₹" + productModel.getPrice());
        productViewDialogBinding.size.setText(productModel.getWeight() +" "+ productModel.getWeightSIUnit());
        productViewDialogBinding.MRP.setText(":" + productModel.getMrp());
        productViewDialogBinding.MRP.setPaintFlags(productViewDialogBinding.MRP.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        if (!productModel.getProductDescription().isEmpty())
        {
            productViewDialogBinding.Description.setVisibility(View.VISIBLE);
            productViewDialogBinding.DescriptionTextView.setVisibility(View.VISIBLE);
            productViewDialogBinding.Description.setText(productModel.getProductDescription());
        }

        if (FirebaseAuth.getInstance().getCurrentUser() !=null) {
            productManager.isProductInCart(productModel.getProductId(), new ProductManager.addListenerForIsProductInCart() {
                @Override
                public void FoundProduct(ShoppingCartFirebaseModel shoppingCartFirebaseModel) {
                    productViewDialogBinding.quantityBox.setVisibility(View.VISIBLE);
                    productViewDialogBinding.AddTOCart.setVisibility(View.GONE);
                    productViewDialogBinding.quantity.setText(""+shoppingCartFirebaseModel.getProductSelectQuantity());
                }

                @Override
                public void notFoundInCart() {
                    productViewDialogBinding.AddTOCart.setVisibility(View.VISIBLE);
                    productViewDialogBinding.quantityBox.setVisibility(View.GONE);
                }
            });


//             String userId = FirebaseAuth.getInstance().getUid();
//            assert userId != null;
//            DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("Cart").child(userId);
//            String productNameToFind = productModel.getProductId();
//            Query query = productsRef.orderByKey().equalTo(productNameToFind);
//            query.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    if (dataSnapshot.exists()) {
//                        productViewDialogBinding.quantityBox.setVisibility(View.VISIBLE);
//                        productViewDialogBinding.AddTOCart.setVisibility(View.GONE);
////                        productViewDialogBinding.AddTOCart.setText("Go to Cart");
//////                        productViewDialogBinding.quantityBox.setVisibility(View.GONE);
//////                        addTOCart.setText("Go to Cart");
////                        productViewDialogBinding.AddTOCart.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
////                        productViewDialogBinding.AddTOCart.setTextColor(ContextCompat.getColor(context, R.color.FixBlack));
//
//                    }
//                }
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//                    // Handle the error in case of a database error.
//                    Toast.makeText(getContext(), "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });
//
        }

        if (productModel.getStockCount() == 0) {
            productViewDialogBinding.OutOfStockBuyOptions.setVisibility(View.VISIBLE);
            productViewDialogBinding.quantity.setText("0");
//            productViewDialogBinding.TextInStock.setText("Out of Stock");
            productViewDialogBinding.quantityBox.setVisibility(View.GONE);
//            productViewDialogBinding.TextInStock.setTextColor(ContextCompat.getColor(context, R.color.FixRed));
            productViewDialogBinding.AddTOCart.setVisibility(View.GONE);
        }

        String diet;
        if (productModel.getProductIsFoodItem().equals("FoodVeg")) {
            diet = "Veg";

            Glide.with(context)
                    .load(R.drawable.food_green)
                    .placeholder(R.drawable.product_image_shimmee_effect) // Placeholder image while loading
                    .error(R.drawable.product_image_shimmee_effect) // Error image if loading fails
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) // Resize the image
                    .centerCrop() // Scale type for resizing
                    .into(productViewDialogBinding.FoodTypeIcon);
//            productViewDialogBinding.FoodTypeIcon.set
        }
        else if (productModel.getProductType().equals("FoodNonVeg")) {
            diet = "NonVeg";
            Glide.with(context)
                    .load(R.drawable.food_brown)
                    .placeholder(R.drawable.product_image_shimmee_effect) // Placeholder image while loading
                    .error(R.drawable.product_image_shimmee_effect) // Error image if loading fails
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) // Resize the image
                    .centerCrop() // Scale type for resizing
                    .into(productViewDialogBinding.FoodTypeIcon);
        }
        else if (productModel.getProductIsFoodItem().equals("VegetableAndFruit")) {
            diet = "VegetableAndFruit";
            Glide.with(context)
                    .load(R.drawable.food_green)
                    .placeholder(R.drawable.product_image_shimmee_effect) // Placeholder image while loading
                    .error(R.drawable.product_image_shimmee_effect) // Error image if loading fails
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) // Resize the image
                    .centerCrop() // Scale type for resizing
                    .into(productViewDialogBinding.FoodTypeIcon);
        }
        else {
            diet = "not food item.";
            productViewDialogBinding.FoodTypeIcon.setVisibility(View.GONE);
//            productViewDialogBinding.dietType.setVisibility(View.GONE);
//            productViewDialogBinding.diet.setVisibility(View.GONE);
        }
//        productViewDialogBinding.dietType.setText(diet);





//   Show the in cat







        productViewDialogBinding.AddTOCart.setOnClickListener(v -> {
            if (FirebaseAuth.getInstance().getCurrentUser() !=null) {
                productManager.addToBothDatabase(new ShoppingCartFirebaseModel(productModel.getProductId(), productModel.getMinSelectableQuantity()), new ProductManager.AddListenerForAddToBothInDatabase() {
                    @Override
                    public void added(ShoppingCartFirebaseModel shoppingCartFirebaseModel) {
                        productViewDialogBinding.AddTOCart.setVisibility(View.GONE);
                        productViewDialogBinding.quantityBox.setVisibility(View.VISIBLE);
                    }
                    @Override
                    public void failure(Exception e) {
                        productViewDialogBinding.AddTOCart.setVisibility(View.VISIBLE);
                        productViewDialogBinding.quantityBox.setVisibility(View.GONE);
                        Toast.makeText(context, "check your network connection and try again ", Toast.LENGTH_SHORT).show();
                    }
                });
            }else {
                context.startActivity(new Intent(context, AuthMangerActivity.class));
            }

//            addToCart(productModel,productViewDialogBinding.AddTOCart,bottomSheetDialog,productViewDialogBinding.quantityBox);

        });


//        end set up





        productViewDialogBinding.plusBtn.setOnClickListener(view -> {
            int quantity = productModel.getSelectableQuantity();
            quantity++;
            if (quantity > productModel.getStockCount()) {
                Toast.makeText(context, "Max stock available: " + productModel.getStockCount(), Toast.LENGTH_SHORT).show();
            }
            else {
                productModel.setSelectableQuantity(quantity);
                productViewDialogBinding.quantity.setText(String.valueOf(productModel.getSelectableQuantity()));
                productManager.UpdateCartQuantityById(userId,productModel.getProductId(),productModel.getSelectableQuantity());
            }

        });
        productViewDialogBinding.minusBtn.setOnClickListener(view -> {
            int quantity = productModel.getSelectableQuantity();
            if (quantity > 1) {
                quantity--;
                productModel.setSelectableQuantity(quantity);
                productViewDialogBinding.quantity.setText(String.valueOf(productModel.getSelectableQuantity()));
                productManager.UpdateCartQuantityById(userId, productModel.getProductId(), productModel.getSelectableQuantity());
            }else {
                Dialog removeBottomSheetDialog = new Dialog(context);
                RemoveProductBoxAlertBinding boxAlertBinding = RemoveProductBoxAlertBinding.inflate(getLayoutInflater());
                removeBottomSheetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                removeBottomSheetDialog.setContentView(boxAlertBinding.getRoot());
                Window window = removeBottomSheetDialog.getWindow();
                if (window != null) {
                    window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    WindowManager.LayoutParams layoutParams = window.getAttributes();
                    layoutParams.windowAnimations = R.style.bottom_sheet_dialogAnimation;
                    layoutParams.gravity = Gravity.CENTER_VERTICAL;
                    window.setAttributes(layoutParams);
                }

                // Handle button clicks
                boxAlertBinding.confirmRemoveButton.setOnClickListener(remove -> {
                    // Handle remove action
                    productManager.RemoveCartProductById(userId,productModel.getProductId());
                    productViewDialogBinding.AddTOCart.setVisibility(View.VISIBLE);
                    productViewDialogBinding.quantityBox.setVisibility(View.GONE);
                    removeBottomSheetDialog.dismiss();
                });

                boxAlertBinding.cancelRemoveButton.setOnClickListener(remove -> removeBottomSheetDialog.dismiss());

                if (!removeBottomSheetDialog.isShowing()) {removeBottomSheetDialog.show();}
            }
//            else {
//                Dialog removeBottomSheetDialog = new Dialog(context);
//                RemoveProductBoxAlertBinding boxAlertBinding = RemoveProductBoxAlertBinding.inflate(getLayoutInflater());
//                removeBottomSheetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                removeBottomSheetDialog.setContentView(binding.getRoot());
//
//                Window window = removeBottomSheetDialog.getWindow();
//                if (window != null) {
//                    window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                    WindowManager.LayoutParams layoutParams = window.getAttributes();
//                    layoutParams.windowAnimations = R.style.bottom_sheet_dialogAnimation;
//                    layoutParams.gravity = Gravity.BOTTOM;
//                    window.setAttributes(layoutParams);
//                }
//
//                // Handle button clicks
//                boxAlertBinding.confirmRemoveButton.setOnClickListener(remove -> {
//                    // Handle remove action
//                    productManager.RemoveCartProductById(userId,productModel.getProductId());
//                    removeBottomSheetDialog.dismiss();
//                });
//
//                boxAlertBinding.cancelRemoveButton.setOnClickListener(remove -> removeBottomSheetDialog.dismiss());
//
//                removeBottomSheetDialog.show();
//
////                Dialog removeBottomSheetDialog = new Dialog(context);
////                RemoveProductBoxBinding removeProductBoxBinding = RemoveProductBoxBinding.inflate(getLayoutInflater());
////                removeBottomSheetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
////                removeBottomSheetDialog.setContentView(binding.getRoot());
////                Window window = removeBottomSheetDialog.getWindow();
////                if (window != null) {
////                    window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
////                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
////                    WindowManager.LayoutParams layoutParams = window.getAttributes();
////                    layoutParams.windowAnimations = R.style.bottom_sheet_dialogAnimation;
////                    layoutParams.gravity = Gravity.BOTTOM;
////                    window.setAttributes(layoutParams);
////                }
////
////
////                removeProductBoxBinding.btnRemove.setOnClickListener(v->{
////                    removeProductBoxBinding.progressCircular.setVisibility(View.VISIBLE);
////                    productManager.RemoveCartProductById(userId,productModel.getProductId());
////
////                    removeProductBoxBinding.progressCircular.setVisibility(View.GONE);
////                    removeBottomSheetDialog.dismiss();
////                });
////
////                removeProductBoxBinding.productName.setText(productModel.getProductName());
////                Glide.with(context).load(productModel.getProductImage().get(0)).into(removeProductBoxBinding.productImage);
////                removeProductBoxBinding.productQty.setText(productModel.getSelectableQuantity()+"");
////                removeProductBoxBinding.productPrice.setText("₹"+productModel.getPrice());
////                removeProductBoxBinding.productQtyUp.setOnClickListener(up->{
////                    int quantity = productModel.getSelectableQuantity();
////                    quantity++;
////                    if(quantity>model.getStockCount()) {
////                        Toast.makeText(context, "Max stock available: "+ model.getStockCount(), Toast.LENGTH_SHORT).show();
////                    } else {
////                        model.setSelectableQuantity(quantity);
////                        UpdateQuantity(model, model.getProductId());
////                        binding.productPrice.setText("₹"+model.getPrice());
////
////                    }
////                });
////                removeProductBoxBinding.productQtyDown.setOnClickListener(Down->{
////                    int quantity = model.getSelectableQuantity();
////                    if(quantity > 1) {
////                        quantity--;
////                        model.setSelectableQuantity(quantity);
////                        UpdateQuantity(model, model.getProductId());
////                        removeProductBoxBinding.productPrice.setText("₹"+model.getPrice());
////                    }
////                });
////                removeProductBoxBinding.btnCancel.setOnClickListener(v -> {
////                    removeBottomSheetDialog.dismiss();
////                });
////
////
////
////                if (!removeBottomSheetDialog.isShowing()) {
////                    removeBottomSheetDialog.show();
////
////                }
////                if (context.getSupportFragmentManager().findFragmentByTag("bottom_sheet_fragment") == null) {
////                    RemoveBottomSheetDialogFragment bottomSheet = new RemoveBottomSheetDialogFragment(uid, id, cartsAdapter, cartsProductModel);
////                    bottomSheet.show(context.getSupportFragmentManager(), "bottom_sheet_fragment");
//                }
//                shoppingCartsInterface.remove(position,model.getProductId(),model);

//
//            if(quantity > 1) {
//                quantity--;
//                model.setSelectableQuantity(quantity);
//                shoppingCartsInterface.UpdateQuantity(model, model.getProductId());
//                holder.binding.productPrice.setText("₹"+model.getPrice());
//            }else {
//            }
        });



//




        bottomSheetDialog.setContentView(productViewDialogBinding.getRoot());
        bottomSheetDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        bottomSheetDialog.getWindow().getAttributes().windowAnimations = R.style.bottom_sheet_dialogAnimation;
        bottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);

        bottomSheetDialog.show();
    }

    private void showImageInDialog(int position,List<String> imageUrls,Activity context) {
        Dialog dialog = new Dialog(context);
        DialogFullscreenImageBinding dialogBinding = DialogFullscreenImageBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(dialogBinding.getRoot());
//        dialogBinding.dialogViewPager
//        LinearLayoutManager layoutManager = new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
        DialogSliderAdapter dialogAdapter = new DialogSliderAdapter(context, imageUrls);
//        dialogViewPager.setLayoutManager(layoutManager);
        dialogBinding.getRoot().setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialogBinding.close.setOnClickListener(v->{
            dialog.dismiss();
        });
        dialogBinding.dialogViewPager.setAdapter(dialogAdapter);
        dialogBinding.dialogViewPager.setCurrentItem(position);
        // Set full screen
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.white);
        }
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_up);
        dialogBinding.getRoot().startAnimation(animation);


        dialog.show();

    }




}