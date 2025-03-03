package com.ronosoft.alwarmart.Component;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
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

import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.ronosoft.alwarmart.Activity.AuthMangerActivity;
import com.ronosoft.alwarmart.Activity.BrandActivity;
import com.ronosoft.alwarmart.Adapter.DialogSliderAdapter;
import com.ronosoft.alwarmart.Adapter.LicensesAdapter;
import com.ronosoft.alwarmart.Adapter.ProductAdapter;
import com.ronosoft.alwarmart.Adapter.SliderAdapter;
import com.ronosoft.alwarmart.Adapter.VariantsAdapter;
import com.ronosoft.alwarmart.Manager.ProductManager;
import com.ronosoft.alwarmart.Model.BrandModel;
import com.ronosoft.alwarmart.Model.ProductModel;
import com.ronosoft.alwarmart.Model.ShoppingCartFirebaseModel;
import com.ronosoft.alwarmart.Model.Variations;
import com.ronosoft.alwarmart.R;
import com.ronosoft.alwarmart.Services.BrandService;
import com.ronosoft.alwarmart.Services.DatabaseService;
import com.ronosoft.alwarmart.databinding.DialogFullscreenImageBinding;
import com.ronosoft.alwarmart.databinding.ProductViewDialogBinding;
import com.ronosoft.alwarmart.databinding.RemoveProductBoxAlertBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import io.noties.markwon.Markwon;
import io.noties.markwon.html.HtmlPlugin;
import io.noties.markwon.image.ImagesPlugin;
import io.noties.markwon.linkify.LinkifyPlugin;


public class ProductViewCard {
    Activity context;
    DatabaseService databaseService;
    String userId;
    ProductManager productManager;
    private static final String TAG = "ProductViewCard";

    static Map<String, String> WEIGHT_SI_UNITS_ABBR = new HashMap<String, String>() {{
        put("Kg", "Kg");
        put("Grams", "g");
        put("HalfKg", "0.5 Kg");
        put("QuarterKg", "0.25 Kg");
        put("Litre", "L");
        put("HalfLitre", "0.5 L");
        put("Milliliters", "mL");
        put("Piece", "Piece");
        put("Pieces", "Pieces");
        put("Dozen", "Dozen");
        put("HalfDozen", "Half Dozen");
        put("Pack", "Pack");
        put("Box", "Box");
        put("Carton", "Carton");
        put("Packet", "Packet");
        put("Bag", "Bag");
        put("Bundle", "Bundle");
        put("Pouch", "Pouch");
        put("Sachet", "Sachet");
        put("Quintal", "Quintal (100 Kg)");
        put("Tola", "Tola (11.66 g)");
        put("Bunch", "Bunch");
        put("Strip", "Strip");
        put("Roll", "Roll");
        put("Sheet", "Sheet");
        put("Pair", "Pair");
        put("Bottle", "Bottle");
        put("Can", "Can");
        put("Jar", "Jar");
        put("Unit", "Unit");
        put("Other", "Other");


    }};
    ProductModel mainProduct;

    public ProductViewCard(Activity context1){
        context=context1;
        databaseService = new DatabaseService();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        productManager = new ProductManager(context1);
        if (currentUser != null){
            userId = currentUser.getUid();
        }
    }
    
