package com.ronosoft.alwarmart.Adapter;

import android.app.Activity;
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
import com.ronosoft.alwarmart.Activity.AuthMangerActivity;
import com.ronosoft.alwarmart.Component.ProductViewCard;
import com.ronosoft.alwarmart.Manager.ProductManager;
import com.ronosoft.alwarmart.Model.ProductModel;
import com.ronosoft.alwarmart.Model.ShoppingCartFirebaseModel;
import com.ronosoft.alwarmart.R;
import com.ronosoft.alwarmart.Services.AuthService;
import com.ronosoft.alwarmart.databinding.ProductViewSerachBinding;
import com.ronosoft.alwarmart.javaClasses.LoadingDialog;
import com.ronosoft.alwarmart.javaClasses.ErrorBox;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

// This below to the main search adapter and this show the product Recommendation

public class SearchViewRecommendationAdapter extends RecyclerView.Adapter<SearchViewRecommendationAdapter.SearchViewRecommendationAdapterViewHolder> {

    private ArrayList<ProductModel> dataList;
    private Activity context;
    private LoadingDialog loadingDialog;
    private ErrorBox errorBox;
    private ProductManager productManager;

    public SearchViewRecommendationAdapter(ArrayList<ProductModel> modelArrayList, Activity context) {
        this.dataList = modelArrayList;
        this.context = context;
        this.loadingDialog = new LoadingDialog((Activity) context); // Initialize LoadingDialog
        this.errorBox = new ErrorBox((Activity) context); // Initialize ErrorBox
        this.productManager = new ProductManager(context);
    }

    @NonNull
    @Override
    public SearchViewRecommendationAdapter.SearchViewRecommendationAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchViewRecommendationAdapter.SearchViewRecommendationAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.product_view_serach, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull SearchViewRecommendationAdapter.SearchViewRecommendationAdapterViewHolder holder, int position) {
        ProductModel model = dataList.get(position);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            productManager.observeCartItem(model.getProductId()).observe((LifecycleOwner) context, cartItem -> {
                if (cartItem != null) {
                    holder.binding.addToCartButton.setVisibility(View.GONE);
                    holder.binding.productQtyLayout.setVisibility(View.VISIBLE);
                    model.setSelectableQuantity(cartItem.getProductSelectQuantity());
                    holder.binding.productQty.setText(String.valueOf(cartItem.getProductSelectQuantity()));
                } else {
                    holder.binding.addToCartButton.setVisibility(View.VISIBLE);
                    holder.binding.productQtyLayout.setVisibility(View.GONE);
                }
            });
        }

        Glide.with(context)
            .load(model.getProductImage().get(0))
            .placeholder(R.drawable.skeleton_shape)
            .into(holder.binding.productImage);

        String formattedName = capitalizeFirstLetter(model.getProductName());
        holder.binding.productName.setText(formattedName);
        
        String weightText = String.format("%s %s", model.getWeight(), model.getWeightSIUnit().toUpperCase());
        holder.binding.weightInfo.setText(weightText);

        double discountPercentage = ((model.getMrp() - model.getPrice()) / model.getMrp()) * 100;
        if (discountPercentage > 0) {
            holder.binding.discountBadge.setVisibility(View.VISIBLE);
            holder.binding.discountBadge.setText(String.format("%.0f%% OFF", discountPercentage));
            
            double savings = model.getMrp() - model.getPrice();
            holder.binding.productMrp.setText(String.format("MRP: ₹%.2f (Save ₹%.2f)", 
                model.getMrp(), savings));
        } else {
            holder.binding.discountBadge.setVisibility(View.GONE);
            holder.binding.productMrp.setText(String.format("MRP: ₹%.2f", model.getMrp()));
        }

        holder.binding.productPrice.setText(String.format("₹%.2f", model.getPrice()));
        holder.binding.productMrp.setPaintFlags(holder.binding.productMrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        holder.binding.addToCartButton.setOnClickListener(v -> {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                loadingDialog.startLoadingDialog(); // Show loading dialog
                ShoppingCartFirebaseModel shoppingCartFirebaseModel = new ShoppingCartFirebaseModel();
                shoppingCartFirebaseModel =  new ShoppingCartFirebaseModel(model.getProductId(), model.getMinSelectableQuantity());


                productManager.addToBothDatabase(shoppingCartFirebaseModel, new ProductManager.AddListenerForAddToBothInDatabase() {
                    @Override
                    public void added(ShoppingCartFirebaseModel shoppingCartFirebaseModel) {
                        holder.binding.addToCartButton.setVisibility(View.GONE);
                        holder.binding.productQtyLayout.setVisibility(View.VISIBLE);
                        holder.binding.productQty.setText(shoppingCartFirebaseModel.getProductSelectQuantity() + "");
                        loadingDialog.dismissDialog(); // Dismiss loading dialog
                    }

                    @Override
                    public void failure(Exception e) {
                        loadingDialog.dismissDialog(); // Dismiss loading dialog
                        errorBox.showErrorDialog("Error", "Failed to add to cart: " + e.getMessage());
                    }
                });

            } else {
                context.startActivity(new Intent(context, AuthMangerActivity.class));
            }
        });

        holder.binding.productQtyUp.setOnClickListener(up -> {
            int quantity = model.getSelectableQuantity();
            int maxSelected = model.getMaxSelectableQuantity();
            quantity++;
            if (quantity > model.getStockCount() || quantity > maxSelected) {
                holder.binding.productQty.setText(String.valueOf(model.getSelectableQuantity()));
                String message = quantity > model.getStockCount() ? "Max stock available: " + model.getStockCount() : "Hey Alwar Mart set the limit of " + maxSelected;
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            } else {
                model.setSelectableQuantity(quantity);
                UpdateQTY(model);
                holder.binding.productQty.setText(String.valueOf(model.getSelectableQuantity()));
            }
        });

        holder.binding.productQtyDown.setOnClickListener(Down -> {
            int quantity = model.getSelectableQuantity();
            int minSelected = model.getMinSelectableQuantity();
            if (quantity > minSelected) {
                quantity--;
                model.setSelectableQuantity(quantity);
                UpdateQTY(model);
                holder.binding.productQty.setText(String.valueOf(model.getSelectableQuantity()));
            } else if (quantity == minSelected) {
                loadingDialog.startLoadingDialog(); // Show loading dialog
                productManager.RemoveCartProductById(new AuthService().getUserId(), model.getProductId());
                holder.binding.addToCartButton.setVisibility(View.VISIBLE);
                holder.binding.productQtyLayout.setVisibility(View.GONE);
                loadingDialog.dismissDialog(); // Dismiss loading dialog
            } else {
                Toast.makeText(context, "Alwar Mart set this limit min select " + minSelected, Toast.LENGTH_SHORT).show();
            }
        });

        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle root click if needed
                new ProductViewCard(context).showProductViewDialog(model,dataList);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    private void UpdateQTY(ProductModel newModel) {
        productManager.UpdateCartQuantityById(new AuthService().getUserId(), newModel.getProductId(), newModel.getSelectableQuantity());
    }

    // Helper method to capitalize product name
    private String capitalizeFirstLetter(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }

    public static class SearchViewRecommendationAdapterViewHolder extends RecyclerView.ViewHolder {
        ProductViewSerachBinding binding;

        public SearchViewRecommendationAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ProductViewSerachBinding.bind(itemView);
        }
    }
}
