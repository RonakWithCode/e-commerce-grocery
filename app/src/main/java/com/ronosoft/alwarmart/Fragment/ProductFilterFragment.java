package com.ronosoft.alwarmart.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;

import com.ronosoft.alwarmart.Adapter.ProductAdapter;
import com.ronosoft.alwarmart.Component.ProductViewCard;
import com.ronosoft.alwarmart.Model.ProductModel;
import com.ronosoft.alwarmart.R;
import com.ronosoft.alwarmart.databinding.FragmentProductFilterBinding;
import com.ronosoft.alwarmart.interfaceClass.onClickProductAdapter;
import com.ronosoft.alwarmart.javaClasses.basicFun;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ProductFilterFragment extends Fragment implements onClickProductAdapter {

    private static final String FILTER = "filter";
    private FragmentProductFilterBinding binding;
    private String category;
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
        } else {
            requireActivity().finish();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProductFilterBinding.inflate(inflater, container, false);
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        binding.titleSpinner.setText(category);
        getProduct();

        binding.search.setOnClickListener(view -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.loader, new SearchFragment())
                    .addToBackStack("ProductFilterFragment")
                    .commit();
        });

        binding.orderDetailsViewBack.setOnClickListener(back -> requireActivity().onBackPressed());

        return binding.getRoot();
    }

    private void getProduct() {
        model = new ArrayList<>();
        // Pass getViewLifecycleOwner() as the first parameter.
        productAdapter = new ProductAdapter(getViewLifecycleOwner(), model, this, requireContext());
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        binding.ProductLIST.setAdapter(productAdapter);
        binding.ProductLIST.setLayoutManager(layoutManager);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Product")
                .whereEqualTo("available", true)
                .whereEqualTo("category", category)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    model.clear();
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                        ProductModel productModel = snapshot.toObject(ProductModel.class);
                        if (productModel != null) {
                            model.add(productModel);
                        }
                    }
                    binding.progressCircular.setVisibility(View.GONE);
                    productAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    basicFun.AlertDialog(requireContext(), e.toString());
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
        }
    }

    @Override
    public void onClick(ProductModel productModel, ArrayList<ProductModel> sameProducts) {
        new ProductViewCard(requireActivity()).showProductViewDialog(productModel,sameProducts);

    }
}
