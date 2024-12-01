package com.crazyostudio.ecommercegrocery.Component;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
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

    public ProductViewCard(Activity context1){
        context=context1;
        databaseService = new DatabaseService();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null){
//            String defaultUserName = ValuesHelper.DEFAULT_USER_NAME;
//            String displayName = currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "Hi user";
//            String phoneNumber = currentUser.getPhoneNumber() != null ? currentUser.getPhoneNumber() : "No phone number";
            userId = currentUser.getUid();
        }
    }
    
    public void showProductViewDialog(ProductModel productModel, ArrayList<ProductModel> sameProducts) {
        ProductManager productManager = new ProductManager(context);
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
                RemoveProductBoxAlertBinding boxAlertBinding = RemoveProductBoxAlertBinding.inflate(context.getLayoutInflater());
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


}
