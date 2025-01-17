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
    private final ArrayList<ProductCategoryModel> categoryModels;
    private final Context context;
    private final CategoryAdapterInterface onclick;
    private int selectedPosition = 0;

    public SlideCategoryAdapter(ArrayList<ProductCategoryModel> categoryModels, 
                              Context context, 
                              CategoryAdapterInterface onclick) {
        this.categoryModels = categoryModels;
        this.context = context;
        this.onclick = onclick;
    }

    @NonNull
    @Override
    public SlideCategoryAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.slider_category, parent, false);
        return new SlideCategoryAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SlideCategoryAdapterViewHolder holder, int position) {
        ProductCategoryModel model = categoryModels.get(position);
        holder.binding.title.setText(model.getTag());
        
        // Load image with error handling
        Glide.with(context)
            .load(model.getImageUri())
            .placeholder(R.drawable.skeleton_shape)
            .error(R.drawable.ic_error)
            .into(holder.binding.imageView1);

        // Update selected state
        holder.itemView.setSelected(selectedPosition == position);

        holder.itemView.setOnClickListener(v -> {
            if (selectedPosition != holder.getAdapterPosition()) {
                int previousPosition = selectedPosition;
                selectedPosition = holder.getAdapterPosition();
                notifyItemChanged(previousPosition);
                notifyItemChanged(selectedPosition);
                onclick.onClick(model);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryModels.size();
    }

    static class SlideCategoryAdapterViewHolder extends RecyclerView.ViewHolder {
        final SliderCategoryBinding binding;

        SlideCategoryAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SliderCategoryBinding.bind(itemView);
        }
    }
}
