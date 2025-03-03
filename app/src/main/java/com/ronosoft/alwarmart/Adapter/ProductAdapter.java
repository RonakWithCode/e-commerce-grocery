package com.ronosoft.alwarmart.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
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



public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductAdapterViewHolder>{
    ArrayList<ProductModel> productModels;
    onClickProductAdapter onClickProductAdapter;
    Context context;
    ProductManager productManager;

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
    public ProductAdapter(ArrayList<ProductModel> productModels, com.ronosoft.alwarmart.interfaceClass.onClickProductAdapter onClickProductAdapter, Context context, String layout) {
        this.productModels = productModels;
        this.onClickProductAdapter = onClickProductAdapter;
        this.context = context;
        this.productManager = new ProductManager(context);
//        this.layout = layout;
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
//        if (layout.equals("Main")) {
////            holder.binding.getRoot().setMaxWidth(match_parent ConstraintLayout);
//            ViewGroup.LayoutParams params =  holder.binding.getRoot().getLayoutParams();
//            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
//            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//            holder.binding.getRoot().setLayoutParams(params);
//        }

        holder.binding.productName.setText(product.getProductName());
        holder.binding.productPrice.setText("₹" + formatPrice(product.getPrice()));
        holder.binding.productMRP.setText("₹" + formatPrice(product.getMrp()));
        holder.binding.productMRP.setPaintFlags(holder.binding.productMRP.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.binding.getRoot().setOnClickListener(onclick-> onClickProductAdapter.onClick(product,productModels));

        if (product.getStockCount() == 0) {
            holder.binding.outOfStockOverlay.setVisibility(View.VISIBLE);
            holder.binding.outOfStockText.setVisibility(View.VISIBLE);
        }
//        if (product.getDiscount() == null) {
//
//        }

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

        // Handle delivery time badge
        if (!TextUtils.isEmpty(product.getTime())) {
            holder.binding.deliveryTimeContainer.setVisibility(View.VISIBLE);
            holder.binding.deliveryTimeBadge.setText(formatDeliveryTime(product.getTime()));
        } else {
            holder.binding.deliveryTimeContainer.setVisibility(View.GONE);
        }

        // Check if product is in cart and show quantity selector
        productManager.observeCartItem(product.getProductId()).observe((LifecycleOwner) context, cartItem -> {
            if (cartItem != null) {
                holder.binding.addToCart.setVisibility(View.GONE);
                holder.binding.quantityLayout.setVisibility(View.VISIBLE);
                holder.binding.quantity.setText(String.valueOf(cartItem.getProductSelectQuantity()));
            } else {
                holder.binding.addToCart.setVisibility(View.VISIBLE);
                holder.binding.quantityLayout.setVisibility(View.GONE);
            }
        });

        if (product.getProductLayoutType().equals(ProductModel.LAYOUT_TYPE_SHOES) || product.getProductLayoutType().equals(ProductModel.LAYOUT_TYPE_CLOTH))
        {
            holder.binding.deliveryTimeContainer.setVisibility(View.VISIBLE);
            holder.binding.deliveryTimeBadge.setVisibility(View.VISIBLE);
            holder.binding.deliveryTimeBadge.setText(product.getTime());
//            holder.binding.productImage.setImageResource(R.drawable.shoes);
        }
        // Add to cart click listener
        holder.binding.addToCart.setOnClickListener(view -> {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                ShoppingCartFirebaseModel shoppingCartFirebaseModel = new ShoppingCartFirebaseModel();
                shoppingCartFirebaseModel =  new ShoppingCartFirebaseModel(product.getProductId(), product.getMinSelectableQuantity());

                //                if (product.isProductIsShoes()) {
//                    shoppingCartFirebaseModel =  new ShoppingCartFirebaseModel(product.getProductId(), product.getMinSelectableQuantity(),true,"UK8");
//                }else {
//                    shoppingCartFirebaseModel =  new ShoppingCartFirebaseModel(product.getProductId(), product.getMinSelectableQuantity(),false,null);
//                }
                productManager.addToBothDatabase(shoppingCartFirebaseModel,
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
            if (newQty <= product.getMaxSelectableQuantity()) {
                holder.binding.quantity.setText(String.valueOf(newQty));
                UpdateQTY(product.getProductId(), newQty);
            }
        });

        holder.binding.decreaseQuantity.setOnClickListener(v -> {
            int currentQty = Integer.parseInt(holder.binding.quantity.getText().toString());
            int newQty = currentQty - 1;
            if (newQty >= product.getMinSelectableQuantity()) {
                holder.binding.quantity.setText(String.valueOf(newQty));
                UpdateQTY(product.getProductId(), newQty);
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
                new AuthService().getUserId(),
                productId,
                quantity
        );
    }

    private String formatPrice(double price) {
        if (price == (long) price) {
            return String.format(Locale.getDefault(), "%d", (long) price);
        } else {
            return String.format(Locale.getDefault(), "%.2f", price);
        }
    }

    private String formatDeliveryTime(String time) {
        if (time == null || time.isEmpty()) {
            return "NA";
        }
        return time + " MINS"; // Format as "30 MINS"
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
}