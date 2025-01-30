package com.ronosoft.alwarmart.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ronosoft.alwarmart.HelperClass.ValuesHelper;
import com.ronosoft.alwarmart.Model.OrderModel;
import com.ronosoft.alwarmart.Model.ShoppingCartsProductModel;
import com.ronosoft.alwarmart.R;
import com.ronosoft.alwarmart.databinding.OrdersViewLayoutBinding;
import com.ronosoft.alwarmart.interfaceClass.OrderInterface;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

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
        return new ViewOrderProductAdapter.ViewOrderProductAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.orders_view_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewOrderProductAdapter.ViewOrderProductAdapterViewHolder holder, int position) {
        OrderModel model = orderModels.get(position);

        String Address = model.getShipping().getShippingAddress().getFlatHouse() + " " + model.getShipping().getShippingAddress().getAddress();
        holder.binding.OrderName.setText(model.getShipping().getShippingAddress().getFullName());
        holder.binding.orderAddress.setText(Address);

        holder.binding.seeAllButton.setText("+" + model.getOrderItems().size() + " more");
        holder.binding.OrderPrice.setText("₹" + model.getOrderTotalPrice());
        holder.binding.OrderStatus.setText(model.getOrderStatus());
        holder.binding.OrderId.setText("#" + model.getOrderId().replace(FirebaseAuth.getInstance().getUid(), ""));

        int textColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.green_primary);
        int backgroundColor = R.drawable.round_status;

        switch (model.getOrderStatus()) {
            case ValuesHelper.PROCESSING:
                backgroundColor = R.drawable.round_status_yellow;
                textColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.FixBlack);
                break;
            case ValuesHelper.CONFIRMED:
                backgroundColor = R.drawable.round_status_light_blue;
                textColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.black);
                break;
            case ValuesHelper.OUTFORDELIVERY:
                backgroundColor = R.drawable.round_status_yellow;
                textColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.FixBlack);
                break;
            case ValuesHelper.DELIVERED:
                backgroundColor = R.drawable.round_status_delivered;
                textColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.green_primary);
                break;
            case ValuesHelper.CANCELLED:
                backgroundColor = R.drawable.round_status_failed;
                textColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.white);
                break;
            case ValuesHelper.CUSTOMER_REJECTED:
                backgroundColor = R.drawable.round_status_purple;
                textColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.white);
                break;
            case ValuesHelper.CUSTOMER_NOT_AVAILABLE:
                backgroundColor = R.drawable.round_status_grey;
                textColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.FixBlack);
                break;
        }

        holder.binding.OrderStatus.setTextColor(textColor);
        holder.binding.OrderStatus.setBackground(ContextCompat.getDrawable(context, backgroundColor));

        holder.binding.orderContactPhone.setText(model.getShipping().getShippingAddress().getMobileNumber());

        holder.binding.Call.setOnClickListener(Call -> {
            // Create the box layout for request for call
        });

        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy 'at' hh:mm a", Locale.ENGLISH);
        String date = outputFormat.format(model.getOrderDate());
        holder.binding.OrderDate.setText(date);

        ArrayList<ShoppingCartsProductModel> firstThreeItems = new ArrayList<>(model.getOrderItems().subList(0, Math.min(3, model.getOrderItems().size())));
        OrderProductAdapter orderProductAdapter = new OrderProductAdapter(firstThreeItems, context, shoppingCartsProductModel -> orderProductInterface.onOrder(model));

        holder.binding.productRecyclerView.setAdapter(orderProductAdapter);
        holder.binding.productRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        orderProductAdapter.notifyDataSetChanged();

        holder.binding.getRoot().setOnClickListener(onclickRoot -> orderProductInterface.onOrder(model));
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
        OrdersViewLayoutBinding binding;
        public ViewOrderProductAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = OrdersViewLayoutBinding.bind(itemView);
        }
    }
}
