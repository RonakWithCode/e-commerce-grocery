package com.crazyostudio.ecommercegrocery.Activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.crazyostudio.ecommercegrocery.Adapter.OrderProductAdapter;
import com.crazyostudio.ecommercegrocery.Adapter.ProductAdapter;
import com.crazyostudio.ecommercegrocery.Dialog.DialogUtils;
import com.crazyostudio.ecommercegrocery.HelperClass.ShoppingCartHelper;
import com.crazyostudio.ecommercegrocery.Model.OrderModel;
import com.crazyostudio.ecommercegrocery.Model.ProductModel;
import com.crazyostudio.ecommercegrocery.Model.ShoppingCartsProductModel;
import com.crazyostudio.ecommercegrocery.Model.Variations;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.Services.DatabaseService;
import com.crazyostudio.ecommercegrocery.databinding.ActivityOrderDetailsBinding;
import com.crazyostudio.ecommercegrocery.databinding.ProductViewDialogBinding;
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
import java.util.Locale;
import java.util.Objects;

public class OrderDetailsActivity extends AppCompatActivity implements OrderProductInterface {
    ActivityOrderDetailsBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    OrderModel orderModel;

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
        



    }



//    @SuppressLint("SetTextI18n")
//    private void showProductViewDialog(ProductModel productModel) {
////        ProductModel model = new ProductModel();
//        Dialog bottomSheetDialog = new Dialog(this);
//        ProductViewDialogBinding productViewDialogBinding = ProductViewDialogBinding.inflate(getLayoutInflater());
//        if (FirebaseAuth.getInstance().getCurrentUser() !=null) {
//            String userId = FirebaseAuth.getInstance().getUid();
//            assert userId != null;
//            DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("Cart").child(userId);
//            String productNameToFind = productModel.getProductId();
//            Query query = productsRef.orderByKey().equalTo(productNameToFind);
//            query.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    if (dataSnapshot.exists()) {
//                        productViewDialogBinding.AddTOCart.setText("Go to Cart");
//                        productViewDialogBinding.quantityBox.setVisibility(View.GONE);
////                        addTOCart.setText("Go to Cart");
//                        productViewDialogBinding.AddTOCart.setBackgroundColor(ContextCompat.getColor(OrderDetailsActivity.this, R.color.white));
//                        productViewDialogBinding.AddTOCart.setTextColor(ContextCompat.getColor(OrderDetailsActivity.this, R.color.FixBlack));
//
//                    }
//                }
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//                    // Handle the error in case of a database error.
//                    Toast.makeText(OrderDetailsActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//
//
//        productViewDialogBinding.ProductName.setText(productModel.getProductName());
//        productViewDialogBinding.closeButton.setOnClickListener(v -> bottomSheetDialog.dismiss());
//        for (String imageUrl : productModel.getProductImage()) {
//            CarouselItem carouselItem = new CarouselItem(imageUrl);
//            productViewDialogBinding.productsImages.addData(carouselItem);
//        }
//
//        if (!Objects.equals(productModel.getProductDescription(), "")) {
//            productViewDialogBinding.productDescription.setVisibility(View.VISIBLE);
//            productViewDialogBinding.Description.setVisibility(View.VISIBLE);
//            productViewDialogBinding.productDescription.setText(productModel.getProductDescription());
//        }
//
//        double mrp = productModel.getMrp();
//        double sellingPrice = productModel.getPrice();
//        double discountPercentage = mrp - sellingPrice;
//
//        productViewDialogBinding.discount.setText("₹" + discountPercentage + "% off");
//        productViewDialogBinding.Price.setText("₹" + productModel.getPrice());
//        productViewDialogBinding.quantitySmail.setText("(₹" + productModel.getPrice() + " / " + productModel.getWeight() + productModel.getWeightSIUnit() + ")");
//        productViewDialogBinding.MRP.setText(":" + productModel.getMrp());
//        productViewDialogBinding.MRP.setPaintFlags(productViewDialogBinding.MRP.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//
//        if (productModel.getStockCount() == 0) {
//            productViewDialogBinding.OutOfStockBuyOptions.setVisibility(View.VISIBLE);
//            productViewDialogBinding.quantity.setText("0");
//            productViewDialogBinding.TextInStock.setText("Out of Stock");
//            productViewDialogBinding.quantityBox.setVisibility(View.INVISIBLE);
//            productViewDialogBinding.TextInStock.setTextColor(ContextCompat.getColor(this, R.color.FixRed));
//            productViewDialogBinding.AddTOCart.setVisibility(View.INVISIBLE);
//        }
//
//        productViewDialogBinding.categoryType.setText(productModel.getCategory());
//        productViewDialogBinding.netQuantity.setText(productModel.getWeight() + productModel.getWeightSIUnit());
//
//        String diet;
//        if (productModel.getProductType().equals("FoodVeg")) {
//            diet = "Veg";
//        } else if (productModel.getProductType().equals("FoodNonVeg")) {
//            diet = "NonVeg";
//        } else {
//            diet = "not food item.";
//            productViewDialogBinding.dietType.setVisibility(View.GONE);
//            productViewDialogBinding.diet.setVisibility(View.GONE);
//        }
//        productViewDialogBinding.dietType.setText(diet);
//        productViewDialogBinding.ExpiryDate.setText(productModel.getProductLife());
//
//        productViewDialogBinding.AddTOCart.setOnClickListener(v -> {
////            addToCart(productModel,productViewDialogBinding.AddTOCart,bottomSheetDialog,productViewDialogBinding.quantityBox);
//
//        });
//
//         new DatabaseService().getAllProductsByCategoryOnly(productModel.getCategory(), new DatabaseService.GetAllProductsCallback() {
//            @Override
//            public void onSuccess(ArrayList<ProductModel> sameProducts) {
//                sameProducts.remove(productModel);
//                if (sameProducts.isEmpty()){
//                    productViewDialogBinding.product.setVisibility(View.GONE);
//                    productViewDialogBinding.itemCategory.setVisibility(View.GONE);
//                }else {
//                    LinearLayoutManager LayoutManager = new LinearLayoutManager(OrderDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false);
//                    productViewDialogBinding.itemCategory.setLayoutManager(LayoutManager);
//                    productViewDialogBinding.itemCategory.setAdapter(new ProductAdapter(sameProducts, new onClickProductAdapter() {
//                        @Override
//                        public void onClick(ProductModel productModel, ArrayList<ProductModel> sameProducts) {
//                            showProductViewDialogs(productModel,sameProducts);
//                        }
//                    }, OrderDetailsActivity.this, "Main"));
//                }
//
//            }
//
//            @Override
//            public void onError(String errorMessage) {
//
//            }
//        });
//
//
//
//
//
//
//
//        productViewDialogBinding.plusBtn.setOnClickListener(view -> {
//            int quantity = productModel.getSelectableQuantity();
//            quantity++;
//            if (quantity > productModel.getStockCount()) {
//                Toast.makeText(this, "Max stock available: " + productModel.getStockCount(), Toast.LENGTH_SHORT).show();
//            }
//            else {
//                productModel.setSelectableQuantity(quantity);
//                productViewDialogBinding.quantity.setText(String.valueOf(productModel.getSelectableQuantity()));
//            }
//
//        });
//        productViewDialogBinding.minusBtn.setOnClickListener(view -> {
//            int quantity = productModel.getSelectableQuantity();
//            if (quantity > 1)
//                quantity--;
////            selectQTY = quantity;
//            productModel.setSelectableQuantity(quantity);
//            productViewDialogBinding.quantity.setText(String.valueOf(productModel.getSelectableQuantity()));
//
//        });
//
//
//
//
//        bottomSheetDialog.setContentView(productViewDialogBinding.getRoot());
//        bottomSheetDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        bottomSheetDialog.getWindow().getAttributes().windowAnimations = R.style.bottom_sheet_dialogAnimation;
//        bottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
//
//        bottomSheetDialog.show();
//    }
//
//




