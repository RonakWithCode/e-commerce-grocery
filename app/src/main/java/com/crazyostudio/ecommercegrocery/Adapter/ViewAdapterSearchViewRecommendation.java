package com.crazyostudio.ecommercegrocery.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crazyostudio.ecommercegrocery.Model.HomeProductModel;
import com.crazyostudio.ecommercegrocery.Model.ProductModel;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.databinding.HomeProductViewBinding;
import com.crazyostudio.ecommercegrocery.interfaceClass.HomeProductInterface;
import com.crazyostudio.ecommercegrocery.interfaceClass.onClickProductAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewAdapterSearchViewRecommendation extends RecyclerView.Adapter<ViewAdapterSearchViewRecommendation.ViewAdapterSearchViewRecommendationViewHolder> {
    ArrayList<HomeProductModel> homeProductModels;
    HomeProductInterface homeProductInterface;
    FragmentActivity context;

    public ViewAdapterSearchViewRecommendation(ArrayList<HomeProductModel> homeProductModels, HomeProductInterface homeProductInterface, FragmentActivity context) {
        this.homeProductModels = homeProductModels;
        this.homeProductInterface = homeProductInterface;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewAdapterSearchViewRecommendation.ViewAdapterSearchViewRecommendationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewAdapterSearchViewRecommendation.ViewAdapterSearchViewRecommendationViewHolder(LayoutInflater.from(context).inflate(R.layout.home_product_view, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull ViewAdapterSearchViewRecommendation.ViewAdapterSearchViewRecommendationViewHolder holder, int position) {
        HomeProductModel model = homeProductModels.get(position);

        holder.binding.title.setText(model.getTitle());
        holder.binding.recycler.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));

        if (model.getProduct() != null) {
            ArrayList<ProductModel> productList = model.getProduct();
            ArrayList<ProductModel> subList;

            // Check if the product list has more than 4 items
            if (productList.size() > 4) {
                subList = new ArrayList<>(productList.subList(0, 4)); // Get the first 4 items
            } else {
                subList = productList; // Get the whole list if it's less than or equal to 4
            }

            holder.binding.recycler.setAdapter(new SearchViewRecommendationAdapter(subList, context, (productModel, sameProducts) -> {
                // Your callback implementation here
            }));
        }

    }

    @Override
    public int getItemCount() {
        return homeProductModels.size();
    }

    public static class ViewAdapterSearchViewRecommendationViewHolder extends RecyclerView.ViewHolder {
        HomeProductViewBinding binding;
        public ViewAdapterSearchViewRecommendationViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = HomeProductViewBinding.bind(itemView);
        }
    }
}
