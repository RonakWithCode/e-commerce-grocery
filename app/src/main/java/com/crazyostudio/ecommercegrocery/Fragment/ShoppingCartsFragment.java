package com.crazyostudio.ecommercegrocery.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.HasDefaultViewModelProviderFactory;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import com.bumptech.glide.Glide;
import com.crazyostudio.ecommercegrocery.Activity.OderActivity;
import com.crazyostudio.ecommercegrocery.Adapter.ShoppingCartsAdapter;
import com.crazyostudio.ecommercegrocery.Model.ProductModel;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.databinding.FragmentShoppingCartsBinding;
import com.crazyostudio.ecommercegrocery.interfaceClass.ShoppingCartsInterface;
import com.crazyostudio.ecommercegrocery.javaClasses.basicFun;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class ShoppingCartsFragment extends Fragment implements ShoppingCartsInterface {
    FragmentShoppingCartsBinding binding;
    ShoppingCartsAdapter cartsAdapter;
    FirebaseDatabase firebaseDatabase;
    boolean IsChatsProgressBar = false;
    ArrayList<ProductModel> models;
    public ShoppingCartsFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentShoppingCartsBinding.inflate(inflater,container,false);
        firebaseDatabase =  FirebaseDatabase.getInstance();

        if (!IsChatsProgressBar){
            binding.progressCircular.setVisibility(View.VISIBLE);
        }

        binding.Buy.setOnClickListener(Buy->{
            Intent i = new Intent(requireContext(), OderActivity.class);
            i.putExtra("BuyType","Cart");
            startActivity(i);

        });

        init();
        return binding.getRoot();
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
                    ProductModel productModel = snapshot1.getValue(ProductModel.class);
                    if (productModel != null) {
                        models.add(productModel);
                        IsChatsProgressBar = true;
                        binding.progressCircular.setVisibility(View.GONE);
                    }
                }
                cartsAdapter.notifyDataSetChanged();
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
    public void TotalPrice(int pos,ArrayList<ProductModel> productModels){
        double totalPrice = 0;

        for (int i = 0; i < productModels.size(); i++) {
            Log.i("Price", "init: "+productModels.get(i).getPrice());
            totalPrice += productModels.get(i).getPrice();
            binding.SubTotal.setText("SubTotal ₹"+totalPrice);
        }

    }
}