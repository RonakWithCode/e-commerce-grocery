package com.crazyostudio.ecommercegrocery.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.crazyostudio.ecommercegrocery.Model.ProductModel;
import com.crazyostudio.ecommercegrocery.Model.ShoppingCartsProductModel;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.databinding.ProductCartViewBinding;
import com.crazyostudio.ecommercegrocery.interfaceClass.ShoppingCartsInterface;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.util.TinyCartHelper;

import java.util.ArrayList;

public class ShoppingCartsAdapter extends RecyclerView.Adapter<ShoppingCartsAdapter.ShoppingCartsAdapterViewHolder> {
    ArrayList<ShoppingCartsProductModel> productModels;
    ShoppingCartsInterface shoppingCartsInterface;
    Cart cart;
    Context context;

    public ShoppingCartsAdapter(ArrayList<ShoppingCartsProductModel> productModels, ShoppingCartsInterface shoppingCartsInterface, Context context) {
        this.productModels = productModels;
        this.shoppingCartsInterface = shoppingCartsInterface;
        this.context = context;
        cart = TinyCartHelper.getCart();
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
        holder.binding.productName.setText(model.getItemName());
        Glide.with(context).load(model.getImageURL().get(0)).into(holder.binding.productImage);
        holder.binding.productQty.setText(model.getDefaultQuantity()+"");
        holder.binding.productPrice.setText("₹"+model.getPrice());
        holder.binding.productQtyUp.setOnClickListener(up->{
            int quantity = model.getDefaultQuantity();
            quantity++;
            if(quantity>model.getQuantity()) {
                Toast.makeText(context, "Max stock available: "+ model.getQuantity(), Toast.LENGTH_SHORT).show();
            } else {
                model.setDefaultQuantity(quantity);
                shoppingCartsInterface.UpdateQuantity(model, model.getProductId());
                holder.binding.productPrice.setText("₹"+model.getPrice());
            }
        });
        holder.binding.productQtyDown.setOnClickListener(Down->{
            int quantity = model.getDefaultQuantity();
            if(quantity > 1) {
                quantity--;
                model.setDefaultQuantity(quantity);
                shoppingCartsInterface.UpdateQuantity(model, model.getProductId());
                holder.binding.productPrice.setText("₹"+model.getPrice());
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

    public static class ShoppingCartsAdapterViewHolder extends RecyclerView.ViewHolder {
        ProductCartViewBinding binding;
        public ShoppingCartsAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ProductCartViewBinding.bind(itemView);
        }
    }
}