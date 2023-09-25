package com.crazyostudio.ecommercegrocery.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.databinding.ProductDisplayImagesBinding;

import java.util.ArrayList;

public class ProductDisplayImagesAdapter extends RecyclerView.Adapter<ProductDisplayImagesAdapter.ProductDisplayImagesAdapterViewHolder>{
    ArrayList<String> uris;
    Context context;

    public ProductDisplayImagesAdapter(ArrayList<String> uris, Context context) {
        this.uris = uris;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductDisplayImagesAdapter.ProductDisplayImagesAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductDisplayImagesAdapter.ProductDisplayImagesAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.product_display_images, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull ProductDisplayImagesAdapter.ProductDisplayImagesAdapterViewHolder holder, int position) {
        String image = uris.get(position);
        Glide.with(context)
                .load(image).placeholder(R.drawable.placeholder)
                .into(holder.binding.ProductImages);



    }

    @Override
    public int getItemCount() {
        return uris.size();
    }

    public static class ProductDisplayImagesAdapterViewHolder extends RecyclerView.ViewHolder {
        ProductDisplayImagesBinding binding;
        public ProductDisplayImagesAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ProductDisplayImagesBinding.bind(itemView);
        }
    }
}
