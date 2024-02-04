package com.crazyostudio.ecommercegrocery.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.crazyostudio.ecommercegrocery.Adapter.ViewOrderProductAdapter;
import com.crazyostudio.ecommercegrocery.Model.OrderModel;
import com.crazyostudio.ecommercegrocery.Model.UserinfoModels;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.databinding.ActivityAllOrderBinding;
import com.crazyostudio.ecommercegrocery.interfaceClass.OrderInterface;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class AllOrderActivity extends AppCompatActivity implements OrderInterface {
    ActivityAllOrderBinding binding;
    FirebaseDatabase database;
    ArrayList<OrderModel> orderModel;
    UserinfoModels userInfo;
    ViewOrderProductAdapter orderProductAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();

        binding.orderDetailsViewBack.setOnClickListener(view->onBackPressed());
        getOrders();

// Create an ArrayAdapter using the string array and a default spinner layout.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.typesSpinnerArray,
                android.R.layout.simple_spinner_item
        );
// Specify the layout to use when the list of choices appears.
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner.
        binding.typesSpinner.setAdapter(adapter);

        binding.typesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                Log.i("LOG", "onItemSelected: "+adapter.getItem(i).toString());
                orderProductAdapter.getFilter().filter(adapter.getItem(i).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
//                binding.typesSpinner.setSelection(0);
            }
        });






    }

    @SuppressLint("NotifyDataSetChanged")
    private void getOrders() {
        binding.progressCircular.setVisibility(View.VISIBLE);
        orderModel = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        binding.itemRecycler.setLayoutManager(layoutManager);

        database.getReference().child("Order").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    OrderModel model = snapshot1.getValue(OrderModel.class);
                    if (model != null) {
                        orderModel.add(model);
                        orderProductAdapter.notifyDataSetChanged();
                        binding.progressCircular.setVisibility(View.INVISIBLE);
                        orderProductAdapter.getFilter().filter("all");
//                        productModels.addAll(model.getProductModel());
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        orderProductAdapter = new ViewOrderProductAdapter(orderModel,this, this);
        binding.itemRecycler.setAdapter(orderProductAdapter);

        orderProductAdapter.notifyDataSetChanged();

    }


    @Override
    public void onOrder(OrderModel orderModel) {
        binding.progressCircular.setVisibility(View.VISIBLE);
        database.getReference().child("UserInfo").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    binding.progressCircular.setVisibility(View.INVISIBLE);
                    userInfo = snapshot.getValue(UserinfoModels.class);
//                    assert userInfo != null;
//                    userInfo.setEmailAddress(snapshot.child("emailAddress").toString());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Intent i = new Intent(this, OrderDetailsActivity.class);
        i.putExtra("Type","seeOrder");
        i.putExtra("userModel",userInfo);
        i.putExtra("orderModel",orderModel);
        startActivity(i);
    }
}