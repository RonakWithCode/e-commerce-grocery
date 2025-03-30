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

    private ArrayList<HomeProductModel> homeProductModel;
    private HomeProductInterface homeProductInterface;
    private FragmentActivity context;

    public HomeProductAdapter(ArrayList<HomeProductModel> homeProductModel, HomeProductInterface homeProductInterface, FragmentActivity context) {
        this.homeProductModel = homeProductModel;
        this.homeProductInterface = homeProductInterface;
        this.context = context;
    }

    @NonNull
    @Override
    public HomeProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_product_view, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull HomeProductAdapter.ViewHolder holder, int position) {
        HomeProductModel model = homeProductModel.get(position);
        // Set the title from the model
        holder.binding.title.setText(model.getTitle());

        // Create and set the nested adapter if product list is available
        ArrayList<ProductModel> products = model.getProduct();
        if (products != null && !products.isEmpty()) {
            ProductAdapter productAdapter = new ProductAdapter(
                    context,
                    products,
                    (productModel, sameProducts) -> homeProductInterface.HomeProductOnclick(productModel, sameProducts),
                    context
            );

            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            holder.binding.recycler.setAdapter(productAdapter);
            holder.binding.recycler.setLayoutManager(layoutManager);
            layoutManager.setSmoothScrollbarEnabled(true);

            CustomSmoothScroller smoothScroller = new CustomSmoothScroller(context);
            smoothScroller.setTargetPosition(0);
            layoutManager.startSmoothScroll(smoothScroller);

            // Optionally, if product data has been updated within the adapter,
            // you can call productAdapter.notifyDataSetChanged() here.
        }

        // "See More" click uses the model title as the filter/category.
        holder.binding.seeMore.setOnClickListener(v -> {
            FragmentTransaction transaction = context.getSupportFragmentManager().beginTransaction();
            Bundle bundle = new Bundle();
            // Use the model title (or a dedicated category field if available)
            String category = model.getTitle();
            bundle.putString("filter", category);
            Log.i("HomeProductAdapter", "See more clicked: " + category);
            ProductWithSlideCategoryFragment fragment = new ProductWithSlideCategoryFragment();
            fragment.setArguments(bundle);
            transaction.replace(R.id.loader, fragment, "ProductFilterFragment");
            transaction.addToBackStack("ProductFilterFragment");
            transaction.commit();
        });

        // Set fixed size and disable nested scrolling for smooth performance.
        holder.binding.recycler.setHasFixedSize(true);
        holder.binding.recycler.setNestedScrollingEnabled(false);
    }

    @Override
    public int getItemCount() {
        return homeProductModel.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        HomeProductViewBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = HomeProductViewBinding.bind(itemView);
        }
    }
}
