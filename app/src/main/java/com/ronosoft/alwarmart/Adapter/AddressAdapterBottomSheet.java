package com.ronosoft.alwarmart.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.ronosoft.alwarmart.Model.AddressModel;
import com.ronosoft.alwarmart.R;
import com.ronosoft.alwarmart.databinding.AddresslayoutBinding;
import com.ronosoft.alwarmart.databinding.ItemAddressBottomSheetBinding;
import java.util.ArrayList;

public class AddressAdapterBottomSheet extends RecyclerView.Adapter<AddressAdapterBottomSheet.AddressViewHolder> {

    private final ArrayList<AddressModel> addresses;
    private final OnItemClickListener listener;
    private Context context;
    public interface OnItemClickListener {
        void onItemClick(AddressModel address);
    }

    public AddressAdapterBottomSheet(ArrayList<AddressModel> addresses,Context context ,OnItemClickListener listener) {
        this.addresses = addresses;
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AddresslayoutBinding binding = AddresslayoutBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false);
        return new AddressViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        AddressModel address = addresses.get(position);
//        holder.binding.txtAddress.setText(address.getFlatHouse() + ", " + address.getAddress());
//        holder.binding.txtType.setText(address.isHomeSelected() ? "Home" : "Work");






        holder.itemView.setOnClickListener(v -> listener.onItemClick(address));
        String type = address.isHomeSelected() ? "Home" : "Work";
        holder.binding.deliveryTo.setText("Delivering to " + type);
        holder.binding.delete.setVisibility(View.GONE);
        // Load icon using Glide with error handling
        Glide.with(context)
                .load(address.isHomeSelected() ?
                        R.drawable.home_shipping : R.drawable.office_building)
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.drawable.ic_error)
                .into(holder.binding.AddressType);

        // Set address details safely
        String fullAddress = "";
        if (address.getFlatHouse() != null) {
            fullAddress += address.getFlatHouse();
        }
        if (address.getAddress() != null) {
            fullAddress += " " + address.getAddress();
        }
        holder.binding.deliveryAddress.setText(fullAddress.trim());

    }

    @Override
    public int getItemCount() {
        return addresses.size();
    }

    static class AddressViewHolder extends RecyclerView.ViewHolder {
//        private final ItemAddressBottomSheetBinding binding;
        AddresslayoutBinding binding;
        AddressViewHolder(AddresslayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
