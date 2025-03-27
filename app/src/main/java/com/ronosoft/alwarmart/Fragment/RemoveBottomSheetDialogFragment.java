package com.ronosoft.alwarmart.Fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.ronosoft.alwarmart.Adapter.ShoppingCartsAdapter;
import com.ronosoft.alwarmart.Manager.ProductManager;
import com.ronosoft.alwarmart.Model.ShoppingCartsProductModel;
import com.ronosoft.alwarmart.R;
import com.ronosoft.alwarmart.databinding.RemoveProductBoxBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class RemoveBottomSheetDialogFragment extends BottomSheetDialogFragment {

    private RemoveProductBoxBinding binding;
    private ShoppingCartsAdapter cartsAdapter;
    private String uid;
    private String id;
    private ShoppingCartsProductModel model;
    private ProductManager productManager;
    private OnCartUpdatedListener listener;

    // Callback interface to notify parent fragment that cart has been updated.
    public interface OnCartUpdatedListener {
        void onCartUpdated();
    }

    // Constructor with listener parameter
    public RemoveBottomSheetDialogFragment(String uid, String id, ShoppingCartsAdapter cartsAdapter,
                                           ShoppingCartsProductModel model, OnCartUpdatedListener listener) {
        this.uid = uid;
        this.id = id;
        this.cartsAdapter = cartsAdapter;
        this.model = model;
        this.listener = listener;
    }

    @Override
    public int getTheme() {
        return R.style.AppBottomSheetDialogTheme;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new BottomSheetDialog(requireContext(), getTheme());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = RemoveProductBoxBinding.inflate(inflater, container, false);
        productManager = new ProductManager(requireActivity());

        binding.btnRemove.setOnClickListener(v -> {
            binding.progressCircular.setVisibility(View.VISIBLE);
            productManager.RemoveCartProductById(id);
            // Optionally notify the adapter (if your local list is being updated elsewhere)
            cartsAdapter.notifyDataSetChanged();
            binding.progressCircular.setVisibility(View.GONE);
            dismiss();
            // Notify the parent fragment that the cart has been updated.
            if (listener != null) {
                listener.onCartUpdated();
            }
        });

        binding.btnCancel.setOnClickListener(v -> dismiss());

        binding.productName.setText(model.getProductName());
        Glide.with(requireContext())
                .load(model.getProductImage().get(0))
                .into(binding.productImage);
        binding.productQty.setText(String.valueOf(model.getSelectableQuantity()));
        binding.productPrice.setText("₹" + model.getPrice());
        binding.productQtyUp.setOnClickListener(v -> {
            int quantity = model.getSelectableQuantity();
            quantity++;
            if (quantity > model.getStockCount()) {
                Toast.makeText(requireContext(), "Max stock available: " + model.getStockCount(), Toast.LENGTH_SHORT).show();
            } else {
                model.setSelectableQuantity(quantity);
                UpdateQuantity(model, model.getProductId());
                binding.productPrice.setText("₹" + model.getPrice());
            }
        });
        binding.productQtyDown.setOnClickListener(v -> {
            int quantity = model.getSelectableQuantity();
            if (quantity > 1) {
                quantity--;
                model.setSelectableQuantity(quantity);
                UpdateQuantity(model, model.getProductId());
                binding.productPrice.setText("₹" + model.getPrice());
            }
        });
        return binding.getRoot();
    }

    public void UpdateQuantity(ShoppingCartsProductModel updateModel, String id) {
        binding.progressCircular.setVisibility(View.VISIBLE);
        productManager.UpdateCartQuantityById(uid, id, updateModel.getSelectableQuantity());
        binding.productQty.setText(String.valueOf(updateModel.getSelectableQuantity()));
        binding.progressCircular.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        dismiss();
    }
}
