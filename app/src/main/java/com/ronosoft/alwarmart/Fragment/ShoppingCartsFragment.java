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
import com.ronosoft.alwarmart.Adapter.RecommendationsAdapter;
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
import com.ronosoft.alwarmart.interfaceClass.RecommendationsInterface;
import com.ronosoft.alwarmart.interfaceClass.ShoppingCartsInterface;
import com.ronosoft.alwarmart.interfaceClass.onClickProductAdapter;

import java.util.ArrayList;

public class ShoppingCartsFragment extends Fragment implements ShoppingCartsInterface {
    FragmentShoppingCartsBinding binding;
    DatabaseService service;
    ShoppingCartsAdapter cartsAdapter;
    ArrayList<ShoppingCartsProductModel> models;
    AuthService authService;
    String uid;

    public ShoppingCartsFragment() {
        // Required empty public constructor
    }
    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentShoppingCartsBinding.inflate(inflater,container,false);
        authService = new AuthService();
        service = new DatabaseService();
        if (authService.IsLogin()) {
            uid  = authService.getUserId();
            models = new ArrayList<>();
            init();
            binding.relativeNotAuth.setVisibility(View.GONE);
            binding.main.setVisibility(View.VISIBLE);
        } else {
            binding.relativeNotAuth.setVisibility(View.VISIBLE);
            binding.main.setVisibility(View.GONE);
            binding.progressCircular.setVisibility(View.GONE);
            binding.linearLayoutPlaceHolder.stopShimmer();
            binding.linearLayoutPlaceHolder.setVisibility(View.GONE);
        }

        binding.siginUp.setOnClickListener(view -> startActivity(new Intent(requireContext(), AuthMangerActivity.class)));
        binding.Buy.setOnClickListener(Buy->{
            if (models.isEmpty()){
                Toast.makeText(requireContext(), "Select Product", Toast.LENGTH_SHORT).show();
            }else {
                Intent intent = new Intent(requireContext(), OderActivity.class);
                startActivity(intent);
            }
        });
        binding.linearLayoutPlaceHolder.startShimmer();







        return binding.getRoot();
    }



    @SuppressLint("SetTextI18n")
    private void init() {
        cartsAdapter = new ShoppingCartsAdapter(models,this,requireContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false);
        binding.ProductCart.setLayoutManager(layoutManager);
        binding.ProductCart.setAdapter(cartsAdapter);
        service.getUserCartById(uid, new DatabaseService.GetUserCartByIdCallback() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(ArrayList<ShoppingCartsProductModel> cartsProductModels) {
                updateCartUI(cartsProductModels);
            }

            @Override
            public void onError(String errorMessage) {
                if (errorMessage.equals("Cart is empty")) {
                    binding.main.setVisibility(View.GONE);
                    binding.IsEnity.setVisibility(View.VISIBLE);
                    binding.linearLayoutPlaceHolder.stopShimmer();
                    binding.linearLayoutPlaceHolder.setVisibility(View.GONE);
                }
                Log.i("ERRORDATABASE", "onError: "+errorMessage);
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void updateCartUI(ArrayList<ShoppingCartsProductModel> cartItems) {
        binding.progressCircular.setVisibility(View.GONE);
        binding.linearLayoutPlaceHolder.stopShimmer();
        binding.linearLayoutPlaceHolder.setVisibility(View.GONE);

        if (cartItems.isEmpty()) {
            binding.main.setVisibility(View.GONE);
            binding.IsEnity.setVisibility(View.VISIBLE);
            return;
        }

        binding.main.setVisibility(View.VISIBLE);
        binding.IsEnity.setVisibility(View.GONE);

        // Update cart items
        models.clear();
        models.addAll(cartItems);
        cartsAdapter.notifyDataSetChanged();

        // Update total
        double totalPrice = ShoppingCartHelper.calculateTotalPrices(models);
        binding.SubTotal.setText(String.format("₹%.2f", totalPrice));

        // Load recommendations
        loadRecommendations(cartItems);
    }

    private void loadRecommendations(ArrayList<ShoppingCartsProductModel> cartItems) {
        ArrayList<String> categories = new ArrayList<>();
        for (ShoppingCartsProductModel item : cartItems) {
            categories.add(item.getCategory());
        }

        new RecommendationSystemService(requireContext())
            .getByCategoryMatching(categories, new RecommendationSystemService.addCategoryListener() {
                @Override
                public void onSuccess(ArrayList<ProductModel> recommendations) {
                    if (!recommendations.isEmpty()) {
                        binding.Recommendations.setVisibility(View.VISIBLE);
                        setupRecommendationsRecyclerView(recommendations);
                    }
                }

                @Override
                public void onError(Exception error) {
                    Log.e("Recommendations", "Error loading recommendations", error);
                }
            });
    }

    private void setupRecommendationsRecyclerView(ArrayList<ProductModel> recommendations) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
            requireContext(), 
            LinearLayoutManager.HORIZONTAL, 
            false
        );
        binding.Recommendations.setLayoutManager(layoutManager);
        
        ProductAdapter adapter = new ProductAdapter(
            recommendations,
            (product, similarProducts) -> new ProductViewCard(requireActivity())
                .showProductViewDialog(product, similarProducts),
            requireContext(),
            ""
        );
        binding.Recommendations.setAdapter(adapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void remove(int pos,String id,ShoppingCartsProductModel cartsProductModel) {
      // cart.removeItem(models.get(pos));
//        binding.progressCircular.setVisibility(View.VISIBLE);


// Show the bottom sheet
        if (requireActivity().getSupportFragmentManager().findFragmentByTag("bottom_sheet_fragment") == null) {
            RemoveBottomSheetDialogFragment bottomSheet = new RemoveBottomSheetDialogFragment(uid, id, cartsAdapter, cartsProductModel);
            bottomSheet.show(requireActivity().getSupportFragmentManager(), "bottom_sheet_fragment");
        }

//        binding.progressCircular.setVisibility(View.GONE);
    }

    @Override
    public void UpdateQuantity(ShoppingCartsProductModel UpdateModel, String id) {
        binding.progressCircular.setVisibility(View.VISIBLE);
        new ProductManager(requireActivity()).UpdateCartQuantityById(uid,id, UpdateModel.getSelectableQuantity());
//        service.UpdateCartQuantityById(uid,id,UpdateModel.getSelectableQuantity());
    }
}