package com.ronosoft.alwarmart.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.text.TextUtils;
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

import java.util.ArrayList;
import java.util.Locale;

public class ProductAdapter extends ListAdapter<ProductModel, ProductAdapter.ProductAdapterViewHolder> {

    private final onClickProductAdapter clickListener;
    private final Context context;
    private final ProductManager productManager;
    private final LifecycleOwner lifecycleOwner;

    public ProductAdapter(@NonNull LifecycleOwner lifecycleOwner, ArrayList<ProductModel> productModels,
                          onClickProductAdapter clickListener, Context context) {
        super(DIFF_CALLBACK);
        this.context = context;
        this.clickListener = clickListener;
        this.lifecycleOwner = lifecycleOwner;
        this.productManager = new ProductManager(context);
        submitList(productModels);
    }

    private static final DiffUtil.ItemCallback<ProductModel> DIFF_CALLBACK = new DiffUtil.ItemCallback<ProductModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull ProductModel oldItem, @NonNull ProductModel newItem) {
            return oldItem.getProductId().equals(newItem.getProductId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull ProductModel oldItem, @NonNull ProductModel newItem) {
            // Manually compare key fields instead of using equals()
            return oldItem.getProductName().equals(newItem.getProductName()) &&
                    oldItem.getPrice() == newItem.getPrice() &&
                    oldItem.getMrp() == newItem.getMrp() &&
                    oldItem.getStockCount() == newItem.getStockCount() &&
                    (oldItem.getTime() == null ? newItem.getTime() == null : oldItem.getTime().equals(newItem.getTime()));
        }
    };

    @NonNull
    @Override
    public ProductAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecommendationsViewBinding binding = RecommendationsViewBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ProductAdapterViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapterViewHolder holder, int position) {
        ProductModel product = getItem(position);

        // Set product details
        holder.binding.productName.setText(product.getProductName());
        holder.binding.productPrice.setText("₹" + formatPrice(product.getPrice()));
        holder.binding.productMRP.setText("₹" + formatPrice(product.getMrp()));
        holder.binding.productMRP.setPaintFlags(holder.binding.productMRP.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        // Pass an ArrayList to onClick to match the interface signature
        holder.binding.getRoot().setOnClickListener(v ->
                clickListener.onClick(product, new ArrayList<>(getCurrentList()))
        );

        // Out-of-stock overlay handling
        if (product.getStockCount() == 0) {
            holder.binding.outOfStockOverlay.setVisibility(android.view.View.VISIBLE);
            holder.binding.outOfStockText.setVisibility(android.view.View.VISIBLE);
        } else {
            holder.binding.outOfStockOverlay.setVisibility(android.view.View.GONE);
            holder.binding.outOfStockText.setVisibility(android.view.View.GONE);
        }

        // Calculate and show discount badge
        double mrp = product.getMrp();
        double sellingPrice = product.getPrice();
        int roundedDiscount = (int) Math.round(((mrp - sellingPrice) / mrp) * 100);
        if (roundedDiscount > 0) {
            holder.binding.discountBadge.setVisibility(android.view.View.VISIBLE);
            holder.binding.discountBadge.setText(roundedDiscount + "% OFF");
        } else {
            holder.binding.discountBadge.setVisibility(android.view.View.GONE);
        }

        // Handle delivery time badge
        if (!TextUtils.isEmpty(product.getTime())) {
            holder.binding.deliveryTimeContainer.setVisibility(android.view.View.VISIBLE);
            holder.binding.deliveryTimeBadge.setText(formatDeliveryTime(product.getTime()));
        } else {
            holder.binding.deliveryTimeContainer.setVisibility(android.view.View.GONE);
        }

        // Observe cart changes using the provided LifecycleOwner
        productManager.observeCartItem(product.getProductId()).observe(lifecycleOwner, cartItem -> {
            if (cartItem != null) {
                holder.binding.addToCart.setVisibility(android.view.View.GONE);
                holder.binding.quantityLayout.setVisibility(android.view.View.VISIBLE);
                holder.binding.quantity.setText(String.valueOf(cartItem.getProductSelectQuantity()));
            } else {
                holder.binding.addToCart.setVisibility(android.view.View.VISIBLE);
                holder.binding.quantityLayout.setVisibility(android.view.View.GONE);
            }
        });

        // Additional layout type handling (e.g., shoes, cloth)
        if (product.getProductLayoutType().equals(ProductModel.LAYOUT_TYPE_SHOES) ||
                product.getProductLayoutType().equals(ProductModel.LAYOUT_TYPE_CLOTH)) {
            holder.binding.deliveryTimeContainer.setVisibility(android.view.View.VISIBLE);
            holder.binding.deliveryTimeBadge.setVisibility(android.view.View.VISIBLE);
            holder.binding.deliveryTimeBadge.setText(product.getTime());
        }

        // Add to cart listener
        holder.binding.addToCart.setOnClickListener(view -> {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                ShoppingCartFirebaseModel cartItem = new ShoppingCartFirebaseModel(product.getProductId(), product.getMinSelectableQuantity());
                productManager.addToBothDatabase(cartItem, new ProductManager.AddListenerForAddToBothInDatabase() {
                    @Override
                    public void added(ShoppingCartFirebaseModel shoppingCartFirebaseModel) {
                        holder.binding.addToCart.setVisibility(android.view.View.GONE);
                        holder.binding.quantityLayout.setVisibility(android.view.View.VISIBLE);
                        holder.binding.quantity.setText(String.valueOf(shoppingCartFirebaseModel.getProductSelectQuantity()));
                    }
                    @Override
                    public void failure(Exception e) {
                        // Handle error appropriately
                    }
                });
            } else {
                context.startActivity(new Intent(context, AuthMangerActivity.class));
            }
        });

        // Quantity adjustment listeners
        holder.binding.increaseQuantity.setOnClickListener(v -> {
            int currentQty = Integer.parseInt(holder.binding.quantity.getText().toString());
            int newQty = currentQty + 1;
            if (newQty <= product.getMaxSelectableQuantity()) {
                holder.binding.quantity.setText(String.valueOf(newQty));
                updateQuantity(product.getProductId(), newQty);
            }
        });

        holder.binding.decreaseQuantity.setOnClickListener(v -> {
            int currentQty = Integer.parseInt(holder.binding.quantity.getText().toString());
            int newQty = currentQty - 1;
            if (newQty >= product.getMinSelectableQuantity()) {
                holder.binding.quantity.setText(String.valueOf(newQty));
                updateQuantity(product.getProductId(), newQty);
            }else {
//                binding.progressCircular.setVisibility(View.VISIBLE);
                productManager.RemoveCartProductById(product.getProductId());
//            new DatabaseService().removeCartItemById(uid,id);
//                cartsAdapter.notifyDataSetChanged();
//                binding.progressCircular.setVisibility(View.GONE);

            }
            
        });

        // Load product image with Glide
        Glide.with(context)
                .load(product.getProductImage().get(0))
                .placeholder(R.drawable.product_image_shimmee_effect)
                .error(R.drawable.product_image_shimmee_effect)
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .centerCrop()
                .into(holder.binding.productImage);
    }

    private void updateQuantity(String productId, int quantity) {
        productManager.UpdateCartQuantityById(new AuthService().getUserId(), productId, quantity);
    }

    private String formatPrice(double price) {
        return (price == (long) price)
                ? String.format(Locale.getDefault(), "%d", (long) price)
                : String.format(Locale.getDefault(), "%.2f", price);
    }

    private String formatDeliveryTime(String time) {
        return (time == null || time.isEmpty()) ? "NA" : time + " MINS";
    }

    public static class ProductAdapterViewHolder extends RecyclerView.ViewHolder {
        RecommendationsViewBinding binding;
        public ProductAdapterViewHolder(@NonNull RecommendationsViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
