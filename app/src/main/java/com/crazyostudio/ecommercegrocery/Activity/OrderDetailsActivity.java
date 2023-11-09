package com.crazyostudio.ecommercegrocery.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.widget.Toast;

import com.crazyostudio.ecommercegrocery.Adapter.OrderProductAdapter;
import com.crazyostudio.ecommercegrocery.Fragment.ProductDetailsFragment;
import com.crazyostudio.ecommercegrocery.MainActivity;
import com.crazyostudio.ecommercegrocery.Model.AddressModel;
import com.crazyostudio.ecommercegrocery.Model.OrderModel;
import com.crazyostudio.ecommercegrocery.Model.ProductModel;
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
        }
        else if (getIntent().getStringExtra("Type").equals("seeOrder")) {
            orderModel = getIntent().getParcelableExtra("orderModel");
            userModel = getIntent().getParcelableExtra("userModel");
        }else {
            orderModel = getIntent().getParcelableExtra("orderModel");
            userModel = getIntent().getParcelableExtra("userModel");
        }

        binding.orderDateBox.setOnClickListener(onclick->{
            if (orderModel.getOrderStatus().equals("deliver"))
                Toast.makeText(this, "Order will be deliver", Toast.LENGTH_SHORT).show();
        });
        binding.orderDateBox.setOnClickListener(onclick->{
            if (orderModel.getOrderStatus().equals("deliver")) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                // Format the current date and time as a string
                String formattedDateTime = dateFormat.format(orderModel.getOrderTime());
                Toast.makeText(this, "Order place on " + formattedDateTime, Toast.LENGTH_SHORT).show();
            }
        });
        getData();
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    private void getData() {
        binding.orderContactName.setText(orderModel.getName());
//        Log.i("getEmailAddress", "getData: "+userModel.getEmailAddress());
        final String[] emailAddress = new String[1];
        if (userModel != null) {
            emailAddress[0] = userModel.getEmailAddress();
        }
        else {
            FirebaseDatabase.getInstance().getReference()
                    .child("UserInfo")
                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                    .child("emailAddress")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                emailAddress[0] = dataSnapshot.getValue(String.class);
                                // Now, you can use the emailAddress value

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle any errors that may occur during the data retrieval
                        }
                    });

            // Handle the case when userinfo is null
        }
        binding.orderContactEmail.setText(emailAddress[0]);
        binding.orderContactPhone.setText(orderModel.getPhoneNumber());
        binding.orderShippingAddress.setText(orderModel.getAdders());
        binding.orderShippingMethod.setText(orderModel.getShipping());
        binding.orderPaymentMethod.setText(orderModel.getPaymentType());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        // Format the current date and time as a string
        String formattedDateTime = dateFormat.format(orderModel.getOrderTime());
        binding.orderDate.setText(formattedDateTime);
        binding.orderDeliveryStatus.setText(orderModel.getOrderStatus());
        binding.subtotal.setText(""+orderModel.getSubTotal());
        binding.shippingFee.setText(""+orderModel.getShippingFee());
        binding.save.setText(""+orderModel.getSave());
        binding.grandTotal.setText(""+orderModel.getTotal());

        binding.ContinueShopping.setOnClickListener(ContinueShopping->{
            this.finish();
            startActivity(new Intent(this, MainActivity.class));
        });


        OrderProductAdapter orderProductAdapter = new OrderProductAdapter(orderModel.getProductModel(),this,this);
        binding.orderItems.setAdapter(orderProductAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        binding.orderItems.setLayoutManager(layoutManager);
        orderProductAdapter.notifyDataSetChanged();
    }

    @Override
    public void onOrder(ProductModel model) {
//
        Intent intent = new Intent(this,FragmentLoader.class);
        intent.putExtra("LoadID","Details");
        intent.putExtra("productDetails",model);
        startActivity(intent);

    }
}