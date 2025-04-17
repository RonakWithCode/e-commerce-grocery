package com.ronosoft.alwarmart.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ronosoft.alwarmart.Adapter.ViewOrderProductAdapter;
import com.ronosoft.alwarmart.HelperClass.ValuesHelper;
import com.ronosoft.alwarmart.Model.OrderModel;
import com.ronosoft.alwarmart.R;
import com.ronosoft.alwarmart.Services.AuthService;
import com.ronosoft.alwarmart.databinding.ActivityAllOrderBinding;

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
    private String lastOrderId;
    private AuthService auth;
    private ValueEventListener ordersListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = new AuthService();
        if (!auth.IsLogin()) {
            Toast.makeText(this, "Please log in to view orders", Toast.LENGTH_SHORT).show();
            finish();
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
            String selectedFilter = filterOptions[position];
            currentFilter = mapFilterOption(selectedFilter);
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

    private String mapFilterOption(String selectedFilter) {
        switch (selectedFilter.toLowerCase()) {
            case "all orders":
                return "all";
            case "processing":
                return ValuesHelper.PROCESSING;
            case "confirmed":
                return ValuesHelper.CONFIRMED;
            case "out for delivery":
                return ValuesHelper.OUTFORDELIVERY;
            case "delivered":
                return ValuesHelper.DELIVERED;
            case "cancelled":
                return ValuesHelper.CANCELLED;
            case "customer rejected":
                return ValuesHelper.CUSTOMER_REJECTED;
            case "customer not available":
                return ValuesHelper.CUSTOMER_NOT_AVAILABLE;
            default:
                return "all";
        }
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.itemRecycler.setLayoutManager(layoutManager);
        binding.itemRecycler.setHasFixedSize(true);

        orderModels = new ArrayList<>();
        orderProductAdapter = new ViewOrderProductAdapter(orderModels, this, orderModel -> {
            if (orderModel != null && orderModel.getOrderId() != null) {
                Intent intent = new Intent(AllOrderActivity.this, OrderDetailsActivity.class);
                intent.putExtra("orderId", orderModel.getOrderId());
                intent.putExtra("orderModel", orderModel);
                startActivity(intent);
            }
        });

        binding.itemRecycler.setAdapter(orderProductAdapter);

        binding.itemRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy <= 0 || isLoading || isFinishing()) return;

                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

                if (totalItemCount <= (lastVisibleItemPosition + 3)) {
                    isLoading = true;
                    loadMoreOrders();
                }
            }
        });
    }

    private void loadInitialData() {
        if (isFinishing() || isDestroyed()) return;

        database = FirebaseDatabase.getInstance();
        Query query = database.getReference("Order")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .orderByChild("orderDate")
                .limitToLast(pageSize);

        ordersListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isFinishing()) {
                    processOrderData(snapshot, true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                handleDatabaseError(error);
            }
        };
        query.addListenerForSingleValueEvent(ordersListener);
    }

    private void loadMoreOrders() {
        if (isFinishing() || isDestroyed() || lastOrderId == null) {
            isLoading = false;
            return;
        }

        Query query = database.getReference("Order")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .orderByChild("orderDate")
                .endBefore(lastOrderId)
                .limitToLast(pageSize);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isFinishing()) {
                    processOrderData(snapshot, false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                handleDatabaseError(error);
            }
        });
    }

    private void processOrderData(DataSnapshot snapshot, boolean isInitialLoad) {
        if (isInitialLoad) {
            orderModels.clear();
        }

        ArrayList<OrderModel> tempOrders = new ArrayList<>();
        for (DataSnapshot child : snapshot.getChildren()) {
            OrderModel model = child.getValue(OrderModel.class);
            if (model != null && model.getOrderId() != null) {
                boolean matchesStatusFilter = currentFilter.equals("all") ||
                        (model.getOrderStatus() != null && model.getOrderStatus().equals(currentFilter));
                boolean matchesDateFilter = filterByDate(model.getOrderDate());
                if (matchesStatusFilter && matchesDateFilter) {
                    tempOrders.add(model);
                }
            }
        }

        if (!tempOrders.isEmpty()) {
            orderModels.addAll(tempOrders);
            lastOrderId = tempOrders.get(tempOrders.size() - 1).getOrderId();
        }

        updateUI();
        isLoading = false;
    }

    private void updateUI() {
        binding.swipeRefreshLayout.setRefreshing(false);
        if (orderModels.isEmpty()) {
            binding.emptyStateText.setVisibility(View.VISIBLE);
            binding.itemRecycler.setVisibility(View.GONE);
        } else {
            binding.emptyStateText.setVisibility(View.GONE);
            binding.itemRecycler.setVisibility(View.VISIBLE);
        }
        if (!isFinishing()) {
            orderProductAdapter.notifyDataSetChanged();
        }
    }

    private void handleDatabaseError(DatabaseError error) {
        if (!isFinishing()) {
            binding.swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(AllOrderActivity.this, "Failed to load orders: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            isLoading = false;
        }
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
        lastOrderId = null;
        loadInitialData();
    }

    @Override
    protected void onDestroy() {
        if (ordersListener != null && database != null) {
            database.getReference("Order").removeEventListener(ordersListener);
        }
        binding = null;
        super.onDestroy();
    }
}