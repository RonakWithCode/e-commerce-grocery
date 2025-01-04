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
import com.crazyostudio.ecommercegrocery.Activity.AuthMangerActivity;
import com.crazyostudio.ecommercegrocery.DAO.CartDAOHelper;
import com.crazyostudio.ecommercegrocery.DAO.ShoppingCartFirebaseModelDAO;
import com.crazyostudio.ecommercegrocery.Manager.ProductManager;
import com.crazyostudio.ecommercegrocery.Model.ProductModel;
import com.crazyostudio.ecommercegrocery.Model.ShoppingCartFirebaseModel;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.databinding.ItemSearchResultBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder>  {
    private ArrayList<ProductModel> dataList;
    private final Context context;
    SearchAdapterInterface searchAdapterInterface;
    List<ShoppingCartFirebaseModelDAO> daoList;
    private ProductManager productManager;

    // Constructor to initialize the data list
    public SearchAdapter(Context context,SearchAdapterInterface searchAdapterInterface) {
        this.dataList = new ArrayList<>();
        this.context = context;
        this.searchAdapterInterface = searchAdapterInterface;
        daoList = CartDAOHelper.getDB(context).ModelDAO().getAllModel();
        productManager = new ProductManager(context);
    }

    // Method to set new data to the adapter
    @SuppressLint("NotifyDataSetChanged")
    public void setData(ArrayList<ProductModel> newData) {
        this.dataList = newData;
        notifyDataSetChanged();
    }
    public boolean IsEmpty(){
        return dataList.isEmpty();
    }
    // Method to clear the data
    @SuppressLint("NotifyDataSetChanged")
    public void clearData() {
        dataList.clear();
        notifyDataSetChanged();
    }

    interface checkInCartCallback{
        void notFound();
        void found(int selectQTY);
    }


    void checkInCart(String checkId , checkInCartCallback callback){
        for (int i = 0; i < daoList.size(); i++) {
            ShoppingCartFirebaseModelDAO id = daoList.get(i);
            if (id.getProductId().equals(checkId)) {
                callback.found(id.getProductSelectQuantity());
            }
        }
        callback.notFound();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Bind data to ViewHolder
        ProductModel data = dataList.get(position);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            productManager.observeCartItem(data.getProductId()).observe((LifecycleOwner) context, cartItem -> {
                if (cartItem != null) {
                    holder.binding.AddTOCartLayout.setVisibility(View.GONE);
                    holder.binding.productQtyLayout.setVisibility(View.VISIBLE);
                    data.setSelectableQuantity(cartItem.getProductSelectQuantity());
                    holder.binding.productQty.setText(String.valueOf(cartItem.getProductSelectQuantity()));
                } else {
                    holder.binding.AddTOCartLayout.setVisibility(View.VISIBLE);
                    holder.binding.productQtyLayout.setVisibility(View.GONE);
                }
            });
        }

        Glide.with(context).load(data.getProductImage().get(0)).placeholder(R.drawable.placeholder).into(holder.binding.productImage);
        holder.binding.productName.setText(data.getProductName());
        holder.binding.productPrice.setText("₹"+data.getPrice());
        holder.binding.productSiUnit.setText(data.getWeight()+" "+data.getWeightSIUnit());
        holder.binding.productQty.setText(""+data.getSelectableQuantity());
        holder.binding.AddTOCart.setOnClickListener(v -> {
            if (FirebaseAuth.getInstance().getCurrentUser()!=null) {
                searchAdapterInterface.AddProduct(new ShoppingCartFirebaseModel(data.getProductId(),data.getMinSelectableQuantity()));
                holder.binding.AddTOCartLayout.setVisibility(View.GONE);
                holder.binding.productQtyLayout.setVisibility(View.VISIBLE);
                holder.binding.productQty.setText(""+data.getSelectableQuantity());
            }else {
                context.startActivity(new Intent(context, AuthMangerActivity.class));
            }
        });

        holder.binding.productQtyUp.setOnClickListener(up -> {
            int quantity = data.getSelectableQuantity();
            int maxSelected = data.getMaxSelectableQuantity();
            quantity++;
            if (quantity > data.getStockCount() || quantity > maxSelected) {
                holder.binding.productQty.setText(String.valueOf(data.getSelectableQuantity()));
                String message = quantity > data.getStockCount() ? "Max stock available: " + data.getStockCount() : "Hey Alwar Mart set the limit of " + maxSelected;
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            } else {
                data.setSelectableQuantity(quantity);
                searchAdapterInterface.UpdateQTY(data);
                holder.binding.productQty.setText(String.valueOf(data.getSelectableQuantity()));
            }
        });

        holder.binding.productQtyDown.setOnClickListener(Down -> {
            int quantity = data.getSelectableQuantity();
            int minSelected = data.getMinSelectableQuantity();
            if (quantity > minSelected) {
                quantity--;
                data.setSelectableQuantity(quantity);
                searchAdapterInterface.UpdateQTY(data);
                holder.binding.productQty.setText(String.valueOf(data.getSelectableQuantity()));
            } else if (quantity == minSelected) {
                searchAdapterInterface.Remove(data);
                holder.binding.AddTOCartLayout.setVisibility(View.VISIBLE);
                holder.binding.productQtyLayout.setVisibility(View.GONE);
            } else {
                Toast.makeText(context, "Alwar Mart set this limit min select " + minSelected, Toast.LENGTH_SHORT).show();
            }
        });



        holder.binding.getRoot().setOnClickListener(v -> searchAdapterInterface.onclick(data));
//        holder.bind(data);
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
