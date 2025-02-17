package com.ronosoft.alwarmart.Adapter;

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

import com.ronosoft.alwarmart.Fragment.ProductWithSlideCategoryFragment;
import com.ronosoft.alwarmart.Model.HomeProductModel;
import com.ronosoft.alwarmart.Model.ProductModel;
import com.ronosoft.alwarmart.R;
import com.ronosoft.alwarmart.databinding.HomeProductViewBinding;
import com.ronosoft.alwarmart.interfaceClass.HomeProductInterface;
import com.ronosoft.alwarmart.interfaceClass.onClickProductAdapter;
import com.ronosoft.alwarmart.javaClasses.CustomSmoothScroller;

import java.util.ArrayList;



public class HomeProductAdapter extends RecyclerView.Adapter<HomeProductAdapter.ViewHolder> {
    ArrayList<HomeProductModel> homeProductModel;
//    showProductViewDialog(productModel);
    HomeProductInterface homeProductInterface;
    //    BannerModels
    FragmentActivity context;


    public HomeProductAdapter(ArrayList<HomeProductModel> homeProductModel, HomeProductInterface homeProductInterface, FragmentActivity context) {
        this.homeProductModel = homeProductModel;
        this.homeProductInterface = homeProductInterface;
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
        ProductAdapter productAdapter = new ProductAdapter(homeProductModel1.getProduct(), new onClickProductAdapter() {
            @Override
            public void onClick(ProductModel productModel, ArrayList<ProductModel> sameProducts) {
                homeProductInterface.HomeProductOnclick(productModel,sameProducts);
            }
        }, context, "Main");
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false);

        holder.binding.recycler.setAdapter(productAdapter);
        holder.binding.recycler.setLayoutManager(layoutManager);

        layoutManager.setSmoothScrollbarEnabled(true);


        CustomSmoothScroller smoothScroller = new CustomSmoothScroller(context);
        smoothScroller.setTargetPosition(0);
        layoutManager.startSmoothScroll(smoothScroller);

        holder.binding.seeMore.setOnClickListener(view -> {
            FragmentTransaction transaction = context.getSupportFragmentManager().beginTransaction();
            Bundle bundle = new Bundle();

            // Check if the position is within the valid range of the product list
            if (position >= 0 && position < homeProductModel1.getProduct().size()) {
                // Retrieve the product's category and proceed with the transaction
                String category = homeProductModel1.getProduct().get(position).getCategory();
                bundle.putString("filter", category);
                Log.i("HomeProductAdapter", "onBindViewHolder: " + category);

                ProductWithSlideCategoryFragment fragment = new ProductWithSlideCategoryFragment();
                fragment.setArguments(bundle);

                transaction.replace(R.id.loader, fragment, "ProductFilterFragment");
                transaction.addToBackStack("ProductFilterFragment");
                transaction.commit();
            } else {
                // Log an error message if the position is out of bounds
                Log.e("HomeProductAdapter", "Invalid position: " + position + ", Product list size: " + homeProductModel1.getProduct().size());
            }
        });

//        holder.binding.seeMore.setOnClickListener(view->{
//            FragmentTransaction transaction = context.getSupportFragmentManager().beginTransaction();
//            Bundle bundle = new Bundle();
//            bundle.putString("filter", homeProductModel1.getProduct().get(position).getCategory());
//            Log.i("HomeProductAdapter", "onBindViewHolder: "+homeProductModel1.getProduct().get(position).getCategory());
//            ProductFilterFragment fragment = new ProductFilterFragment();
//            fragment.setArguments(bundle);
//            transaction.replace(R.id.loader, fragment, "ProductFilterFragment");
//            transaction.addToBackStack("ProductFilterFragment");
//            transaction.commit();
//        });

        productAdapter.notifyDataSetChanged();

        // Add animations for smoother scrolling
        holder.binding.recycler.setHasFixedSize(true);
        holder.binding.recycler.setNestedScrollingEnabled(false);

    }

    @Override
    public int getItemCount() {
        return homeProductModel.size();
    }


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);

    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        HomeProductViewBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = HomeProductViewBinding.bind(itemView);
        }
    }
}