    @SuppressLint("NotifyDataSetChanged")
    public void showProductViewDialog(ProductModel productModel, ArrayList<ProductModel> sameProducts) {

        mainProduct = productModel;
        String fullUnit = productModel.getWeightSIUnit();  // This will give you the full unit name like "Grams"
        String displayUnit = WEIGHT_SI_UNITS_ABBR.getOrDefault(fullUnit, fullUnit); // Default to full name if no abbreviation is found

        List<String> imageUrls = productModel.getProductImage(); // Add your image URLs here
        Dialog bottomSheetDialog = new Dialog(context);
        ProductViewDialogBinding productViewDialogBinding = ProductViewDialogBinding.inflate(context.getLayoutInflater());
        ViewPager2 viewPager = productViewDialogBinding.viewPager;
        SliderAdapter sliderAdapter = new SliderAdapter(context, imageUrls, position -> showImageInDialog(position, imageUrls));
        viewPager.setAdapter(sliderAdapter);


        productViewDialogBinding.closeButton.setOnClickListener(v -> bottomSheetDialog.dismiss());



        ArrayList<Variations> variations = new ArrayList<>();
        ArrayList<ProductModel> variationsProduct = new ArrayList<>();
        VariantsAdapter variantsAdapter = new VariantsAdapter(context,variations , productModel.getProductId(), new VariantsAdapter.VariantsCallback() {
            @Override
            public void Product(String id) {

                Optional<ProductModel> matchingProduct = variationsProduct.stream()
                        .filter(product -> id.equals(product.getProductId()))
                        .findFirst();

                if (matchingProduct.isPresent()) {
                    showProductViewDialog(matchingProduct.get(), sameProducts);
                } else {

                    // Optionally handle the case when the product is not found.


//                    TODO:3/3/25 add a function to get the product from the database with

                }

            }
        }, productModel.getProductLayoutType());
        RecyclerView variantsRecyclerView = productViewDialogBinding.variantsList;
        variantsRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        variantsRecyclerView.setAdapter(variantsAdapter);

        variations.add(new Variations(productModel.getProductId(),productModel.getPrice()+"",productModel.getWeight(),productModel.getWeightSIUnit(),productModel.getProductImage().get(0)));

        assert productModel.getVariations() != null;

        if (!productModel.getVariations().isEmpty()) {
            for (int i = 0; i < productModel.getVariations().size(); i++) {
                databaseService.getAllProductById(
                        productModel.getVariations().get(i).getId(),
                        new DatabaseService.GetAllProductsModelCallback() {
                            @Override
                            public void onSuccess(ProductModel oneProduct) {
                                variations.add(new Variations(
                                        oneProduct.getProductId(),
                                        String.valueOf(oneProduct.getPrice()),
                                        oneProduct.getWeight(),
                                        oneProduct.getWeightSIUnit(),
                                        oneProduct.getProductImage().get(0)
                                ));
                                variationsProduct.add(oneProduct);
                                variantsAdapter.notifyDataSetChanged(); // Update adapter after each successful load
                            }
                            @Override
                            public void onError(String errorMessage) {
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            }
            // Remove notifyDataSetChanged() call here if already handled inside callbacks.
        }











//        handleVariations(productModel, productViewDialogBinding, sameProducts, displayUnit);

//        End Product Variations

//        Set up
        productViewDialogBinding.categoryName.setText(productModel.getCategory());
        productViewDialogBinding.ProductName.setText(productModel.getProductName());
        productViewDialogBinding.ProductName.post(() -> {
            if (productViewDialogBinding.ProductName.getLineCount() > 1) {
                // Save original text
                String fullText = productModel.getProductName();
                // Create truncated text
                String truncatedText = fullText.substring(0, Math.min(fullText.length(), 60)) + "...";
                
                // Set initial state
                productViewDialogBinding.ProductName.setText(truncatedText);
                productViewDialogBinding.seeMoreButton.setVisibility(View.VISIBLE);
                
                // Set up click listener
                productViewDialogBinding.seeMoreButton.setOnClickListener(v -> {
                    if (productViewDialogBinding.seeMoreButton.getText().toString().equals("See More")) {
                        // Expand
                        productViewDialogBinding.ProductName.setText(fullText);
                        productViewDialogBinding.seeMoreButton.setText("See Less");
                    } else {
                        // Collapse
                        productViewDialogBinding.ProductName.setText(truncatedText);
                        productViewDialogBinding.seeMoreButton.setText("See More");
                    }
                });
            } else {
                productViewDialogBinding.seeMoreButton.setVisibility(View.GONE);
            }
        });

        productViewDialogBinding.Price.setText("₹" + productModel.getPrice());

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

        productViewDialogBinding.brandBox.setOnClickListener(view -> {
            Intent intent = new Intent(context, BrandActivity.class);
            intent.putExtra("brand", productModel.getBrand());
            context.startActivity(intent);
        });

        productViewDialogBinding.size.setText(productModel.getWeight() + " " + displayUnit);

//        productViewDialogBinding.size.setText(productModel.getWeight() +" "+ productModel.getWeightSIUnit());
        productViewDialogBinding.MRP.setText(":" + productModel.getMrp());
        productViewDialogBinding.MRP.setPaintFlags(productViewDialogBinding.MRP.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        if (!productModel.getProductDescription().isEmpty()) {
            productViewDialogBinding.Description.setVisibility(View.VISIBLE);
            productViewDialogBinding.DescriptionTextView.setVisibility(View.VISIBLE);

            try {
                // Initialize Markwon with all required plugins
                Markwon markwon = Markwon.builder(context)
                        .usePlugin(HtmlPlugin.create())
                        .usePlugin(ImagesPlugin.create())
                        .usePlugin(LinkifyPlugin.create())
                        .build();

                // Set the markdown/HTML content
                markwon.setMarkdown(productViewDialogBinding.Description, productModel.getProductDescription());
            } catch (Exception e) {
                // Fallback to plain text if Markwon fails
                productViewDialogBinding.Description.setText(productModel.getProductDescription());
            }
        }
        
        // Handle Licenses if available
        if (productModel.getLicenses() != null && !productModel.getLicenses().isEmpty()) {
            productViewDialogBinding.LicensesTextView.setVisibility(View.VISIBLE);
            productViewDialogBinding.Licenses.setVisibility(View.VISIBLE);
            
            // Create and set adapter for licenses
            LicensesAdapter licensesAdapter = new LicensesAdapter(context, productModel.getLicenses());
            productViewDialogBinding.Licenses.setAdapter(licensesAdapter);
            
            // Set fixed height based on number of items (to avoid scrolling issues in NestedScrollView)
            ViewGroup.LayoutParams params = productViewDialogBinding.Licenses.getLayoutParams();
            params.height = (int) (productModel.getLicenses().size() * context.getResources().getDimension(com.intuit.sdp.R.dimen._48sdp));
            productViewDialogBinding.Licenses.setLayoutParams(params);
        } else {
            productViewDialogBinding.LicensesTextView.setVisibility(View.GONE);
            productViewDialogBinding.Licenses.setVisibility(View.GONE);
        }



// TODO  Check the cart if product in cart it set a quantity selector
        productManager.observeCartItem(productModel.getProductId()).observe((LifecycleOwner) context, cartItem -> {
            if (cartItem != null) {
//                holder.binding.addToCart.setVisibility(View.GONE);
//                holder.binding.quantityLayout.setVisibility(View.VISIBLE);
//                holder.binding.quantity.setText(String.valueOf(cartItem.getProductSelectQuantity()));
                productViewDialogBinding.quantityBox.setVisibility(View.VISIBLE);
                productViewDialogBinding.AddTOCart.setVisibility(View.GONE);
                productViewDialogBinding.quantity.setText(""+cartItem.getProductSelectQuantity());
//
            } else {
//                holder.binding.addToCart.setVisibility(View.VISIBLE);
//                holder.binding.quantityLayout.setVisibility(View.GONE);
                productViewDialogBinding.AddTOCart.setVisibility(View.VISIBLE);
                productViewDialogBinding.quantityBox.setVisibility(View.GONE);

            }
        });


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




        ArrayList<ProductModel> similarProducts = new ArrayList<>(sameProducts);

// Remove the current product from the copy only
        similarProducts.remove(productModel);

// Now, set up your adapter with this new list
        if (similarProducts.isEmpty()) {
            productViewDialogBinding.similarProductsRecyclerView.setVisibility(View.GONE);
            productViewDialogBinding.similarProductsTitle.setVisibility(View.GONE);
        } else {
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            productViewDialogBinding.similarProductsRecyclerView.setLayoutManager(layoutManager);
            productViewDialogBinding.similarProductsRecyclerView.setAdapter(
                    new ProductAdapter(similarProducts, this::showProductViewDialog, context, "Main")
            );
        }


        productViewDialogBinding.AddTOCart.setOnClickListener(view -> {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                if (productModel.getStockCount() < productModel.getMinSelectableQuantity()) {
                    Toast.makeText(context, "Sorry, not enough stock available", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                productManager.addToBothDatabase(
                    new ShoppingCartFirebaseModel(
                        productModel.getProductId(), 
                        productModel.getMinSelectableQuantity()
                    ),
                    new ProductManager.AddListenerForAddToBothInDatabase() {
                        @Override
                        public void added(ShoppingCartFirebaseModel shoppingCartFirebaseModel) {
                            productViewDialogBinding.AddTOCart.setVisibility(View.GONE);
                            productViewDialogBinding.quantityBox.setVisibility(View.VISIBLE);
                            productViewDialogBinding.quantity.setText(
                                String.valueOf(productModel.getMinSelectableQuantity())
                            );
                        }

                        @Override
                        public void failure(Exception e) {
                            Toast.makeText(context, "Failed to add to cart", Toast.LENGTH_SHORT).show();
                        }
                    });
            } else {
                context.startActivity(new Intent(context, AuthMangerActivity.class));
            }
        });


//        end set up





        productViewDialogBinding.plusBtn.setOnClickListener(view -> {
            int currentQty = Integer.parseInt(productViewDialogBinding.quantity.getText().toString());
            int newQty = currentQty + 1;
            int maxAllowed = Math.min(productModel.getMaxSelectableQuantity(), productModel.getStockCount());
            
            if (newQty <= maxAllowed) {
                productViewDialogBinding.quantity.setText(String.valueOf(newQty));
                productManager.UpdateCartQuantityById(userId, productModel.getProductId(), newQty);
            } else {
                @SuppressLint("DefaultLocale") String message = String.format("Maximum quantity available: %d", maxAllowed);
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });

        productViewDialogBinding.minusBtn.setOnClickListener(view -> {
            int currentQty = Integer.parseInt(productViewDialogBinding.quantity.getText().toString());
            int newQty = currentQty - 1;
            
            if (newQty >= productModel.getMinSelectableQuantity()) {
                productViewDialogBinding.quantity.setText(String.valueOf(newQty));
                productManager.UpdateCartQuantityById(userId, productModel.getProductId(), newQty);
            } else {
                showRemoveConfirmationDialog(productModel, productViewDialogBinding);
            }
        });



//




        bottomSheetDialog.setContentView(productViewDialogBinding.getRoot());
        Objects.requireNonNull(bottomSheetDialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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
        dialogBinding.getRoot().setOnClickListener(v -> dialog.dismiss());
//        dialogBinding.close.setOnClickListener(v->{
//            dialog.dismiss();
//        });
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
