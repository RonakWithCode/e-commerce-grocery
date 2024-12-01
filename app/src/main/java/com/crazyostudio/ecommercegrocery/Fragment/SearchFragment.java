package com.crazyostudio.ecommercegrocery.Fragment;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
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
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.crazyostudio.ecommercegrocery.Activity.AuthMangerActivity;
import com.crazyostudio.ecommercegrocery.Adapter.DialogSliderAdapter;
import com.crazyostudio.ecommercegrocery.Adapter.ProductAdapter;
import com.crazyostudio.ecommercegrocery.Adapter.SearchAdapter;
import com.crazyostudio.ecommercegrocery.Adapter.SearchAdapterInterface;
import com.crazyostudio.ecommercegrocery.Adapter.SliderAdapter;
import com.crazyostudio.ecommercegrocery.Adapter.VariantsAdapter;
import com.crazyostudio.ecommercegrocery.Adapter.ViewAdapterSearchViewRecommendation;
import com.crazyostudio.ecommercegrocery.Manager.ProductManager;
import com.crazyostudio.ecommercegrocery.Model.BrandModel;
import com.crazyostudio.ecommercegrocery.Model.HomeProductModel;
import com.crazyostudio.ecommercegrocery.Model.ProductModel;
import com.crazyostudio.ecommercegrocery.Model.ShoppingCartFirebaseModel;
import com.crazyostudio.ecommercegrocery.Model.Variations;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.Services.AuthService;
import com.crazyostudio.ecommercegrocery.Services.BrandService;
import com.crazyostudio.ecommercegrocery.Services.DatabaseService;
import com.crazyostudio.ecommercegrocery.databinding.DialogFullscreenImageBinding;
import com.crazyostudio.ecommercegrocery.databinding.FragmentSearchBinding;
import com.crazyostudio.ecommercegrocery.databinding.ProductViewDialogBinding;
import com.crazyostudio.ecommercegrocery.databinding.RemoveProductBoxAlertBinding;
import com.crazyostudio.ecommercegrocery.interfaceClass.HomeProductInterface;
import com.crazyostudio.ecommercegrocery.interfaceClass.onClickProductAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchFragment extends Fragment implements SearchAdapterInterface {
    private FragmentSearchBinding binding;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference productsRef = db.collection("Product");
    private ActionBar actionBar;
    ViewAdapterSearchViewRecommendation adapterSearchViewRecommendation;
    private SearchAdapter adapter;

    private static final int SPEECH_REQUEST_CODE = 0;
    ArrayList<HomeProductModel> homeProductModels;
    DatabaseService databaseService;
    String userId;
    ProductManager productManager;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            // Hide the ActionBar when the fragment is created
            actionBar.hide();
        }
        if (getArguments() != null) {
            // Check if the arguments contain the "model" key
             if (getArguments().containsKey("model")) {
                homeProductModels = getArguments().getParcelableArrayList("model");
            }
        } else {
            // Handle the case where arguments are null
//            Log.e("FragmentName", "Arguments are null");
            homeProductModels = new ArrayList<>(); // Initialize with an empty list
        }

        productManager = new ProductManager(requireActivity());
        userId = new AuthService().getUserId();
        databaseService = new DatabaseService();
        adapterSearchViewRecommendation = new ViewAdapterSearchViewRecommendation(homeProductModels, new HomeProductInterface() {
            @Override
            public void HomeProductOnclick(ProductModel productModel, ArrayList<ProductModel> sameProducts) {

            }}, requireActivity());

        binding.Recommendation.setLayoutManager(new LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false));
        binding.Recommendation.setAdapter(adapterSearchViewRecommendation);

        adapter = new SearchAdapter(requireContext(),this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);



