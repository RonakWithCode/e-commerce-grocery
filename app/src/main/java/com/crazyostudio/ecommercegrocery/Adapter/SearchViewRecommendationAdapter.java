package com.crazyostudio.ecommercegrocery.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.crazyostudio.ecommercegrocery.Activity.AuthMangerActivity;
import com.crazyostudio.ecommercegrocery.DAO.CartDAOHelper;
import com.crazyostudio.ecommercegrocery.DAO.ShoppingCartFirebaseModelDAO;
import com.crazyostudio.ecommercegrocery.Manager.ProductManager;
import com.crazyostudio.ecommercegrocery.Model.ProductModel;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.Services.AuthService;
import com.crazyostudio.ecommercegrocery.Services.DatabaseService;
import com.crazyostudio.ecommercegrocery.databinding.ProductViewSerachBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class SearchViewRecommendationAdapter extends RecyclerView.Adapter<SearchViewRecommendationAdapter.SearchViewRecommendationAdapterViewHolder> {
    private ArrayList<ProductModel> dataList;
     Context context;
//    SearchAdapterInterface searchAdapterInterface;
    List<ShoppingCartFirebaseModelDAO> daoList;
    ProductManager productManager;

    public SearchViewRecommendationAdapter(ArrayList<ProductModel> modelArrayList ,Context context) {
        this.dataList = modelArrayList;
        this.context = context;
//        this.searchAdapterInterface = searchAdapterInterface;
        productManager = new ProductManager(context);
        daoList = productManager.getAllRoomCartData();

    }

    @NonNull
    @Override
    public SearchViewRecommendationAdapter.SearchViewRecommendationAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchViewRecommendationAdapter.SearchViewRecommendationAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.product_view_serach, parent, false));

    }


    public interface checkInCartCall{
        void notFound();
        void found(int selectQTY);
    }



    void checkInCart(String checkId , checkInCartCall callback){
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
        ProductModel model = dataList.get(position);

        Glide.with(context).load(model.getProductImage()
                .get(0))
                .placeholder(R.drawable.skeleton_shape).into(holder.binding.productImage);
        holder.binding.productName.setText(model.getProductName());
        holder.binding.productPrice.setText("₹"+model.getPrice());

//        if (checkInCart(model.getProductId())) {
//            holder.binding.addToCartButton.setVisibility(View.GONE);
//            holder.binding.quantityController.setVisibility(View.VISIBLE);
//        }

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            checkInCart(model.getProductId(), new checkInCartCall() {
                @Override
                public void notFound() {
    //
                }

                @Override
                public void found(int selectQTY) {
                    holder.binding.addToCartButton.setVisibility(View.GONE);
                    holder.binding.productQtyLayout.setVisibility(View.VISIBLE);
                    holder.binding.productQty.setText(String.valueOf(selectQTY));
                }
            });
        }



        holder.binding.addToCartButton.setOnClickListener(v -> {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                new DatabaseService().checkCartByIdAndAdd(model.getProductId(), model.getMinSelectableQuantity(), new DatabaseService.AddCartByIdAndADD() {
                    @Override
                    public void added() {
                        holder.binding.addToCartButton.setVisibility(View.GONE);
                        holder.binding.productQtyLayout.setVisibility(View.VISIBLE);
                        holder.binding.productQty.setText(model.getSelectableQuantity()+"");
                    }

                    @Override
                    public void ExistsInCart(int DefaultQuantity) {
                        holder.binding.addToCartButton.setVisibility(View.GONE);
                        holder.binding.productQtyLayout.setVisibility(View.VISIBLE);
                        holder.binding.productQty.setText(DefaultQuantity+"");

                    }

                    @Override
                    public void failure(Exception error) {

                    }

                });
            } else {
                context.startActivity(new Intent(context, AuthMangerActivity.class));
            }
        });

        holder.binding.productQtyUp.setOnClickListener(up -> {
            int quantity = model.getSelectableQuantity();
            int maxSelected = model.getMaxSelectableQuantity();
            quantity++;
            if (quantity > model.getStockCount() || quantity > maxSelected) {
                holder.binding.productQty.setText(String.valueOf(model.getSelectableQuantity()));
                String message = quantity > model.getStockCount() ? "Max stock available: " + model.getStockCount() : "Hey Alwar Mart set the limit of " + maxSelected;
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            } else {
                model.setSelectableQuantity(quantity);
//                searchAdapterInterface.UpdateQTY(data);
                UpdateQTY(model);
                holder.binding.productQty.setText(String.valueOf(model.getSelectableQuantity()));
            }
        });

        holder.binding.productQtyDown.setOnClickListener(Down -> {
            int quantity = model.getSelectableQuantity();
            int minSelected = model.getMinSelectableQuantity();
            if (quantity > minSelected) {
                quantity--;
                model.setSelectableQuantity(quantity);
                UpdateQTY(model);
//                searchAdapterInterface.UpdateQTY(model);

                holder.binding.productQty.setText(String.valueOf(model.getSelectableQuantity()));
            } else if (quantity == minSelected) {
                productManager.RemoveCartProductById(new AuthService().getUserId(), model.getProductId());
//                searchAdapterInterface.Remove(model);
            } else {
                Toast.makeText(context, "Alwar Mart set this limit min select " + minSelected, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }


    private void UpdateQTY(ProductModel newModel){
        productManager.UpdateCartQuantityById(new AuthService().getUserId(),newModel.getProductId(),newModel.getSelectableQuantity());

//        return false;
    }


    public static class SearchViewRecommendationAdapterViewHolder extends RecyclerView.ViewHolder {
        ProductViewSerachBinding binding;

        public SearchViewRecommendationAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ProductViewSerachBinding.bind(itemView);
        }
    }
}
