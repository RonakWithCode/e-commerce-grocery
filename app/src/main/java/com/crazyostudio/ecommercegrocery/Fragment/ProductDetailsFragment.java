package com.crazyostudio.ecommercegrocery.Fragment;

import static androidx.recyclerview.widget.RecyclerView.*;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.crazyostudio.ecommercegrocery.Activity.AuthMangerActivity;
import com.crazyostudio.ecommercegrocery.Adapter.ProductAdapter;
import com.crazyostudio.ecommercegrocery.Adapter.ProductDisplayImagesAdapter;
import com.crazyostudio.ecommercegrocery.Model.ProductModel;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.databinding.ActivityAuthMangerBinding;
import com.crazyostudio.ecommercegrocery.databinding.FragmentProductDetailsBinding;
import com.crazyostudio.ecommercegrocery.interfaceClass.onClickProductAdapter;
import com.crazyostudio.ecommercegrocery.javaClasses.basicFun;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

import java.util.ArrayList;
import java.util.Objects;

import me.ibrahimsn.lib.SmoothBottomBar;

public class ProductDetailsFragment extends Fragment implements onClickProductAdapter {
    private FragmentProductDetailsBinding binding;
    private ProductModel productModel;
    private FragmentTransaction transaction;
    private boolean IsChatsProgressBar = false;

    public ProductDetailsFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            productModel = getArguments().getParcelable("productDetails");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProductDetailsBinding.inflate(inflater,container,false);
        transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        if (IsChatsProgressBar){
            binding.recyclerViewProgressBar.setVisibility(GONE);
        }
        initValue();
        binding.AddTOCart.setOnClickListener(view -> AddTOCart());
        binding.plusBtn.setOnClickListener(view -> {
            int quantity = productModel.getSelectProductQuantity();
            quantity++;
            if(quantity>productModel.getStock()) {
                Toast.makeText(requireContext(), "Max stock available: "+ productModel.getStock(), Toast.LENGTH_SHORT).show();
            } else {
                productModel.setSelectProductQuantity(quantity);
                binding.quantity.setText(String.valueOf(quantity));
            }
        });
        binding.minusBtn.setOnClickListener(view -> {
            int quantity = productModel.getSelectProductQuantity();
            if(quantity > 1)
                quantity--;
            productModel.setSelectProductQuantity(quantity);
            binding.quantity.setText(String.valueOf(quantity));

        });
        return binding.getRoot();
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor", "NotifyDataSetChanged"})
    private void initValue() {
        if (FirebaseAuth.getInstance().getCurrentUser() !=null) {

            String userId = FirebaseAuth.getInstance().getUid();
            assert userId != null;
            DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("Cart").child(userId);
            String productNameToFind = productModel.getId();
            Query query = productsRef.orderByKey().equalTo(productNameToFind);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        binding.AddTOCart.setText("Go to Cart");
                        binding.quantityBox.setVisibility(INVISIBLE);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle the error in case of a database error.
                    Toast.makeText(getContext(), "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        binding.category.setText(productModel.getCategory());
        binding.ProductTitle.setText(productModel.getItemName());
        for (int i = 0; i < productModel.getProductImages().size(); i++) {
            binding.productsImages.addData(new CarouselItem(productModel.getProductImages().get(i)));
        }
        double percentageSaved = ((productModel.getMRP() - productModel.getPrice()) / productModel.getMRP()) * 100;
        binding.Save.setText("-"+percentageSaved+"%");
        binding.Price.setText("₹"+productModel.getPrice());
        binding.PricePerUnit.setText("(₹"+productModel.getPrice()+" / "+ productModel.getQuantity()+productModel.getItemUnit()+")");
        binding.MRP.setText("M.R.P.: ₹"+productModel.getMRP());
        if (productModel.getStock()==0){
            binding.OutOfStockBuyOptions.setVisibility(VISIBLE);
            binding.quantity.setText("0");
            binding.TextInStock.setText("Out of Stock");
            binding.quantityBox.setVisibility(INVISIBLE);
            binding.TextInStock.setTextColor(R.color.FixRed);
            binding.AddTOCart.setVisibility(INVISIBLE);
            binding.BuyNow.setVisibility(INVISIBLE);
        }
        binding.categoryType.setText(productModel.getCategory());
        binding.categoryType.setText(productModel.getQuantity()+productModel.getItemUnit());
        binding.dietType.setText("veg");
//        binding.MRP.setText("M.R.P.: ₹"+productModel.getMRP());
//        ArrayList<String>
        ProductDisplayImagesAdapter productDisplayImagesAdapter = new ProductDisplayImagesAdapter(productModel.getProductImages(),getContext());
        LinearLayoutManager manager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        binding.ProductItemImage.setLayoutManager(manager);
        binding.ProductItemImage.setAdapter(productDisplayImagesAdapter);
        productDisplayImagesAdapter.notifyDataSetChanged();
        LoadProduct();
    }
    void LoadProduct(){
        ArrayList<ProductModel> model = new ArrayList<>();
        ProductAdapter productAdapter = new ProductAdapter(model,this,requireContext(),"HORIZONTAL");
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false);
        binding.itemCategory.setAdapter(productAdapter);
        binding.itemCategory.setLayoutManager(layoutManager);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference().child("Product").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                model.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    ProductModel productModel = snapshot1.getValue(ProductModel.class);
                    if (productModel != null && productModel.isLive()) {
                        model.add(productModel);
                        IsChatsProgressBar = true;
                        binding.recyclerViewProgressBar.setVisibility(View.GONE);
                    }
                }
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                basicFun.AlertDialog(requireContext(),error.toString());
            }
        });
    }

    public void buyNow() {


    }
    public void AddTOCart() {
        if (FirebaseAuth.getInstance().getCurrentUser()!=null){
            DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("Cart").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
            String productNameToFind = productModel.getId();
            Query query = productsRef.orderByKey().equalTo(productNameToFind);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.loader,new ShoppingCartsFragment(),"ShoppingCartsFragment");
                        transaction.addToBackStack("ShoppingCartsFragment");
                        transaction.commit();
                        SmoothBottomBar smoothBottomBar = requireActivity().findViewById(R.id.bottomBar);
                        smoothBottomBar.setItemActiveIndex(1);
                    }else {
                        productsRef.child(productModel.getId()).setValue(productModel).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                binding.AddTOCart.setText("Go to Cart");
                            }
                        }).addOnFailureListener(error -> basicFun.AlertDialog(requireContext(),error.toString()));
                    }
                }
                @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle the error in case of a database error.
                        Toast.makeText(getContext(), "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        else {
            basicFun.AlertDialog(requireContext(),"Please Sign up for add to cart");
            startActivity(new Intent(getContext(),AuthMangerActivity.class));
        }

    }

    @Override
    public void onClick(ProductModel productModel) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("productDetails", productModel);
        ProductDetailsFragment productDetailsFragment = new ProductDetailsFragment();
        productDetailsFragment.setArguments(bundle);
        transaction.replace(R.id.loader,productDetailsFragment,"productDetailsFragment");
        transaction.addToBackStack("productDetailsFragment").commit();
    }

}