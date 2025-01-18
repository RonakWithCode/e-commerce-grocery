package com.crazyostudio.ecommercegrocery.Fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
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

import com.crazyostudio.ecommercegrocery.Activity.AuthMangerActivity;
import com.crazyostudio.ecommercegrocery.Activity.OderActivity;
import com.crazyostudio.ecommercegrocery.Adapter.ShoppingCartsAdapter;
import com.crazyostudio.ecommercegrocery.HelperClass.ShoppingCartHelper;
import com.crazyostudio.ecommercegrocery.Manager.ProductManager;
import com.crazyostudio.ecommercegrocery.Model.ProductModel;
import com.crazyostudio.ecommercegrocery.Model.ShoppingCartsProductModel;
import com.crazyostudio.ecommercegrocery.Services.AuthService;
import com.crazyostudio.ecommercegrocery.Services.DatabaseService;
import com.crazyostudio.ecommercegrocery.databinding.FragmentShoppingCartsBinding;
import com.crazyostudio.ecommercegrocery.interfaceClass.ShoppingCartsInterface;
import com.crazyostudio.ecommercegrocery.javaClasses.basicFun;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.util.TinyCartHelper;

import java.math.BigDecimal;
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

//        if (!authService.IsLogin()) {
//            binding.relativeNotAuth.setVisibility(View.VISIBLE);
//            binding.main.setVisibility(View.GONE);
//            binding.progressCircular.setVisibility(View.GONE);
//            binding.linearLayoutPlaceHolder.stopShimmer();
//            binding.linearLayoutPlaceHolder.setVisibility(View.GONE);
//        }  else {
//            init();
//        }
        return binding.getRoot();
    }



    @SuppressLint("SetTextI18n")
    private void init() {
        cartsAdapter = new ShoppingCartsAdapter(models,this,requireContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false);
        binding.ProductCart.setLayoutManager(layoutManager);
        binding.ProductCart.setAdapter(cartsAdapter);
        service.getUserCartById(uid, new DatabaseService.GetUserCartByIdCallback() {
            @Override
            public void onSuccess(ArrayList<ShoppingCartsProductModel> cartsProductModels) {
                models.clear();
                models.addAll(cartsProductModels);
                binding.progressCircular.setVisibility(View.GONE);
                cartsAdapter.notifyDataSetChanged();
                double totalPrice = ShoppingCartHelper.calculateTotalPrices(models);
                binding.SubTotal.setText("SubTotal ₹"+totalPrice);
                binding.main.setVisibility(View.VISIBLE);
                binding.IsEnity.setVisibility(View.GONE);
                binding.linearLayoutPlaceHolder.stopShimmer();
                binding.linearLayoutPlaceHolder.setVisibility(View.GONE);
            }

            @Override
            public void onError(String errorMessage) {
                if (errorMessage.equals("Cart is empty")) {
                    binding.main.setVisibility(View.GONE);
                    binding.IsEnity.setVisibility(View.VISIBLE);
                    binding.linearLayoutPlaceHolder.stopShimmer();
                    binding.linearLayoutPlaceHolder.setVisibility(View.GONE);
                }
//                basicFun.AlertDialog(requireContext(), errorMessage);
                Log.i("ERRORDATABASE", "onError: "+errorMessage);
            }
        });

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