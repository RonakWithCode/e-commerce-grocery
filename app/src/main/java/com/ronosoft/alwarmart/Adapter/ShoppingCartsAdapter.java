package com.ronosoft.alwarmart.Adapter;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.ronosoft.alwarmart.Manager.ProductManager;
import com.ronosoft.alwarmart.Model.ShoppingCartsProductModel;
import com.ronosoft.alwarmart.R;
import com.ronosoft.alwarmart.Services.AuthService;
import com.ronosoft.alwarmart.databinding.ProductCartViewBinding;
import com.ronosoft.alwarmart.interfaceClass.ShoppingCartsInterface;

import java.util.ArrayList;

public class ShoppingCartsAdapter extends RecyclerView.Adapter<ShoppingCartsAdapter.ShoppingCartsAdapterViewHolder> {
    ArrayList<ShoppingCartsProductModel> productModels;
    ShoppingCartsInterface shoppingCartsInterface;
    Context context;
    ProductManager productManager;
    public ShoppingCartsAdapter(ArrayList<ShoppingCartsProductModel> productModels, ShoppingCartsInterface shoppingCartsInterface, Context context) {
        this.productModels = productModels;
        this.shoppingCartsInterface = shoppingCartsInterface;
        this.context = context;
        productManager = new ProductManager(context);

    }

    @NonNull
    @Override
    public ShoppingCartsAdapter.ShoppingCartsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ShoppingCartsAdapter.ShoppingCartsAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.product_cart_view, parent, false));

    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull ShoppingCartsAdapter.ShoppingCartsAdapterViewHolder holder, int position) {
        ShoppingCartsProductModel model = productModels.get(position);
        
        // Set product name
        holder.binding.productName.setText(model.getProductName());
        
        // Load image with rounded corners
        Glide.with(context)
            .load(model.getProductImage().get(0))
            .transform(new RoundedCorners(context.getResources()
                .getDimensionPixelSize(R.dimen.image_corner_radius)))
            .placeholder(R.drawable.product_image_shimmer_effect)
            .error(R.drawable.placeholder)
            .into(holder.binding.productImage);

        // Set price with currency symbol
        holder.binding.productPrice.setText("₹" + String.format("%.2f", model.getPrice()));
        
        // Set MRP if available
        if (model.getMrp() > model.getPrice()) {
            holder.binding.productMrp.setVisibility(View.VISIBLE);
            holder.binding.productMrp.setText("₹" + String.format("%.2f", model.getMrp()));
        } else {
            holder.binding.productMrp.setVisibility(View.GONE);
        }

        // Set quantity
        holder.binding.productQty.setText(String.valueOf(model.getSelectableQuantity()));

        // Handle quantity changes with animations
        holder.binding.productQtyUp.setOnClickListener(v -> {
            int currentQty = model.getSelectableQuantity();
            int newQty = currentQty + 1;
            if (newQty <= model.getMaxSelectableQuantity()) {
                animateQuantityChange(holder.binding.productQty, newQty);
                UpdateQTY(model.getProductId(), newQty);
            } else {
                showMaxQuantityToast();
            }
        });

        holder.binding.productQtyDown.setOnClickListener(v -> {
            int currentQty = model.getSelectableQuantity();
            int newQty = currentQty - 1;
            if (newQty >= model.getMinSelectableQuantity()) {
                animateQuantityChange(holder.binding.productQty, newQty);
                UpdateQTY(model.getProductId(), newQty);
            } else {
                showRemoveItemDialog(position, model);
            }
        });
    }

    private void animateQuantityChange(TextView quantityView, int newValue) {
        ValueAnimator animator = ValueAnimator.ofInt(
            Integer.parseInt(quantityView.getText().toString()), 
            newValue
        );
        animator.setDuration(200);
        animator.addUpdateListener(animation -> 
            quantityView.setText(String.valueOf(animation.getAnimatedValue()))
        );
        animator.start();
    }

    private void showMaxQuantityToast() {
        Toast.makeText(context, 
            "Maximum quantity reached", 
            Toast.LENGTH_SHORT
        ).show();
    }

    private void showRemoveItemDialog(int position, ShoppingCartsProductModel model) {
        shoppingCartsInterface.remove(position, model.getProductId(), model);

    }

    @Override
    public int getItemCount() {
        return productModels.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    private void UpdateQTY(String productId, int quantity) {
        productManager.UpdateCartQuantityById(
                new AuthService().getUserId(),
                productId,
                quantity
        );
    }

    public static class ShoppingCartsAdapterViewHolder extends RecyclerView.ViewHolder {
        ProductCartViewBinding binding;
        public ShoppingCartsAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ProductCartViewBinding.bind(itemView);
        }
    }
}