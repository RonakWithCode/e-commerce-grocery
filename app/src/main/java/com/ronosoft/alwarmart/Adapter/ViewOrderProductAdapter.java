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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ViewOrderProductAdapter extends RecyclerView.Adapter<ViewOrderProductAdapter.ViewOrderProductAdapterViewHolder> {

    private final ArrayList<OrderModel> orderModels;
    private final Context context;
    private final OrderInterface orderInterface;

    public ViewOrderProductAdapter(ArrayList<OrderModel> orderModels, Context context, OrderInterface orderInterface) {
        this.orderModels = orderModels != null ? orderModels : new ArrayList<>();
        this.context = context;
        this.orderInterface = orderInterface;
    }

    @NonNull
    @Override
    public ViewOrderProductAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.orders_view_layout, parent, false);
        return new ViewOrderProductAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewOrderProductAdapterViewHolder holder, int position) {
        OrderModel model = orderModels.get(position);
        if (model == null) return;

        // Bind customer and address details
        if (model.getShipping() != null && model.getShipping().getShippingAddress() != null) {
            String address = model.getShipping().getShippingAddress().getFlatHouse() + ", " +
                    model.getShipping().getShippingAddress().getAddress();
            holder.binding.OrderName.setText(model.getShipping().getShippingAddress().getFullName());
            holder.binding.orderAddress.setText(address);
            holder.binding.orderContactPhone.setText(model.getShipping().getShippingAddress().getMobileNumber());
        } else {
            holder.binding.OrderName.setText("N/A");
            holder.binding.orderAddress.setText("N/A");
            holder.binding.orderContactPhone.setText("N/A");
        }

        // Bind order details
        holder.binding.OrderPrice.setText(String.format("₹%.2f", model.getOrderTotalPrice()));
        holder.binding.OrderStatus.setText(model.getOrderStatus() != null ? model.getOrderStatus() : "Unknown");
        holder.binding.OrderId.setText(model.getOrderId() != null ? "#" + model.getOrderId() : "#N/A");

        // Set status styling
        int textColor = ContextCompat.getColor(context, R.color.green_primary);
        int backgroundColor = R.drawable.round_status;
        switch (model.getOrderStatus() != null ? model.getOrderStatus() : "") {
            case ValuesHelper.PROCESSING:
                backgroundColor = R.drawable.round_status_yellow;
                textColor = ContextCompat.getColor(context, R.color.FixBlack);
                break;
            case ValuesHelper.CONFIRMED:
                backgroundColor = R.drawable.round_status_light_blue;
                textColor = ContextCompat.getColor(context, R.color.black);
                break;
            case ValuesHelper.OUTFORDELIVERY:
                backgroundColor = R.drawable.round_status_yellow;
                textColor = ContextCompat.getColor(context, R.color.FixBlack);
                break;
            case ValuesHelper.DELIVERED:
                backgroundColor = R.drawable.round_status_delivered;
                textColor = ContextCompat.getColor(context, R.color.green_primary);
                break;
            case ValuesHelper.CANCELLED:
                backgroundColor = R.drawable.round_status_failed;
                textColor = ContextCompat.getColor(context, R.color.white);
                break;
            case ValuesHelper.CUSTOMER_REJECTED:
                backgroundColor = R.drawable.round_status_purple;
                textColor = ContextCompat.getColor(context, R.color.white);
                break;
            case ValuesHelper.CUSTOMER_NOT_AVAILABLE:
                backgroundColor = R.drawable.round_status_grey;
                textColor = ContextCompat.getColor(context, R.color.FixBlack);
                break;
        }
        holder.binding.OrderStatus.setTextColor(textColor);
        holder.binding.OrderStatus.setBackground(ContextCompat.getDrawable(context, backgroundColor));

        // Bind order date
        if (model.getOrderDate() != null) {
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy 'at' hh:mm a", Locale.ENGLISH);
            holder.binding.OrderDate.setText(outputFormat.format(model.getOrderDate()));
        } else {
            holder.binding.OrderDate.setText("N/A");
        }

        // Bind product RecyclerView
        ArrayList<ShoppingCartsProductModel> items = model.getOrderItems() != null ? model.getOrderItems() : new ArrayList<>();
        int displayCount = Math.min(items.size(), 3);
        ArrayList<ShoppingCartsProductModel> displayItems = new ArrayList<>(items.subList(0, displayCount));
        OrderProductAdapter productAdapter = new OrderProductAdapter(displayItems, context, item ->
                orderInterface.onOrder(model));
        holder.binding.productRecyclerView.setAdapter(productAdapter);
        holder.binding.productRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        holder.binding.productRecyclerView.setHasFixedSize(true);

        // Bind "See All" button
        if (items.size() > 3) {
            holder.binding.seeAllButton.setText("+" + (items.size() - 3) + " more");
            holder.binding.seeAllButton.setVisibility(View.VISIBLE);
        } else {
            holder.binding.seeAllButton.setVisibility(View.GONE);
        }

        // Handle click to open order details
        holder.binding.getRoot().setOnClickListener(v -> orderInterface.onOrder(model));
    }

    @Override
    public int getItemCount() {
        return orderModels.size();
    }

    public static class ViewOrderProductAdapterViewHolder extends RecyclerView.ViewHolder {
        final OrdersViewLayoutBinding binding;

        public ViewOrderProductAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = OrdersViewLayoutBinding.bind(itemView);
        }
    }
}