//        binding.backBtn.setOnClickListener(view-> requireActivity().onBackPressed());
//        binding.searchBar.setSpeechMode(true); // Enable voice search
//        binding.searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
//            @Override
//            public void onSearchStateChanged(boolean enabled) {
//                // Handle search state changes
//            }
//
//            @Override
//            public void onSearchConfirmed(CharSequence text) {
//                // Perform a search based on the entered text
//
//            }
//
//            @Override
//            public void onButtonClicked(int buttonCode) {
//                if (buttonCode == MaterialSearchBar.BUTTON_SPEECH) {
//                    openVoiceRecognizer();
//                }
//                else if (buttonCode == MaterialSearchBar.BUTTON_BACK){
//                    binding.searchBar.closeSearch();
////                    binding.backBtn.setVisibility(View.VISIBLE);
////                    Toast.makeText(requireContext(), "", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        });

        binding.backIcon.setOnClickListener(v -> requireActivity().onBackPressed());

        binding.micIcon.setOnClickListener(v -> openVoiceRecognizer());

        binding.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString();
                searchProductByKeyword(query);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        return binding.getRoot();
    }
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        if (actionBar != null) {
//            actionBar.show();
//        }
//    }


//    rivate void searchProductByName(String productName) {
//        // Perform search only if product name is not empty
//        if (!productName.isEmpty()) {
//            // Convert the product name to lowercase for case-insensitive search
//            String lowercaseProductName = productName.toLowerCase();
//
//            // Create a query to filter products by name
//            Query query = productsRef.whereGreaterThanOrEqualTo("productName", lowercaseProductName)
//                    .whereLessThanOrEqualTo("productName", lowercaseProductName + "\uf8ff");
//
//            // Execute the query
//            query.get().addOnSuccessListener(queryDocumentSnapshots -> {
//                // Handle successful query result
//                ArrayList<ProductModel> models = new ArrayList<>();
//                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//                    // Retrieve and process each document
//                    ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
//                    models.add(productModel);
//                }
//                // Set data to your adapter after the loop to avoid setting data for each iteration
//                adapter.setData(models);
//            }).addOnFailureListener(e -> {
//                // Handle query failure
//                System.err.println("Error searching for products: " + e.getMessage());
//            });
//        }
//        else {
//            // If product name is empty, do nothing or show a message
//            System.out.println("Product name is empty");
//        }
//    } p

    private void searchProductByKeyword(String keyword) {

        // Perform search only if keyword is not empty
        if (!keyword.isEmpty()) {
            // Convert the keyword to lowercase for case-insensitive search
            String lowercaseKeyword = keyword.toLowerCase(Locale.getDefault());

            // Create a query to filter products based on the keyword
            Query query = productsRef.whereArrayContains("keywords", lowercaseKeyword);

            // Execute the query
            query.get().addOnSuccessListener(queryDocumentSnapshots -> {
                // Initialize an ArrayList to store matching products
                ArrayList<ProductModel> matchingProducts = new ArrayList<>();

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    // Retrieve each matching product
                    ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
                    matchingProducts.add(productModel);
                }

                // Set data to your adapter after completing the search
                adapter.setData(matchingProducts);
            }).addOnFailureListener(e -> {
                // Handle query failure
                Log.e("SearchFragment", "Error searching for products: " + e.getMessage());
            });
        } else {
            // If keyword is empty, do nothing or show a message
            Log.d("SearchFragment", "Keyword is empty");
        }
    }

//    private void searchProductByKeyword(String keyword) {
//        // Perform search only if keyword is not empty
//        if (!keyword.isEmpty()) {
//            // Convert the keyword to lowercase for case-insensitive search
//            String lowercaseKeyword = keyword.toLowerCase();
//
//            // Create a query to filter products based on the keyword
//            Query query = productsRef
//                    .orderBy("productName") // Order products by name for consistency
//                    .startAt(lowercaseKeyword) // Start the query at the keyword
//                    .endAt(lowercaseKeyword + "\uf8ff"); // End the query at the keyword + Unicode character
//
//            // Execute the query
//            query.get().addOnSuccessListener(queryDocumentSnapshots -> {
//                // Initialize an ArrayList to store matching products
//                ArrayList<ProductModel> matchingProducts = new ArrayList<>();
//
//                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//                    // Retrieve each matching product
//                    ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
//                    matchingProducts.add(productModel);
//                }
//
//                // Set data to your adapter after completing the search
//                adapter.setData(matchingProducts);
//            }).addOnFailureListener(e -> {
//                // Handle query failure
//                Log.e("SearchFragment", "Error searching for products: " + e.getMessage());
//            });
//        } else {
//            // If keyword is empty, do nothing or show a message
//            Log.d("SearchFragment", "Keyword is empty");
//        }
//    }



