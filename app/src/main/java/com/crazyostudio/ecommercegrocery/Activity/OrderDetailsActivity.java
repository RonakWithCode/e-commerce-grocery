package com.crazyostudio.ecommercegrocery.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.crazyostudio.ecommercegrocery.Adapter.OrderProductAdapter;
import com.crazyostudio.ecommercegrocery.Fragment.ProductDetailsFragment;
import com.crazyostudio.ecommercegrocery.HelperClass.ShoppingCartHelper;
import com.crazyostudio.ecommercegrocery.MainActivity;
import com.crazyostudio.ecommercegrocery.Model.AddressModel;
import com.crazyostudio.ecommercegrocery.Model.OrderModel;
import com.crazyostudio.ecommercegrocery.Model.ProductModel;
import com.crazyostudio.ecommercegrocery.Model.ShoppingCartsProductFirebaseModel;
import com.crazyostudio.ecommercegrocery.Model.ShoppingCartsProductModel;
import com.crazyostudio.ecommercegrocery.Model.UserinfoModels;
import com.crazyostudio.ecommercegrocery.R;
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
    UserinfoModels userModel;
    OrderModel orderModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        if (getIntent().getStringExtra("Type").equals("placeOrder")) {
            orderModel = getIntent().getParcelableExtra("orderModel");
            userModel = getIntent().getParcelableExtra("userModel");
            getData();

        }
        else if (getIntent().getStringExtra("Type").equals("seeOrder")) {
            orderModel = getIntent().getParcelableExtra("orderModel");
            userModel = getIntent().getParcelableExtra("userModel");
            getData();

        }
        else if (getIntent().getStringExtra("Type").equals("seeOrderNotification")){
            binding.progressCircular.setVisibility(View.INVISIBLE);
            database.getReference().child("Order").child(auth.getUid()).child(getIntent().getStringExtra("orderId")).addListenerForSingleValueEvent(new ValueEventListener() {
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
        else {
            orderModel = getIntent().getParcelableExtra("orderModel");
            userModel = getIntent().getParcelableExtra("userModel");
            getData();

        }

        binding.orderDateBox.setOnClickListener(onclick->{
            if (orderModel.getOrderStatus().equals("deliver"))
                Toast.makeText(this, "Order will be deliver", Toast.LENGTH_SHORT).show();
        });
        binding.orderDateBox.setOnClickListener(onclick->{
            if (orderModel.getOrderStatus().equals("deliver")) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                // Format the current date and time as a string
                String formattedDateTime = dateFormat.format(orderModel.getOrderDate());
                Toast.makeText(this, "Order place on " + formattedDateTime, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    private void getData() {
        binding.orderContactName.setText(orderModel.getCustomer().getFullName());
//        Log.i("getEmailAddress", "getData: "+userModel.getEmailAddress());
//        binding.orderContactEmail.setText(emailAddress[0]);
        binding.orderContactPhone.setText(orderModel.getShipping().getShippingAddress().getMobileNumber());
        binding.orderShippingAddress.setText(orderModel.getShipping().getShippingAddress().getFlatHouse()+orderModel.getShipping().getShippingAddress().getAddress());
        binding.orderShippingMethod.setText(orderModel.getShipping().getShippingMethod());
        binding.orderPaymentMethod.setText(orderModel.getPayment().getPaymentMethod());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        // Format the current date and time as a string
        String formattedDateTime = dateFormat.format(orderModel.getOrderDate());
        binding.orderDate.setText(formattedDateTime);
        binding.orderDeliveryStatus.setText(orderModel.getOrderStatus());
//        binding.subtotal.setText(""+orderModel.getSubTotal());
        binding.shippingFee.setText("₹"+orderModel.getShipping().getShippingFee());
        binding.save.setText("₹"+ ShoppingCartHelper.calculateTotalSavings(orderModel.getOrderItems()));
        binding.grandTotal.setText("₹"+orderModel.getOrderTotalPrice());

        binding.ContinueShopping.setOnClickListener(ContinueShopping->{
            this.finish();
            startActivity(new Intent(this, MainActivity.class));
        });


        OrderProductAdapter orderProductAdapter = new OrderProductAdapter(orderModel.getOrderItems(),this,this);
        binding.orderItems.setAdapter(orderProductAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        binding.orderItems.setLayoutManager(layoutManager);
        orderProductAdapter.notifyDataSetChanged();
    }

    @Override
    public void onOrder(ShoppingCartsProductModel model) {
//
        Intent intent = new Intent(this,FragmentLoader.class);
        intent.putExtra("LoadID","Details");
//        intent.putExtra("productDetails",model);
        intent.putExtra("productDetails",  (Parcelable) model);
        startActivity(intent);

    }


}