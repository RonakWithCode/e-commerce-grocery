package com.crazyostudio.ecommercegrocery.Component;

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
import com.crazyostudio.ecommercegrocery.Activity.AuthMangerActivity;
import com.crazyostudio.ecommercegrocery.Activity.BrandActivity;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    
    @SuppressLint("NotifyDataSetChanged")
    public void showProductViewDialog(ProductModel productModel, ArrayList<ProductModel> sameProducts) {
//        ProductManager productManager = new ProductManager(context);
//        Log.i(TAG, "showProductViewDialog: Product ID"+ productModel.getProductId());
        String fullUnit = productModel.getWeightSIUnit();  // This will give you the full unit name like "Grams"

        String displayUnit = WEIGHT_SI_UNITS_ABBR.getOrDefault(fullUnit, fullUnit); // Default to full name if no abbreviation is found

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
        VariantsAdapter variantsAdapter = new VariantsAdapter(context, variationsList, productModel.getProductId(), id -> {
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
        });


        RecyclerView variantsRecyclerView = productViewDialogBinding.variantsList;
        variantsRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        variantsRecyclerView.setAdapter(variantsAdapter);
        variationsList.add(new Variations(productModel.getProductId(),productModel.getMinSelectableQuantity()+" * "+productModel.getWeight() + " " + displayUnit,productModel.getPrice()+""));
        variantsAdapter.notifyDataSetChanged();
//        Product Variations

        productViewDialogBinding.Price.setText("₹" + productModel.getPrice());



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
            // Create an instance of SliderBrandFragment with parameters
//            SliderBrandFragment sliderBrandFragment = SliderBrandFragment.newInstance(
//                    productModel.getBrand()
//                     // Pass the brand name
//            );

//            SliderBrandFragment fragment  = new SliderBrandFragment();
//            Bundle bundle = new Bundle();
//            bundle.putString("brand",productModel.getBrand());
//            fragment.setArguments(bundle);
//            AppCompatActivity activity = (AppCompatActivity) context;
//            activity.getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.loader,fragment)
//                    .addToBackStack("HomeFragment")
//                    .commit();

            Intent intent = new Intent(context, BrandActivity.class);
            intent.putExtra("brand", productModel.getBrand());
//            .startActivity
            context.startActivity(intent);
//            bottomSheetDialog.dismiss();


        });

//TODO

// Now you can display `displayUnit` in your UI
        productViewDialogBinding.size.setText(productModel.getWeight() + " " + displayUnit);

//        productViewDialogBinding.size.setText(productModel.getWeight() +" "+ productModel.getWeightSIUnit());
        productViewDialogBinding.MRP.setText(":" + productModel.getMrp());
        productViewDialogBinding.MRP.setPaintFlags(productViewDialogBinding.MRP.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        if (!productModel.getProductDescription().isEmpty())
        {
            productViewDialogBinding.Description.setVisibility(View.VISIBLE);
            productViewDialogBinding.DescriptionTextView.setVisibility(View.VISIBLE);
            productViewDialogBinding.Description.setText(productModel.getProductDescription());
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





//   Show the in cat
        if (sameProducts.isEmpty()){

//            productViewDialogBinding.product.setVisibility(View.GONE);
//            productViewDialogBinding.itemCategory.setVisibility(View.GONE);
        }else {
            LinearLayoutManager LayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            productViewDialogBinding.similarProductsRecyclerView.setLayoutManager(LayoutManager);
            productViewDialogBinding.similarProductsRecyclerView.setAdapter(new ProductAdapter(sameProducts, this::showProductViewDialog,context,"Main"));
            sameProducts.remove(productModel);

        }






//        productViewDialogBinding.AddTOCart.setOnClickListener(v -> {
//            if (FirebaseAuth.getInstance().getCurrentUser()!=null) {
//                productManager.addToBothDatabase(new ShoppingCartFirebaseModel(productModel.getProductId(), productModel.getMinSelectableQuantity()), new ProductManager.AddListenerForAddToBothInDatabase() {
//                    @Override
//                    public void added(ShoppingCartFirebaseModel shoppingCartFirebaseModel) {
//                        productViewDialogBinding.AddTOCart.setVisibility(View.GONE);
//                        productViewDialogBinding.quantityBox.setVisibility(View.VISIBLE);
//                    }
//                    @Override
//                    public void failure(Exception e) {
//                        productViewDialogBinding.AddTOCart.setVisibility(View.VISIBLE);
//                        productViewDialogBinding.quantityBox.setVisibility(View.GONE);
//                        Toast.makeText(context, "check your network connection and try again ", Toast.LENGTH_SHORT).show();
//                    }
//                });
//
////            addToCart(productModel,productViewDialogBinding.AddTOCart,bottomSheetDialog,productViewDialogBinding.quantityBox);
//            }else {
//                context.startActivity(new Intent(context, AuthMangerActivity.class));
//            }
//
//        });




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
                String message = String.format("Maximum quantity available: %d", maxAllowed);
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
