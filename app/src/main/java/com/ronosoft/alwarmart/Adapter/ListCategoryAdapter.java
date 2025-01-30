package com.ronosoft.alwarmart.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ronosoft.alwarmart.Model.ProductCategoryModel;
import com.ronosoft.alwarmart.R;
import com.ronosoft.alwarmart.databinding.CategorylayoutBinding;
import com.ronosoft.alwarmart.databinding.ListCategorylayoutBinding;
import com.ronosoft.alwarmart.interfaceClass.CategoryAdapterInterface;

import java.util.ArrayList;

public class ListCategoryAdapter extends RecyclerView.Adapter<ListCategoryAdapter.ListCategoryAdapterViewHolder>{

    ArrayList<ProductCategoryModel> categoryModel;
    Context context;
    CategoryAdapterInterface onclick;

    public ListCategoryAdapter(ArrayList<ProductCategoryModel> categoryModel, Context context,CategoryAdapterInterface Onclick) {
        this.categoryModel = categoryModel;
        this.context = context;
        this.onclick = Onclick;
    }

    @NonNull
    @Override
    public ListCategoryAdapter.ListCategoryAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ListCategoryAdapter.ListCategoryAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.list_categorylayout, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull ListCategoryAdapter.ListCategoryAdapterViewHolder holder, int position) {
        ProductCategoryModel model = categoryModel.get(position);

        holder.binding.CategoryTitle.setText(model.getTag());
        Glide
                .with(context)
                .load(model.getImageUri())
                .placeholder(R.drawable.spinner_gif)
                .into(holder.binding.categoryImage);
        holder.binding.getRoot().setOnClickListener(view -> onclick.onClick(model));

    }

    @Override
    public int getItemCount() {
        return categoryModel.size();
    }

    public static class ListCategoryAdapterViewHolder extends RecyclerView.ViewHolder {
        ListCategorylayoutBinding binding;
        public ListCategoryAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ListCategorylayoutBinding.bind(itemView);
        }
    }
}
