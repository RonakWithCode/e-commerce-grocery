package com.ronosoft.alwarmart.Fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.ronosoft.alwarmart.Adapter.AddressAdapterBottomSheet;
import com.ronosoft.alwarmart.Model.AddressModel;
import com.ronosoft.alwarmart.databinding.FragmentDefaultAddressBottomSheetBinding;
import java.util.ArrayList;

public class DefaultAddressBottomSheetFragment extends BottomSheetDialogFragment {

    private FragmentDefaultAddressBottomSheetBinding binding;
    private ArrayList<AddressModel> addressList;
    private OnAddressSelectedListener listener;

    public interface OnAddressSelectedListener {
        void onAddressSelected(AddressModel address);
    }

    public void setOnAddressSelectedListener(OnAddressSelectedListener listener) {
        this.listener = listener;
    }

    // Pass in the list of addresses when creating the bottom sheet.
    public DefaultAddressBottomSheetFragment(ArrayList<AddressModel> addressList) {
        this.addressList = addressList;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Get the BottomSheetDialog from the super call
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        // When the dialog is shown, set its bottom sheet background to transparent.
        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
            View bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                bottomSheet.setBackground(new ColorDrawable(Color.TRANSPARENT));
            }
        });
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDefaultAddressBottomSheetBinding.inflate(inflater, container, false);

        AddressAdapterBottomSheet adapter = new AddressAdapterBottomSheet(addressList,requireContext() ,address -> {
            if (listener != null) {
                listener.onAddressSelected(address);
                dismiss();
            }
        });
        binding.recyclerAddresses.setAdapter(adapter);
        binding.recyclerAddresses.setLayoutManager(new LinearLayoutManager(getContext()));

        binding.btnAddAddress.setOnClickListener(v -> {
            // Handle "Add New Address" action (e.g., navigate to AddAddressFragment)
            dismiss();
        });

        return binding.getRoot();
    }
}
