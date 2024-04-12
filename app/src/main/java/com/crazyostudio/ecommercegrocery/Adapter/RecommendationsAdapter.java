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
import com.crazyostudio.ecommercegrocery.databinding.RecommendationsViewBinding;
import com.crazyostudio.ecommercegrocery.interfaceClass.RecommendationsInterface;

import java.util.ArrayList;

public class RecommendationsAdapter extends RecyclerView.Adapter<RecommendationsAdapter.RecommendationsAdapterViewHolder>{
    ArrayList<ProductModel> productModels;
    Context context;
    RecommendationsInterface recommendationsInterface;


    public RecommendationsAdapter(ArrayList<ProductModel> productModels,RecommendationsInterface onClickProductAdapter, Context context) {
        this.productModels = productModels;
        this.recommendationsInterface = onClickProductAdapter;
        this.context = context;
    }

    @NonNull
    @Override
    public RecommendationsAdapter.RecommendationsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecommendationsAdapter.RecommendationsAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.recommendations_view, parent, false));

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecommendationsAdapter.RecommendationsAdapterViewHolder holder, int position) {
        ProductModel product = productModels.get(position);
        Glide.with(context)
                .load(product.getImageURL().get(0))
                .into(holder.binding.productImage);
        holder.binding.productName.setText(product.getProductName());
        holder.binding.productPrice.setText("₹" + product.getPrice());
        holder.binding.getRoot().setOnClickListener(onclick-> recommendationsInterface.addProduct(product));
    }

    @Override
    public int getItemCount() {
        return productModels.size();
    }

    public static class RecommendationsAdapterViewHolder extends RecyclerView.ViewHolder {
        RecommendationsViewBinding binding;
        public RecommendationsAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RecommendationsViewBinding.bind(itemView);
        }
    }
}
