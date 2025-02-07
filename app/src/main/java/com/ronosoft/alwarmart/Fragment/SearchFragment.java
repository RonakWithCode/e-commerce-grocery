package com.ronosoft.alwarmart.Fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ronosoft.alwarmart.Adapter.RecommendationsAdapter;
import com.ronosoft.alwarmart.Adapter.SearchAdapter;
import com.ronosoft.alwarmart.Adapter.SearchViewRecommendationAdapter;
import com.ronosoft.alwarmart.Adapter.ViewAdapterSearchViewRecommendation;
import com.ronosoft.alwarmart.Component.ProductViewCard;
import com.ronosoft.alwarmart.Manager.ProductManager;
import com.ronosoft.alwarmart.Model.HomeProductModel;
import com.ronosoft.alwarmart.Model.ProductModel;
import com.ronosoft.alwarmart.R;
import com.ronosoft.alwarmart.Services.AuthService;
import com.ronosoft.alwarmart.Services.DatabaseService;
import com.ronosoft.alwarmart.Services.RecommendationSystemService;
import com.ronosoft.alwarmart.databinding.FragmentSearchBinding;
import com.ronosoft.alwarmart.databinding.LayoutNoResultsBinding;
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
    private View noResultsView;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference productsRef = db.collection("Product");
    private ActionBar actionBar;
    private SearchAdapter adapter;
    ProductViewCard card;
    private static final int SPEECH_REQUEST_CODE = 0;
    DatabaseService databaseService;
    String userId;
    ProductManager productManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        
        // Initialize no results view
        noResultsView = binding.getRoot().findViewById(R.id.noResultsView);
        
        // Hide ActionBar
        actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        if (getArguments() != null) {
            // Check if the arguments contain the "model" key
             if (getArguments().containsKey("model")) {
//                homeProductModels = getArguments().getParcelableArrayList("model");
            }
        } else {
            // Handle the case where arguments are null
//            Log.e("FragmentName", "Arguments are null");
//            homeProductModels = new ArrayList<>(); // Initialize with an empty list
        }

        card = new ProductViewCard(requireActivity());
        productManager = new ProductManager(requireActivity());
        userId = new AuthService().getUserId();
        databaseService = new DatabaseService();

        setupViews();
        setupSearchListener();
        loadRecommendations();

        return binding.getRoot();
    }

    private void setupViews() {
        // Initialize RecyclerView
        adapter = new SearchAdapter(requireContext(), (productModel, sameProducts) -> card.showProductViewDialog(productModel, sameProducts));
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Setup click listeners
        binding.backIcon.setOnClickListener(v -> requireActivity().onBackPressed());
        binding.micIcon.setOnClickListener(v -> openVoiceRecognizer());
    }

    private void searchProductByKeyword(String keyword) {
        if (keyword.isEmpty()) {
            binding.recyclerView.setVisibility(View.GONE);
            noResultsView.setVisibility(View.GONE);
            binding.Recommendation.setVisibility(View.VISIBLE);
            adapter.setData(new ArrayList<>());
            return;
        }

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
            
            if (matchingProducts.isEmpty()) {
                noResultsView.setVisibility(View.VISIBLE);
                binding.recyclerView.setVisibility(View.GONE);
            } else {
                noResultsView.setVisibility(View.GONE);
                binding.recyclerView.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(e -> {
            Log.e("SearchFragment", "Error searching for products: " + e.getMessage());
            Toast.makeText(requireContext(), "Search failed. Please try again.", Toast.LENGTH_SHORT).show();
            binding.Recommendation.setVisibility(View.VISIBLE);
            binding.recyclerView.setVisibility(View.GONE);
            noResultsView.setVisibility(View.GONE);
        });
    }

    private void openVoiceRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);

            binding.searchEditText.setText(spokenText);

            binding.searchEditText.setSelection(spokenText.length());
            binding.searchEditText.requestFocus();

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void loadRecommendations() {
        new RecommendationSystemService(requireContext()).getByAdsProductsForSearch(new RecommendationSystemService.addCategoryListener() {
            @Override
            public void onSuccess(ArrayList<ProductModel> productModels) {
                binding.Recommendation.setAdapter(new SearchViewRecommendationAdapter(productModels,requireActivity()));
                binding.Recommendation.setLayoutManager(new LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false));
                binding.Recommendation.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(Exception errorMessage) {

            }
        });
    }

    private void setupSearchListener() {
        binding.searchEditText.addTextChangedListener(new TextWatcher() {
            private Runnable searchRunnable;
            private final Handler handler = new Handler(Looper.getMainLooper());

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Cancel any pending searches
                if (searchRunnable != null) {
                    handler.removeCallbacks(searchRunnable);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Create a new search with delay
                searchRunnable = () -> {
                    String query = s.toString().trim();
                    searchProductByKeyword(query);
                };
                
                // Delay search by 300ms to avoid too many requests while typing
                handler.postDelayed(searchRunnable, 300);
            }
        });

        // Handle search action from keyboard
        binding.searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = binding.searchEditText.getText().toString().trim();
                searchProductByKeyword(query);
                
                // Hide keyboard
                InputMethodManager imm = (InputMethodManager) requireContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return true;
            }
            return false;
        });

        // Clear focus when touching outside
        binding.getRoot().setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                View currentFocus = requireActivity().getCurrentFocus();
                if (currentFocus != null) {
                    InputMethodManager imm = (InputMethodManager) requireContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
                    currentFocus.clearFocus();
                }
            }
            return false;
        });
    }
}


