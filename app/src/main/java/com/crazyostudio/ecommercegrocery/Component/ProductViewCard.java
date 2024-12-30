package com.crazyostudio.ecommercegrocery.Component;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.crazyostudio.ecommercegrocery.Activity.AuthMangerActivity;
import com.crazyostudio.ecommercegrocery.Adapter.DialogSliderAdapter;
import com.crazyostudio.ecommercegrocery.Adapter.ProductAdapter;
import com.crazyostudio.ecommercegrocery.Adapter.SliderAdapter;
import com.crazyostudio.ecommercegrocery.Adapter.VariantsAdapter;
import com.crazyostudio.ecommercegrocery.Manager.ProductManager;
import com.crazyostudio.ecommercegrocery.Model.BrandModel;
import com.crazyostudio.ecommercegrocery.Model.ProductModel;
import com.crazyostudio.ecommercegrocery.Model.ShoppingCartFirebaseModel;
import com.crazyostudio.ecommercegrocery.Model.Variations;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.Services.BrandService;
import com.crazyostudio.ecommercegrocery.Services.DatabaseService;
import com.crazyostudio.ecommercegrocery.databinding.DialogFullscreenImageBinding;
import com.crazyostudio.ecommercegrocery.databinding.ProductViewDialogBinding;
import com.crazyostudio.ecommercegrocery.databinding.RemoveProductBoxAlertBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class ProductViewCard {
    Activity context;
    DatabaseService databaseService;
    String userId;
    ProductManager productManager;
    public ProductViewCard(Activity context1){
        context=context1;
        databaseService = new DatabaseService();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        productManager = new ProductManager(context1);
        if (currentUser != null){
//            String defaultUserName = ValuesHelper.DEFAULT_USER_NAME;
//            String displayName = currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "Hi user";
//            String phoneNumber = currentUser.getPhoneNumber() != null ? currentUser.getPhoneNumber() : "No phone number";
            userId = currentUser.getUid();
        }
    }
    
    public void showProductViewDialog(ProductModel productModel, ArrayList<ProductModel> sameProducts) {
//        ProductManager productManager = new ProductManager(context);
        List<String> imageUrls = productModel.getProductImage(); // Add your image URLs here
        Dialog bottomSheetDialog = new Dialog(context);
        ProductViewDialogBinding productViewDialogBinding = ProductViewDialogBinding.inflate(context.getLayoutInflater());
        ViewPager2 viewPager = productViewDialogBinding.viewPager;
        SliderAdapter sliderAdapter = new SliderAdapter(context, imageUrls, position -> {
            showImageInDialog(position, imageUrls);
        });
        viewPager.setAdapter(sliderAdapter);


        productViewDialogBinding.closeButton.setOnClickListener(v -> bottomSheetDialog.dismiss());


        List<Variations> variationsList = new ArrayList<>();
        ArrayList<ProductModel> productModels = new ArrayList<>();
        VariantsAdapter variantsAdapter = new VariantsAdapter(context, variationsList, productModel.getProductId(), new VariantsAdapter.VariantsCallback() {
            @Override
            public void Product(String id) {
                boolean isTrue = false;
                for (int i = 0; i < productModels.size(); i++) {
                    if (id.equals(productModels.get(i).getProductId())) {
                        showProductViewDialog(productModels.get(i), sameProducts);
                        isTrue = true;
                    }
                }
                if (!isTrue) {
                    if (!id.equals(productModel.getProductId())) {
                        databaseService.getAllProductById(id, new DatabaseService.GetAllProductsModelCallback() {
                            @Override
                            public void onSuccess(ProductModel products) {
                                if (sameProducts != null) {
                                    showProductViewDialog(products,sameProducts);
                                }
                            }
                            @Override
                            public void onError(String errorMessage) {

                            }
                        });
                    }
                }
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
        else if (productModel.getProductIsFoodItem().equals("FoodNonVeg")) {
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
        sameProducts.remove(productModel);
        if (sameProducts.isEmpty()){
//            productViewDialogBinding.product.setVisibility(View.GONE);
//            productViewDialogBinding.itemCategory.setVisibility(View.GONE);
        }else {
            LinearLayoutManager LayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            productViewDialogBinding.similarProductsRecyclerView.setLayoutManager(LayoutManager);
            productViewDialogBinding.similarProductsRecyclerView.setAdapter(new ProductAdapter(sameProducts, this::showProductViewDialog,context,"Main"));

        }






        productViewDialogBinding.AddTOCart.setOnClickListener(v -> {
            if (FirebaseAuth.getInstance().getCurrentUser()!=null) {
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

//            addToCart(productModel,productViewDialogBinding.AddTOCart,bottomSheetDialog,productViewDialogBinding.quantityBox);
            }else {
                context.startActivity(new Intent(context, AuthMangerActivity.class));
            }

        });


//        end set up





        productViewDialogBinding.plusBtn.setOnClickListener(view -> {
            int currentQty = Integer.parseInt(productViewDialogBinding.quantity.getText().toString());
            int newQty = currentQty + 1;
            
            if (newQty <= productModel.getMaxSelectableQuantity() && newQty <= productModel.getStockCount()) {
                productViewDialogBinding.quantity.setText(String.valueOf(newQty));
                productManager.UpdateCartQuantityById(userId, productModel.getProductId(), newQty);
            } else {
                Toast.makeText(context, "Maximum quantity available: " + 
                    Math.min(productModel.getMaxSelectableQuantity(), productModel.getStockCount()), 
                    Toast.LENGTH_SHORT).show();
            }
        });

        productViewDialogBinding.minusBtn.setOnClickListener(view -> {
            int currentQty = Integer.parseInt(productViewDialogBinding.quantity.getText().toString());
            int newQty = currentQty - 1;
            
            if (newQty >= productModel.getMinSelectableQuantity()) {
                productViewDialogBinding.quantity.setText(String.valueOf(newQty));
                productManager.UpdateCartQuantityById(userId, productModel.getProductId(), newQty);
            } else {
                // Show remove confirmation dialog
                showRemoveConfirmationDialog(productModel, productViewDialogBinding);
            }
        });



//




        bottomSheetDialog.setContentView(productViewDialogBinding.getRoot());
        bottomSheetDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        bottomSheetDialog.getWindow().getAttributes().windowAnimations = R.style.bottom_sheet_dialogAnimation;
        bottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);

        bottomSheetDialog.show();
    }


    private void showImageInDialog(int position,List<String> imageUrls) {
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

    private void showRemoveConfirmationDialog(ProductModel productModel, ProductViewDialogBinding productViewDialogBinding) {
        Dialog removeDialog = new Dialog(context);
        RemoveProductBoxAlertBinding alertBinding = RemoveProductBoxAlertBinding.inflate(context.getLayoutInflater());
        
        removeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        removeDialog.setContentView(alertBinding.getRoot());
        
        // Set up dialog window
        Window window = removeDialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams params = window.getAttributes();
            params.windowAnimations = android.R.style.Animation_Dialog;
            params.gravity = Gravity.CENTER;
            window.setAttributes(params);
            
            // Add dim background
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.setDimAmount(0.5f);
        }

        // Set custom message with product name
        String message = "Are you sure you want to remove " + productModel.getProductName() + " from your cart?";
        alertBinding.removeProductMessage.setText(message);

        // Add click animations to buttons
        alertBinding.cancelRemoveButton.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(context, R.anim.button_click));
            new Handler().postDelayed(() -> {
                removeDialog.dismiss();
                // Reset quantity to minimum
                productViewDialogBinding.quantity.setText(String.valueOf(productModel.getMinSelectableQuantity()));
            }, 150);
        });

        alertBinding.confirmRemoveButton.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(context, R.anim.button_click));
            new Handler().postDelayed(() -> {
                productManager.RemoveCartProductById(userId, productModel.getProductId());
                productViewDialogBinding.AddTOCart.setVisibility(View.VISIBLE);
                productViewDialogBinding.quantityBox.setVisibility(View.GONE);
                removeDialog.dismiss();
            }, 150);
        });

        removeDialog.show();
    }

}
