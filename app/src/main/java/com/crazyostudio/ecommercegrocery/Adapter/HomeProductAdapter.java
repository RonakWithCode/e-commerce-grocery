package com.crazyostudio.ecommercegrocery.Adapter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crazyostudio.ecommercegrocery.Fragment.ProductDetailsFragment;
import com.crazyostudio.ecommercegrocery.Fragment.ProductFilterFragment;
import com.crazyostudio.ecommercegrocery.Model.HomeProductModel;
import com.crazyostudio.ecommercegrocery.Model.ProductModel;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.databinding.HomeProductViewBinding;
import com.crazyostudio.ecommercegrocery.interfaceClass.onClickProductAdapter;

import java.util.ArrayList;

public class HomeProductAdapter extends RecyclerView.Adapter<HomeProductAdapter.ViewHolder> implements onClickProductAdapter {
    ArrayList<HomeProductModel> homeProductModel;
    
    FragmentActivity context;

    public HomeProductAdapter(ArrayList<HomeProductModel> homeProductModel, FragmentActivity context) {
        this.homeProductModel = homeProductModel;
        this.context = context;
    }

    @NonNull
    @Override
    public HomeProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HomeProductAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.home_product_view, parent, false));
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull HomeProductAdapter.ViewHolder holder, int position) {
        HomeProductModel homeProductModel1 = homeProductModel.get(position);
        holder.binding.title.setText(homeProductModel1.getTitle());
        ProductAdapter productAdapter = new ProductAdapter(homeProductModel1.getProduct(), this, context, "Main");
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false);
        holder.binding.recycler.setAdapter(productAdapter);
        holder.binding.recycler.setLayoutManager(layoutManager);
        holder.binding.seeMore.setOnClickListener(view->{
            FragmentTransaction transaction = context.getSupportFragmentManager().beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putString("filter", homeProductModel1.getProduct().get(position).getCategory());
            Log.i("HomeProductAdapter", "onBindViewHolder: "+homeProductModel1.getProduct().get(position).getCategory());
            ProductFilterFragment fragment = new ProductFilterFragment();
            fragment.setArguments(bundle);
            transaction.replace(R.id.loader, fragment, "ProductFilterFragment");
            transaction.addToBackStack("ProductFilterFragment");
            transaction.commit();
        });

        productAdapter.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return homeProductModel.size();
    }

    @Override
    public void onClick(ProductModel productModel) {
        FragmentTransaction transaction = context.getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putParcelable("productDetails", productModel);
        ProductDetailsFragment productDetailsFragment = new ProductDetailsFragment();
        productDetailsFragment.setArguments(bundle);
        transaction.replace(R.id.loader, productDetailsFragment, "HomeFragment");
        transaction.addToBackStack("HomeFragment");
        transaction.commit();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        HomeProductViewBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = HomeProductViewBinding.bind(itemView);
        }
    }
}
