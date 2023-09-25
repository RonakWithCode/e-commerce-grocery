package com.crazyostudio.ecommercegrocery.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.crazyostudio.ecommercegrocery.Model.ProductModel;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.databinding.ProductCartViewBinding;
import com.crazyostudio.ecommercegrocery.interfaceClass.ShoppingCartsInterface;

import java.util.ArrayList;

public class ShoppingCartsAdapter extends RecyclerView.Adapter<ShoppingCartsAdapter.ShoppingCartsAdapterViewHolder> {
    ArrayList<ProductModel> productModels;
    ShoppingCartsInterface shoppingCartsInterface;
    Context context;

    public ShoppingCartsAdapter(ArrayList<ProductModel> productModels, ShoppingCartsInterface shoppingCartsInterface, Context context) {
        this.productModels = productModels;
        this.shoppingCartsInterface = shoppingCartsInterface;
        this.context = context;
    }

    @NonNull
    @Override
    public ShoppingCartsAdapter.ShoppingCartsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ShoppingCartsAdapter.ShoppingCartsAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.product_cart_view, parent, false));

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ShoppingCartsAdapter.ShoppingCartsAdapterViewHolder holder, int position) {
        ProductModel model  = productModels.get(position);
        holder.binding.name.setText(model.getItemName());
        Glide.with(context).load(model.getProductImages().get(0)).into(holder.binding.image);
        holder.binding.quantity.setText(model.getSelectProductQuantity()+" item(s)");
        holder.binding.price.setText("INR "+model.getPrice());
        holder.binding.remove.setOnClickListener(view -> {
            shoppingCartsInterface.remove(position,model.getId());
        });
        shoppingCartsInterface.TotalPrice(position,productModels);



    }

    @Override
    public int getItemCount() {
        return productModels.size();
    }

    public static class ShoppingCartsAdapterViewHolder extends RecyclerView.ViewHolder {
        ProductCartViewBinding binding;
        public ShoppingCartsAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ProductCartViewBinding.bind(itemView);
        }
    }
}