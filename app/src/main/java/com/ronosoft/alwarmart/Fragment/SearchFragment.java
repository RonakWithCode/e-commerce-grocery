package com.ronosoft.alwarmart.Fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ronosoft.alwarmart.Adapter.SearchAdapter;
import com.ronosoft.alwarmart.Adapter.ViewAdapterSearchViewRecommendation;
import com.ronosoft.alwarmart.Component.ProductViewCard;
import com.ronosoft.alwarmart.Manager.ProductManager;
import com.ronosoft.alwarmart.Model.HomeProductModel;
import com.ronosoft.alwarmart.Model.ProductModel;
import com.ronosoft.alwarmart.Services.AuthService;
import com.ronosoft.alwarmart.Services.DatabaseService;
import com.ronosoft.alwarmart.databinding.FragmentSearchBinding;
import com.ronosoft.alwarmart.interfaceClass.HomeProductInterface;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchFragment extends Fragment {
    private FragmentSearchBinding binding;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference productsRef = db.collection("Product");
    private ActionBar actionBar;
    ViewAdapterSearchViewRecommendation adapterSearchViewRecommendation;
    private SearchAdapter adapter;
    ProductViewCard card;
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
        card = new ProductViewCard(requireActivity());
        productManager = new ProductManager(requireActivity());
        userId = new AuthService().getUserId();
        databaseService = new DatabaseService();
        adapterSearchViewRecommendation = new ViewAdapterSearchViewRecommendation(homeProductModels, new HomeProductInterface() {
            @Override
            public void HomeProductOnclick(ProductModel productModel, ArrayList<ProductModel> sameProducts) {

            }}, requireActivity());

        binding.Recommendation.setLayoutManager(new LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false));
        binding.Recommendation.setAdapter(adapterSearchViewRecommendation);

        adapter = new SearchAdapter(requireContext(), (productModel, sameProducts) -> card.showProductViewDialog(productModel, sameProducts));
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);
// recycler
        binding.recyclerView.setVisibility(View.GONE);

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
        if (keyword.isEmpty()) {
            // When search is empty, show recommendations and hide search results
            binding.recyclerView.setVisibility(View.GONE);
            binding.noResultsView.setVisibility(View.GONE);
            binding.Recommendation.setVisibility(View.VISIBLE);
            adapter.setData(new ArrayList<>());
            return;
        }

        // When searching, hide recommendations
        binding.Recommendation.setVisibility(View.GONE);

        String lowercaseKeyword = keyword.toLowerCase(Locale.getDefault());
        Query query = productsRef.whereArrayContains("keywords", lowercaseKeyword);

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            ArrayList<ProductModel> matchingProducts = new ArrayList<>();
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
                if (productModel != null) {
                    matchingProducts.add(productModel);
                }
            }
            adapter.setData(matchingProducts);
            
            // Update UI based on search results
            if (matchingProducts.isEmpty()) {
                binding.noResultsView.setVisibility(View.VISIBLE);
                binding.recyclerView.setVisibility(View.GONE);
            } else {
                binding.noResultsView.setVisibility(View.GONE);
                binding.recyclerView.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(e -> {
            Log.e("SearchFragment", "Error searching for products: " + e.getMessage());
            Toast.makeText(requireContext(), "Search failed. Please try again.", Toast.LENGTH_SHORT).show();
            // On error, show recommendations
            binding.Recommendation.setVisibility(View.VISIBLE);
            binding.recyclerView.setVisibility(View.GONE);
            binding.noResultsView.setVisibility(View.GONE);
        });
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

    }