//    private void searchProductByKeyword(String query) {
//        adapter.clearData();
//        // Perform search only if query is not empty
//        if (!query.isEmpty()) {
//            // Convert the query to lowercase for case-insensitive search
//            String lowercaseQuery = query.toLowerCase();
//
//            // Initialize an ArrayList to store matching products
//            ArrayList<ProductModel> matchingProducts = new ArrayList<>();
//
//            // Create a query to filter products by name
//            Query nameQuery = productsRef.whereGreaterThanOrEqualTo("productName", lowercaseQuery)
//                    .whereLessThanOrEqualTo("productName", lowercaseQuery + "\uf8ff");
//
//            // Execute the name query
//            nameQuery.get().addOnSuccessListener(nameQuerySnapshot -> {
//                for (QueryDocumentSnapshot documentSnapshot : nameQuerySnapshot) {
//                    // Retrieve each matching product
//                    ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
//                    // Add the product to the list of matching products
//                    matchingProducts.add(productModel);
//                }
//
//                // Create a query to filter products based on keywords
//                Query keywordQuery = productsRef.whereArrayContains("keywords", lowercaseQuery);
//
//                // Execute the keyword query
//                keywordQuery.get().addOnSuccessListener(keywordQuerySnapshot -> {
//                    for (QueryDocumentSnapshot documentSnapshot : keywordQuerySnapshot) {
//                        // Retrieve each matching product
//                        ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
//                        // Add the product to the list of matching products if not already added
//                        if (!matchingProducts.contains(productModel)) {
//                            matchingProducts.add(productModel);
//                        }
//                    }
//
//                    // Set data to your adapter after completing the search
//                    adapter.setData(matchingProducts);
//                }).addOnFailureListener(e -> {
//                    // Handle query failure
//                    Log.e("SearchFragment", "Error searching for products: " + e.getMessage());
//                });
//
//            }).addOnFailureListener(e -> {
//                // Handle query failure
//                Log.e("SearchFragment", "Error searching for products: " + e.getMessage());
//            });
//        } else {
//            // If query is empty, do nothing or show a message
//            Log.d("SearchFragment", "Query is empty");
//        }
//    }


//    private void searchProductByKeyword(String query) {
//        adapter.clearData();
//        // Perform search only if query is not empty
//        if (!query.isEmpty()) {
//            // Convert the query to lowercase for case-insensitive search
//            String lowercaseQuery = query.toLowerCase();
//
//            // Initialize a HashSet to store unique product IDs
//            HashSet<String> uniqueProductIds = new HashSet<>();
//
//            // Create a query to filter products by name
//            Query nameQuery = productsRef.whereGreaterThanOrEqualTo("productName", lowercaseQuery)
//                    .whereLessThanOrEqualTo("productName", lowercaseQuery + "\uf8ff");
//
//            // Execute the name query
//            nameQuery.get().addOnSuccessListener(nameQuerySnapshot -> {
//                for (QueryDocumentSnapshot documentSnapshot : nameQuerySnapshot) {
//                    // Retrieve each matching product
//                    ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
//                    // Add the product ID to the set of unique product IDs
//                    uniqueProductIds.add(productModel.getProductId());
//                }
//
//                // Create a query to filter products based on keywords
//                Query keywordQuery = productsRef.whereArrayContains("keywords", lowercaseQuery);
//
//                // Execute the keyword query
//                keywordQuery.get().addOnSuccessListener(keywordQuerySnapshot -> {
//                    for (QueryDocumentSnapshot documentSnapshot : keywordQuerySnapshot) {
//                        // Retrieve each matching product
//                        ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
//                        // Add the product ID to the set of unique product IDs
//                        uniqueProductIds.add(productModel.getProductId());
//                    }
//
//                    // Initialize an ArrayList to store matching products
//                    ArrayList<ProductModel> matchingProducts = new ArrayList<>();
//
//                    // Retrieve products based on the unique product IDs
//                    for (String productId : uniqueProductIds) {
//                        // Create a query to retrieve the product by its ID
//                        Query productQuery = productsRef.whereEqualTo("productId", productId);
//
//                        // Execute the product query
//                        productQuery.get().addOnSuccessListener(productQuerySnapshot -> {
//                            // Retrieve the product and add it to the list of matching products
//                            for (QueryDocumentSnapshot snapshot : productQuerySnapshot) {
//                                ProductModel productModel = snapshot.toObject(ProductModel.class);
//                                matchingProducts.add(productModel);
//                            }
//
//                            // Set data to your adapter after completing the search
//                            adapter.setData(matchingProducts);
//                        }).addOnFailureListener(e -> {
//                            // Handle query failure
//                            Log.e("SearchFragment", "Error retrieving product: " + e.getMessage());
//                        });
//                    }
//
//                }).addOnFailureListener(e -> {
//                    // Handle query failure
//                    Log.e("SearchFragment", "Error searching for products: " + e.getMessage());
//                });
//
//            }).addOnFailureListener(e -> {
//                // Handle query failure
//                Log.e("SearchFragment", "Error searching for products: " + e.getMessage());
//            });
//        } else {
//            // If query is empty, do nothing or show a message
//            Log.d("SearchFragment", "Query is empty");
//        }
//    }


    private void openVoiceRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
