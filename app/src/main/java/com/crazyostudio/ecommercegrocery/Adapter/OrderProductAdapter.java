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
import com.crazyostudio.ecommercegrocery.databinding.OrderProductLayoutBinding;
import com.crazyostudio.ecommercegrocery.interfaceClass.OrderProductInterface;

import java.util.ArrayList;

public class OrderProductAdapter extends RecyclerView.Adapter<OrderProductAdapter.OrderProductAdapterViewHolder>{
    ArrayList<ProductModel> productModels;
    Context context;
    OrderProductInterface orderProductInterface;

    public OrderProductAdapter(ArrayList<ProductModel> productModels, Context context, OrderProductInterface orderProductInterface) {
        this.productModels = productModels;
        this.context = context;
        this.orderProductInterface = orderProductInterface;
    }

    @NonNull
    @Override
    public OrderProductAdapter.OrderProductAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OrderProductAdapter.OrderProductAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.order_product_layout, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull OrderProductAdapter.OrderProductAdapterViewHolder holder, int position) {
        ProductModel productModel = productModels.get(position);
        Glide.with(context).load(productModel.getProductImages().get(0)).into(holder.binding.productImage);
        holder.binding.productName.setText(productModel.getItemName());
        holder.binding.quantity.setText(productModel.getSelectProductQuantity()+" '(item)");
        holder.binding.productPrice.setText("₹"+productModel.getPrice()*productModel.getSelectProductQuantity());
        holder.binding.getRoot().setOnClickListener(view -> orderProductInterface.onOrder(productModel));
    }

    @Override
    public int getItemCount() {
        return productModels.size();
    }

    public static class OrderProductAdapterViewHolder extends RecyclerView.ViewHolder {
        OrderProductLayoutBinding binding;
        public OrderProductAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = OrderProductLayoutBinding.bind(itemView);
        }
    }
}
