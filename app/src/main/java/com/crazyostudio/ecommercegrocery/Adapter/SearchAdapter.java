package com.crazyostudio.ecommercegrocery.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.crazyostudio.ecommercegrocery.Model.ProductModel;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.databinding.ItemSearchResultBinding;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private ArrayList<ProductModel> dataList;
    private Context context;
    // Constructor to initialize the data list
    public SearchAdapter(Context context) {
        this.dataList = new ArrayList<>();
        this.context = context;
    }

    // Method to set new data to the adapter
    public void setData(ArrayList<ProductModel> newData) {
        this.dataList = newData;
        notifyDataSetChanged();
    }
    // Method to clear the data
    public void clearData() {
        dataList.clear();
        notifyDataSetChanged();
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
        Glide.with(context).load(data.getImageURL()).placeholder(R.drawable.placeholder).into(holder.binding.productImage);
        holder.binding.productName.setText(data.getProductName());
        holder.binding.productPrice.setText("₹"+data.getPrice());

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
