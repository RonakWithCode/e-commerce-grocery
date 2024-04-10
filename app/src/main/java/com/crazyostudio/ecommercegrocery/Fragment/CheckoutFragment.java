package com.crazyostudio.ecommercegrocery.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crazyostudio.ecommercegrocery.Adapter.ShoppingCartsAdapter;
import com.crazyostudio.ecommercegrocery.HelperClass.ShoppingCartHelper;
import com.crazyostudio.ecommercegrocery.Model.ShoppingCartsProductModel;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.Services.AuthService;
import com.crazyostudio.ecommercegrocery.Services.DatabaseService;
import com.crazyostudio.ecommercegrocery.databinding.FragmentCheckoutBinding;
import com.crazyostudio.ecommercegrocery.interfaceClass.ShoppingCartsInterface;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;


public class CheckoutFragment extends Fragment implements ShoppingCartsInterface {
    FragmentCheckoutBinding binding;
    AuthService authService;
    DatabaseService databaseService;
//    Show product in recycle view
    ShoppingCartsAdapter cartsAdapter;
    ArrayList<ShoppingCartsProductModel> models;
    String uid;
    public CheckoutFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCheckoutBinding.inflate(inflater,container,false);
        authService = new AuthService();
        databaseService = new DatabaseService();
        uid = authService.getUserId();

        models = new ArrayList<>();
        cartsAdapter  = new ShoppingCartsAdapter(models,this , requireContext());
        binding.CardView.setAdapter(cartsAdapter);
        binding.CardView.setLayoutManager(new LinearLayoutManager(requireContext()));
        LoadProductFromCart();
        return binding.getRoot();
    }



    private void LoadProductFromCart(){
        databaseService.getUserCartById(authService.getUserId(), new DatabaseService.GetUserCartByIdCallback() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(ArrayList<ShoppingCartsProductModel> cartsProductModels) {
                models.clear();
                models.addAll(cartsProductModels);
                cartsAdapter.notifyDataSetChanged();
                binding.progressCircular.setVisibility(View.GONE);
                binding.SubTotalPrice.setText("₹"+ShoppingCartHelper.calculateTotalPrices(models));
            }

            @Override
            public void onError(String errorMessage) {

            }
        });
    }

    private void LoadRecommendations(){

    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void remove(int pos,String id,ShoppingCartsProductModel cartsProductModel) {
        RemoveBottomSheetDialogFragment bottomSheet = new RemoveBottomSheetDialogFragment(uid,id,cartsAdapter,cartsProductModel);
        bottomSheet.show(requireActivity().getSupportFragmentManager(), bottomSheet.getTag());
    }

    @Override
    public void UpdateQuantity(ShoppingCartsProductModel UpdateModel, String id) {
        binding.progressCircular.setVisibility(View.VISIBLE);
        databaseService.UpdateCartQuantityById(uid,id,UpdateModel.getDefaultQuantity());
        binding.progressCircular.setVisibility(View.GONE);

    }

}