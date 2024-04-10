package com.crazyostudio.ecommercegrocery.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.crazyostudio.ecommercegrocery.Adapter.AddressAdapter;
import com.crazyostudio.ecommercegrocery.Adapter.newAddressFragment;
import com.crazyostudio.ecommercegrocery.Model.AddressModel;
import com.crazyostudio.ecommercegrocery.Model.UserinfoModels;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.Services.DatabaseService;
import com.crazyostudio.ecommercegrocery.databinding.FragmentAddressBinding;
import com.crazyostudio.ecommercegrocery.interfaceClass.AddressInterface;
import com.crazyostudio.ecommercegrocery.javaClasses.basicFun;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;


public class AddressFragment extends Fragment implements AddressInterface {
    FragmentAddressBinding binding;
//    FirebaseDatabase
//
//
//    ;
    AddressAdapter addressAdapter;
    UserinfoModels userInfo;

    ArrayList<AddressModel> adderes;
    FirebaseFirestore firestore;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddressBinding.inflate(inflater,container,false);
//        firebaseDatabase = FirebaseDatabase.getInstance();
        firestore = FirebaseFirestore.getInstance();
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

        new DatabaseService().getUserInfoByDocumentSnapshot(FirebaseAuth.getInstance().getUid(), new DatabaseService.getUserInfoDocumentSnapshotCallback() {
            @Override

            public void onSuccess(DocumentSnapshot user) {
                UserinfoModels models = user.toObject(UserinfoModels.class);
                if (models != null) {
//                        binding.email.setText(models.getEmailAddress());
                    if (models.getAddress() != null && !models.getAddress().isEmpty()) {
                        adderes.clear();
                        adderes.addAll(models.getAddress());
                        addressAdapter.notifyDataSetChanged();
                    }
                }
                binding.progressCircular.setVisibility(View.GONE);
            }

            @Override
            public void onError(String errorMessage) {

            }
        });





        return binding.getRoot();
    }


    @Override
    public void addersSelect(AddressModel adders) {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        Fragment fragment = new PaymentScreenFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("adders",adders);
        bundle.putParcelable("user",userInfo);
//            bundle.putParcelable("productModel",productModel);
        fragment.setArguments(bundle);
        transaction.replace(R.id.fragment_container,fragment,"PaymentScreen");
        transaction.addToBackStack("PaymentScreen");
        transaction.commit();
        }


    @Override
    public void remove(AddressModel address, int pos) {
        binding.progressCircular.setVisibility(View.VISIBLE);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            new DatabaseService().removeAdders(userId, address, new DatabaseService.removeAddersCallback() {

                @Override
                public void onSuccess() {
                    binding.progressCircular.setVisibility(View.INVISIBLE);
                    addressAdapter.notifyDataSetChanged();
                }

                @Override
                public void onError(String errorMessage) {
                    basicFun.AlertDialog(requireContext(), "Failed to remove address: " + errorMessage);

                }
            });
        }

        else {
            basicFun.AlertDialog(requireContext(), "User not authenticated.");
        }
    }

}