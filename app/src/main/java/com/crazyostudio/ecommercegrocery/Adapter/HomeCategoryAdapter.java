package com.crazyostudio.ecommercegrocery.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.crazyostudio.ecommercegrocery.Model.HomeCategoryModel;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.databinding.HomeCategoryLayoutBinding;
import com.crazyostudio.ecommercegrocery.interfaceClass.HomeCategoryInterface;

import java.util.ArrayList;

public class HomeCategoryAdapter extends RecyclerView.Adapter<HomeCategoryAdapter.HomeCategoryAdapterViewHolder>{

    ArrayList<HomeCategoryModel> models;
    Context context;
    HomeCategoryInterface homeCategoryInterface;


    public HomeCategoryAdapter(ArrayList<HomeCategoryModel> models, Context context, HomeCategoryInterface homeCategoryInterface) {
        this.models = models;
        this.context = context;
        this.homeCategoryInterface = homeCategoryInterface;
    }

    @NonNull
    @Override
    public HomeCategoryAdapter.HomeCategoryAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HomeCategoryAdapter.HomeCategoryAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.home_category_layout, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull HomeCategoryAdapter.HomeCategoryAdapterViewHolder holder, int position) {
        HomeCategoryModel homeCategoryModel = models.get(position);

        holder.binding.ProductName.setText(homeCategoryModel.getCategoryName());
        holder.binding.ProductSize.setText(homeCategoryModel.getProductSize()+" products");
        holder.binding.Text1.setText("+"+homeCategoryModel.getProductSize());
        Glide.with(context).load(homeCategoryModel.getCategoryImages().get(0)).into(holder.binding.View1);
        Glide.with(context).load(homeCategoryModel.getCategoryImages().get(1)).into(holder.binding.View2);
        Glide.with(context).load(homeCategoryModel.getCategoryImages().get(2)).into(holder.binding.View3);


        holder.binding.getRoot().setOnClickListener(v -> homeCategoryInterface.onClick(homeCategoryModel.getTag()));
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public static class HomeCategoryAdapterViewHolder extends RecyclerView.ViewHolder {
        HomeCategoryLayoutBinding binding;
        public HomeCategoryAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = HomeCategoryLayoutBinding.bind(itemView);
        }
    }
}
