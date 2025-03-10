package com.ronosoft.alwarmart.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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
    private String currentFilter = "no";

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
        if (getArguments() != null) {
            currentFilter = getArguments().getString("filter", "no");
        }
    }

    private void setupAdapters() {
        // Initialize and set category adapter
        categoryAdapter = new SlideCategoryAdapter(categoryModels, requireContext(),
                productCategory -> loadProductByCategory(productCategory.getTag()));
        binding.slideView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        binding.slideView.setAdapter(categoryAdapter);

        // Initialize and set product adapter; using getViewLifecycleOwner() for LiveData observation
        productAdapter = new ProductAdapter(
                getViewLifecycleOwner(),
                productModels,
                (ProductModel productModel, ArrayList<ProductModel> sameProducts) ->
                        new ProductViewCard(getActivity()).showProductViewDialog(productModel, sameProducts),
                requireContext()
        );
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

    // Helper methods for UI feedback
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
        if (getActivity() instanceof AppCompatActivity) {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.hide();
            }
        }
    }

    private void handleBackPress() {
        if (getActivity() != null) {
            getActivity().onBackPressed();
        }
    }

    private void openSearchFragment() {
        Intent intent = new Intent(requireContext(), FragmentLoader.class);
        intent.putExtra("LoadID", "search");
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
