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
import java.util.Random;

public class HomeCategoryAdapter extends RecyclerView.Adapter<HomeCategoryAdapter.HomeCategoryAdapterViewHolder> {

    private ArrayList<HomeProductModel> models;
    private Context context;
    private HomeCategoryInterface homeCategoryInterface;
    private Random random; // Added Random object

    public HomeCategoryAdapter(ArrayList<HomeProductModel> models, Context context, HomeCategoryInterface homeCategoryInterface) {
        this.models = models;
        this.context = context;
        this.homeCategoryInterface = homeCategoryInterface;
        this.random = new Random(); // Initialize Random object
    }

    @NonNull
    @Override
    public HomeCategoryAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_category_layout, parent, false);
        return new HomeCategoryAdapterViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull HomeCategoryAdapterViewHolder holder, int position) {
        HomeProductModel homeCategoryModel = models.get(position);
        holder.binding.ProductName.setText(homeCategoryModel.getTitle());

        // Get actual product list size and add random number between 10 and 29
        int actualSize = 0;
        if (homeCategoryModel.getProduct() != null) {
            actualSize = homeCategoryModel.getProduct().size();
        }
        int randomAddition = random.nextInt(20) + 10; // Generates number between 10 and 29 (0-19 + 10)
        int productSize = actualSize + randomAddition;

        holder.binding.ProductSize.setText(productSize + " products");
        holder.binding.Text1.setText("+" + productSize);

        // Load images for first three products if available.
        try {
            if (actualSize > 0) {  // Use actualSize here since we're only displaying real products
                if (homeCategoryModel.getProduct().get(0) != null &&
                        homeCategoryModel.getProduct().get(0).getProductImage() != null &&
                        !homeCategoryModel.getProduct().get(0).getProductImage().isEmpty()) {
                    Glide.with(context)
                            .load(homeCategoryModel.getProduct().get(0).getProductImage().get(0))
                            .placeholder(R.drawable.product_image_shimmee_effect)
                            .error(R.drawable.product_image_shimmee_effect)
                            .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                            .centerCrop()
                            .into(holder.binding.View1);
                }
            }
            if (actualSize > 1) {
                if (homeCategoryModel.getProduct().get(1) != null &&
                        homeCategoryModel.getProduct().get(1).getProductImage() != null &&
                        !homeCategoryModel.getProduct().get(1).getProductImage().isEmpty()) {
                    Glide.with(context)
                            .load(homeCategoryModel.getProduct().get(1).getProductImage().get(0))
                            .placeholder(R.drawable.product_image_shimmee_effect)
                            .error(R.drawable.product_image_shimmee_effect)
                            .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                            .centerCrop()
                            .into(holder.binding.View2);
                }
            }
            if (actualSize > 2) {
                if (homeCategoryModel.getProduct().get(2) != null &&
                        homeCategoryModel.getProduct().get(2).getProductImage() != null &&
                        !homeCategoryModel.getProduct().get(2).getProductImage().isEmpty()) {
                    Glide.with(context)
                            .load(homeCategoryModel.getProduct().get(2).getProductImage().get(0))
                            .placeholder(R.drawable.product_image_shimmee_effect)
                            .error(R.drawable.product_image_shimmee_effect)
                            .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                            .centerCrop()
                            .into(holder.binding.View3);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Set click listeners on multiple views to trigger the interface callback.
        View.OnClickListener clickListener = v -> homeCategoryInterface.onClick(homeCategoryModel);
        holder.binding.getRoot().setOnClickListener(clickListener);
        holder.binding.seeAllButton.setOnClickListener(clickListener);
        holder.binding.View1.setOnClickListener(clickListener);
        holder.binding.View2.setOnClickListener(clickListener);
        holder.binding.View3.setOnClickListener(clickListener);
        holder.binding.Text1.setOnClickListener(clickListener);
        holder.binding.ProductSize.setOnClickListener(clickListener);
        holder.binding.ProductName.setOnClickListener(clickListener);
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