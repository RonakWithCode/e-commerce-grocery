package com.ronosoft.alwarmart.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ronosoft.alwarmart.Activity.FragmentLoader;
import com.ronosoft.alwarmart.Adapter.ProductAdapter;
import com.ronosoft.alwarmart.Adapter.SlideCategoryAdapter;
import com.ronosoft.alwarmart.Component.ProductViewCard;
import com.ronosoft.alwarmart.Model.ProductCategoryModel;
import com.ronosoft.alwarmart.Model.ProductModel;
import com.ronosoft.alwarmart.R;
import com.ronosoft.alwarmart.Services.DatabaseService;
import com.ronosoft.alwarmart.databinding.FragmentProductWithSlideCategoryBinding;

import java.util.ArrayList;

public class ProductWithSlideCategoryFragment extends Fragment {
    private FragmentProductWithSlideCategoryBinding binding;
    private ProductAdapter productAdapter;
    private SlideCategoryAdapter categoryAdapter;
    private DatabaseService databaseService;
    private ArrayList<ProductModel> productModels;
    private ArrayList<ProductCategoryModel> categoryModels;
    private String currentFilter;
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        binding = FragmentProductWithSlideCategoryBinding.inflate(inflater, container, false);
        initializeViews();
        setupAdapters();
        setupListeners();
        loadInitialData();
        return binding.getRoot();
    }

    private void initializeViews() {
        hideActionBar();
        productModels = new ArrayList<>();
        categoryModels = new ArrayList<>();
        databaseService = new DatabaseService();
        currentFilter = getArguments() != null ? getArguments().getString("filter", "no") : "no";
    }

    private void setupAdapters() {
        // Setup category adapter
        categoryAdapter = new SlideCategoryAdapter(categoryModels, requireContext(), 
            productModel -> loadProductByCategory(productModel.getTag()));
        binding.slideView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.slideView.setAdapter(categoryAdapter);

        // Setup product adapter
        productAdapter = new ProductAdapter(productModels, 
            (productModel, sameProducts) -> new ProductViewCard(getActivity())
                .showProductViewDialog(productModel, sameProducts), 
            requireContext(), "main");
        binding.Products.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.Products.setAdapter(productAdapter);
    }

    private void setupListeners() {
        binding.backBtn.setOnClickListener(v -> handleBackPress());
        binding.searchBta.setOnClickListener(v -> openSearchFragment());
        binding.swipeRefresh.setOnRefreshListener(this::refreshData);
        binding.retryButton.setOnClickListener(v -> loadInitialData());
    }

    private void loadInitialData() {
        showLoading();
        databaseService.getAllCategory(new DatabaseService.GetAllCategoryCallback() {
            @Override
            public void onSuccess(ArrayList<ProductCategoryModel> categories) {
                if (!isAdded()) return;
                
                categoryModels.clear();
                categoryModels.addAll(categories);
                categoryAdapter.notifyDataSetChanged();
                
                if (categories.isEmpty()) {
                    showError("No categories found");
                    return;
                }

                String categoryToLoad = currentFilter.equals("no") ? 
                    categories.get(0).getTag() : currentFilter;
                loadProductByCategory(categoryToLoad);
            }

            @Override
            public void onError(String errorMessage) {
                if (!isAdded()) return;
                showError(errorMessage);
            }
        });
    }

    private void loadProductByCategory(String category) {
        showLoading();
        databaseService.getAllProductsByCategoryOnly(category, new DatabaseService.GetAllProductsCallback() {
            @Override
            public void onSuccess(ArrayList<ProductModel> products) {
                if (!isAdded()) return;
                
                productModels.clear();
                productModels.addAll(products);
                productAdapter.notifyDataSetChanged();
                binding.title.setText(category);
                
                hideLoading();
                if (products.isEmpty()) {
                    showError("No products found in this category");
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (!isAdded()) return;
                showError(errorMessage);
            }
        });
    }

    private void refreshData() {
        loadInitialData();
    }

    private void showLoading() {
        binding.loadingProgress.setVisibility(View.VISIBLE);
        binding.errorState.setVisibility(View.GONE);
    }

    private void hideLoading() {
        binding.loadingProgress.setVisibility(View.GONE);
        binding.swipeRefresh.setRefreshing(false);
    }

    private void showError(String message) {
        hideLoading();
        binding.errorState.setVisibility(View.VISIBLE);
        binding.errorMessage.setText(message);
    }

    private void hideActionBar() {
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    private void handleBackPress() {
        if (getActivity() != null) {
            getActivity().onBackPressed();
        }
    }

    private void openSearchFragment() {
        // Create bundle to pass current products if needed
//        Bundle bundle = new Bundle();
//        bundle.putParcelableArrayList("model", productModels);
//
//        // Create and navigate to search fragment
//        SearchFragment searchFragment = new SearchFragment();
//        searchFragment.setArguments(bundle);
//
//        // Replace current fragment with search fragment
//        if (getActivity() != null) {
//            getActivity().getSupportFragmentManager()
//                .beginTransaction()
////                .setCustomAnimations(
////                    R.anim.slide_in_right,  // enter
////                    R.anim.slide_out_left,   // exit
////                    R.anim.slide_in_left,    // popEnter
////                    R.anim.slide_out_right   // popExit
////                )
//                .replace(R.id.fragment_container, searchFragment)
//                .addToBackStack(null)
//                .commit();
//        }



        Intent intent = new Intent(requireContext(), FragmentLoader.class);
        intent.putExtra("LoadID","search");
//        intent.putParcelableArrayListExtra("model",productModel);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}