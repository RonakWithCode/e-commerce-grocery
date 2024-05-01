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
import com.crazyostudio.ecommercegrocery.Model.ShoppingCartsProductFirebaseModel;
import com.crazyostudio.ecommercegrocery.Model.ShoppingCartsProductModel;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.databinding.OrderProductLayoutBinding;
import com.crazyostudio.ecommercegrocery.interfaceClass.OrderProductInterface;

import java.util.ArrayList;

public class ShoppingCartsProductAdapter extends RecyclerView.Adapter<ShoppingCartsProductAdapter.ShoppingCartsProductAdapterViewHolder>{
    ArrayList<ShoppingCartsProductModel> productModels;
    Context context;
    OrderProductInterface orderProductInterface;
    public ShoppingCartsProductAdapter(ArrayList<ShoppingCartsProductModel> productModels, Context context) {
        this.productModels = productModels;
        this.context = context;
    }

    @NonNull
    @Override
    public ShoppingCartsProductAdapter.ShoppingCartsProductAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ShoppingCartsProductAdapter.ShoppingCartsProductAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.order_product_layout, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ShoppingCartsProductAdapter.ShoppingCartsProductAdapterViewHolder holder, int position) {
        ShoppingCartsProductModel productModel = productModels.get(position);
        Glide.with(context).load(productModel.getImageURL().get(0)).into(holder.binding.productImage);
        holder.binding.productName.setText(productModel.getProductName());
        holder.binding.oneProductPrice.setText(productModel.getDefaultQuantity()+" '(item) * ₹" + productModel.getPrice()+" =");
        holder.binding.TotalProductPrice.setText("Total ₹"+productModel.getPrice()*productModel.getDefaultQuantity());
//        holder.binding.getRoot().setOnClickListener(view -> orderProductInterface.onOrder(ShoppingCartsProductModel));
    }

    @Override
    public int getItemCount() {
        return productModels.size();
    }

    public static class ShoppingCartsProductAdapterViewHolder extends RecyclerView.ViewHolder {
        OrderProductLayoutBinding binding;
        public ShoppingCartsProductAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = OrderProductLayoutBinding.bind(itemView);
        }
    }
}