// This starts the activity and populates the intent with the speech text.
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
//        binding.backBtn.setVisibility(View.GONE);


    }
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);

            binding.searchEditText.setText(spokenText);

            // Set cursor position to the end of the text
//            binding.searchBar.setSelection(binding.searchBar.getText().length());
            binding.searchEditText.setSelection(spokenText.length());
            // Request focus on the search bar
            binding.searchEditText.requestFocus();

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onclick(ProductModel productModel) {
        showProductViewDialog(productModel);
    }

    @Override
    public void Remove(ProductModel productModel) {
        binding.loading.setVisibility(View.VISIBLE);
        productManager.RemoveCartProductById(userId,productModel.getProductId());
        binding.loading.setVisibility(View.GONE);
    }

    @Override
    public void UpdateQTY(ProductModel productModel) {
        binding.loading.setVisibility(View.VISIBLE);
        productManager.UpdateCartQuantityById(userId,productModel.getProductId(),productModel.getSelectableQuantity());
        binding.loading.setVisibility(View.GONE);

    }

    @Override
    public void AddProduct(ShoppingCartFirebaseModel shoppingCartFirebaseModel) {
        binding.loading.setVisibility(View.VISIBLE);
        productManager.addToBothDatabase(shoppingCartFirebaseModel, new ProductManager.AddListenerForAddToBothInDatabase() {
            @Override
            public void added(ShoppingCartFirebaseModel shoppingCartFirebaseModel) {
                binding.loading.setVisibility(View.GONE);
            }

            @Override
            public void failure(Exception e) {
                Toast.makeText(requireContext(), e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showProductViewDialog(ProductModel productModel) {
        List<String> imageUrls = productModel.getProductImage(); // Add your image URLs here
        Dialog bottomSheetDialog = new Dialog(requireContext());
        ProductViewDialogBinding productViewDialogBinding = ProductViewDialogBinding.inflate(getLayoutInflater());
        ViewPager2 viewPager = productViewDialogBinding.viewPager;
        SliderAdapter sliderAdapter = new SliderAdapter(requireContext(), imageUrls, position -> {
            showImageInDialog(position, imageUrls);
        });
        viewPager.setAdapter(sliderAdapter);

        ArrayList<ProductModel> sameProducts = new ArrayList<>();
        databaseService.getAllProductsByCategoryOnly(productModel.getCategory(), new DatabaseService.GetAllProductsCallback() {
                @Override
                public void onSuccess(ArrayList<ProductModel> products) {
                    sameProducts.addAll(products);
                    sameProducts.remove(productModel);
                    LinearLayoutManager LayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
                    productViewDialogBinding.similarProductsRecyclerView.setLayoutManager(LayoutManager);
                    productViewDialogBinding.similarProductsRecyclerView.setAdapter(new ProductAdapter(sameProducts, new onClickProductAdapter() {
                        @Override
                        public void onClick(ProductModel subProductModel, ArrayList<ProductModel> SubsameProducts) {
                            showProductViewDialog(subProductModel);
                        }
                    }, requireContext(), "Main"));

                }

                @Override
                public void onError(String errorMessage) {

                }
            });

        productViewDialogBinding.closeButton.setOnClickListener(v -> bottomSheetDialog.dismiss());

        productViewDialogBinding.quantity.setText(""+productModel.getSelectableQuantity());

        List<Variations> variationsList = new ArrayList<>();
        ArrayList<ProductModel> productModels = new ArrayList<>();
        VariantsAdapter variantsAdapter = new VariantsAdapter(requireContext(), variationsList, productModel.getProductId(), new VariantsAdapter.VariantsCallback() {
            @Override
            public void Product(String id) {
                for (int i = 0; i < productModels.size(); i++) {
                    if (id.equals(productModels.get(i).getProductId())) {
                        showProductViewDialog(productModels.get(i));

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
        variantsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
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
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
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
        BrandService brandService = new BrandService(requireContext());
        brandService.getAllBrandWithIconsById(productModel.getBrand(), new BrandService.addBrandsByIdListener() {
            @Override
            public void onFailure(Exception error) {

            }

            @Override
            public void onSuccess(BrandModel brandModel) {
                Glide.with(requireContext())
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
////                        productViewDialogBinding.AddTOCart.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white));
////                        productViewDialogBinding.AddTOCart.setTextColor(ContextCompat.getColor(requireContext(), R.color.FixBlack));
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
//            productViewDialogBinding.TextInStock.setTextColor(ContextCompat.getColor(requireContext(), R.color.FixRed));
            productViewDialogBinding.AddTOCart.setVisibility(View.GONE);
        }

        String diet;
        if (productModel.getProductIsFoodItem().equals("FoodVeg")) {
            diet = "Veg";

            Glide.with(requireActivity())
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
            Glide.with(requireActivity())
                    .load(R.drawable.food_brown)
                    .placeholder(R.drawable.product_image_shimmee_effect) // Placeholder image while loading
                    .error(R.drawable.product_image_shimmee_effect) // Error image if loading fails
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) // Resize the image
                    .centerCrop() // Scale type for resizing
                    .into(productViewDialogBinding.FoodTypeIcon);
        }
        else if (productModel.getProductIsFoodItem().equals("VegetableAndFruit")) {
            diet = "VegetableAndFruit";
            Glide.with(requireActivity())
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
                    Toast.makeText(requireContext(), "check your network connection and try again ", Toast.LENGTH_SHORT).show();
                }
            });
            }else {
                requireActivity().startActivity(new Intent(requireContext(), AuthMangerActivity.class));
            }

//            addToCart(productModel,productViewDialogBinding.AddTOCart,bottomSheetDialog,productViewDialogBinding.quantityBox);

        });


//        end set up





        productViewDialogBinding.plusBtn.setOnClickListener(view -> {
            int quantity = productModel.getSelectableQuantity();
            quantity++;
            if (quantity > productModel.getStockCount()) {
                Toast.makeText(requireContext(), "Max stock available: " + productModel.getStockCount(), Toast.LENGTH_SHORT).show();
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
                Dialog removeBottomSheetDialog = new Dialog(requireContext());
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
//                Dialog removeBottomSheetDialog = new Dialog(requireContext());
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
////                Dialog removeBottomSheetDialog = new Dialog(requireContext());
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
////                Glide.with(requireContext()).load(productModel.getProductImage().get(0)).into(removeProductBoxBinding.productImage);
////                removeProductBoxBinding.productQty.setText(productModel.getSelectableQuantity()+"");
////                removeProductBoxBinding.productPrice.setText("₹"+productModel.getPrice());
////                removeProductBoxBinding.productQtyUp.setOnClickListener(up->{
////                    int quantity = productModel.getSelectableQuantity();
////                    quantity++;
////                    if(quantity>model.getStockCount()) {
////                        Toast.makeText(requireContext(), "Max stock available: "+ model.getStockCount(), Toast.LENGTH_SHORT).show();
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
////                if (requireActivity().getSupportFragmentManager().findFragmentByTag("bottom_sheet_fragment") == null) {
////                    RemoveBottomSheetDialogFragment bottomSheet = new RemoveBottomSheetDialogFragment(uid, id, cartsAdapter, cartsProductModel);
////                    bottomSheet.show(requireActivity().getSupportFragmentManager(), "bottom_sheet_fragment");
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
        Dialog dialog = new Dialog(requireContext());
        DialogFullscreenImageBinding dialogBinding = DialogFullscreenImageBinding.inflate(LayoutInflater.from(requireContext()));
        dialog.setContentView(dialogBinding.getRoot());
//        dialogBinding.dialogViewPager
//        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false);
        DialogSliderAdapter dialogAdapter = new DialogSliderAdapter(requireContext(), imageUrls);
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
        Animation animation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_up);
        dialogBinding.getRoot().startAnimation(animation);


        dialog.show();

    }

}
