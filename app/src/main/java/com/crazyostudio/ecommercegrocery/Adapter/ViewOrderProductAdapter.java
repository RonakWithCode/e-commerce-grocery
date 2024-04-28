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

public class ViewOrderProductAdapter  extends RecyclerView.Adapter<ViewOrderProductAdapter.ViewOrderProductAdapterViewHolder> {

    ArrayList<OrderModel> orderModels;
    Context context;
    OrderInterface orderProductInterface;

    public ViewOrderProductAdapter(ArrayList<OrderModel> orderModels, Context context, OrderInterface orderProductInterface) {
        this.orderModels = orderModels;
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

        String Address = model.getShipping().getShippingAddress().getFlatHouse() +" "  +model.getShipping().getShippingAddress().getAddress();
        holder.binding.productName.setText(model.getShipping().getShippingAddress().getFullName());
        holder.binding.productAdders.setText(Address);
        holder.binding.quantity.setText(model.getOrderItems().size()+" (Item)'");
        holder.binding.TotalProductPrice.setText("₹"+model.getOrderTotalPrice());
//        holder.binding.TotalProductPrice.setText("₹"+model.getOrderTotalPrice());
        holder.binding.getRoot().setOnClickListener(onclickRoot->{
                    orderProductInterface.onOrder(model);
        });



    }

    @Override
    public int getItemCount() {
        return orderModels.size();
    }

//    @Override
//    public Filter getFilter() {
//        return mFilter;
//    }
//
//    private final Filter mFilter = new Filter() {
//        @Override
//        protected FilterResults performFiltering(CharSequence charSequence) {
//            ArrayList<OrderModel> filter = new ArrayList<>();
//            if (charSequence.equals("all")) {
//                filter.addAll(orderModelsFull);
//            } else {
//                for (OrderModel model : orderModelsFull) {
//                    if (model.getOrderStatus().equals(charSequence)) {
//                        filter.add(model);
//                    }
//                }
//            }
//            // Check if there are more orders than the maximum limit
////            if (filter.size() > MAX_ORDERS_TO_DISPLAY) {
////                // Show the option to see more orders
////                showMoreOrders = true;
////            } else {
////                // Hide the option to see more orders
////                showMoreOrders = false;
////            }
//            FilterResults results = new FilterResults();
//            results.values = filter;
//            results.count = filter.size();
//            return results;
//        }
//
//        @Override
//        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
//            orderModels.clear();
//            orderModels.addAll((ArrayList<OrderModel>) filterResults.values);
//            notifyDataSetChanged();
//            // Check if there are more orders than the maximum limit
//            if (showMoreOrders) {
//                // Show the option to see more orders
//                // Perform the action to show more orders here
//            } else {
//                // Hide the option to see more orders
//                // Perform the action to hide the option here
//            }
//        }
//
//    };
//
//


    public static class ViewOrderProductAdapterViewHolder extends RecyclerView.ViewHolder {
        OrderProductLayoutBinding binding;
        public ViewOrderProductAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = OrderProductLayoutBinding.bind(itemView);
        }
    }
}
