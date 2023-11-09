package com.crazyostudio.ecommercegrocery.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import com.crazyostudio.ecommercegrocery.Adapter.AddressAdapter;
import com.crazyostudio.ecommercegrocery.Adapter.newAddressFragment;
import com.crazyostudio.ecommercegrocery.Model.AddressModel;
import com.crazyostudio.ecommercegrocery.Model.ProductModel;
import com.crazyostudio.ecommercegrocery.Model.UserinfoModels;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.databinding.FragmentAddressBinding;
import com.crazyostudio.ecommercegrocery.interfaceClass.AddressInterface;
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

public class AddressFragment extends Fragment implements AddressInterface {
    FragmentAddressBinding binding;
    FirebaseDatabase firebaseDatabase;
    AddressAdapter addressAdapter;
    String id,Now;
    UserinfoModels userInfo;
    ArrayList<AddressModel> adderes;
    ProductModel productModel;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddressBinding.inflate(inflater,container,false);
        firebaseDatabase = FirebaseDatabase.getInstance();

        if (getArguments() != null) {
            id = getArguments().getString("LoadID");
            Now = getArguments().getString("BuyType","s");
            if (Now.equals("Now")) {
                productModel = getArguments().getParcelable("productModel");
            }
        }

        binding.backButton.setOnClickListener(back->requireActivity().finish());
        binding.AddAddress.setOnClickListener(address->{
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container,new newAddressFragment(),"newAddress");
            transaction.addToBackStack("newAddress");
            transaction.commit();


        });
        adderes = new ArrayList<>();
        addressAdapter = new AddressAdapter(adderes,this,requireContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false);
        binding.UserAddress.setLayoutManager(layoutManager);
        binding.UserAddress.setAdapter(addressAdapter);
        firebaseDatabase.getReference().child("UserInfo").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    binding.progressCircular.setVisibility(View.INVISIBLE);
                    // Data exists, create a UserInfoModel instance and populate it
                    if (snapshot.child("address").exists()) {
//                        if (snapshot.child("address").equals(AddressModel.class)) {
                            userInfo = snapshot.getValue(UserinfoModels.class);
                            assert userInfo != null;
                            adderes.addAll(userInfo.getAddress());
                            binding.NotFoundText.setVisibility(View.INVISIBLE);
                            binding.UserAddress.setVisibility(View.VISIBLE);
                        }
                else {
                        binding.NotFoundText.setVisibility(View.VISIBLE);
                        binding.UserAddress.setVisibility(View.INVISIBLE);
                    }
                    }

                addressAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        return binding.getRoot();
    }


    @Override
    public void addersSelect(AddressModel adders) {
        if (!id.equals("MoreAddress")){
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            Fragment fragment = new PaymentScreenFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("adders",adders);
            bundle.putParcelable("user",userInfo);
            bundle.putString("id",id);
            bundle.putString("BuyType",Now);
            if (Now.equals("Now")) {
                bundle.putParcelable("productModel",productModel);
            }
            bundle.putParcelable("productModel",productModel);
            fragment.setArguments(bundle);
            transaction.replace(R.id.fragment_container,fragment,"PaymentScreen");
            transaction.addToBackStack("PaymentScreen");
            transaction.commit();
        }

    }

    @Override
    public void remove(AddressModel adders, int pos) {
        binding.progressCircular.setVisibility(View.VISIBLE);
        adderes.remove(pos);
        userInfo.setAddress(adderes);
        firebaseDatabase.getReference().child("UserInfo").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).setValue(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    binding.progressCircular.setVisibility(View.INVISIBLE);
                }
                addressAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(e -> basicFun.AlertDialog(requireContext(),e.toString()));
    }
}