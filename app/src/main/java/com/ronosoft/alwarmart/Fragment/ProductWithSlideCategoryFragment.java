package com.ronosoft.alwarmart.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import java.util.Objects;

public class ProductWithSlideCategoryFragment extends Fragment {

    private static final String TAG = "ProductSlideCategory";
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

    /**
     * Initialize required variables and check incoming filter arguments.
     */
    private void initializeViews() {
        // Hide ActionBar only if activity is AppCompatActivity
        if (getActivity() instanceof AppCompatActivity) {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.hide();
            }
        }

        productModels = new ArrayList<>();
        categoryModels = new ArrayList<>();
        databaseService = new DatabaseService();

        // Safely retrieve arguments
        Bundle arguments = getArguments();
        if (arguments != null) {
            currentFilter = arguments.getString("filter", "no");
        }

        // Start shimmer effect for skeleton screen
        if (binding != null) {
            binding.skeletonLayout.startShimmer();
        }
    }

    /**
     * Sets up adapters for category list and products grid.
     */
    private void setupAdapters() {
        // Initialize SlideCategoryAdapter
        categoryAdapter = new SlideCategoryAdapter(categoryModels, requireContext(), productCategory -> {
            if (productCategory != null && productCategory.getTag() != null && isAdded()) {
                loadProductByCategory(productCategory.getTag());
            }
        });
        binding.slideView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        binding.slideView.setAdapter(categoryAdapter);

        // Initialize ProductAdapter
        productAdapter = new ProductAdapter(
                getViewLifecycleOwner(),
                productModels,
                (productModel, sameProducts) -> {
                    // Ensure fragment is attached before showing dialog
                    if (isAdded() && getActivity() != null) {
                        new ProductViewCard(getActivity()).showProductViewDialog(productModel, sameProducts);
                    }
                },
                requireContext()
        );
        binding.Products.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.Products.setAdapter(productAdapter);
    }

    /**
     * Sets up click and refresh listeners.
     */
    private void setupListeners() {
        binding.searchBta.setOnClickListener(v -> {
            if (isAdded()) {
                openSearchFragment();
            }
        });
        binding.swipeRefresh.setOnRefreshListener(() -> {
            if (isAdded()) {
                refreshData();
            }
        });
        binding.retryButton.setOnClickListener(v -> {
            if (isAdded()) {
                loadInitialData();
            }
        });
    }

    /**
     * Loads initial data: categories and products.
     */
    private void loadInitialData() {
        if (!isAdded() || binding == null) return;
        showLoading();

        // Load categories
        databaseService.getAllCategory(new DatabaseService.GetAllCategoryCallback() {
            @Override
            public void onSuccess(ArrayList<ProductCategoryModel> categories) {
                if (!isAdded() || binding == null) return;

                categoryModels.clear();
                categoryModels.addAll(categories);
                categoryAdapter.notifyDataSetChanged();

                if (categories.isEmpty()) {
                    showError("No categories found");
                    return;
                }

                // Load products for the first category or filtered category
                String categoryToLoad = currentFilter.equals("no") && !categories.isEmpty()
                        ? categories.get(0).getTag()
                        : currentFilter;
                loadProductByCategory(categoryToLoad);
            }

            @Override
            public void onError(String errorMessage) {
                if (!isAdded() || binding == null) return;
                showError(errorMessage);
            }
        });
    }

    /**
     * Loads products for the given category.
     */
    private void loadProductByCategory(String category) {
        if (!isAdded() || binding == null) return;
        showLoading();

        databaseService.getAllProductsByCategoryOnly(category, new DatabaseService.GetAllProductsCallback() {
            @Override
            public void onSuccess(ArrayList<ProductModel> products) {
                if (!isAdded() || binding == null) return;

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
                if (!isAdded() || binding == null) return;
                showError(errorMessage);
            }
        });
    }

    /**
     * Refresh data on swipe.
     */
    private void refreshData() {
        loadInitialData();
    }

    /**
     * Shows the skeleton screen and hides error state.
     */
    private void showLoading() {
        if (binding != null) {
            binding.skeletonLayout.setVisibility(View.VISIBLE);
            binding.swipeRefresh.setVisibility(View.GONE);
            binding.errorState.setVisibility(View.GONE);
        }
    }

    /**
     * Hides the skeleton screen and shows the main content.
     */
    private void hideLoading() {
        if (binding != null) {
            binding.skeletonLayout.stopShimmer();
            binding.skeletonLayout.setVisibility(View.GONE);
            binding.swipeRefresh.setVisibility(View.VISIBLE);
            binding.swipeRefresh.setRefreshing(false);
        }
    }

    /**
     * Displays an error message.
     */
    private void showError(String message) {
        if (binding != null) {
            hideLoading();
            binding.errorState.setVisibility(View.VISIBLE);
            binding.errorMessage.setText(message);
            // Ensure context is available before showing Toast
            if (isAdded() && getContext() != null) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Opens the search fragment by starting the FragmentLoader activity.
     */
    private void openSearchFragment() {
        Intent intent = new Intent(requireContext(), FragmentLoader.class);
        intent.putExtra("LoadID", "search");
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clean up binding and stop shimmer to prevent memory leaks
        if (binding != null) {
            binding.skeletonLayout.stopShimmer();
            binding = null;
        }
        // Clear adapters to prevent memory leaks
        if (binding != null) {
            binding.slideView.setAdapter(null);
            binding.Products.setAdapter(null);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Stop shimmer when fragment is paused to save resources
        if (binding != null) {
            binding.skeletonLayout.stopShimmer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Restart shimmer if loading is visible
        if (binding != null && binding.skeletonLayout.getVisibility() == View.VISIBLE) {
            binding.skeletonLayout.startShimmer();
        }
    }
}