//
//    @SuppressLint("SetTextI18n")
//    private void showProductViewDialogs(ProductModel productModel,ArrayList<ProductModel> sameProducts) {
////        ProductModel model = new ProductModel();
//        Dialog bottomSheetDialog = new Dialog(this);
//        ProductViewDialogBinding productViewDialogBinding = ProductViewDialogBinding.inflate(getLayoutInflater());
//        if (FirebaseAuth.getInstance().getCurrentUser() !=null) {
//            String userId = FirebaseAuth.getInstance().getUid();
//            assert userId != null;
//            DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("Cart").child(userId);
//            String productNameToFind = productModel.getProductId();
//            Query query = productsRef.orderByKey().equalTo(productNameToFind);
//            query.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    if (dataSnapshot.exists()) {
//                        productViewDialogBinding.AddTOCart.setText("Go to Cart");
//                        productViewDialogBinding.quantityBox.setVisibility(View.GONE);
////                        addTOCart.setText("Go to Cart");
//                        productViewDialogBinding.AddTOCart.setBackgroundColor(ContextCompat.getColor(OrderDetailsActivity.this, R.color.white));
//                        productViewDialogBinding.AddTOCart.setTextColor(ContextCompat.getColor(OrderDetailsActivity.this, R.color.FixBlack));
//
//                    }
//                }
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//                    // Handle the error in case of a database error.
//                    Toast.makeText(OrderDetailsActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//
//
//        productViewDialogBinding.ProductName.setText(productModel.getProductName());
//        productViewDialogBinding.closeButton.setOnClickListener(v -> bottomSheetDialog.dismiss());
//        for (String imageUrl : productModel.getProductImage()) {
//            CarouselItem carouselItem = new CarouselItem(imageUrl);
//            productViewDialogBinding.productsImages.addData(carouselItem);
//        }
//
//        if (!Objects.equals(productModel.getProductDescription(), "")) {
//            productViewDialogBinding.productDescription.setVisibility(View.VISIBLE);
//            productViewDialogBinding.Description.setVisibility(View.VISIBLE);
//            productViewDialogBinding.productDescription.setText(productModel.getProductDescription());
//        }
//
//        double mrp = productModel.getMrp();
//        double sellingPrice = productModel.getPrice();
//        double discountPercentage = mrp - sellingPrice;
//
//        productViewDialogBinding.discount.setText("₹" + discountPercentage + "% off");
//        productViewDialogBinding.Price.setText("₹" + productModel.getPrice());
//        productViewDialogBinding.quantitySmail.setText("(₹" + productModel.getPrice() + " / " + productModel.getWeight() + productModel.getWeightSIUnit() + ")");
//        productViewDialogBinding.MRP.setText(":" + productModel.getMrp());
//        productViewDialogBinding.MRP.setPaintFlags(productViewDialogBinding.MRP.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//
//        if (productModel.getStockCount() == 0) {
//            productViewDialogBinding.OutOfStockBuyOptions.setVisibility(View.VISIBLE);
//            productViewDialogBinding.quantity.setText("0");
//            productViewDialogBinding.TextInStock.setText("Out of Stock");
//            productViewDialogBinding.quantityBox.setVisibility(View.INVISIBLE);
//            productViewDialogBinding.TextInStock.setTextColor(ContextCompat.getColor(this, R.color.FixRed));
//            productViewDialogBinding.AddTOCart.setVisibility(View.INVISIBLE);
//        }
//
//        productViewDialogBinding.categoryType.setText(productModel.getCategory());
//        productViewDialogBinding.netQuantity.setText(productModel.getWeight() + productModel.getWeightSIUnit());
//
//        String diet;
//        if (productModel.getProductType().equals("FoodVeg")) {
//            diet = "Veg";
//        } else if (productModel.getProductType().equals("FoodNonVeg")) {
//            diet = "NonVeg";
//        } else {
//            diet = "not food item.";
//            productViewDialogBinding.dietType.setVisibility(View.GONE);
//            productViewDialogBinding.diet.setVisibility(View.GONE);
//        }
//        productViewDialogBinding.dietType.setText(diet);
//        productViewDialogBinding.ExpiryDate.setText(productModel.getProductLife());
//
//        productViewDialogBinding.AddTOCart.setOnClickListener(v -> {
////            addToCart(productModel,productViewDialogBinding.AddTOCart,bottomSheetDialog,productViewDialogBinding.quantityBox);
//        });
//        sameProducts.remove(productModel);
//        if (sameProducts.isEmpty()){
//            productViewDialogBinding.product.setVisibility(View.GONE);
//            productViewDialogBinding.itemCategory.setVisibility(View.GONE);
//        }else {
//            LinearLayoutManager LayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//            productViewDialogBinding.itemCategory.setLayoutManager(LayoutManager);
//            productViewDialogBinding.itemCategory.setAdapter(new ProductAdapter(sameProducts, new onClickProductAdapter() {
//                @Override
//                public void onClick(ProductModel productModel, ArrayList<ProductModel> sameProducts) {
//                    showProductViewDialogs(productModel,sameProducts);
//                }
//            }, this, "Main"));
//        }
//
//
//
//        productViewDialogBinding.plusBtn.setOnClickListener(view -> {
//            int quantity = productModel.getSelectableQuantity();
//            quantity++;
//            if (quantity > productModel.getStockCount()) {
//                Toast.makeText(this, "Max stock available: " + productModel.getStockCount(), Toast.LENGTH_SHORT).show();
//            }
//            else {
//                productModel.setSelectableQuantity(quantity);
//                productViewDialogBinding.quantity.setText(String.valueOf(productModel.getSelectableQuantity()));
//            }
//
//        });
//        productViewDialogBinding.minusBtn.setOnClickListener(view -> {
//            int quantity = productModel.getSelectableQuantity();
//            if (quantity > 1)
//                quantity--;
////            selectQTY = quantity;
//            productModel.setSelectableQuantity(quantity);
//            productViewDialogBinding.quantity.setText(String.valueOf(productModel.getSelectableQuantity()));
//
//        });
//
//
//
//
//        bottomSheetDialog.setContentView(productViewDialogBinding.getRoot());
//        bottomSheetDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        bottomSheetDialog.getWindow().getAttributes().windowAnimations = R.style.bottom_sheet_dialogAnimation;
//        bottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
//
//        bottomSheetDialog.show();
//    }
//

}