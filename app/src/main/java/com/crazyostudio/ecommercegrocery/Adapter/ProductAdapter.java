package com.crazyostudio.ecommercegrocery.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.crazyostudio.ecommercegrocery.Model.ProductModel;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.databinding.ProductboxviewBinding;
import com.crazyostudio.ecommercegrocery.databinding.RecommendationsViewBinding;
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
    public void setData(ArrayList<ProductModel> newData) {
        this.productModels.clear();
        this.productModels.addAll(newData);
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
        return new ProductAdapter.ProductAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.recommendations_view, parent, false));

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ProductAdapterViewHolder holder, int position) {
        ProductModel product = productModels.get(position);
//        if (layout.equals("Main")) {
////            holder.binding.getRoot().setMaxWidth(match_parent ConstraintLayout);
//            ViewGroup.LayoutParams params =  holder.binding.getRoot().getLayoutParams();
//            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
//            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//            holder.binding.getRoot().setLayoutParams(params);
//        }

        holder.binding.productName.setText(product.getProductName());
        holder.binding.productPrice.setText("₹" + product.getPrice());
        holder.binding.productMRP.setText("₹" + product.getMrp());
        holder.binding.productMRP.setPaintFlags(holder.binding.productMRP.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.binding.getRoot().setOnClickListener(onclick-> onClickProductAdapter.onClick(product));
        Glide.with(context)
                .load(product.getImageURL().get(0))
                .placeholder(R.drawable.product_image_shimmee_effect)
                .into(holder.binding.productImage);



        Glide.with(context)
                .load(product.getImageURL().get(0))
                .placeholder(R.drawable.product_image_shimmee_effect) // Placeholder image while loading
                .error(R.drawable.product_image_shimmee_effect) // Error image if loading fails
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) // Resize the image
                .centerCrop() // Scale type for resizing
                .into(holder.binding.productImage);
    }

    @Override
    public int getItemCount() {
        return productModels.size();
    }

    public static class ProductAdapterViewHolder extends RecyclerView.ViewHolder {
//        recommendations_view
        RecommendationsViewBinding binding;
        public ProductAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RecommendationsViewBinding.bind(itemView);
        }
    }
}
