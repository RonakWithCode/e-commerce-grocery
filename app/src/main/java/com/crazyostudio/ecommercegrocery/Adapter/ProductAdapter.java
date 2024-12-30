package com.crazyostudio.ecommercegrocery.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.crazyostudio.ecommercegrocery.Activity.AuthMangerActivity;
import com.crazyostudio.ecommercegrocery.Manager.ProductManager;
import com.crazyostudio.ecommercegrocery.Model.ProductModel;
import com.crazyostudio.ecommercegrocery.Model.ShoppingCartFirebaseModel;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.Services.AuthService;
import com.crazyostudio.ecommercegrocery.databinding.ProductboxviewBinding;
import com.crazyostudio.ecommercegrocery.databinding.RecommendationsViewBinding;
import com.crazyostudio.ecommercegrocery.interfaceClass.onClickProductAdapter;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;



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
    public ProductAdapter(ArrayList<ProductModel> productModels, com.crazyostudio.ecommercegrocery.interfaceClass.onClickProductAdapter onClickProductAdapter, Context context,String layout) {
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
        holder.binding.productPrice.setText("₹" + product.getPrice());
        holder.binding.productMRP.setText("₹" + product.getMrp());
        holder.binding.productMRP.setPaintFlags(holder.binding.productMRP.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.binding.getRoot().setOnClickListener(onclick-> onClickProductAdapter.onClick(product,productModels));
//        Glide.with(context)
//                .load(product.getImageURL().get(0))
//                .placeholder(R.drawable.product_image_shimmee_effect)
//                .into(holder.binding.productImage);


//
//        holder.binding.AddTOCart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

        // Check if product is in cart and show quantity selector
        productManager.isProductInCart(product.getProductId(), new ProductManager.addListenerForIsProductInCart() {
            @Override
            public void FoundProduct(ShoppingCartFirebaseModel cartModel) {
                holder.binding.addToCart.setVisibility(View.GONE);
                holder.binding.quantityLayout.setVisibility(View.VISIBLE);
                holder.binding.quantity.setText(String.valueOf(cartModel.getProductSelectQuantity()));
            }

            @Override
            public void notFoundInCart() {
                holder.binding.addToCart.setVisibility(View.VISIBLE);
                holder.binding.quantityLayout.setVisibility(View.GONE);
            }
        });

        // Add to cart click listener
        holder.binding.addToCart.setOnClickListener(view -> {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
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
