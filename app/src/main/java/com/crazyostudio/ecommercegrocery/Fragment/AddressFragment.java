package com.crazyostudio.ecommercegrocery.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import com.crazyostudio.ecommercegrocery.Adapter.AddressAdapter;
import com.crazyostudio.ecommercegrocery.Adapter.newAddressFragment;
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
    UserinfoModels userInfo;
    ArrayList<String> adderes;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddressBinding.inflate(inflater,container,false);
        firebaseDatabase = FirebaseDatabase.getInstance();



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
                        userInfo = snapshot.getValue(UserinfoModels.class);
                        assert userInfo != null;
                        adderes.addAll(userInfo.getAddress());
                        binding.NotFoundText.setVisibility(View.INVISIBLE);
                        binding.UserAddress.setVisibility(View.VISIBLE);

                    }else {
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
    public void Edit(String adders, int pos) {

    }

    @Override
    public void addersSelect(String adders) {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        Fragment fragment = new PaymentScreenFragment();
        Bundle bundle = new Bundle();
        bundle.putString("adders",adders);
        bundle.putString("BuyType",adders);
        fragment.setArguments(bundle);
        transaction.replace(R.id.fragment_container,fragment,"PaymentScreen");
        transaction.addToBackStack("PaymentScreen");
        transaction.commit();

    }

    @Override
    public void remove(String adders, int pos) {
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