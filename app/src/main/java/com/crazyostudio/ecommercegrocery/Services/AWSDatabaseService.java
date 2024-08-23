package com.crazyostudio.ecommercegrocery.Services;

import android.util.Log;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.crazyostudio.ecommercegrocery.HelperClass.DynamoDBHelper;
import com.crazyostudio.ecommercegrocery.Model.ProductModel;

import java.util.ArrayList;
import java.util.List;

public class AWSDatabaseService {
    private static final String TAG = "DatabaseService";
    private final DynamoDBMapper dynamoDBMapper;

    public AWSDatabaseService() {
        this.dynamoDBMapper = DynamoDBHelper.getDynamoDBMapper();
    }

    public interface GetAllProductsCallback {
        void onSuccess(ArrayList<ProductModel> products);
        void onError(String errorMessage);
    }

    public interface GetProductByIdCallback {
        void onSuccess(ProductModel product);
        void onError(String errorMessage);
    }

    public void getAllProducts(GetAllProductsCallback callback) {
        new Thread(() -> {
            try {
                List<ProductModel> productList = dynamoDBMapper.scan(ProductModel.class, new DynamoDBScanExpression());
                ArrayList<ProductModel> products = new ArrayList<>(productList);
                callback.onSuccess(products);
            } catch (Exception e) {
                Log.e(TAG, "Error fetching products: ", e);
                callback.onError(e.getMessage());
            }
        }).start();
    }

    public void getProductById(String productId, GetProductByIdCallback callback) {
        new Thread(() -> {
            try {
                ProductModel product = dynamoDBMapper.load(ProductModel.class, productId);
                if (product != null) {
                    callback.onSuccess(product);
                } else {
                    callback.onError("Product not found");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error fetching product by ID: ", e);
                callback.onError(e.getMessage());
            }
        }).start();
    }
}
