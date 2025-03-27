package com.ronosoft.alwarmart.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.ronosoft.alwarmart.Adapter.AddressAdapter;
import com.ronosoft.alwarmart.Model.AddressModel;
import com.ronosoft.alwarmart.Model.UserinfoModels;
import com.ronosoft.alwarmart.R;
import com.ronosoft.alwarmart.Services.DatabaseService;
import com.ronosoft.alwarmart.databinding.FragmentAddressBinding;
import com.ronosoft.alwarmart.interfaceClass.AddressInterface;
import com.ronosoft.alwarmart.javaClasses.basicFun;

import java.util.ArrayList;

public class AddressFragment extends Fragment implements AddressInterface {

    private FragmentAddressBinding binding;
    private AddressAdapter addressAdapter;
    private UserinfoModels userInfo;
    private ArrayList<AddressModel> adders;
    private final DatabaseService databaseService = new DatabaseService();
    private ShimmerFrameLayout shimmerLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddressBinding.inflate(inflater, container, false);

        // Setup back button listener
        binding.backButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        // Setup add-address button: Navigate using Navigation Component.
        binding.AddAddress.setOnClickListener(v ->
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_addressFragment_to_newAddressFragment)
        );

        adders = new ArrayList<>();
        shimmerLayout = binding.shimmerLayout;
        if (shimmerLayout != null) {
            shimmerLayout.startShimmer();
        }

        // Initialize adapter and RecyclerView
        addressAdapter = new AddressAdapter(adders, this, requireContext());
        binding.UserAddress.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        binding.UserAddress.setAdapter(addressAdapter);

        // Fetch user info (which includes the address list) from Firestore
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) {
            databaseService.getUserInfoByDocumentSnapshot(uid, new DatabaseService.getUserInfoDocumentSnapshotCallback() {
                @Override
                public void onSuccess(DocumentSnapshot document) {
                    if (!isAdded() || binding == null) return;
                    userInfo = document.toObject(UserinfoModels.class);
                    if (userInfo != null && userInfo.getAddress() != null && !userInfo.getAddress().isEmpty()) {
                        adders.clear();
                        adders.addAll(userInfo.getAddress());
                        addressAdapter.notifyDataSetChanged();
                        binding.UserAddress.setVisibility(View.VISIBLE);
                        binding.NotFoundText.setVisibility(View.GONE);
                    } else {
                        adders.clear();
                        binding.NotFoundText.setVisibility(View.VISIBLE);
                        binding.UserAddress.setVisibility(View.GONE);
                    }
                    stopShimmer();
                }

                @Override
                public void onError(String errorMessage) {
                    stopShimmer();
                    basicFun.AlertDialog(requireContext(), "Error fetching addresses: " + errorMessage);
                }
            });
        } else {
            stopShimmer();
            basicFun.AlertDialog(requireContext(), "User not authenticated.");
        }

        return binding.getRoot();
    }

    private void stopShimmer() {
        if (shimmerLayout != null) {
            shimmerLayout.stopShimmer();
            shimmerLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void addersSelect(AddressModel selectedAddress) {
        if (!isAdded() || binding == null) return;
        Bundle bundle = new Bundle();
        bundle.putParcelable("adders", selectedAddress);
        if (userInfo != null) {
            bundle.putParcelable("user", userInfo);
        } else {
            Toast.makeText(requireContext(), "User info not available", Toast.LENGTH_SHORT).show();
            return;
        }
        // Navigate to checkout fragment using Navigation Component.
        Navigation.findNavController(requireView())
                .navigate(R.id.action_addressFragment_to_checkoutFragment, bundle);
    }

    @Override
    public void remove(AddressModel address, int pos) {
        if (!isAdded() || binding == null) return;
        binding.progressCircular.setVisibility(View.VISIBLE);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            databaseService.removeAdders(userId, address, new DatabaseService.removeAddersCallback() {
                @Override
                public void onSuccess() {
                    if (!isAdded() || binding == null) return;
                    binding.progressCircular.setVisibility(View.INVISIBLE);
                    adders.remove(address);
                    addressAdapter.notifyItemRemoved(pos);
                }

                @Override
                public void onError(String errorMessage) {
                    binding.progressCircular.setVisibility(View.INVISIBLE);
                    basicFun.AlertDialog(requireContext(), "Failed to remove address: " + errorMessage);
                }
            });
        } else {
            basicFun.AlertDialog(requireContext(), "User not authenticated.");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
