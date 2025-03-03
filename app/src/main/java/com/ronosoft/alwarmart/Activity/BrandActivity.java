package com.ronosoft.alwarmart.Activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.ronosoft.alwarmart.Adapter.ProductAdapter;
import com.ronosoft.alwarmart.Component.ProductViewCard;
import com.ronosoft.alwarmart.Model.BrandModel;
import com.ronosoft.alwarmart.Model.ProductModel;
import com.ronosoft.alwarmart.R;
import com.ronosoft.alwarmart.Services.BrandService;
import com.ronosoft.alwarmart.Services.DatabaseService;
import com.ronosoft.alwarmart.databinding.ActivityBrandBinding;

import java.util.ArrayList;

public class BrandActivity extends AppCompatActivity {
    ActivityBrandBinding binding;
    private static final String ARG_BRAND = "brand";
    private static final String TAG = "BrandActivity";
    private String BrandName;
    ArrayList<ProductModel> productModel;
    DatabaseService databaseService;
    BrandService brandService;
    ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBrandBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeComponents();
        setupListeners();
        setupAdapter();
        loadBrandData();
    }

    private void initializeComponents() {
        BrandName = getIntent().getStringExtra(ARG_BRAND);
        if (BrandName == null) {
            finish();
            return;
        }
        
        databaseService = new DatabaseService();
        brandService = new BrandService(this);
        productModel = new ArrayList<>();
    }

    private void setupListeners() {
        binding.backBtn.setOnClickListener(v -> onBackPressed());
        binding.searchBta.setOnClickListener(v -> openSearchFragment());
    }

    private void openSearchFragment() {
        Intent intent = new Intent(BrandActivity.this, FragmentLoader.class);
        intent.putExtra("LoadID", "search");
        startActivity(intent);
    }

    private void loadBrandData() {
        brandService.getAllBrandWithIconsById(BrandName, new BrandService.addBrandsByIdListener() {
            @Override
            public void onFailure(Exception error) {
                runOnUiThread(() -> {
                    binding.errorMessage.setText(error.getMessage());
                    binding.errorState.setVisibility(View.VISIBLE);
                });
            }

            @Override
            public void onSuccess(BrandModel brandModel) {
                runOnUiThread(() -> {
                    updateBrandUI(brandModel);
                    getProducts(brandModel);
                });
            }
        });
    }

    private void updateBrandUI(BrandModel brandModel) {
        binding.brandName.setText(brandModel.getBrandName());
        Glide.with(this)
            .load(brandModel.getBrandIcon())
            .placeholder(R.drawable.product_image_shimmee_effect)
            .into(binding.brandIcon);
    }

    private void getProducts(BrandModel brandModel) {
        databaseService.getBrandProducts(brandModel.getBrandName(), new DatabaseService.GetAllProductsCallback() {
            @Override
            public void onSuccess(ArrayList<ProductModel> products) {
                runOnUiThread(() -> {
                    productModel.clear();
                    productModel.addAll(products);
                    productAdapter.notifyDataSetChanged();
                    binding.errorState.setVisibility(View.GONE);
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    binding.errorMessage.setText(errorMessage);
                    binding.errorState.setVisibility(View.VISIBLE);
                });
            }
        });
    }

    private void setupAdapter() {
        productAdapter = new ProductAdapter(
            productModel,
            (productModel, sameProducts) -> {
                ArrayList<ProductModel> limitedProducts = new ArrayList<>(
                    sameProducts.subList(0, Math.min(sameProducts.size(), 10))
                );
                new ProductViewCard(this).showProductViewDialog(productModel, limitedProducts);
            },
            this,
            ""
        );

        binding.Products.setAdapter(productAdapter);
        binding.Products.setLayoutManager(
            new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Bundle clean = new Bundle();
        clean.putString(ARG_BRAND, BrandName);
        super.onSaveInstanceState(clean);
    }
}
