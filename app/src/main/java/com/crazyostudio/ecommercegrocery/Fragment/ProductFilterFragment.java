package com.crazyostudio.ecommercegrocery.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import com.crazyostudio.ecommercegrocery.Adapter.ProductAdapter;
import com.crazyostudio.ecommercegrocery.Model.ProductModel;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.databinding.FragmentProductFilterBinding;
import com.crazyostudio.ecommercegrocery.interfaceClass.onClickProductAdapter;
import com.crazyostudio.ecommercegrocery.javaClasses.basicFun;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProductFilterFragment extends Fragment implements onClickProductAdapter {
    private static final String FILTER = "filter";
    private FragmentProductFilterBinding binding;
    private String category;
    private FirebaseDatabase firebaseDatabase;
    private ProductAdapter productAdapter;
    private ArrayList<ProductModel> model;
    public ProductFilterFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = getArguments().getString(FILTER);
        }else {
            requireActivity().finish();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProductFilterBinding.inflate(inflater,container,false);
        firebaseDatabase = FirebaseDatabase.getInstance();
        binding.titleSpinner.setText(category);
        getProduct();
        binding.orderDetailsViewBack.setOnClickListener(back-> requireActivity().onBackPressed());
        return binding.getRoot();
    }

    private void getProduct() {
        model = new ArrayList<>();
        productAdapter = new ProductAdapter(model,this,requireContext(),"Main");
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        binding.ProductLIST.setAdapter(productAdapter);
        binding.ProductLIST.setLayoutManager(layoutManager);
        firebaseDatabase.getReference().child("Product").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                model.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    ProductModel productModel = snapshot1.getValue(ProductModel.class);
                    if (productModel != null && productModel.isAvailable()&&productModel.getCategory().equals(category)) {
                        model.add(productModel);
                        binding.progressCircular.setVisibility(View.GONE);
                        productAdapter.notifyDataSetChanged();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                basicFun.AlertDialog(requireContext(),error.toString());
            }
        });

    }



    @Override
    public void onClick(ProductModel productModel) {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putParcelable("productDetails", productModel);
        bundle.putInt("backButton", 0);
        ProductDetailsFragment productDetailsFragment = new ProductDetailsFragment();
        productDetailsFragment.setArguments(bundle);
//        productDetailsFragment.setArguments(bundle);
        transaction.replace(R.id.loader,productDetailsFragment,"ProductFilterFragment");
        transaction.addToBackStack("ProductFilterFragment");
        transaction.commit();

    }
}