package com.crazyostudio.ecommercegrocery.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.crazyostudio.ecommercegrocery.Model.HomeProductModel;
import com.crazyostudio.ecommercegrocery.Model.ProductModel;
import com.crazyostudio.ecommercegrocery.Model.ShoppingCartsProductModel;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.databinding.ProductViewSerachBinding;
import com.crazyostudio.ecommercegrocery.interfaceClass.onClickProductAdapter;

import java.util.ArrayList;

public class SearchViewRecommendationAdapter extends RecyclerView.Adapter<SearchViewRecommendationAdapter.SearchViewRecommendationAdapterViewHolder> {
    ArrayList<ProductModel> productModels;
    Context context;

    com.crazyostudio.ecommercegrocery.interfaceClass.onClickProductAdapter onClickProductAdapter;


    public SearchViewRecommendationAdapter(ArrayList<ProductModel> productModels, Context context, com.crazyostudio.ecommercegrocery.interfaceClass.onClickProductAdapter onClickProductAdapter) {
        this.productModels = productModels;
        this.context = context;
        this.onClickProductAdapter = onClickProductAdapter;
    }

    @NonNull
    @Override
    public SearchViewRecommendationAdapter.SearchViewRecommendationAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchViewRecommendationAdapter.SearchViewRecommendationAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.product_view_serach, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewRecommendationAdapter.SearchViewRecommendationAdapterViewHolder holder, int position) {
        ProductModel model = productModels.get(position);

        Glide.with(context).load(model.getImageURL()
                .get(0))
                .placeholder(R.drawable.skeleton_shape).into(holder.binding.productImage);
        holder.binding.productName.setText(model.getProductName());
        holder.binding.productPrice.setText("₹"+model.getPrice());

    }

    @Override
    public int getItemCount() {
        return productModels.size();
    }

    public static class SearchViewRecommendationAdapterViewHolder extends RecyclerView.ViewHolder {
        ProductViewSerachBinding binding;

        public SearchViewRecommendationAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ProductViewSerachBinding.bind(itemView);
        }
    }
}
