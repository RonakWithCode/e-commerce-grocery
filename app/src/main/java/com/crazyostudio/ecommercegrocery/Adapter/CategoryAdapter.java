package com.crazyostudio.ecommercegrocery.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.crazyostudio.ecommercegrocery.Model.ProductCategoryModel;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.databinding.CategorylayoutBinding;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryAdapterViewHolder>{

    ArrayList<ProductCategoryModel> categoryModel;
    Context context;

    public CategoryAdapter(ArrayList<ProductCategoryModel> categoryModel, Context context) {
        this.categoryModel = categoryModel;
        this.context = context;
    }

    @NonNull
    @Override
    public CategoryAdapter.CategoryAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryAdapter.CategoryAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.categorylayout, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.CategoryAdapterViewHolder holder, int position) {
        ProductCategoryModel model = categoryModel.get(position);

        holder.binding.CategoryTitle.setText(model.getTag());
        Glide
                .with(context)
                .load(model.getImageUri())
                .placeholder(R.drawable.spinner_gif)
                .into(holder.binding.CategoryImage);

    }

    @Override
    public int getItemCount() {
        return categoryModel.size();
    }

    public static class CategoryAdapterViewHolder extends RecyclerView.ViewHolder {
        CategorylayoutBinding binding;
        public CategoryAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = CategorylayoutBinding.bind(itemView);
        }
    }
}
