package com.crazyostudio.ecommercegrocery.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crazyostudio.ecommercegrocery.Adapter.ViewOrderProductAdapter;
import com.crazyostudio.ecommercegrocery.Model.OrderModel;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.databinding.ActivityAllOrderBinding;
import com.crazyostudio.ecommercegrocery.interfaceClass.OrderInterface;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class AllOrderActivity extends AppCompatActivity implements OrderInterface {
    ActivityAllOrderBinding binding;
    FirebaseDatabase database;
    ArrayList<OrderModel> orderModel;
    ViewOrderProductAdapter orderProductAdapter;
    private ActionBar actionBar;
    private int itemCount = 10; // Number of items to initially load
    private int lastVisibleItemPosition;
    private boolean isLoading = false;
    private int totalItemCount;
    private LinearLayoutManager layoutManager;
    private String filter = "all"; // Default filter value

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        binding.orderDetailsViewBack.setOnClickListener(view -> onBackPressed());
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.typesSpinnerArray,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.typesSpinner.setAdapter(adapter);
        binding.typesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                filter = adapter.getItem(i).toString();
                getOrders(filter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                filter = "all";
                getOrders(filter);
            }
        });

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.itemRecycler.setLayoutManager(layoutManager);
        orderModel = new ArrayList<>();
        orderProductAdapter = new ViewOrderProductAdapter(orderModel, this, this);
        binding.itemRecycler.setAdapter(orderProductAdapter);

        binding.itemRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = layoutManager.getItemCount();
                lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

                if (!isLoading && totalItemCount <= (lastVisibleItemPosition + itemCount)) {
                    // Load more data
                    isLoading = true;
                    getMoreOrders(filter);
                }
            }
        });

        getOrders(filter);
    }


    @SuppressLint("NotifyDataSetChanged")
    private void getOrders(String filter) {
        binding.progressCircular.setVisibility(View.VISIBLE);
        Query query = database.getReference("Order")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .limitToLast(itemCount)
                .orderByChild("orderStatus");

        if (!filter.equals("all")) {
            query = query.equalTo(filter);
        }

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderModel.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    OrderModel model = snapshot1.getValue(OrderModel.class);
                    if (model != null) {
                        orderModel.add(model);
                    }
                }
                orderProductAdapter.notifyDataSetChanged();
                binding.progressCircular.setVisibility(View.GONE);
                isLoading = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.progressCircular.setVisibility(View.GONE);
                isLoading = false;
            }
        });
    }




    private void getMoreOrders(String filter) {
        // Increase itemCount for loading more items
        itemCount += 10;
        getOrders(filter);
    }

    @Override
    public void onOrder(OrderModel orderModels) {
        Intent i = new Intent(this, OrderDetailsActivity.class);
        i.putExtra("orderID", orderModels.getOrderId());
        i.putExtra("orderModel", orderModels);
        startActivity(i);
    }

    @Override
    protected void onDestroy() {
        if (actionBar != null) {
            actionBar.show();
        }
        super.onDestroy();
    }
}