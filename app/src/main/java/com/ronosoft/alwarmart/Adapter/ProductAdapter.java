package com.ronosoft.alwarmart.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.TransitionOptions;
import com.bumptech.glide.request.target.Target;
import com.ronosoft.alwarmart.Activity.AuthMangerActivity;
import com.ronosoft.alwarmart.Manager.ProductManager;
import com.ronosoft.alwarmart.Model.ProductModel;
import com.ronosoft.alwarmart.Model.ShoppingCartFirebaseModel;
import com.ronosoft.alwarmart.R;
import com.ronosoft.alwarmart.Services.AuthService;
import com.ronosoft.alwarmart.databinding.RecommendationsViewBinding;
import com.ronosoft.alwarmart.interfaceClass.onClickProductAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.ronosoft.alwarmart.javaClasses.TokenManager;

import java.util.ArrayList;
import java.util.Locale;

public class ProductAdapter extends ListAdapter<ProductModel, ProductAdapter.ProductAdapterViewHolder> {

    private final onClickProductAdapter clickListener;
    private final Context context;
    private final ProductManager productManager;
    private final LifecycleOwner lifecycleOwner;

    public ProductAdapter(@NonNull LifecycleOwner lifecycleOwner,
                          ArrayList<ProductModel> productModels,
                          onClickProductAdapter clickListener,
                          Context context) {
        super(DIFF_CALLBACK);
        this.context = context;
        this.clickListener = clickListener;
        this.lifecycleOwner = lifecycleOwner;
        this.productManager = new ProductManager(context);
        // ListAdapter handles diffing automatically.
        submitList(productModels);
    }

