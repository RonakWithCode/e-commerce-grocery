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
        put("Gram", "g");
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

    public ProductViewCard(Activity context1) {
        context = context1;
        databaseService = new DatabaseService();
        productManager = new ProductManager(context1);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void showProductViewDialog(ProductModel productModel, ArrayList<ProductModel> sameProducts) {
        mainProduct = productModel;
        String fullUnit = productModel.getWeightSIUnit();


        String displayUnit = WEIGHT_SI_UNITS_ABBR.getOrDefault(fullUnit, fullUnit);

        List<String> imageUrls = productModel.getProductImage();


        Dialog bottomSheetDialog = new Dialog(context);
        ProductViewDialogBinding binding = ProductViewDialogBinding.inflate(context.getLayoutInflater());

        // Set up image slider
        ViewPager2 viewPager = binding.viewPager;
        SliderAdapter sliderAdapter = new SliderAdapter(context, imageUrls, position -> showImageInDialog(position, imageUrls));
        viewPager.setAdapter(sliderAdapter);
        binding.closeButton.setOnClickListener(v -> bottomSheetDialog.dismiss());

        // Set up variations adapter
        ArrayList<Variations> variations = new ArrayList<>();
        ArrayList<ProductModel> variationsProduct = new ArrayList<>();
        VariantsAdapter variantsAdapter = new VariantsAdapter(context, variations, productModel.getProductId(), new VariantsAdapter.VariantsCallback() {
            @Override
            public void Product(String id) {
                Optional<ProductModel> matchingProduct = variationsProduct.stream()
                        .filter(product -> id.equals(product.getProductId()))
                        .findFirst();
                if (matchingProduct.isPresent()) {
                    showProductViewDialog(matchingProduct.get(), sameProducts);
                }
            }
        }, productModel.getProductLayoutType());
        RecyclerView variantsRecyclerView = binding.variantsList;
        variantsRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        variantsRecyclerView.setAdapter(variantsAdapter);

        // Add the main product as the first variation
        variations.add(new Variations(productModel.getProductId(), String.valueOf(productModel.getPrice()),
                productModel.getWeight(), productModel.getWeightSIUnit(), productModel.getProductImage().get(0)));

        if (productModel.getVariations() != null && !productModel.getVariations().isEmpty()) {
            for (int i = 0; i < productModel.getVariations().size(); i++) {
                databaseService.getAllProductById(
                        productModel.getVariations().get(i).getId(),
                        new DatabaseService.GetAllProductsModelCallback() {
                            @Override
                            public void onSuccess(ProductModel oneProduct) {
                                variations.add(new Variations(oneProduct.getProductId(), String.valueOf(oneProduct.getPrice()),
                                        oneProduct.getWeight(), oneProduct.getWeightSIUnit(), oneProduct.getProductImage().get(0)));
                                variationsProduct.add(oneProduct);
                                variantsAdapter.notifyDataSetChanged();
                            }
                            @Override
                            public void onError(String errorMessage) {
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            }
        }

        // Set product information
        binding.categoryName.setText(productModel.getCategory());
        binding.ProductName.setText(productModel.getProductName());
        binding.ProductName.post(() -> {
            if (binding.ProductName.getLineCount() > 1) {
                String fullText = productModel.getProductName();
                String truncatedText = fullText.substring(0, Math.min(fullText.length(), 60)) + "...";
                binding.ProductName.setText(truncatedText);
                binding.seeMoreButton.setVisibility(View.VISIBLE);
                binding.seeMoreButton.setOnClickListener(v -> {
                    if (binding.seeMoreButton.getText().toString().equals("See More")) {
                        binding.ProductName.setText(fullText);
                        binding.seeMoreButton.setText("See Less");
                    } else {
                        binding.ProductName.setText(truncatedText);
                        binding.seeMoreButton.setText("See More");
                    }
                });
            } else {
                binding.seeMoreButton.setVisibility(View.GONE);
            }
        });
        binding.Price.setText("₹" + productModel.getPrice());

        // Set brand info and click listener
        binding.brandNameInBox.setText(productModel.getBrand());
        BrandService brandService = new BrandService(context);
        brandService.getAllBrandWithIconsById(productModel.getBrand(), new BrandService.addBrandsByIdListener() {
            @Override
            public void onFailure(Exception error) { }
            @Override
            public void onSuccess(BrandModel brandModel) {
                Glide.with(context)
                        .load(brandModel.getBrandIcon())
                        .placeholder(R.drawable.product_image_shimmee_effect)
                        .error(R.drawable.product_image_shimmee_effect)
                        .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                        .centerCrop()
                        .into(binding.brandIcon);
            }
        });
        binding.brandBox.setOnClickListener(view -> {
            Intent intent = new Intent(context, BrandActivity.class);
            intent.putExtra("brand", productModel.getBrand());
            context.startActivity(intent);
        });


        if (productModel.getProductLayoutType().equals("Cloth")) {
//            displayUnit = fullUnit;
            binding.size.setText(displayUnit);


        }
        else {
            binding.size.setText(productModel.getWeight() + " " + displayUnit);
        }
        binding.MRP.setText(":" + productModel.getMrp());
        binding.MRP.setPaintFlags(binding.MRP.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        // Set product description with markdown
        if (!productModel.getProductDescription().isEmpty()) {
            binding.Description.setVisibility(View.VISIBLE);
            binding.DescriptionTextView.setVisibility(View.VISIBLE);
            try {
                Markwon markwon = Markwon.builder(context)
                        .usePlugin(HtmlPlugin.create())
                        .usePlugin(ImagesPlugin.create())
                        .usePlugin(LinkifyPlugin.create())
                        .build();
                markwon.setMarkdown(binding.Description, productModel.getProductDescription());
            } catch (Exception e) {
                binding.Description.setText(productModel.getProductDescription());
            }
        }

        // Handle Licenses if available
        if (productModel.getLicenses() != null && !productModel.getLicenses().isEmpty()) {
            binding.LicensesTextView.setVisibility(View.VISIBLE);
            binding.Licenses.setVisibility(View.VISIBLE);
            LicensesAdapter licensesAdapter = new LicensesAdapter(context, productModel.getLicenses());
            binding.Licenses.setAdapter(licensesAdapter);
            ViewGroup.LayoutParams params = binding.Licenses.getLayoutParams();
            params.height = (int) (productModel.getLicenses().size() * context.getResources().getDimension(com.intuit.sdp.R.dimen._48sdp));
            binding.Licenses.setLayoutParams(params);
        } else {
            binding.LicensesTextView.setVisibility(View.GONE);
            binding.Licenses.setVisibility(View.GONE);
        }

        // Observe cart state and update quantity or add-to-cart button
        productManager.observeCartItem(productModel.getProductId()).observe((LifecycleOwner) context, cartItem -> {
            if (cartItem != null) {
                binding.quantityBox.setVisibility(View.VISIBLE);
                binding.AddTOCart.setVisibility(View.GONE);
                binding.quantity.setText(String.valueOf(cartItem.getProductSelectQuantity()));
            } else {
                binding.AddTOCart.setVisibility(View.VISIBLE);
                binding.quantityBox.setVisibility(View.GONE);
            }
        });

        if (productModel.getStockCount() == 0) {
            binding.OutOfStockBuyOptions.setVisibility(View.VISIBLE);
            binding.quantity.setText("0");
            binding.quantityBox.setVisibility(View.GONE);
            binding.AddTOCart.setVisibility(View.GONE);
        }

        // Set food type icon
        String diet;
        switch (productModel.getProductIsFoodItem()) {
            case "FoodVeg":
                diet = "Veg";
                Glide.with(context)
                        .load(R.drawable.food_green)
                        .placeholder(R.drawable.product_image_shimmee_effect)
                        .error(R.drawable.product_image_shimmee_effect)
                        .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                        .centerCrop()
                        .into(binding.FoodTypeIcon);
                break;
            case "FoodNonVeg":
                diet = "NonVeg";
                Glide.with(context)
                        .load(R.drawable.food_brown)
                        .placeholder(R.drawable.product_image_shimmee_effect)
                        .error(R.drawable.product_image_shimmee_effect)
                        .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                        .centerCrop()
                        .into(binding.FoodTypeIcon);
                break;
            case "VegetableAndFruit":
                diet = "VegetableAndFruit";
                Glide.with(context)
                        .load(R.drawable.food_green)
                        .placeholder(R.drawable.product_image_shimmee_effect)
                        .error(R.drawable.product_image_shimmee_effect)
                        .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                        .centerCrop()
                        .into(binding.FoodTypeIcon);
                break;
            default:
                diet = "not food item.";
                binding.FoodTypeIcon.setVisibility(View.GONE);
                break;
        }

        // Set up similar products adapter
        ArrayList<ProductModel> similarProducts = new ArrayList<>(sameProducts);
        similarProducts.remove(productModel);
        if (similarProducts.isEmpty()) {
            binding.similarProductsRecyclerView.setVisibility(View.GONE);
            binding.similarProductsTitle.setVisibility(View.GONE);
        } else {
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            binding.similarProductsRecyclerView.setLayoutManager(layoutManager);
//            binding.similarProductsRecyclerView.setAdapter(
//                    new ProductAdapter(similarProducts,
//                            (ProductModel product, ArrayList<ProductModel> productList) ->
//                                    showProductViewDialog(product, productList),
//                            context, "Main")
//            );


            binding.similarProductsRecyclerView.setAdapter(
                    new ProductAdapter((LifecycleOwner) context,
                            similarProducts,
                            (ProductModel product, ArrayList<ProductModel> productList) ->
                                    showProductViewDialog(product, productList),
                            context)
            );


        }

        // Add-to-cart button
        binding.AddTOCart.setOnClickListener(view -> {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                if (productModel.getStockCount() < productModel.getMinSelectableQuantity()) {
                    Toast.makeText(context, "Sorry, not enough stock available", Toast.LENGTH_SHORT).show();
                    return;
                }
                productManager.addToBothDatabase(
                        new ShoppingCartFirebaseModel(productModel.getProductId(), productModel.getMinSelectableQuantity()),
                        new ProductManager.AddListenerForAddToBothInDatabase() {
                            @Override
                            public void added(ShoppingCartFirebaseModel shoppingCartFirebaseModel) {
                                binding.AddTOCart.setVisibility(View.GONE);
                                binding.quantityBox.setVisibility(View.VISIBLE);
                                binding.quantity.setText(String.valueOf(productModel.getMinSelectableQuantity()));
                            }
                            @Override
                            public void failure(Exception e) {
                                Toast.makeText(context, "Failed to add to cart", Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            } else {
                context.startActivity(new Intent(context, AuthMangerActivity.class));
            }
        });

        // Quantity adjustment buttons
        binding.plusBtn.setOnClickListener(view -> {
            int currentQty = Integer.parseInt(binding.quantity.getText().toString());
            int newQty = currentQty + 1;
            int maxAllowed = Math.min(productModel.getMaxSelectableQuantity(), productModel.getStockCount());
            if (newQty <= maxAllowed) {
                binding.quantity.setText(String.valueOf(newQty));
                productManager.UpdateCartQuantityById(userId, productModel.getProductId(), newQty);
            } else {
                String message = String.format("Maximum quantity available: %d", maxAllowed);
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });

        binding.minusBtn.setOnClickListener(view -> {
            int currentQty = Integer.parseInt(binding.quantity.getText().toString());
            int newQty = currentQty - 1;
            if (newQty >= productModel.getMinSelectableQuantity()) {
                binding.quantity.setText(String.valueOf(newQty));
                productManager.UpdateCartQuantityById(userId, productModel.getProductId(), newQty);
            } else {
                showRemoveConfirmationDialog(productModel, binding);
            }
        });

        // Set up dialog window and show
        bottomSheetDialog.setContentView(binding.getRoot());
        Objects.requireNonNull(bottomSheetDialog.getWindow())
                .setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        bottomSheetDialog.getWindow().getAttributes().windowAnimations = R.style.bottom_sheet_dialogAnimation;
        bottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomSheetDialog.show();
    }

    private void showImageInDialog(int position, List<String> imageUrls) {
        Dialog dialog = new Dialog(context);
        DialogFullscreenImageBinding binding = DialogFullscreenImageBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(binding.getRoot());

        // Create a new list that includes the original image URLs plus two extra images.
        List<String> extendedImageUrls = new ArrayList<>(imageUrls);
        String contactUri = "android.resource://" + context.getPackageName() + "/" + R.drawable.contact_info;
        String disclaimerUri = "android.resource://" + context.getPackageName() + "/" + R.drawable.disclaimer;
        extendedImageUrls.add(contactUri);
        extendedImageUrls.add(disclaimerUri);

        DialogSliderAdapter adapter = new DialogSliderAdapter(context, extendedImageUrls);
        binding.dialogViewPager.setAdapter(adapter);
        binding.dialogViewPager.setCurrentItem(position);
        binding.getRoot().setOnClickListener(v -> dialog.dismiss());
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.white);
        }
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_up);
        binding.getRoot().startAnimation(animation);
        dialog.show();
    }

    private void showRemoveConfirmationDialog(ProductModel productModel, ProductViewDialogBinding binding) {
        Dialog removeDialog = new Dialog(context);
        RemoveProductBoxAlertBinding alertBinding = RemoveProductBoxAlertBinding.inflate(context.getLayoutInflater());
        removeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        removeDialog.setContentView(alertBinding.getRoot());
        Window window = removeDialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams params = window.getAttributes();
            params.windowAnimations = android.R.style.Animation_Dialog;
            params.gravity = Gravity.CENTER;
            window.setAttributes(params);
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.setDimAmount(0.5f);
        }
        String message = "Are you sure you want to remove " + productModel.getProductName() + " from your cart?";
        alertBinding.removeProductMessage.setText(message);
        alertBinding.cancelRemoveButton.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(context, R.anim.button_click));
            new Handler().postDelayed(() -> {
                removeDialog.dismiss();
                binding.quantity.setText(String.valueOf(productModel.getMinSelectableQuantity()));
            }, 150);
        });
        alertBinding.confirmRemoveButton.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(context, R.anim.button_click));
            new Handler().postDelayed(() -> {
                productManager.RemoveCartProductById(productModel.getProductId());
                binding.AddTOCart.setVisibility(View.VISIBLE);
                binding.quantityBox.setVisibility(View.GONE);
                removeDialog.dismiss();
            }, 150);
        });
        removeDialog.show();
    }
}
