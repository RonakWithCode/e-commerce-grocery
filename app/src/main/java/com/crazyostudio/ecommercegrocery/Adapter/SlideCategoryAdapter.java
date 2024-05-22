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
import com.crazyostudio.ecommercegrocery.databinding.SliderCategoryBinding;
import com.crazyostudio.ecommercegrocery.interfaceClass.CategoryAdapterInterface;

import java.util.ArrayList;


public class SlideCategoryAdapter extends RecyclerView.Adapter<SlideCategoryAdapter.SlideCategoryAdapterViewHolder>{
    ArrayList<ProductCategoryModel> categoryModels;
    Context context;
    CategoryAdapterInterface onclick;
    private int selectedPosition = RecyclerView.NO_POSITION;


    public SlideCategoryAdapter(ArrayList<ProductCategoryModel> categoryModels, Context context, CategoryAdapterInterface onclick) {
        this.categoryModels = categoryModels;
        this.context = context;
        this.onclick = onclick;
    }

    @NonNull
    @Override
    public SlideCategoryAdapter.SlideCategoryAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SlideCategoryAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.slider_category, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull SlideCategoryAdapter.SlideCategoryAdapterViewHolder holder, int position) {
//        holder.binding.line.setVisibility(View.INVISIBLE);
        ProductCategoryModel model  = categoryModels.get(position);
        holder.binding.title.setText(model.getTag());
        Glide.with(context).load(model.getImageUri()).placeholder(R.drawable.skeleton_shape).into(holder.binding.imageView1);

        holder.itemView.setSelected(selectedPosition == position);


        holder.binding.imageView1.setOnClickListener(v -> {
//            holder.binding.line.setVisibility(View.VISIBLE);
            onclick.onClick(model);
            notifyItemChanged(selectedPosition);
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(selectedPosition);
        });
    }

    @Override
    public int getItemCount() {
        return categoryModels.size();
    }

    public static class SlideCategoryAdapterViewHolder extends RecyclerView.ViewHolder {
        SliderCategoryBinding binding;
        public SlideCategoryAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SliderCategoryBinding.bind(itemView);
        }
    }
}