    // DiffUtil for efficient updates
    private static final DiffUtil.ItemCallback<ProductModel> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<ProductModel>() {
                @Override
                public boolean areItemsTheSame(@NonNull ProductModel oldItem, @NonNull ProductModel newItem) {
                    return oldItem.getProductId().equals(newItem.getProductId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull ProductModel oldItem, @NonNull ProductModel newItem) {
                    // Compare key fields for equality
                    return oldItem.getProductName().equals(newItem.getProductName())
                            && oldItem.getPrice() == newItem.getPrice()
                            && oldItem.getMrp() == newItem.getMrp()
                            && oldItem.getStockCount() == newItem.getStockCount()
                            && (oldItem.getTime() == null
                            ? newItem.getTime() == null
                            : oldItem.getTime().equals(newItem.getTime()));
                }
            };

    @NonNull
    @Override
    public ProductAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecommendationsViewBinding binding = RecommendationsViewBinding.inflate(
                LayoutInflater.from(context), parent, false);
        return new ProductAdapterViewHolder(binding);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ProductAdapterViewHolder holder, int position) {
        ProductModel product = getItem(position);
        if (product == null) return;

        // Set product name and price information
        holder.binding.productName.setText(product.getProductName());
        holder.binding.productPrice.setText("₹" + formatPrice(product.getPrice()));
        holder.binding.productMRP.setText("₹" + formatPrice(product.getMrp()));
        holder.binding.productMRP.setPaintFlags(holder.binding.productMRP.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        // Use a unified click listener for the item
        holder.binding.getRoot().setOnClickListener(v ->
                clickListener.onClick(product, new ArrayList<>(getCurrentList()))
        );

        // Out-of-stock overlay handling
        if (product.getStockCount() == 0) {
            holder.binding.outOfStockOverlay.setVisibility(View.VISIBLE);
            holder.binding.outOfStockText.setVisibility(View.VISIBLE);
        } else {
            holder.binding.outOfStockOverlay.setVisibility(View.GONE);
            holder.binding.outOfStockText.setVisibility(View.GONE);
        }

        // Calculate and display discount badge if MRP > price
        double mrp = product.getMrp();
        double sellingPrice = product.getPrice();
        if (mrp > 0 && sellingPrice < mrp) {
            int roundedDiscount = (int) Math.round(((mrp - sellingPrice) / mrp) * 100);
            if (roundedDiscount > 0) {
                holder.binding.discountBadge.setVisibility(View.VISIBLE);
                holder.binding.discountBadge.setText(roundedDiscount + "% OFF");
            } else {
                holder.binding.discountBadge.setVisibility(View.GONE);
            }
        } else {
            holder.binding.discountBadge.setVisibility(View.GONE);
        }

        // Delivery time handling for specific layout types (e.g., Shoes, Cloth)
        if ("Shoes".equalsIgnoreCase(product.getProductLayoutType()) ||
                "Cloth".equalsIgnoreCase(product.getProductLayoutType())) {
            holder.binding.deliveryTimeContainer.setVisibility(View.VISIBLE);
            holder.binding.deliveryTimeBadge.setVisibility(View.VISIBLE);
            holder.binding.deliveryTimeBadge.setText(product.getTime());
        } else {
            holder.binding.deliveryTimeContainer.setVisibility(View.GONE);
        }

        // Observe cart changes (if supported by your productManager)
        productManager.observeCartItem(product.getProductId()).observe(lifecycleOwner, cartItem -> {
            if (cartItem != null) {
                holder.binding.addToCart.setVisibility(View.GONE);
                holder.binding.quantityLayout.setVisibility(View.VISIBLE);
                holder.binding.quantity.setText(String.valueOf(cartItem.getProductSelectQuantity()));
            } else {
                holder.binding.addToCart.setVisibility(View.VISIBLE);
                holder.binding.quantityLayout.setVisibility(View.GONE);
            }
        });

        // Add to cart action
        holder.binding.addToCart.setOnClickListener(v -> {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                ShoppingCartFirebaseModel cartItem = new ShoppingCartFirebaseModel(
                        product.getProductId(), product.getMinSelectableQuantity());
                productManager.addToBothDatabase(cartItem, new ProductManager.AddListenerForAddToBothInDatabase() {
                    @Override
                    public void added(ShoppingCartFirebaseModel shoppingCartFirebaseModel) {
                        holder.binding.addToCart.setVisibility(View.GONE);
                        holder.binding.quantityLayout.setVisibility(View.VISIBLE);
                        holder.binding.quantity.setText(String.valueOf(shoppingCartFirebaseModel.getProductSelectQuantity()));
                    }
                    @Override
                    public void failure(Exception e) {
                        Log.e("ProductAdapter", "Error adding to cart", e);
                    }
                });
            } else {
                context.startActivity(new Intent(context, AuthMangerActivity.class));
            }
        });

        // Quantity adjustment listeners with safe number parsing
        holder.binding.increaseQuantity.setOnClickListener(v -> {
            try {
                int currentQty = Integer.parseInt(holder.binding.quantity.getText().toString());
                int newQty = currentQty + 1;
                if (newQty <= product.getMaxSelectableQuantity()) {
                    holder.binding.quantity.setText(String.valueOf(newQty));
                    updateQuantity(product.getProductId(), newQty);
                }
                else {
                    Toast.makeText(context, "Maximum quantity can be select "+product.getMaxSelectableQuantity(), Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Log.e("ProductAdapter", "Invalid quantity format", e);
            }
        });

        holder.binding.decreaseQuantity.setOnClickListener(v -> {
            try {
                int currentQty = Integer.parseInt(holder.binding.quantity.getText().toString());
                int newQty = currentQty - 1;
                if (newQty >= product.getMinSelectableQuantity()) {
                    holder.binding.quantity.setText(String.valueOf(newQty));
                    updateQuantity(product.getProductId(), newQty);
                } else {
                    // Remove product from cart if quantity drops below minimum
                    productManager.RemoveCartProductById(product.getProductId());
                }
            } catch (NumberFormatException e) {
                Log.e("ProductAdapter", "Invalid quantity format", e);
            }
        });

        // Load product image safely using Glide
        if (product.getProductImage() != null && !product.getProductImage().isEmpty()) {
            Glide.with(context)
                    .load(product.getProductImage().get(0))
                    .placeholder(R.drawable.spinner_gif)
                    .error(R.drawable.product_image_shimmee_effect)
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .centerCrop()
                    .into(holder.binding.productImage);
        } else {
            // Set a default image if no images are available
            holder.binding.productImage.setImageResource(R.drawable.product_image_shimmee_effect);
        }
    }

    /**
     * Updates the cart quantity using the ProductManager.
     */
    private void updateQuantity(String productId, int quantity) {
        String uid = new AuthService().getUserId();
        if (uid != null) {
            productManager.UpdateCartQuantityById(uid, productId, quantity);
        }
    }

    /**
     * Formats price by removing unnecessary decimals.
     */
    private String formatPrice(double price) {
        return (price == (long) price)
                ? String.format(Locale.getDefault(), "%d", (long) price)
                : String.format(Locale.getDefault(), "%.2f", price);
    }

    public static class ProductAdapterViewHolder extends RecyclerView.ViewHolder {
        RecommendationsViewBinding binding;

        public ProductAdapterViewHolder(@NonNull RecommendationsViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
