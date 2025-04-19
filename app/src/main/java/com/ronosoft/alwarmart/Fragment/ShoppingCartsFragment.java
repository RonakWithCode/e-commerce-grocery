package com.ronosoft.alwarmart.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.ronosoft.alwarmart.Activity.AuthMangerActivity;
import com.ronosoft.alwarmart.Activity.OderActivity;
import com.ronosoft.alwarmart.Adapter.ProductAdapter;
import com.ronosoft.alwarmart.Adapter.ShoppingCartsAdapter;
import com.ronosoft.alwarmart.Component.ProductViewCard;
import com.ronosoft.alwarmart.HelperClass.ShoppingCartHelper;
import com.ronosoft.alwarmart.Manager.ProductManager;
import com.ronosoft.alwarmart.Model.ProductModel;
import com.ronosoft.alwarmart.Model.ShoppingCartsProductModel;
import com.ronosoft.alwarmart.Services.AuthService;
import com.ronosoft.alwarmart.Services.DatabaseService;
import com.ronosoft.alwarmart.Services.RecommendationSystemService;
import com.ronosoft.alwarmart.databinding.FragmentShoppingCartsBinding;
import com.ronosoft.alwarmart.interfaceClass.ShoppingCartsInterface;
import java.util.ArrayList;

public class ShoppingCartsFragment extends Fragment implements ShoppingCartsInterface {

