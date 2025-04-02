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
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.ronosoft.alwarmart.Manager.ProductManager;
import com.ronosoft.alwarmart.Model.ShoppingCartsProductModel;
import com.ronosoft.alwarmart.R;
import com.ronosoft.alwarmart.Services.AuthService;
import com.ronosoft.alwarmart.databinding.ProductCartViewBinding;
import com.ronosoft.alwarmart.interfaceClass.ShoppingCartsInterface;

import java.util.ArrayList;

public class ShoppingCartsAdapter extends RecyclerView.Adapter<ShoppingCartsAdapter.ShoppingCartsAdapterViewHolder> {
    private ArrayList<ShoppingCartsProductModel> productModels;
    private final ShoppingCartsInterface shoppingCartsInterface;
    private final Context context;
    private final ProductManager productManager;

    public ShoppingCartsAdapter(ArrayList<ShoppingCartsProductModel> productModels,
                                ShoppingCartsInterface shoppingCartsInterface,
                                Context context) {
        this.productModels = productModels;
        this.shoppingCartsInterface = shoppingCartsInterface;
        this.context = context;
        this.productManager = new ProductManager(context);
    }

    @NonNull
    @Override
    public ShoppingCartsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ShoppingCartsAdapterViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.product_cart_view, parent, false));
    }

    @SuppressLint({"SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull ShoppingCartsAdapterViewHolder holder, int position) {
        ShoppingCartsProductModel model = productModels.get(position);

        holder.binding.productName.setText(model.getProductName());

        Glide.with(context)
                .load(model.getProductImage().get(0))
                .thumbnail(0.1f)
                .transform(new RoundedCorners(context.getResources().getDimensionPixelSize(R.dimen.image_corner_radius)))
                .placeholder(R.drawable.product_image_shimmer_effect)
                .error(R.drawable.placeholder)
                .into(holder.binding.productImage);

        holder.binding.productPrice.setText("₹" + String.format("%.2f", model.getPrice()));

        if (model.getMrp() > model.getPrice()) {
            holder.binding.productMrp.setVisibility(View.VISIBLE);
            holder.binding.productMrp.setText("₹" + String.format("%.2f", model.getMrp()));
        } else {
            holder.binding.productMrp.setVisibility(View.GONE);
        }

        holder.binding.productQty.setText(String.valueOf(model.getSelectableQuantity()));

        holder.binding.productQtyUp.setOnClickListener(v -> {
            int currentQty = model.getSelectableQuantity();
            int newQty = currentQty + 1;
            if (newQty <= model.getMaxSelectableQuantity()) {
                animateQuantityChange(holder.binding.productQty, newQty);
                model.setSelectableQuantity(newQty);
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
                model.setSelectableQuantity(newQty);
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
        Toast.makeText(context, "Maximum quantity reached", Toast.LENGTH_SHORT).show();
    }

    private void showRemoveItemDialog(int position, ShoppingCartsProductModel model) {
        shoppingCartsInterface.remove(position, model.getProductId(), model);
    }

    private void UpdateQTY(String productId, int quantity) {
        productManager.UpdateCartQuantityById(new AuthService().getUserId(), productId, quantity);
    }

    @Override
    public int getItemCount() {
        return productModels.size();
    }

    public void updateData(ArrayList<ShoppingCartsProductModel> newModels) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new CartDiffCallback(productModels, newModels));
        productModels.clear();
        productModels.addAll(newModels);
        diffResult.dispatchUpdatesTo(this);
    }

    private static class CartDiffCallback extends DiffUtil.Callback {
        private final ArrayList<ShoppingCartsProductModel> oldList;
        private final ArrayList<ShoppingCartsProductModel> newList;

        CartDiffCallback(ArrayList<ShoppingCartsProductModel> oldList, ArrayList<ShoppingCartsProductModel> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() { return oldList.size(); }
        @Override
        public int getNewListSize() { return newList.size(); }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).getProductId().equals(newList.get(newItemPosition).getProductId());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
        }
    }

    public static class ShoppingCartsAdapterViewHolder extends RecyclerView.ViewHolder {
        ProductCartViewBinding binding;
        public ShoppingCartsAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ProductCartViewBinding.bind(itemView);
        }
    }
}