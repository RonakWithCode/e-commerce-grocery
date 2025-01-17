package com.crazyostudio.ecommercegrocery.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import com.crazyostudio.ecommercegrocery.databinding.ItemSearchResultBinding;
import com.crazyostudio.ecommercegrocery.interfaceClass.onClickProductAdapter;

import java.util.ArrayList;


public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder>  {
    ArrayList<ProductModel> dataList;
    Context context;
    onClickProductAdapter searchAdapterInterface;
    ProductManager productManager;
    boolean IsLogin;
    String userId;
    // Constructor to initialize the data list
    public SearchAdapter(Context context, onClickProductAdapter searchAdapter) {
        this.dataList = new ArrayList<>();
        this.context = context;
        this.searchAdapterInterface = searchAdapter;
        productManager = new ProductManager(context);
        AuthService authService = new AuthService();
        userId = authService.getUserId();
        IsLogin = authService.IsLogin();

    }

    // Method to set new data to the adapter
    @SuppressLint("NotifyDataSetChanged")
    public void setData(ArrayList<ProductModel> newData) {
        dataList.clear();
        this.dataList = newData;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_result, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Bind data to ViewHolder
        ProductModel data = dataList.get(position);
        if (IsLogin) {
            productManager.observeCartItem(data.getProductId()).observe((LifecycleOwner) context, cartItem -> {
                if (cartItem != null) {
                    holder.binding.AddTOCartLayout.setVisibility(View.GONE);
                    holder.binding.productQtyLayout.setVisibility(View.VISIBLE);
                    data.setSelectableQuantity(cartItem.getProductSelectQuantity());
                    holder.binding.productQty.setText(String.valueOf(cartItem.getProductSelectQuantity()));
                } else {
//                    holder.binding.AddTOCartLayout.setVisibility(View.VISIBLE);
//                    holder.binding.productQtyLayout.setVisibility(View.GONE);
                    holder.binding.AddTOCartLayout.setVisibility(View.VISIBLE);
                holder.binding.AddTOCart.setVisibility(View.VISIBLE);
                holder.binding.productQtyLayout.setVisibility(View.GONE);
                }
            });
        }

        Glide.with(context)
                .load(data.getProductImage().get(0))
                .placeholder(R.drawable.product_image_shimmee_effect) // Placeholder image while loading
                .error(R.drawable.product_image_shimmee_effect) // Error image if loading fails
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) // Resize the image
                .centerCrop() // Scale type for resizing
                .into(holder.binding.productImage);

        holder.binding.productName.setText(data.getProductName());
        holder.binding.productPrice.setText("₹"+data.getPrice());
        holder.binding.productSiUnit.setText(data.getWeight()+" "+data.getWeightSIUnit());
        holder.binding.productQty.setText(""+data.getSelectableQuantity());
//        holder.binding.AddTOCart.setOnClickListener(v -> {
//            if (FirebaseAuth.getInstance().getCurrentUser()!=null) {
////                searchAdapterInterface.AddProduct(new ShoppingCartFirebaseModel(data.getProductId(),data.getMinSelectableQuantity()));
//
//                holder.binding.AddTOCartLayout.setVisibility(View.GONE);
//                holder.binding.productQtyLayout.setVisibility(View.VISIBLE);
//                holder.binding.productQty.setText(""+data.getSelectableQuantity());
//            }else {
//                context.startActivity(new Intent(context, AuthMangerActivity.class));
//            }
//        });
//
//        holder.binding.productQtyUp.setOnClickListener(up -> {
//            int quantity = data.getSelectableQuantity();
//            int maxSelected = data.getMaxSelectableQuantity();
//            quantity++;
//            if (quantity > data.getStockCount() || quantity > maxSelected) {
//                holder.binding.productQty.setText(String.valueOf(data.getSelectableQuantity()));
//                String message = quantity > data.getStockCount() ? "Max stock available: " + data.getStockCount() : "Hey Alwar Mart set the limit of " + maxSelected;
//                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
//            } else {
//                data.setSelectableQuantity(quantity);
////                searchAdapterInterface.UpdateQTY(data);
//
//                holder.binding.productQty.setText(String.valueOf(data.getSelectableQuantity()));
//            }
//        });
//
//        holder.binding.productQtyDown.setOnClickListener(Down -> {
//            int quantity = data.getSelectableQuantity();
//            int minSelected = data.getMinSelectableQuantity();
//            if (quantity > minSelected) {
//                quantity--;
//                data.setSelectableQuantity(quantity);
////                searchAdapterInterface.UpdateQTY(data);
//                holder.binding.productQty.setText(String.valueOf(data.getSelectableQuantity()));
//            } else if (quantity == minSelected) {
////                searchAdapterInterface.Remove(data);
//                holder.binding.AddTOCartLayout.setVisibility(View.VISIBLE);
//                holder.binding.productQtyLayout.setVisibility(View.GONE);
//            } else {
//                Toast.makeText(context, "Alwar Mart set this limit min select " + minSelected, Toast.LENGTH_SHORT).show();
//            }
//        });



        if (data.getStockCount() == 0) {
            holder.binding.outOfStockOverlay.setVisibility(View.VISIBLE);
            holder.binding.outOfStockText.setVisibility(View.VISIBLE);
            holder.binding.AddTOCartLayout.setVisibility(View.GONE);
        }

        double mrp = data.getMrp();
        double sellingPrice = data.getPrice();
        double discountPercentage = ((mrp - sellingPrice) / mrp) * 100;
        int roundedDiscount = (int) Math.round(discountPercentage);

        if (roundedDiscount > 0) {
            holder.binding.discountBadge.setVisibility(View.VISIBLE);
            holder.binding.discountBadge.setText(roundedDiscount + "% OFF");
        } else {
            holder.binding.discountBadge.setVisibility(View.GONE);
        }




        holder.binding.AddTOCart.setOnClickListener(view -> {
            if (IsLogin) {
                productManager.addToBothDatabase(
                        new ShoppingCartFirebaseModel(data.getProductId(), data.getMinSelectableQuantity()),
                        new ProductManager.AddListenerForAddToBothInDatabase() {
                            @Override
                            public void added(ShoppingCartFirebaseModel shoppingCartFirebaseModel) {
                                holder.binding.AddTOCart.setVisibility(View.GONE);
                                holder.binding.productQtyLayout.setVisibility(View.VISIBLE);
                                holder.binding.productQty.setText(String.valueOf(shoppingCartFirebaseModel.getProductSelectQuantity()));
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
        holder.binding.productQtyUp.setOnClickListener(v -> {
            int currentQty = Integer.parseInt(holder.binding.productQty.getText().toString());
            int newQty = currentQty + 1;

            if (newQty <= data.getMaxSelectableQuantity() && newQty <= data.getStockCount()) {
                holder.binding.productQty.setText(String.valueOf(newQty));
                UpdateQTY(data.getProductId(), newQty);
            } else {
                Toast.makeText(context, "Maximum quantity available: " +
                                Math.min(data.getMaxSelectableQuantity(), data.getStockCount()),
                        Toast.LENGTH_SHORT).show();
            }
        });

        holder.binding.productQtyDown.setOnClickListener(v -> {
            int currentQty = Integer.parseInt(holder.binding.productQty.getText().toString());
            int newQty = currentQty - 1;

            if (newQty >= data.getMinSelectableQuantity()) {
                holder.binding.productQty.setText(String.valueOf(newQty));
                UpdateQTY(data.getProductId(), newQty);
            } else {
                // Show remove confirmation dialog or handle minimum quantity reached
//                Toast.makeText(context, "Minimum quantity is " + data.getMinSelectableQuantity(),
//                        Toast.LENGTH_SHORT).show();
                productManager.RemoveCartProductById(userId, data.getProductId());
                holder.binding.AddTOCartLayout.setVisibility(View.VISIBLE);
                holder.binding.AddTOCart.setVisibility(View.VISIBLE);
                holder.binding.productQtyLayout.setVisibility(View.GONE);
                data.setSelectableQuantity(data.getMinSelectableQuantity());
            }
        });




        holder.binding.getRoot().setOnClickListener(v -> searchAdapterInterface.onClick(data,dataList));
//        holder.bind(data);
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
        // Return the size of the data list
        return dataList.size();
    }

    // ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemSearchResultBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemSearchResultBinding.bind(itemView); // Example TextView ID, replace with actual IDs
        }


    }
}