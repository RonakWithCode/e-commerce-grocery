package com.ronosoft.alwarmart.Fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ronosoft.alwarmart.Adapter.AddressAdapter;
import com.ronosoft.alwarmart.Adapter.NewAddressFragment;
import com.ronosoft.alwarmart.Model.AddressModel;
import com.ronosoft.alwarmart.Model.UserinfoModels;
import com.ronosoft.alwarmart.R;
import com.ronosoft.alwarmart.Services.DatabaseService;
import com.ronosoft.alwarmart.databinding.FragmentUserAccountBinding;
import com.ronosoft.alwarmart.interfaceClass.AddressInterface;
import com.ronosoft.alwarmart.javaClasses.basicFun;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class UserAccountFragment extends Fragment implements AddressInterface {
    private FragmentUserAccountBinding binding;
    private AddressAdapter addressAdapter;
    private ArrayList<AddressModel> adderes;
    private FirebaseAuth auth;
    private DatabaseService databaseService;

    public UserAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUserAccountBinding.inflate(inflater, container, false);
        auth = FirebaseAuth.getInstance();
        databaseService = new DatabaseService();

        // Back button click listener
        binding.backButton.setOnClickListener(v -> {
            if (isAdded() && getActivity() != null) {
                getActivity().finish();
            }
        });

        // Save button click listener
        binding.saveBtn.setOnClickListener(v -> showConfirmationDialog());

        // Add address button click listener
        binding.AddAddress.setOnClickListener(v -> {
            if (isAdded() && getActivity() != null) {
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new NewAddressFragment(), "newAddress");
                transaction.addToBackStack("newAddress");
                transaction.commit();
            }
        });

        init();
        return binding.getRoot();
    }

    private void init() {
        binding.progressCircular.setVisibility(View.VISIBLE);
        adderes = new ArrayList<>();
        addressAdapter = new AddressAdapter(adderes, this, requireContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        binding.UserAddress.setLayoutManager(layoutManager);
        binding.UserAddress.setAdapter(addressAdapter);

        String userId = auth.getUid();
        if (userId != null) {
            databaseService.getUserInfoByDocumentSnapshot(userId, new DatabaseService.getUserInfoDocumentSnapshotCallback() {
                @Override
                public void onSuccess(DocumentSnapshot user) {
                    if (isAdded() && getActivity() != null) {
                        UserinfoModels models = user.toObject(UserinfoModels.class);
                        if (models != null) {
                            binding.Name.setText(models.getUsername());
                            if (models.getAddress() != null && !models.getAddress().isEmpty()) {
                                adderes.clear();
                                adderes.addAll(models.getAddress());
                                addressAdapter.notifyDataSetChanged();
                            }
                        }
                        binding.progressCircular.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    if (isAdded() && getActivity() != null) {
                        binding.progressCircular.setVisibility(View.GONE);
                        Toast.makeText(requireContext(), "Error loading user info: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            binding.progressCircular.setVisibility(View.GONE);
            Toast.makeText(requireContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }

    private void showConfirmationDialog() {
        if (!isAdded() || getActivity() == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Save Changes");
        builder.setMessage("Do you want to save the changes?");
        builder.setPositiveButton("Yes", (dialog, which) -> save());
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void save() {
        if (!isAdded() || getActivity() == null) return;

        String userId = auth.getUid();
        if (userId == null) {
            Toast.makeText(requireContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.progressCircular.setVisibility(View.VISIBLE);
        String newName = Objects.requireNonNull(binding.Name.getText()).toString().trim();
        if (newName.isEmpty()) {
            binding.Name.setError("Name cannot be empty");
            binding.progressCircular.setVisibility(View.GONE);
            return;
        }

        databaseService.UpdateUserInfo(userId, newName, new DatabaseService.UpdateUserInfoCallback() {
            @Override
            public void onSuccess() {
                if (isAdded() && getActivity() != null) {
                    binding.progressCircular.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (isAdded() && getActivity() != null) {
                    binding.progressCircular.setVisibility(View.GONE);
                    binding.hintView.setText(errorMessage);
                    binding.Name.setError(errorMessage);
                    Toast.makeText(requireContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void addersSelect(AddressModel adders) {
        // Implement if needed
    }

    @Override
    public void remove(AddressModel address, int pos) {
        if (!isAdded() || getActivity() == null) return;

        binding.progressCircular.setVisibility(View.VISIBLE);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            databaseService.removeAdders(userId, address, new DatabaseService.removeAddersCallback() {
                @Override
                public void onSuccess() {
                    if (isAdded() && getActivity() != null) {
                        adderes.remove(address); // Update the local list
                        binding.progressCircular.setVisibility(View.GONE);
                        addressAdapter.notifyItemRemoved(pos); // Notify specific item removal
                        Toast.makeText(requireContext(), "Address removed successfully", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    if (isAdded() && getActivity() != null) {
                        binding.progressCircular.setVisibility(View.GONE);
                        basicFun.AlertDialog(requireContext(), "Failed to remove address: " + errorMessage);
                    }
                }
            });
        } else {
            binding.progressCircular.setVisibility(View.GONE);
            basicFun.AlertDialog(requireContext(), "User not authenticated.");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Avoid memory leaks
    }
}