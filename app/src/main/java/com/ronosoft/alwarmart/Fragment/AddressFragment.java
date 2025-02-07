package com.ronosoft.alwarmart.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ronosoft.alwarmart.Adapter.AddressAdapter;
import com.ronosoft.alwarmart.Model.AddressModel;
import com.ronosoft.alwarmart.Model.UserinfoModels;
import com.ronosoft.alwarmart.R;
import com.ronosoft.alwarmart.Services.DatabaseService;
import com.ronosoft.alwarmart.databinding.FragmentAddressBinding;
import com.ronosoft.alwarmart.interfaceClass.AddressInterface;
import com.ronosoft.alwarmart.javaClasses.basicFun;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;


public class AddressFragment extends Fragment implements AddressInterface {
    FragmentAddressBinding binding;
    AddressAdapter addressAdapter;
    UserinfoModels userInfo;
    ArrayList<AddressModel> adders;
    NavController navController;

    private ShimmerFrameLayout shimmerLayout;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddressBinding.inflate(inflater,container,false);
//        firebaseDatabase = FirebaseDatabase.getInstance();
        navController = Navigation.findNavController(requireActivity(),R.id.oder_host_fragment);
        binding.backButton.setOnClickListener(back->requireActivity().finish());
        binding.AddAddress.setOnClickListener(address-> navController.navigate(R.id.action_addressFragment_to_newAddressFragment));
        adders = new ArrayList<>();
        shimmerLayout = binding.shimmerLayout;

        // Start shimmer animation
        shimmerLayout.startShimmer();
        addressAdapter = new AddressAdapter(adders,this,requireContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false);
        binding.UserAddress.setLayoutManager(layoutManager);
        binding.UserAddress.setAdapter(addressAdapter);
        new DatabaseService().getUserInfoByDocumentSnapshot(FirebaseAuth.getInstance().getUid(), new DatabaseService.getUserInfoDocumentSnapshotCallback() {
            @SuppressLint("NotifyDataSetChanged")
            @Override

            public void onSuccess(DocumentSnapshot user) {
                UserinfoModels models = user.toObject(UserinfoModels.class);
                if (models != null) {
                    if (models.getAddress() != null && !models.getAddress().isEmpty()) {
                        adders.clear();
                        adders.addAll(models.getAddress());
                        addressAdapter.notifyDataSetChanged();
                        binding.UserAddress.setVisibility(View.VISIBLE);
                        binding.NotFoundText.setVisibility(View.GONE);
                    }
                    else {
                        adders.clear();
                        binding.NotFoundText.setVisibility(View.VISIBLE);
                        binding.UserAddress.setVisibility(View.GONE);
                    }
                }
                shimmerLayout.stopShimmer();
                shimmerLayout.setVisibility(View.GONE);
            }

            @Override
            public void onError(String errorMessage) {
                shimmerLayout.stopShimmer();
                shimmerLayout.setVisibility(View.GONE);
                basicFun.AlertDialog(requireContext(),"getting adders error "+errorMessage);
            }
        });

        return binding.getRoot();
    }


    @Override
    public void addersSelect(AddressModel adders) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("adders",adders);
        bundle.putParcelable("user",userInfo);
        navController.navigate(R.id.action_addressFragment_to_checkoutFragment,bundle);
    }

    @Override
    public void remove(AddressModel address, int pos) {
        binding.progressCircular.setVisibility(View.VISIBLE);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            new DatabaseService().removeAdders(userId, address, new DatabaseService.removeAddersCallback() {

                @SuppressLint("NotifyDataSetChanged")
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