    private static final String TAG = "ShoppingCartsFragment";
    private static final String BOTTOM_SHEET_TAG = "bottom_sheet_fragment";
    private FragmentShoppingCartsBinding binding;
    private DatabaseService databaseService;
    private AuthService authService;
    private ShoppingCartsAdapter cartsAdapter;
    private ArrayList<ShoppingCartsProductModel> cartItems;
    private String userId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentShoppingCartsBinding.inflate(inflater, container, false);
        initializeServices();
        setupInitialState();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (authService.IsLogin()) {
            initializeCart();
        }
    }

    private void initializeServices() {
        authService = new AuthService();
        databaseService = new DatabaseService();
        cartItems = new ArrayList<>();
    }

    private void setupInitialState() {
        if (authService.IsLogin()) {
            userId = authService.getUserId();
            setViewVisibility(binding.relativeNotAuth, View.GONE);
            setViewVisibility(binding.main, View.VISIBLE);
            setViewVisibility(binding.Buy, View.VISIBLE);
            setViewVisibility(binding.errorState, View.GONE);
        } else {
            setViewVisibility(binding.relativeNotAuth, View.VISIBLE);
            setViewVisibility(binding.main, View.GONE);
            setViewVisibility(binding.loadingAnimation, View.GONE);
            setViewVisibility(binding.IsEnity, View.GONE);
            setViewVisibility(binding.Buy, View.GONE);
            setViewVisibility(binding.errorState, View.GONE);
            binding.siginUp.setOnClickListener(v -> navigateToAuth());
        }

        binding.Buy.setOnClickListener(v -> proceedToCheckout());
        binding.retryButton.setOnClickListener(v -> {
            setViewVisibility(binding.errorState, View.GONE);
            setViewVisibility(binding.loadingAnimation, View.VISIBLE);
            fetchCartItems();
        });
        setViewVisibility(binding.loadingAnimation, View.VISIBLE);
    }

    private void initializeCart() {
        if (!isAdded()) return;

        cartsAdapter = new ShoppingCartsAdapter(cartItems, this, requireContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        binding.ProductCart.setLayoutManager(layoutManager);
        binding.ProductCart.setAdapter(cartsAdapter);
        binding.ProductCart.setHasFixedSize(true);

        fetchCartItems();
    }

    private void fetchCartItems() {
        if (!isAdded() || userId == null) return;

        databaseService.getUserCartById(userId, new DatabaseService.GetUserCartByIdCallback() {
            @Override
            public void onSuccess(ArrayList<ShoppingCartsProductModel> cartsProductModels) {
                if (!isAdded()) return;
                updateCartUI(cartsProductModels);
            }

            @Override
            public void onError(String errorMessage) {
                if (!isAdded()) return;
                handleCartError(errorMessage);
            }
        });
    }

    private void updateCartUI(ArrayList<ShoppingCartsProductModel> newCartItems) {
        if (!isAdded()) return;

        setViewVisibility(binding.loadingAnimation, View.GONE);
        setViewVisibility(binding.errorState, View.GONE);

        if (newCartItems == null || newCartItems.isEmpty()) {
            showEmptyCartState();
            return;
        }

        setViewVisibility(binding.main, View.VISIBLE);
        setViewVisibility(binding.IsEnity, View.GONE);
        setViewVisibility(binding.Buy, View.VISIBLE);

        cartItems.clear();
        cartItems.addAll(newCartItems);
        cartsAdapter.notifyDataSetChanged();

        updateSubtotal();
        loadRecommendations(newCartItems);
    }

    private void showEmptyCartState() {
        setViewVisibility(binding.main, View.GONE);
        setViewVisibility(binding.IsEnity, View.VISIBLE);
        setViewVisibility(binding.Buy, View.GONE);
        setViewVisibility(binding.errorState, View.GONE);
        showToast("Your cart is empty");
    }

    private void updateSubtotal() {
        double totalPrice = ShoppingCartHelper.calculateTotalPrices(cartItems);
        binding.SubTotal.setText(String.format("₹%.2f", totalPrice));
    }

    private void handleCartError(String errorMessage) {
        setViewVisibility(binding.loadingAnimation, View.GONE);
        if ("Cart is empty".equals(errorMessage) || "No available products found in cart".equals(errorMessage)) {
            showEmptyCartState();
        } else {
            setViewVisibility(binding.errorState, View.VISIBLE);
            binding.errorMessage.setText("Error loading cart: " + errorMessage);
            setViewVisibility(binding.main, View.GONE);
            setViewVisibility(binding.IsEnity, View.GONE);
            setViewVisibility(binding.Buy, View.GONE);
            showToast("Error loading cart: " + errorMessage);
        }
        Log.e(TAG, "Cart error: " + errorMessage);
    }

    private void loadRecommendations(ArrayList<ShoppingCartsProductModel> cartItems) {
        if (!isAdded()) return;

        ArrayList<String> categories = new ArrayList<>();
        for (ShoppingCartsProductModel item : cartItems) {
            if (item.getCategory() != null) {
                categories.add(item.getCategory());
            }
        }

        if (categories.isEmpty()) {
            setViewVisibility(binding.Recommendations, View.GONE);
            return;
        }

        new RecommendationSystemService(requireContext())
                .getByCategoryMatching(categories, new RecommendationSystemService.addCategoryListener() {
                    @Override
                    public void onSuccess(ArrayList<ProductModel> recommendations) {
                        if (!isAdded()) return;
                        handleRecommendationsSuccess(recommendations);
                    }

                    @Override
                    public void onError(Exception error) {
                        if (!isAdded()) return;
                        handleRecommendationsError(error);
                    }
                });
    }

    private void handleRecommendationsSuccess(ArrayList<ProductModel> recommendations) {
        if (recommendations == null || recommendations.isEmpty()) {
            setViewVisibility(binding.Recommendations, View.GONE);
            return;
        }

        setViewVisibility(binding.Recommendations, View.VISIBLE);
        setupRecommendationsRecyclerView(recommendations);
    }

    private void handleRecommendationsError(Exception error) {
        setViewVisibility(binding.Recommendations, View.GONE);
        Log.e(TAG, "Error loading recommendations", error);
        showToast("Failed to load recommendations");
    }

    private void setupRecommendationsRecyclerView(ArrayList<ProductModel> recommendations) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.Recommendations.setLayoutManager(layoutManager);
        binding.Recommendations.setHasFixedSize(true);

        ProductAdapter adapter = new ProductAdapter(
                this,
                recommendations,
                (product, similarProducts) -> new ProductViewCard(requireActivity())
                        .showProductViewDialog(product, similarProducts),
                requireContext()
        );
        binding.Recommendations.setAdapter(adapter);
    }

    @Override
    public void remove(int pos, String id, ShoppingCartsProductModel cartsProductModel) {
        if (!isAdded() || getParentFragmentManager().findFragmentByTag(BOTTOM_SHEET_TAG) != null) {
            return;
        }

        RemoveBottomSheetDialogFragment bottomSheet = new RemoveBottomSheetDialogFragment(
                userId, id, cartsAdapter, cartsProductModel, this::fetchCartItems
        );
        bottomSheet.show(getParentFragmentManager(), BOTTOM_SHEET_TAG);
    }

    @Override
    public void UpdateQuantity(ShoppingCartsProductModel updateModel, String id) {
        if (!isAdded()) return;

        setViewVisibility(binding.loadingAnimation, View.VISIBLE);
        new ProductManager(requireActivity()).UpdateCartQuantityById(userId, id, updateModel.getSelectableQuantity());
    }

    private void proceedToCheckout() {
        if (cartItems.isEmpty()) {
            showToast("Please add products to your cart");
        } else {
            startActivity(new Intent(requireContext(), OderActivity.class));
        }
    }

    private void navigateToAuth() {
        startActivity(new Intent(requireContext(), AuthMangerActivity.class));
    }

    private void setViewVisibility(View view, int visibility) {
        if (isAdded() && view != null) {
            view.setVisibility(visibility);
        }
    }

    private void showToast(String message) {
        if (isAdded() && getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        if (cartsAdapter != null) {
            cartsAdapter = null;
        }
        binding = null;
        super.onDestroyView();
    }
}