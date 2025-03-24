package com.ronosoft.alwarmart.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ronosoft.alwarmart.Adapter.ViewOrderProductAdapter;
import com.ronosoft.alwarmart.HelperClass.ValuesHelper;
import com.ronosoft.alwarmart.Model.OrderModel;
import com.ronosoft.alwarmart.R;
import com.ronosoft.alwarmart.Services.AuthService;
import com.ronosoft.alwarmart.databinding.ActivityAllOrderBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class AllOrderActivity extends AppCompatActivity {
    private ActivityAllOrderBinding binding;
    private FirebaseDatabase database;
    private ArrayList<OrderModel> orderModels;
    private ViewOrderProductAdapter orderProductAdapter;
    private String currentFilter = "all";
    private String currentDateFilter = "all_time";
    private boolean isLoading = false;
    private int pageSize = 10;
    private Query lastQuery;
    private DataSnapshot lastVisible;
    private AuthService auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = new AuthService();
        if (!auth.IsLogin()) {
            this.finish();
            return;
        }
        setupViews();
        setupRecyclerView();
        setupFilters();
        loadInitialData();
    }

    private void setupViews() {
        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Setup back button
        binding.orderDetailsViewBack.setOnClickListener(v -> onBackPressed());

        // Setup SwipeRefreshLayout
        binding.swipeRefreshLayout.setOnRefreshListener(this::refreshData);
        
        // Setup empty state
        binding.emptyStateText.setVisibility(View.GONE);
    }

    private void setupFilters() {
        // Setup order status filter
        String[] filterOptions = getResources().getStringArray(R.array.order_status_options);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            com.google.android.material.R.layout.support_simple_spinner_dropdown_item, 
            filterOptions);
        
        AutoCompleteTextView filterView = binding.typesSpinner;
        filterView.setAdapter(adapter);
        filterView.setText(filterOptions[0], false);
        filterView.setOnItemClickListener((parent, view, position, id) -> {
            // Convert filter option to match the status in database
            String selectedFilter = filterOptions[position];
            switch (selectedFilter.toLowerCase()) {
                case "all orders":
                    currentFilter = "all";
                    break;
                case "processing":
                    currentFilter = ValuesHelper.PROCESSING;
                    break;
                case "confirmed":
                    currentFilter = ValuesHelper.CONFIRMED;
                    break;
                case "out for delivery":
                    currentFilter = ValuesHelper.OUTFORDELIVERY;
                    break;
                case "delivered":
                    currentFilter = ValuesHelper.DELIVERED;
                    break;
                case "cancelled":
                    currentFilter = ValuesHelper.CANCELLED;
                    break;
                case "customer rejected":
                    currentFilter = ValuesHelper.CUSTOMER_REJECTED;
                    break;
                case "customer not available":
                    currentFilter = ValuesHelper.CUSTOMER_NOT_AVAILABLE;
                    break;
                default:
                    currentFilter = "all";
            }
            refreshData();
        });

        // Setup date filter chips
        binding.dateFilterChipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.allTimeChip) {
                currentDateFilter = "all_time";
            } else if (checkedId == R.id.lastMonthChip) {
                currentDateFilter = "last_month";
            } else if (checkedId == R.id.last3MonthsChip) {
                currentDateFilter = "last_3_months";
            }
            refreshData();
        });
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.itemRecycler.setLayoutManager(layoutManager);

        orderModels = new ArrayList<>();
        orderProductAdapter = new ViewOrderProductAdapter(orderModels, this, orderModel -> {
            Intent i = new Intent(AllOrderActivity.this, OrderDetailsActivity.class);
            i.putExtra("orderID", orderModel.getOrderId());
            i.putExtra("orderModel", orderModel);
            startActivity(i);
        });


        binding.itemRecycler.setAdapter(orderProductAdapter);


        binding.itemRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

                if (!isLoading && totalItemCount <= (lastVisibleItemPosition + pageSize)) {
                    // Load more data
                    isLoading = true;
                    getMoreOrders(currentFilter);
                }
            }
        });


    }

    private void loadInitialData() {
        database = FirebaseDatabase.getInstance();
        Query query = database.getReference("Order")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .limitToLast(pageSize);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderModels.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    OrderModel model = snapshot1.getValue(OrderModel.class);
                    if (model != null) {
                        // Apply filters
                        boolean matchesStatusFilter = currentFilter.equals("all") || 
                            model.getOrderStatus().equals(currentFilter);
                        boolean matchesDateFilter = filterByDate(model.getOrderDate());
                        
                        if (matchesStatusFilter && matchesDateFilter) {
                            orderModels.add(model);
                        }
                    }
                }
                
                // Show/hide empty state
                if (orderModels.isEmpty()) {
                    binding.emptyStateText.setVisibility(View.VISIBLE);
                    binding.itemRecycler.setVisibility(View.GONE);
                } else {
                    binding.emptyStateText.setVisibility(View.GONE);
                    binding.itemRecycler.setVisibility(View.VISIBLE);
                }
                
                binding.swipeRefreshLayout.setRefreshing(false);
                orderProductAdapter.notifyDataSetChanged();
                isLoading = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.swipeRefreshLayout.setRefreshing(false);
                isLoading = false;
                // Show error message
                // Toast.makeText(AllOrderActivity.this, getString(R.string.error_loading_orders), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean filterByDate(Date orderDate) {
        if (orderDate == null || currentDateFilter.equals("all_time")) {
            return true;
        }

        Calendar calendar = Calendar.getInstance();
        Calendar orderCalendar = Calendar.getInstance();
        orderCalendar.setTime(orderDate);

        switch (currentDateFilter) {
            case "last_month":
                calendar.add(Calendar.MONTH, -1);
                return orderCalendar.after(calendar);
                
            case "last_3_months":
                calendar.add(Calendar.MONTH, -3);
                return orderCalendar.after(calendar);
                
            default:
                return true;
        }
    }

    private void refreshData() {
        loadInitialData();
    }

    private void getMoreOrders(String filter) {
        // Increase itemCount for loading more items
        pageSize += 10;
        loadInitialData();

    }

    @Override
    protected void onDestroy() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().show();
        }
        super.onDestroy();
    }
}