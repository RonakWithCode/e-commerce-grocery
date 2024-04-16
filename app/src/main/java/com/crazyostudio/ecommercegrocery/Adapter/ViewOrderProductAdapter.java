package com.crazyostudio.ecommercegrocery.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.crazyostudio.ecommercegrocery.Model.OrderModel;
import com.crazyostudio.ecommercegrocery.Model.ProductModel;
import com.crazyostudio.ecommercegrocery.Model.ShoppingCartsProductModel;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.databinding.OrderProductLayoutBinding;
import com.crazyostudio.ecommercegrocery.interfaceClass.OrderInterface;
import com.crazyostudio.ecommercegrocery.interfaceClass.OrderProductInterface;

import java.lang.annotation.Target;
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

        String Address = model.getShipping().getShippingAddress().getFullName() +"\n" +model.getShipping().getShippingAddress().getFlatHouse() +" "  +model.getShipping().getShippingAddress().getAddress();
        holder.binding.productName.setText(Address);
        holder.binding.quantity.setText(model.getOrderItems().size()+" (Item)'");
//        holder.binding.quantity.setVisibility(View.GONE);
//        holder.binding.TotalProductPrice.setText("₹"+model.getOrderTotalPrice());
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
