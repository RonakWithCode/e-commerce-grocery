package com.crazyostudio.ecommercegrocery.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.crazyostudio.ecommercegrocery.Model.ProductModel;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.databinding.ProductboxviewBinding;
import com.crazyostudio.ecommercegrocery.interfaceClass.onClickProductAdapter;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductAdapterViewHolder>{
    ArrayList<ProductModel> productModels;
    onClickProductAdapter onClickProductAdapter;
    Context context;
    String layout;
    //    productboxview
    public void setFilerList(ArrayList<ProductModel> FilterModels){
        this.productModels = FilterModels;
        notifyDataSetChanged();
    }
    public ProductAdapter(ArrayList<ProductModel> productModels, com.crazyostudio.ecommercegrocery.interfaceClass.onClickProductAdapter onClickProductAdapter, Context context,String layout) {
        this.productModels = productModels;
        this.onClickProductAdapter = onClickProductAdapter;
        this.context = context;
        this.layout = layout;
    }

    @NonNull
    @Override
    public ProductAdapter.ProductAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductAdapter.ProductAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.productboxview, parent, false));

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ProductAdapterViewHolder holder, int position) {
        ProductModel product = productModels.get(position);
        if (layout.equals("Main")) {
//            holder.binding.getRoot().setMaxWidth(match_parent ConstraintLayout);
            ViewGroup.LayoutParams params =  holder.binding.getRoot().getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            holder.binding.getRoot().setLayoutParams(params);
        }
        Glide.with(context)
                .load(product.getImageURL().get(0))
                .into(holder.binding.ProductImage);
        holder.binding.label.setText(product.getProductName());
        holder.binding.price.setText("₹" + product.getPrice());
        holder.binding.getRoot().setOnClickListener(onclick-> onClickProductAdapter.onClick(product));
    }

    @Override
    public int getItemCount() {
        return productModels.size();
    }

    public static class ProductAdapterViewHolder extends RecyclerView.ViewHolder {
        ProductboxviewBinding binding;
        public ProductAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ProductboxviewBinding.bind(itemView);
        }
    }
}
