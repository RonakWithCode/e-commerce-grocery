package com.ronosoft.alwarmart.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ronosoft.alwarmart.Manager.ProductManager;
import com.ronosoft.alwarmart.Model.ShoppingCartsProductModel;
import com.ronosoft.alwarmart.R;
import com.ronosoft.alwarmart.Services.AuthService;
import com.ronosoft.alwarmart.databinding.ProductCartViewBinding;
import com.ronosoft.alwarmart.interfaceClass.ShoppingCartsInterface;

import java.util.ArrayList;

public class ShoppingCartsAdapter extends RecyclerView.Adapter<ShoppingCartsAdapter.ShoppingCartsAdapterViewHolder> {
    ArrayList<ShoppingCartsProductModel> productModels;
    ShoppingCartsInterface shoppingCartsInterface;
    Context context;
    ProductManager productManager;
    public ShoppingCartsAdapter(ArrayList<ShoppingCartsProductModel> productModels, ShoppingCartsInterface shoppingCartsInterface, Context context) {
        this.productModels = productModels;
        this.shoppingCartsInterface = shoppingCartsInterface;
        this.context = context;
        productManager = new ProductManager(context);

    }

    @NonNull
    @Override
    public ShoppingCartsAdapter.ShoppingCartsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ShoppingCartsAdapter.ShoppingCartsAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.product_cart_view, parent, false));

    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull ShoppingCartsAdapter.ShoppingCartsAdapterViewHolder holder, int position) {
        ShoppingCartsProductModel model  = productModels.get(position);
        holder.binding.productName.setText(model.getProductName());
        Glide.with(context).load(model.getProductImage().get(0))
                .placeholder(R.drawable.product_image_shimmee_effect)
                .into(holder.binding.productImage);
//        holder.binding.productImage.setZoomable(false);

        holder.binding.productQty.setText(model.getSelectableQuantity()+"");
        holder.binding.productPrice.setText("₹"+model.getPrice());
//
        holder.binding.productQtyUp.setOnClickListener(up->{
//            int quantity = model.getSelectableQuantity();
//            quantity++;
//            if(quantity>model.getStockCount()) {
//                Toast.makeText(context, "Max stock available: "+ model.getStockCount(), Toast.LENGTH_SHORT).show();
//            } else {
//                model.setSelectableQuantity(quantity);
//                shoppingCartsInterface.UpdateQuantity(model, model.getProductId());
//                holder.binding.productPrice.setText("₹"+model.getPrice());
//            }

            int currentQty = model.getSelectableQuantity();
            int newQty = currentQty + 1;
            if (newQty <= model.getMaxSelectableQuantity()) {
                holder.binding.productQty.setText(String.valueOf(newQty));
                UpdateQTY(model.getProductId(), newQty);
            }
        });
        holder.binding.productQtyDown.setOnClickListener(Down->{
//            int quantity = model.getSelectableQuantity();
//            if(quantity > 1) {
//                quantity--;
//                model.setSelectableQuantity(quantity);
//                shoppingCartsInterface.UpdateQuantity(model, model.getProductId());
//                holder.binding.productPrice.setText("₹"+model.getPrice());
//            }else {
//                shoppingCartsInterface.remove(position,model.getProductId(),model);
//            }
            int currentQty = model.getSelectableQuantity();
            int newQty = currentQty - 1;
            if (newQty >= model.getMinSelectableQuantity()) {
                holder.binding.productQty.setText(String.valueOf(newQty));
                UpdateQTY(model.getProductId(), newQty);
            }else {
                shoppingCartsInterface.remove(position,model.getProductId(),model);

            }
        });

    }

    @Override
    public int getItemCount() {
        return productModels.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    private void UpdateQTY(String productId, int quantity) {
        productManager.UpdateCartQuantityById(
                new AuthService().getUserId(),
                productId,
                quantity
        );
    }

    public static class ShoppingCartsAdapterViewHolder extends RecyclerView.ViewHolder {
        ProductCartViewBinding binding;
        public ShoppingCartsAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ProductCartViewBinding.bind(itemView);
        }
    }
}