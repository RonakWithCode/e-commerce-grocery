package com.ronosoft.alwarmart.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ronosoft.alwarmart.Model.ProductCategoryModel;
import com.ronosoft.alwarmart.R;
import com.ronosoft.alwarmart.databinding.SliderCategoryBinding;
import com.ronosoft.alwarmart.interfaceClass.CategoryAdapterInterface;

import java.util.ArrayList;

public class SlideCategoryAdapter extends RecyclerView.Adapter<SlideCategoryAdapter.SlideCategoryAdapterViewHolder> {

    private final ArrayList<ProductCategoryModel> categoryModels;
    private final Context context;
    private final CategoryAdapterInterface onClick;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public SlideCategoryAdapter(ArrayList<ProductCategoryModel> categoryModels,
                                Context context,
                                CategoryAdapterInterface onClick) {
        this.categoryModels = categoryModels;
        this.context = context;
        this.onClick = onClick;
    }

    @NonNull
    @Override
    public SlideCategoryAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SliderCategoryBinding binding = SliderCategoryBinding.inflate(
                LayoutInflater.from(context), parent, false);
        return new SlideCategoryAdapterViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SlideCategoryAdapterViewHolder holder, int position) {
        ProductCategoryModel model = categoryModels.get(position);

        // Set the title safely (use default if null)
        String tag = (model.getTag() != null && !model.getTag().isEmpty()) ? model.getTag() : "N/A";
        holder.binding.title.setText(tag);

        // Load image using Glide with error handling
        try {
            String imageUri = model.getImageUri();
            if (imageUri != null && !imageUri.isEmpty()) {
                Glide.with(context)
                        .load(imageUri)
                        .placeholder(R.drawable.skeleton_shape)
                        .error(R.drawable.ic_error)
                        .into(holder.binding.imageView1);
            } else {
                holder.binding.imageView1.setImageResource(R.drawable.ic_error);
            }
        } catch (Exception e) {
            e.printStackTrace();
            holder.binding.imageView1.setImageResource(R.drawable.ic_error);
        }

        // Update selected state visually
        holder.itemView.setSelected(selectedPosition == position);

        // Set click listener to update selection and callback
        holder.itemView.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition == RecyclerView.NO_POSITION) return;
            if (selectedPosition != currentPosition) {
                int previousPosition = selectedPosition;
                selectedPosition = currentPosition;
                if (previousPosition != RecyclerView.NO_POSITION) {
                    notifyItemChanged(previousPosition);
                }
                notifyItemChanged(selectedPosition);
                onClick.onClick(model);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryModels.size();
    }

    public static class SlideCategoryAdapterViewHolder extends RecyclerView.ViewHolder {
        final SliderCategoryBinding binding;

        public SlideCategoryAdapterViewHolder(@NonNull SliderCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
