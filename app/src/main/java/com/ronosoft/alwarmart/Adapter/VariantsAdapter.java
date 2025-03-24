package com.ronosoft.alwarmart.Adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.ronosoft.alwarmart.Model.ProductModel;
import com.ronosoft.alwarmart.Model.Variations;
import com.ronosoft.alwarmart.R;
import com.ronosoft.alwarmart.Services.DatabaseService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VariantsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public interface VariantsCallback {
        void Product(String id);
    }

    private static final int VIEW_TYPE_GROCERY = 0;
    private static final int VIEW_TYPE_FASHION = 1;

    private final Context context;
    private final List<Variations> variationsList;
    private final VariantsCallback callback;
    private final String Id;
    private final String productType;

    public VariantsAdapter(Context context, List<Variations> variationsList, String id, VariantsCallback callback, String productType) {
        this.context = context;
        this.variationsList = variationsList;
        this.Id = id;
        this.callback = callback;
        this.productType = productType;
    }

    @Override
    public int getItemViewType(int position) {
        if (productType == null || productType.equals(ProductModel.LAYOUT_TYPE_GROCERY)) {
            return VIEW_TYPE_GROCERY;
        } else {
            return VIEW_TYPE_FASHION;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_GROCERY) {
            View view = LayoutInflater.from(context).inflate(R.layout.list_item_variant, parent, false);
            return new GroceryViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.list_item_variant_fashion, parent, false);
            return new FashionViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Variations variation = variationsList.get(position);

        if (holder instanceof GroceryViewHolder) {
            bindGroceryViewHolder((GroceryViewHolder) holder, variation, position);
        } else if (holder instanceof FashionViewHolder) {
            bindFashionViewHolder((FashionViewHolder) holder, variation, position);
        }
    }

    private void bindGroceryViewHolder(GroceryViewHolder holder, Variations variation, int position) {
        // Display weight/size information
        String weightInfo = variation.getWeight() != null ? variation.getWeight() : "";
        if (variation.getWeightSIUnit() != null && !variation.getWeightSIUnit().isEmpty()) {
            weightInfo += " " + variation.getWeightSIUnit();
        }


        holder.variantName.setText(weightInfo);
        
        // Set loading state for price
        holder.variantWeight.setText("₹"+variation.getName());
        


        // Handle click
        holder.itemView.setOnClickListener(view -> {
            String varId = variation.getId();
            if (varId != null && !varId.equals(Id)) {
                callback.Product(varId);
            }
        });

        // Highlight selected variant
        String varId = variation.getId();
        if (varId.equals(Id)) {
            holder.itemView.setBackgroundResource(R.drawable.selected_border_background);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.border_background);
        }
    }
    

    private void bindFashionViewHolder(FashionViewHolder holder, Variations variation, int position) {
        // Set size (for shoes/clothes)
        String sizeText = variation.getWeight() != null ? variation.getWeight() : "";
        if (productType.equals("Cloth")) {
            if (!variation.getWeightSIUnit().isEmpty()) {
                sizeText =  variation.getWeightSIUnit();
            }
        }
        else {

        if (variation.getWeightSIUnit() != null && !variation.getWeightSIUnit().isEmpty()) {
            sizeText += " " + variation.getWeightSIUnit();
        }
        }


        holder.variantSize.setText(sizeText);

        // Set name/color
        holder.variantName.setText(variation.getName() != null ? "₹ "+variation.getName() : "click to check price");
        
        // Set loading state for price
        holder.variantPrice.setVisibility(View.GONE);
        holder.variantMrp.setVisibility(View.GONE);

        // Load image if available
        if (variation.getImage() != null && !variation.getImage().isEmpty()) {
            Glide.with(context)
                    .load(variation.getImage())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.product_image_shimmee_effect) // Error image if loading fails
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) // Resize the image
                    .centerCrop() // Scale type for resizing
                    .into(holder.variantImage);
        } else {
            holder.variantImage.setImageResource(R.drawable.placeholder);
        }
        
        // Handle click
        holder.itemView.setOnClickListener(view -> {
            String varId = variation.getId();
            if (varId != null && !varId.equals(Id)) {
                callback.Product(varId);
            }
        });
        
        // Highlight selected variant
        MaterialCardView cardView = (MaterialCardView) holder.itemView;
        String varId = variation.getId();
        if (varId != null && varId.equals(Id)) {
            cardView.setStrokeColor(context.getResources().getColor(R.color.green_primary));
            cardView.setStrokeWidth(context.getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._2sdp));
        } else {
            cardView.setStrokeColor(context.getResources().getColor(R.color.gray));
            cardView.setStrokeWidth(context.getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._1sdp));
        }
    }
    

    @Override
    public int getItemCount() {
        return variationsList.size();
    }

    static class GroceryViewHolder extends RecyclerView.ViewHolder {
        TextView variantName;
        TextView variantWeight;

        GroceryViewHolder(View itemView) {
            super(itemView);
            variantName = itemView.findViewById(R.id.variation_name);
            variantWeight = itemView.findViewById(R.id.variation_weight);
        }
    }

    static class FashionViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView variantImage;
        TextView variantSize;
        TextView variantName;
        TextView variantPrice;
        TextView variantMrp;

        FashionViewHolder(View itemView) {
            super(itemView);
            variantImage = itemView.findViewById(R.id.variant_image);
            variantSize = itemView.findViewById(R.id.variant_size);
            variantName = itemView.findViewById(R.id.variant_name);
            variantPrice = itemView.findViewById(R.id.variant_price);
            variantMrp = itemView.findViewById(R.id.variant_mrp);
        }
    }
}
