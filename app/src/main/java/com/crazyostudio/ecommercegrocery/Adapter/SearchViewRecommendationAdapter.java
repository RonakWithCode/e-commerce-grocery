package com.crazyostudio.ecommercegrocery.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.crazyostudio.ecommercegrocery.Activity.AuthMangerActivity;
import com.crazyostudio.ecommercegrocery.DAO.ShoppingCartFirebaseModelDAO;
import com.crazyostudio.ecommercegrocery.Model.HomeProductModel;
import com.crazyostudio.ecommercegrocery.Model.ProductModel;
import com.crazyostudio.ecommercegrocery.Model.ShoppingCartFirebaseModel;
import com.crazyostudio.ecommercegrocery.Model.ShoppingCartsProductModel;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.Services.DatabaseService;
import com.crazyostudio.ecommercegrocery.databinding.ProductViewSerachBinding;
import com.crazyostudio.ecommercegrocery.interfaceClass.onClickProductAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchViewRecommendationAdapter extends RecyclerView.Adapter<SearchViewRecommendationAdapter.SearchViewRecommendationAdapterViewHolder> {
    ArrayList<ProductModel> productModels;
    Context context;

    com.crazyostudio.ecommercegrocery.interfaceClass.onClickProductAdapter onClickProductAdapter;
    List<ShoppingCartFirebaseModelDAO> daoList = new ArrayList<>();


    public SearchViewRecommendationAdapter(ArrayList<ProductModel> productModels, Context context, com.crazyostudio.ecommercegrocery.interfaceClass.onClickProductAdapter onClickProductAdapter) {
        this.productModels = productModels;
        this.context = context;
        this.onClickProductAdapter = onClickProductAdapter;
        new DatabaseService().getCartRoomDatabase(context, new DatabaseService.addCartRoomDatabase() {
            @Override
            public void Found(List<ShoppingCartFirebaseModelDAO> list) {
                daoList.clear();
                daoList.addAll(list);
            }

            @Override
            public void notFound() {

            }
        });

    }

    @NonNull
    @Override
    public SearchViewRecommendationAdapter.SearchViewRecommendationAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchViewRecommendationAdapter.SearchViewRecommendationAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.product_view_serach, parent, false));

    }




    interface checkInCartCallback{
        void notFound();
        void found(int selectQTY);
    }


    void checkInCart(String checkId ,checkInCartCallback callback){
        for (int i = 0; i < daoList.size(); i++) {
            ShoppingCartFirebaseModelDAO id = daoList.get(i);
            if (id.getProductId().equals(checkId)) {
                callback.found(id.getProductSelectQuantity());
            }
        }
        callback.notFound();
    }




    @Override
    public void onBindViewHolder(@NonNull SearchViewRecommendationAdapter.SearchViewRecommendationAdapterViewHolder holder, int position) {
        ProductModel model = productModels.get(position);

        Glide.with(context).load(model.getProductImage()
                .get(0))
                .placeholder(R.drawable.skeleton_shape).into(holder.binding.productImage);
        holder.binding.productName.setText(model.getProductName());
        holder.binding.productPrice.setText("₹"+model.getPrice());

//        if (checkInCart(model.getProductId())) {
//            holder.binding.addToCartButton.setVisibility(View.GONE);
//            holder.binding.quantityController.setVisibility(View.VISIBLE);
//        }


        checkInCart(model.getProductId(), new checkInCartCallback() {
            @Override
            public void notFound() {
//
            }

            @Override
            public void found(int selectQTY) {
                holder.binding.addToCartButton.setVisibility(View.GONE);
                holder.binding.quantityController.setVisibility(View.VISIBLE);
                holder.binding.currentQuantity.setText(String.valueOf(selectQTY));
            }
        });
        holder.binding.increaseQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.binding.decreaseQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.binding.addToCartButton.setOnClickListener(v -> {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//                    ShoppingCartFirebaseModel shoppingCartFirebaseModel = new ShoppingCartFirebaseModel(productModel.getProductId(), productModel.getDefaultQuantity());
                new DatabaseService().checkCartByIdAndAdd(model.getProductId(), model.getMinSelectableQuantity(), new DatabaseService.AddCartByIdAndADD() {
                    @Override
                    public void added() {
                        holder.binding.addToCartButton.setVisibility(View.GONE);
                        holder.binding.quantityController.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void ExistsInCart(int DefaultQuantity) {
                        holder.binding.addToCartButton.setVisibility(View.GONE);
                        holder.binding.quantityController.setVisibility(View.VISIBLE);
                        holder.binding.currentQuantity.setText(DefaultQuantity+"");

                    }

                    @Override
                    public void failure(Exception error) {

                    }

                });
            } else {
                // User not authenticated, navigate to authentication screen
//                    startActivity(new Intent(getContext(), AuthMangerActivity.class));
            }

        });

    }

    @Override
    public int getItemCount() {
        return productModels.size();
    }

    public static class SearchViewRecommendationAdapterViewHolder extends RecyclerView.ViewHolder {
        ProductViewSerachBinding binding;

        public SearchViewRecommendationAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ProductViewSerachBinding.bind(itemView);
        }
    }
}
