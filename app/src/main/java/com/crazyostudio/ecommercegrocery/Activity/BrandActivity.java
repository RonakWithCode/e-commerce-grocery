package com.crazyostudio.ecommercegrocery.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.crazyostudio.ecommercegrocery.Adapter.ProductAdapter;
import com.crazyostudio.ecommercegrocery.Component.ProductViewCard;
import com.crazyostudio.ecommercegrocery.Fragment.SearchFragment;
import com.crazyostudio.ecommercegrocery.Model.BrandModel;
import com.crazyostudio.ecommercegrocery.Model.ProductModel;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.Services.BrandService;
import com.crazyostudio.ecommercegrocery.Services.DatabaseService;
import com.crazyostudio.ecommercegrocery.databinding.ActivityBrandBinding;

import java.util.ArrayList;

public class BrandActivity extends AppCompatActivity {
    ActivityBrandBinding binding;
    private static final String ARG_BRAND = "brand";
    private static final String TAG = "SliderBrandFragment";
    private String BrandName;
    ArrayList<ProductModel> productModel;

//    FragmentSliderBrandBinding binding;
    DatabaseService databaseService;
    BrandService brandService;
    ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBrandBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BrandName = getIntent().getStringExtra(ARG_BRAND);
        if (BrandName == null) {
         finish();
        }
        databaseService =  new DatabaseService();
        brandService =  new BrandService(this);


        binding.backBtn.setOnClickListener(v -> {
            this.onBackPressed();
        });
        productModel = new ArrayList<>();

        setupAdpter();
        loadBrandData();
        binding.searchBta.setOnClickListener(view -> openSearchFragment());
    }


    private void openSearchFragment() {
//        if (isAdded() && getActivity() != null) {
//        finish();
//        SearchFragment fragment  = new SearchFragment();
//            Bundle bundle = new Bundle();
//            bundle.putParcelableArrayList("model",new ArrayList<>());
//            fragment.setArguments(bundle);
//            this.getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.loader,fragment)
//                    .addToBackStack("HomeFragment")
//                    .commit();


//        }

        Intent intent = new Intent(BrandActivity.this, FragmentLoader.class);
        intent.putExtra("LoadID","search");
//        intent.putParcelableArrayListExtra("model",productModel);
        startActivity(intent);
    }

    void loadBrandData(){
        brandService.getAllBrandWithIconsById(BrandName, new BrandService.addBrandsByIdListener() {
            @Override
            public void onFailure(Exception error) {

            }

            @Override
            public void onSuccess(BrandModel brandModel) {
                binding.brandName.setText(brandModel.getBrandName());
                Glide
                        .with(BrandActivity.this)
                        .load(brandModel.getBrandIcon())
                        .placeholder(R.drawable.product_image_shimmee_effect)
                        .into(binding.brandIcon);
                getProducts(brandModel);
//                Glide.with(this)
//                        .load(brandModel.getBrandIcon())
            }
        });
    }

    private void getProducts(BrandModel brandModel) {
        databaseService.getBrandProducts(brandModel.getBrandName(), new DatabaseService.GetAllProductsCallback() {
            @Override
            public void onSuccess(ArrayList<ProductModel> products) {
//                Log.i(TAG, "onSuccess: "+products.size());
                productModel.addAll(products);
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String errorMessage) {
                binding.errorMessage.setText(errorMessage);
                binding.errorState.setVisibility(View.VISIBLE);
            }
        });

    }

    void setupAdpter(){
        productAdapter = new ProductAdapter(productModel, (productModel, sameProducts) -> {
//
            new ProductViewCard(BrandActivity.this).showProductViewDialog(productModel,sameProducts);
        },this,"");

        binding.Products.setAdapter(productAdapter);
        binding.Products.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

    }

}
