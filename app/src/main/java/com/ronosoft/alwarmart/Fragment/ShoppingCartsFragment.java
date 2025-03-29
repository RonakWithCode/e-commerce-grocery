package com.ronosoft.alwarmart.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private FragmentShoppingCartsBinding binding;
    private DatabaseService service;
    private ShoppingCartsAdapter cartsAdapter;
    private ArrayList<ShoppingCartsProductModel> models;
    private AuthService authService;
    private String uid;

    public ShoppingCartsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentShoppingCartsBinding.inflate(inflater, container, false);
        authService = new AuthService();
        service = new DatabaseService();

        // Check user authentication
        if (authService.IsLogin()) {
            uid = authService.getUserId();
            models = new ArrayList<>();
            init();
            binding.relativeNotAuth.setVisibility(View.GONE);
            binding.main.setVisibility(View.VISIBLE);
            binding.Buy.setVisibility(View.VISIBLE);

        } else {
            binding.relativeNotAuth.setVisibility(View.VISIBLE);
            binding.main.setVisibility(View.GONE);
            binding.progressCircular.setVisibility(View.GONE);
            binding.linearLayoutPlaceHolder.stopShimmer();
            binding.linearLayoutPlaceHolder.setVisibility(View.GONE);
            binding.Buy.setVisibility(View.GONE);

        }

        // Buy button: check if cart is empty before proceeding
        binding.Buy.setOnClickListener(view -> {
            if (models == null || models.isEmpty() || cartsAdapter.getItemCount() == 0) {
                Toast.makeText(requireContext(), "Select Product", Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(requireContext(), OderActivity.class));
            }
        });

        binding.siginUp.setOnClickListener(view ->
                startActivity(new Intent(requireContext(), AuthMangerActivity.class))
        );

        // Start shimmer animation for loading placeholder
        binding.linearLayoutPlaceHolder.startShimmer();

        return binding.getRoot();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void init() {
        cartsAdapter = new ShoppingCartsAdapter(models, this, requireContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        binding.ProductCart.setLayoutManager(layoutManager);
        binding.ProductCart.setAdapter(cartsAdapter);

        service.getUserCartById(uid, new DatabaseService.GetUserCartByIdCallback() {
            @Override
            public void onSuccess(ArrayList<ShoppingCartsProductModel> cartsProductModels) {
                if (!isAdded() || binding == null) return;
                updateCartUI(cartsProductModels);
            }

            @Override
            public void onError(String errorMessage) {
                if (!isAdded() || binding == null) return;
                if ("Cart is empty".equals(errorMessage)) {
                    binding.main.setVisibility(View.GONE);
                    binding.Buy.setVisibility(View.GONE);
                    binding.IsEnity.setVisibility(View.VISIBLE);
                    binding.linearLayoutPlaceHolder.stopShimmer();
                    binding.linearLayoutPlaceHolder.setVisibility(View.GONE);
                }
                Log.i(TAG, "onError: " + errorMessage);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void updateCartUI(ArrayList<ShoppingCartsProductModel> cartItems) {
        if (!isAdded() || binding == null) return;

        binding.progressCircular.setVisibility(View.GONE);
        binding.linearLayoutPlaceHolder.stopShimmer();
        binding.linearLayoutPlaceHolder.setVisibility(View.GONE);

        // If the cart is empty, update the UI and finish the activity
        if (cartItems.isEmpty()) {
            binding.main.setVisibility(View.GONE);
            binding.IsEnity.setVisibility(View.VISIBLE);
            binding.Buy.setVisibility(View.GONE);
            Toast.makeText(requireContext(), "Your cart is empty", Toast.LENGTH_SHORT).show();
            if (getActivity() != null) {
                getActivity().finish();
            }
            return;
        }

        binding.main.setVisibility(View.VISIBLE);
        binding.IsEnity.setVisibility(View.GONE);
        binding.Buy.setVisibility(View.VISIBLE);

        models.clear();
        models.addAll(cartItems);
        cartsAdapter.notifyDataSetChanged();

        // Update subtotal using helper calculation
        double totalPrice = ShoppingCartHelper.calculateTotalPrices(models);
        binding.SubTotal.setText(String.format("₹%.2f", totalPrice));

        // Load recommendations based on cart items
        loadRecommendations(cartItems);
    }

    private void loadRecommendations(ArrayList<ShoppingCartsProductModel> cartItems) {
        if (!isAdded() || binding == null) return;

        ArrayList<String> categories = new ArrayList<>();
        for (ShoppingCartsProductModel item : cartItems) {
            if (item.getCategory() != null) {
                categories.add(item.getCategory());
            }
        }

        new RecommendationSystemService(requireContext())
                .getByCategoryMatching(categories, new RecommendationSystemService.addCategoryListener() {
                    @Override
                    public void onSuccess(ArrayList<ProductModel> recommendations) {
                        if (!isAdded() || binding == null) return;
                        if (!recommendations.isEmpty()) {
                            binding.Recommendations.setVisibility(View.VISIBLE);
                            setupRecommendationsRecyclerView(recommendations);
                        }
                    }

                    @Override
                    public void onError(Exception error) {
                        Log.e(TAG, "Error loading recommendations", error);
                    }
                });
    }

    private void setupRecommendationsRecyclerView(ArrayList<ProductModel> recommendations) {
        if (!isAdded() || binding == null) return;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.Recommendations.setLayoutManager(layoutManager);

        ProductAdapter adapter = new ProductAdapter(
                this,
                recommendations,
                (ProductModel product, ArrayList<ProductModel> similarProducts) ->
                        new ProductViewCard(requireActivity()).showProductViewDialog(product, similarProducts),
                requireContext()
        );
        binding.Recommendations.setAdapter(adapter);
    }

    @Override
    public void remove(int pos, String id, ShoppingCartsProductModel cartsProductModel) {
        // Show bottom sheet dialog to remove item if not already showing
        if (requireActivity().getSupportFragmentManager().findFragmentByTag("bottom_sheet_fragment") == null) {
            RemoveBottomSheetDialogFragment bottomSheet =
                    new RemoveBottomSheetDialogFragment(uid, id, cartsAdapter, cartsProductModel, () -> {
                        // Callback: reload the cart data after removal.
                        service.getUserCartById(uid, new DatabaseService.GetUserCartByIdCallback() {
                            @Override
                            public void onSuccess(ArrayList<ShoppingCartsProductModel> cartsProductModels) {
                                if (!isAdded() || binding == null) return;
                                updateCartUI(cartsProductModels);
                            }

                            @Override
                            public void onError(String errorMessage) {
                                Log.i(TAG, "onError after removal: " + errorMessage);
                            }
                        });
                    });
            bottomSheet.show(requireActivity().getSupportFragmentManager(), "bottom_sheet_fragment");
        }
    }

    @Override
    public void UpdateQuantity(ShoppingCartsProductModel updateModel, String id) {
        if (binding != null) {
            binding.progressCircular.setVisibility(View.VISIBLE);
        }
        new ProductManager(requireActivity()).UpdateCartQuantityById(uid, id, updateModel.getSelectableQuantity());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
