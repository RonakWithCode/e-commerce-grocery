package com.ronosoft.alwarmart.Fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.ronosoft.alwarmart.Adapter.ShoppingCartsAdapter;
import com.ronosoft.alwarmart.Manager.ProductManager;
import com.ronosoft.alwarmart.Model.ShoppingCartsProductModel;
import com.ronosoft.alwarmart.R;
import com.ronosoft.alwarmart.databinding.RemoveProductBoxBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class RemoveBottomSheetDialogFragment  extends BottomSheetDialogFragment {

    private RemoveProductBoxBinding binding;
    ShoppingCartsAdapter cartsAdapter;
    String uid;
    String id;
    ShoppingCartsProductModel model;
    ProductManager productManager;
    public RemoveBottomSheetDialogFragment(String uid, String id, ShoppingCartsAdapter cartsAdapter,ShoppingCartsProductModel model) {
        this.uid = uid;
        this.id = id;
        this.cartsAdapter = cartsAdapter;
        this.model = model;

    }

//    @NonNull
//    @Override
//    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
//        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
//        return dialog;
//    }






    @Override
    public int getTheme() {
        //return super.getTheme();
        return R.style.AppBottomSheetDialogTheme;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //return super.onCreateDialog(savedInstanceState);
        return new BottomSheetDialog(requireContext(), getTheme());  //set your created theme here

    }








    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = RemoveProductBoxBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        productManager =  new ProductManager(requireActivity());

//        view.s
//        view.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Set background transparent

//        view.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        view.setBackground(new ColorDrawable(Color.TRANSPARENT));
//        view.setBackgroundResource(R.drawable.dialogbg);
//
//




        binding.btnRemove.setOnClickListener(v->{
            binding.progressCircular.setVisibility(View.VISIBLE);
            productManager.RemoveCartProductById(id);
//            new DatabaseService().removeCartItemById(uid,id);
            cartsAdapter.notifyDataSetChanged();
            binding.progressCircular.setVisibility(View.GONE);
            dismiss();
        });

        binding.productName.setText(model.getProductName());
        Glide.with(requireContext()).load(model.getProductImage().get(0)).into(binding.productImage);
        binding.productQty.setText(model.getSelectableQuantity()+"");
        binding.productPrice.setText("₹"+model.getPrice());
        binding.productQtyUp.setOnClickListener(up->{
            int quantity = model.getSelectableQuantity();
            quantity++;
            if(quantity>model.getStockCount()) {
                Toast.makeText(requireContext(), "Max stock available: "+ model.getStockCount(), Toast.LENGTH_SHORT).show();
            } else {
                model.setSelectableQuantity(quantity);
                UpdateQuantity(model, model.getProductId());
                binding.productPrice.setText("₹"+model.getPrice());

            }
        });
        binding.productQtyDown.setOnClickListener(Down->{
            int quantity = model.getSelectableQuantity();
            if(quantity > 1) {
                quantity--;
                model.setSelectableQuantity(quantity);
                UpdateQuantity(model, model.getProductId());
                binding.productPrice.setText("₹"+model.getPrice());
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
        productManager.UpdateCartQuantityById(uid,id,UpdateModel.getSelectableQuantity());
        binding.productQty.setText(""+UpdateModel.getSelectableQuantity());
        binding.progressCircular.setVisibility(View.GONE);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Release the binding when the view is destroyed
        dismiss();
    }
}