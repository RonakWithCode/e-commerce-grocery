package com.crazyostudio.ecommercegrocery.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crazyostudio.ecommercegrocery.Model.ProductModel;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.databinding.FragmentPaymentScreenBinding;
import com.crazyostudio.ecommercegrocery.javaClasses.basicFun;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class PaymentScreenFragment extends Fragment {
    FragmentPaymentScreenBinding binding;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private String adders;
    ArrayList<ProductModel> models;
    FirebaseDatabase firebaseDatabase;
    public PaymentScreenFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            adders = getArguments().getString("adders");
        }else {

            requireActivity().onBackPressed();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPaymentScreenBinding.inflate(inflater,container,false);
        firebaseDatabase = FirebaseDatabase.getInstance();
//        final double[] totalPrice = {0};

//        models = new ArrayList<>();
//
//        firebaseDatabase.getReference().child("Cart").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).addValueEventListener(new ValueEventListener() {
//            @SuppressLint("NotifyDataSetChanged")
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
////                models.clear();
//                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
//                    ProductModel productModel = snapshot1.getValue(ProductModel.class);
//                    if (productModel != null) {
//                        models.add(productModel);
//                        for (int i = 0; i < models.size(); i++) {
//                            totalPrice[0] += models.get(i).getPrice();
//                            binding.DeliveryOptions.setText("SubTotal ₹"+ totalPrice[0]);
////        binding.DeliveryOptions.setText("INR "+total);
//                        }
//                        binding.progressCircular.setVisibility(View.GONE);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                basicFun.AlertDialog(requireContext(),error.toString());
//            }
//        });






        binding.Change.setOnClickListener(Change-> requireActivity().onBackPressed());
        binding.DeliveryAddress.setText(adders);
        binding.COD.setOnClickListener(COD->{
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            Fragment fragment = new PlaceOderFragment();
            Bundle bundle = new Bundle();
            bundle.putString("adders",adders);
            bundle.putString("PaymentMode","COD");
            fragment.setArguments(bundle);
            transaction.replace(R.id.fragment_container,fragment,"PlaceOderFragment");
            transaction.addToBackStack("PlaceOderFragment");
            transaction.commit();

        });







        return binding.getRoot();
    }
}