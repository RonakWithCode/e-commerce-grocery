package com.crazyostudio.ecommercegrocery.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.crazyostudio.ecommercegrocery.Model.AddressModel;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.databinding.AddresslayoutBinding;
import com.crazyostudio.ecommercegrocery.interfaceClass.AddressInterface;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {
    private final ArrayList<AddressModel> addresses;
    private final AddressInterface addressInterface;
    private final WeakReference<Context> contextRef;

    public AddressAdapter(ArrayList<AddressModel> addresses, AddressInterface addressInterface, Context context) {
        this.addresses = addresses; // Don't create defensive copy as fragments manage the list
        this.addressInterface = addressInterface;
        this.contextRef = new WeakReference<>(context);
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AddresslayoutBinding binding = AddresslayoutBinding.inflate(
            LayoutInflater.from(parent.getContext()), parent, false);
        return new AddressViewHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        Context context = contextRef.get();
        if (context == null || position >= addresses.size()) return;

        AddressModel address = addresses.get(position);
        if (address == null) return;

        // Set address type and icon
        String type = address.isHomeSelected() ? "Home" : "Work";
        holder.binding.deliveryTo.setText("Delivering to " + type);
        
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

        // Setup click listeners with position checks
        setupClickListeners(holder, address, holder.getBindingAdapterPosition());
    }

    private void setupClickListeners(AddressViewHolder holder, AddressModel address, int position) {
        View.OnClickListener selectListener = v -> {
            if (addressInterface != null && position != RecyclerView.NO_POSITION) {
                addressInterface.addersSelect(address);
            }
        };

        View.OnClickListener deleteListener = v -> {
            if (addressInterface != null && position != RecyclerView.NO_POSITION) {
                addressInterface.remove(address, position);
            }
        };

        View.OnClickListener editListener = v -> {
            Context context = contextRef.get();
            if (context != null) {
                Toast.makeText(context, "Edit functionality coming soon", Toast.LENGTH_SHORT).show();
            }
        };

        // Set click listeners
        holder.binding.getRoot().setOnClickListener(selectListener);
        holder.binding.delete.setOnClickListener(deleteListener);
        holder.binding.edit.setOnClickListener(editListener);
    }

    @Override
    public int getItemCount() {
        return addresses != null ? addresses.size() : 0;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateAddresses(ArrayList<AddressModel> newAddresses) {
        if (newAddresses != null) {
            addresses.clear();
            addresses.addAll(newAddresses);
            notifyDataSetChanged();
        }
    }

    static class AddressViewHolder extends RecyclerView.ViewHolder {
        private final AddresslayoutBinding binding;

        AddressViewHolder(AddresslayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
