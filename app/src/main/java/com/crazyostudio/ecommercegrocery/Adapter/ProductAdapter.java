package com.crazyostudio.ecommercegrocery.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.crazyostudio.ecommercegrocery.Activity.AuthMangerActivity;
import com.crazyostudio.ecommercegrocery.Manager.ProductManager;
import com.crazyostudio.ecommercegrocery.Model.ProductModel;
import com.crazyostudio.ecommercegrocery.Model.ShoppingCartFirebaseModel;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.Services.AuthService;
import com.crazyostudio.ecommercegrocery.databinding.RecommendationsViewBinding;
import com.crazyostudio.ecommercegrocery.interfaceClass.onClickProductAdapter;

import java.util.ArrayList;



public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductAdapterViewHolder>{
    ArrayList<ProductModel> productModels;
    onClickProductAdapter onClickProductAdapter;
    Context context;
    ProductManager productManager;
    boolean IsLogin;
    String userId;
//    String layout;
    //    productboxview
    public void setFilerList(ArrayList<ProductModel> FilterModels){
        this.productModels = FilterModels;
        notifyDataSetChanged();
    }
    public void setData(ArrayList<ProductModel> newData) {
        this.productModels.clear();
        this.productModels.addAll(newData);
        notifyDataSetChanged();
    }
    public ProductAdapter(ArrayList<ProductModel> productModels, com.crazyostudio.ecommercegrocery.interfaceClass.onClickProductAdapter onClickProductAdapter, Context context,String layout) {
        this.productModels = productModels;
        this.onClickProductAdapter = onClickProductAdapter;
        this.context = context;
        this.productManager = new ProductManager(context);
//        this.layout = layout;
        AuthService authService = new AuthService();

        IsLogin = authService.IsLogin();
        userId = authService.getUserId();
    }

    @NonNull
    @Override
    public ProductAdapter.ProductAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductAdapter.ProductAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.recommendations_view, parent, false));

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ProductAdapterViewHolder holder, int position) {
        ProductModel product = productModels.get(position);
        
        // Format price to remove .0 if it's a whole number
        String formattedPrice = formatPrice(product.getPrice());
        String formattedMRP = formatPrice(product.getMrp());

        holder.binding.productName.setText(product.getProductName());
        holder.binding.productPrice.setText("₹" + formattedPrice);
        holder.binding.productMRP.setText("₹" + formattedMRP);
        holder.binding.productMRP.setPaintFlags(holder.binding.productMRP.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.binding.getRoot().setOnClickListener(onclick-> onClickProductAdapter.onClick(product,productModels));

        if (product.getStockCount() == 0) {
            holder.binding.outOfStockOverlay.setVisibility(View.VISIBLE);
            holder.binding.outOfStockText.setVisibility(View.VISIBLE);
            holder.binding.AddTOCartLayout.setVisibility(View.GONE);

        }


        double mrp = product.getMrp();
        double sellingPrice = product.getPrice();
        double discountPercentage = ((mrp - sellingPrice) / mrp) * 100;
        int roundedDiscount = (int) Math.round(discountPercentage);

        if (roundedDiscount > 0) {
            holder.binding.discountBadge.setVisibility(View.VISIBLE);
            holder.binding.discountBadge.setText(roundedDiscount + "% OFF");
        } else {
            holder.binding.discountBadge.setVisibility(View.GONE);
        }
//         Check if product is in cart and show quantity selector
        if (IsLogin) {
            productManager.observeCartItem(product.getProductId()).observe((LifecycleOwner) context, cartItem -> {
            if (cartItem != null) {
                holder.binding.addToCart.setVisibility(View.GONE);
                holder.binding.quantityLayout.setVisibility(View.VISIBLE);
                holder.binding.quantity.setText(String.valueOf(cartItem.getProductSelectQuantity()));
            } else {
                holder.binding.addToCart.setVisibility(View.VISIBLE);
                holder.binding.quantityLayout.setVisibility(View.GONE);
                holder.binding.AddTOCartLayout.setVisibility(View.VISIBLE);
            }
        });
        }

        // Add to cart click listener
        holder.binding.addToCart.setOnClickListener(view -> {
            if (IsLogin) {
                productManager.addToBothDatabase(
                    new ShoppingCartFirebaseModel(product.getProductId(), product.getMinSelectableQuantity()),
                    new ProductManager.AddListenerForAddToBothInDatabase() {
                        @Override
                        public void added(ShoppingCartFirebaseModel shoppingCartFirebaseModel) {
                            holder.binding.addToCart.setVisibility(View.GONE);
                            holder.binding.quantityLayout.setVisibility(View.VISIBLE);
                            holder.binding.quantity.setText(String.valueOf(shoppingCartFirebaseModel.getProductSelectQuantity()));
                        }

                        @Override
                        public void failure(Exception e) {
                            // Handle error
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
            
            if (newQty <= product.getMaxSelectableQuantity() && newQty <= product.getStockCount()) {
                holder.binding.quantity.setText(String.valueOf(newQty));
                UpdateQTY(product.getProductId(), newQty);
            } else {
                Toast.makeText(context, "Maximum quantity available: " + 
                    Math.min(product.getMaxSelectableQuantity(), product.getStockCount()), 
                    Toast.LENGTH_SHORT).show();
            }
        });

        holder.binding.decreaseQuantity.setOnClickListener(v -> {
            int currentQty = Integer.parseInt(holder.binding.quantity.getText().toString());
            int newQty = currentQty - 1;
            
            if (newQty >= product.getMinSelectableQuantity()) {
                holder.binding.quantity.setText(String.valueOf(newQty));
                UpdateQTY(product.getProductId(), newQty);
            } else {
                // Show remove confirmation dialog or handle minimum quantity reached
//                Toast.makeText(context, "Minimum quantity is " + product.getMinSelectableQuantity(),
//                    Toast.LENGTH_SHORT).show();
                productManager.RemoveCartProductById(userId, product.getProductId());
                holder.binding.AddTOCartLayout.setVisibility(View.VISIBLE);
                holder.binding.addToCart.setVisibility(View.VISIBLE);
                holder.binding.quantityLayout.setVisibility(View.GONE);
                product.setSelectableQuantity(product.getMinSelectableQuantity());

            }
        });

        Glide.with(context)
                .load(product.getProductImage().get(0))
                .placeholder(R.drawable.product_image_shimmee_effect) // Placeholder image while loading
                .error(R.drawable.product_image_shimmee_effect) // Error image if loading fails
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) // Resize the image
                .centerCrop() // Scale type for resizing
                .into(holder.binding.productImage);
    }

    private void UpdateQTY(String productId, int quantity) {
        productManager.UpdateCartQuantityById(
                userId,
            productId,
            quantity
        );
    }

    @Override
    public int getItemCount() {
        return productModels.size();
    }

    public static class ProductAdapterViewHolder extends RecyclerView.ViewHolder {
//        recommendations_view

        RecommendationsViewBinding binding;
        public ProductAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RecommendationsViewBinding.bind(itemView);
        }
    }

    // Add this helper method to format prices
    private String formatPrice(double price) {
        if (price == (long) price) {
            return String.format("%d", (long) price);
        } else {
            return String.format("%.2f", price);
        }
    }
}
