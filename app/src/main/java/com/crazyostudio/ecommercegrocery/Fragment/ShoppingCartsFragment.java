package com.crazyostudio.ecommercegrocery.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.crazyostudio.ecommercegrocery.Activity.OderActivity;
import com.crazyostudio.ecommercegrocery.Adapter.ShoppingCartsAdapter;
import com.crazyostudio.ecommercegrocery.Model.ShoppingCartsProductModel;
import com.crazyostudio.ecommercegrocery.databinding.FragmentShoppingCartsBinding;
import com.crazyostudio.ecommercegrocery.interfaceClass.ShoppingCartsInterface;
import com.crazyostudio.ecommercegrocery.javaClasses.basicFun;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.util.TinyCartHelper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Objects;
public class ShoppingCartsFragment extends Fragment implements ShoppingCartsInterface {
    FragmentShoppingCartsBinding binding;
    ShoppingCartsAdapter cartsAdapter;
    FirebaseDatabase firebaseDatabase;
    Cart cart;

    ArrayList<ShoppingCartsProductModel> models;
    public ShoppingCartsFragment() {
        // Required empty public constructor
    }
    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentShoppingCartsBinding.inflate(inflater,container,false);
        firebaseDatabase =  FirebaseDatabase.getInstance();
        cart = TinyCartHelper.getCart();


        binding.Buy.setOnClickListener(Buy->{
            startActivity(new Intent(requireContext(), OderActivity.class));
        });
        init();

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        cart.clearCart();
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        models = new ArrayList<>();
        cartsAdapter = new ShoppingCartsAdapter(models,this,requireContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false);
        binding.ProductCart.setLayoutManager(layoutManager);
        binding.ProductCart.setAdapter(cartsAdapter);
        firebaseDatabase.getReference().child("Cart").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    ShoppingCartsProductModel productModel = snapshot1.getValue(ShoppingCartsProductModel.class);
                    if (productModel != null) {
                        models.add(productModel);
                        cart.addItem(productModel,productModel.getSelectProductQuantity());
                        binding.progressCircular.setVisibility(View.GONE);
                    }


                }
                cartsAdapter.notifyDataSetChanged();
                BigDecimal totalPrice = cart.getTotalPrice();
                binding.SubTotal.setText("SubTotal ₹"+totalPrice);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                basicFun.AlertDialog(requireContext(),error.toString());
            }
        });

    }


    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void remove(int pos,String id) {
      // cart.removeItem(models.get(pos));
        models.remove(pos);
        binding.progressCircular.setVisibility(View.VISIBLE);
        firebaseDatabase.getReference().child("Cart").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    binding.progressCircular.setVisibility(View.GONE);
                }
            }
        }).addOnFailureListener(e -> {
            binding.progressCircular.setVisibility(View.GONE);
            basicFun.AlertDialog(requireContext(),e.toString());
        });
        cartsAdapter.notifyDataSetChanged();
    }
    @Override
    public void TotalPrice(int pos,ArrayList<ShoppingCartsProductModel> productModels){
//        double totalPrice = 0;
//
//        for (int i = 0; i < productModels.size(); i++) {
//            Log.i("Price", "init: "+productModels.get(i).getPrice());
//            totalPrice += productModels.get(i).getPrice();
//            binding.SubTotal.setText("SubTotal ₹"+totalPrice);
//        }
    }
}