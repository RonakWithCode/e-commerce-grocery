package com.ronosoft.alwarmart.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.ronosoft.alwarmart.Model.HomeProductModel;
import com.ronosoft.alwarmart.R;
import com.ronosoft.alwarmart.databinding.HomeCategoryLayoutBinding;
import com.ronosoft.alwarmart.interfaceClass.HomeCategoryInterface;

import java.util.ArrayList;

public class HomeCategoryAdapter extends RecyclerView.Adapter<HomeCategoryAdapter.HomeCategoryAdapterViewHolder>{

    ArrayList<HomeProductModel> models;
    Context context;
    HomeCategoryInterface homeCategoryInterface;


    public HomeCategoryAdapter(ArrayList<HomeProductModel> models, Context context, HomeCategoryInterface homeCategoryInterface) {
        this.models = models;
        this.context = context;
        this.homeCategoryInterface = homeCategoryInterface;
    }

    @NonNull
    @Override
    public HomeCategoryAdapter.HomeCategoryAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HomeCategoryAdapter.HomeCategoryAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.home_category_layout, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull HomeCategoryAdapter.HomeCategoryAdapterViewHolder holder, int position) {
        HomeProductModel homeCategoryModel = models.get(position);

        holder.binding.ProductName.setText(homeCategoryModel.getTitle());
        holder.binding.ProductSize.setText(homeCategoryModel.getProduct().size() + " products");
        holder.binding.Text1.setText("+" + homeCategoryModel.getProduct().size());

        int productSize = homeCategoryModel.getProduct().size();

        if (productSize > 0) {
            Glide.with(context).load(homeCategoryModel.getProduct().get(0).getProductImage().get(0))
                    .placeholder(R.drawable.product_image_shimmee_effect) // Placeholder image while loading
                    .error(R.drawable.product_image_shimmee_effect) // Error image if loading fails
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) // Resize the image
                    .centerCrop() // Scale type for resizing
                    .into(holder.binding.View1);
        }
        if (productSize > 1) {
            Glide.with(context).load(homeCategoryModel.getProduct().get(1).getProductImage().get(0))
                    .placeholder(R.drawable.product_image_shimmee_effect) // Placeholder image while loading
                    .error(R.drawable.product_image_shimmee_effect) // Error image if loading fails
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) // Resize the image
                    .centerCrop() // Scale type for resizing
                    .into(holder.binding.View2); // Fix: Using index 0 for the second product's image URL
        }
        if (productSize > 2) {
            Glide.with(context).load(homeCategoryModel.getProduct().get(2).getProductImage().get(0))
                    .placeholder(R.drawable.product_image_shimmee_effect) // Placeholder image while loading
                    .error(R.drawable.product_image_shimmee_effect) // Error image if loading fails
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) // Resize the image
                    .centerCrop() // Scale type for resizing
                    .into(holder.binding.View3); // Fix: Using index 0 for the third product's image URL
        }
        holder.binding.getRoot().setOnClickListener(v -> homeCategoryInterface.onClick(homeCategoryModel));
        holder.binding.seeAllButton.setOnClickListener(v -> homeCategoryInterface.onClick(homeCategoryModel));
        holder.binding.View1.setOnClickListener(v -> homeCategoryInterface.onClick(homeCategoryModel));
        holder.binding.View2.setOnClickListener(v -> homeCategoryInterface.onClick(homeCategoryModel));
        holder.binding.View3.setOnClickListener(v -> homeCategoryInterface.onClick(homeCategoryModel));
        holder.binding.Text1.setOnClickListener(v -> homeCategoryInterface.onClick(homeCategoryModel));
        holder.binding.ProductSize.setOnClickListener(v -> homeCategoryInterface.onClick(homeCategoryModel));
        holder.binding.ProductName.setOnClickListener(v -> homeCategoryInterface.onClick(homeCategoryModel));

    }





    @Override
    public int getItemCount() {
        return models.size();
    }

    public static class HomeCategoryAdapterViewHolder extends RecyclerView.ViewHolder {
        HomeCategoryLayoutBinding binding;
        public HomeCategoryAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = HomeCategoryLayoutBinding.bind(itemView);
        }
    }
}
