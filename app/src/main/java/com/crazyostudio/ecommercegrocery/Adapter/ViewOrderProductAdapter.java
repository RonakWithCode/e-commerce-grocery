package com.crazyostudio.ecommercegrocery.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.crazyostudio.ecommercegrocery.Model.OrderModel;
import com.crazyostudio.ecommercegrocery.Model.ProductModel;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.databinding.OrderProductLayoutBinding;
import com.crazyostudio.ecommercegrocery.interfaceClass.OrderInterface;
import com.crazyostudio.ecommercegrocery.interfaceClass.OrderProductInterface;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class ViewOrderProductAdapter  extends RecyclerView.Adapter<ViewOrderProductAdapter.ViewOrderProductAdapterViewHolder> implements Filterable {
    ArrayList<OrderModel> orderModelsFull;
    ArrayList<OrderModel> orderModels;
    Context context;
    OrderInterface orderProductInterface;


    public ViewOrderProductAdapter(ArrayList<OrderModel> orderModels, Context context, OrderInterface orderProductInterface) {
        this.orderModelsFull = orderModels;
        this.orderModels = new ArrayList<>(orderModels);
        this.context = context;
        this.orderProductInterface = orderProductInterface;
    }

    @NonNull
    @Override
    public ViewOrderProductAdapter.ViewOrderProductAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewOrderProductAdapter.ViewOrderProductAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.order_product_layout, parent, false));

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewOrderProductAdapter.ViewOrderProductAdapterViewHolder holder, int position) {
        OrderModel model = orderModels.get(position);

        holder.binding.productName.setText(model.getName());
        holder.binding.quantity.setVisibility(View.GONE);
        holder.binding.TotalProductPrice.setText("₹"+model.getTotal());
        holder.binding.getRoot().setOnClickListener(onclickRoot->{
                    orderProductInterface.onOrder(model);
                });
    }

    @Override
    public int getItemCount() {
        return orderModels.size();
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private final Filter mFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<OrderModel> filter =new ArrayList<>();
            if (charSequence.equals("all"))
            {
                filter.addAll(orderModelsFull);

            }
            else {
                for(OrderModel model: orderModelsFull){
                    if (model.getOrderStatus().contentEquals(charSequence)){
                        filter.add(model);
                    }
                }
            }
            FilterResults results =new FilterResults();
            results.values = filter;
            results.count = filter.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            orderModels.clear();
            orderModels.addAll((ArrayList)filterResults.values);
            notifyDataSetChanged();
        }
    };

    public static class ViewOrderProductAdapterViewHolder extends RecyclerView.ViewHolder {
        OrderProductLayoutBinding binding;
        public ViewOrderProductAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = OrderProductLayoutBinding.bind(itemView);
        }
    }
}
