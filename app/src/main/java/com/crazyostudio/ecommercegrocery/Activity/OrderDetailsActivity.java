package com.crazyostudio.ecommercegrocery.Activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.crazyostudio.ecommercegrocery.Adapter.OrderProductAdapter;
import com.crazyostudio.ecommercegrocery.Dialog.DialogUtils;
import com.crazyostudio.ecommercegrocery.HelperClass.ShoppingCartHelper;
import com.crazyostudio.ecommercegrocery.Model.OrderModel;
import com.crazyostudio.ecommercegrocery.Model.ShoppingCartsProductModel;
import com.crazyostudio.ecommercegrocery.databinding.ActivityOrderDetailsBinding;
import com.crazyostudio.ecommercegrocery.interfaceClass.OrderProductInterface;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

public class OrderDetailsActivity extends AppCompatActivity implements OrderProductInterface {
    ActivityOrderDetailsBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    OrderModel orderModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
//        if (getIntent().getBooleanExtra("DialogUtils", false)){
//            DialogUtils.showConfirmationDialog(this, "Do you want to remove all items from the cart?", (dialog, which) -> {
//
//        }
        binding.orderDetailsViewBack.setOnClickListener(view-> this.onBackPressed());
        binding.download.setOnClickListener(v -> downloadBill());
        binding.ContinueShopping.setOnClickListener(v->{
            this.finish();
        });
         String orderID = getIntent().getStringExtra("orderID");
        database.getReference().child("Order").child(Objects.requireNonNull(auth.getUid())).child(orderID).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    orderModel = snapshot.getValue(OrderModel.class);
                    binding.progressCircular.setVisibility(View.GONE);
                    getData();

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
   }

    private void downloadBill() {
        DialogUtils.showConfirmationDialog(this, "Do you want to remove all items from the cart?", (dialog, which) -> {
            Toast.makeText(this, "wait", Toast.LENGTH_SHORT).show();
        });

    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    private void getData() {
        if (orderModel.getCustomer() != null) {
            binding.orderContactName.setText(orderModel.getCustomer().getFullName());
            binding.orderContactPhone.setText(orderModel.getShipping().getShippingAddress().getMobileNumber());
            binding.orderShippingAddress.setText(orderModel.getShipping().getShippingAddress().getFlatHouse()+orderModel.getShipping().getShippingAddress().getAddress());
            binding.orderShippingMethod.setText(orderModel.getShipping().getShippingMethod());
            binding.orderPaymentMethod.setText(orderModel.getPayment().getPaymentMethod());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            // Format the current date and time as a string
            String formattedDateTime = dateFormat.format(orderModel.getOrderDate());
            binding.orderDate.setText(formattedDateTime);
            binding.orderDeliveryStatus.setText(orderModel.getOrderStatus());
            if (orderModel.getOrderStatus().equals("delivered")) {
                binding.orderDeliveryOnBox.setVisibility(View.VISIBLE);
                String formattedDateOfDeliveredOrder = dateFormat.format(orderModel.getShipping().getDeliveredData());
                binding.orderDeliveryStatus.setText(formattedDateOfDeliveredOrder);
            }
            OrderProductAdapter orderProductAdapter  = new OrderProductAdapter(orderModel.getOrderItems(),this,this::onOrder);
            binding.orderItems.setAdapter(orderProductAdapter);
            binding.orderItems.setLayoutManager(new LinearLayoutManager(this));
            orderProductAdapter.notifyDataSetChanged();

            binding.shippingFee.setText("₹"+orderModel.getShipping().getShippingFee());
            binding.save.setText("₹"+ ShoppingCartHelper.calculateTotalSavings(orderModel.getOrderItems()));
            binding.grandTotal.setText("₹"+orderModel.getOrderTotalPrice());
            if (orderModel.getOrderStatus().equals("successfully"))
            {
                binding.download.setVisibility(View.VISIBLE);
            }
        } else {
            Toast.makeText(this, "Customer information is missing", Toast.LENGTH_SHORT).show();
            binding.download.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onOrder(ShoppingCartsProductModel model) {
        Intent intent = new Intent(this,FragmentLoader.class);
        intent.putExtra("LoadID","Details");
        intent.putExtra("productId",model.getProductId());
        startActivity(intent);


    }


}