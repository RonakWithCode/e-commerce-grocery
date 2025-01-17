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

import com.crazyostudio.ecommercegrocery.Fragment.ProductFilterFragment;
import com.crazyostudio.ecommercegrocery.Fragment.ProductWithSlideCategoryFragment;
import com.crazyostudio.ecommercegrocery.Model.HomeProductModel;
import com.crazyostudio.ecommercegrocery.Model.ProductModel;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.databinding.HomeProductViewBinding;
import com.crazyostudio.ecommercegrocery.interfaceClass.HomeProductInterface;
import com.crazyostudio.ecommercegrocery.interfaceClass.onClickProductAdapter;
import com.crazyostudio.ecommercegrocery.javaClasses.CustomSmoothScroller;

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


        holder.binding.seeMore.setOnClickListener(view->{
            FragmentTransaction transaction = context.getSupportFragmentManager().beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putString("filter", homeProductModel1.getProduct().get(position).getCategory());
            Log.i("HomeProductAdapter", "onBindViewHolder: "+homeProductModel1.getProduct().get(position).getCategory());
            ProductWithSlideCategoryFragment fragment = new ProductWithSlideCategoryFragment();
            fragment.setArguments(bundle);
            transaction.replace(R.id.loader, fragment, "ProductFilterFragment");
            transaction.addToBackStack("ProductFilterFragment");
            transaction.commit();
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
