package com.crazyostudio.ecommercegrocery.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.crazyostudio.ecommercegrocery.Adapter.ShoppingCartsAdapter;
import com.crazyostudio.ecommercegrocery.Model.ShoppingCartsProductModel;
import com.crazyostudio.ecommercegrocery.Services.DatabaseService;
import com.crazyostudio.ecommercegrocery.databinding.RemoveProductBoxBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class RemoveBottomSheetDialogFragment  extends BottomSheetDialogFragment {

    private RemoveProductBoxBinding binding;
    ShoppingCartsAdapter cartsAdapter;
    String uid;
    String id;
    ShoppingCartsProductModel model;


    public RemoveBottomSheetDialogFragment(String uid, String id, ShoppingCartsAdapter cartsAdapter,ShoppingCartsProductModel model) {
        this.uid = uid;
        this.id = id;
        this.cartsAdapter = cartsAdapter;
        this.model = model;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = RemoveProductBoxBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.btnRemove.setOnClickListener(v->{
            binding.progressCircular.setVisibility(View.VISIBLE);
            new DatabaseService().removeCartItemById(uid,id);
            cartsAdapter.notifyDataSetChanged();
            binding.progressCircular.setVisibility(View.GONE);
            dismiss();
        });

        binding.productName.setText(model.getItemName());
        Glide.with(requireContext()).load(model.getImageURL().get(0)).into(binding.productImage);
        binding.productQty.setText(model.getDefaultQuantity()+"");
        binding.productPrice.setText("₹"+model.getPrice());
        binding.productQtyUp.setOnClickListener(up->{
            int quantity = model.getDefaultQuantity();
            quantity++;
            if(quantity>model.getQuantity()) {
                Toast.makeText(requireContext(), "Max stock available: "+ model.getQuantity(), Toast.LENGTH_SHORT).show();
            } else {
                model.setDefaultQuantity(quantity);
                UpdateQuantity(model, model.getProductId());
                binding.productPrice.setText("₹"+model.getPrice());

            }
        });
        binding.productQtyDown.setOnClickListener(Down->{
            int quantity = model.getDefaultQuantity();
            if(quantity > 1) {
                quantity--;
                model.setDefaultQuantity(quantity);
                UpdateQuantity(model, model.getProductId());
                binding.productPrice.setText("₹"+model.getPrice());
            }else {
//                shoppingCartsInterface.remove(position,model.getProductId(),model);
            }
        });





        binding.btnCancel.setOnClickListener(v -> {
            dismiss();
        });


        // Return the inflated layout
        return view;
    }


    public void UpdateQuantity(ShoppingCartsProductModel UpdateModel, String id) {
        binding.progressCircular.setVisibility(View.VISIBLE);
        new DatabaseService().UpdateCartQuantityById(uid,id,UpdateModel.getDefaultQuantity());
        binding.productQty.setText(""+UpdateModel.getDefaultQuantity());
        binding.progressCircular.setVisibility(View.GONE);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Release the binding when the view is destroyed
        dismiss();
    }
}