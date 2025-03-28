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

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ronosoft.alwarmart.Adapter.ProductAdapter;
import com.ronosoft.alwarmart.Component.ProductViewCard;
import com.ronosoft.alwarmart.Model.ProductModel;
import com.ronosoft.alwarmart.R;
import com.ronosoft.alwarmart.databinding.FragmentProductFilterByQueryBinding;
import com.ronosoft.alwarmart.interfaceClass.onClickProductAdapter;
import com.ronosoft.alwarmart.javaClasses.basicFun;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class ProductFilterByQueryFragment extends Fragment implements onClickProductAdapter {

    private static final String FILTER = "filter";
    private static final String FILTER_NAME = "filterName";
    private FragmentProductFilterByQueryBinding binding;
    private String category;
    private String categoryName;
    private ProductAdapter productAdapter;
    private ArrayList<ProductModel> model;

    public ProductFilterByQueryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = getArguments().getString(FILTER);
            categoryName = getArguments().getString(FILTER_NAME);
        } else {
            requireActivity().finish();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProductFilterByQueryBinding.inflate(inflater, container, false);
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        binding.titleSpinner.setText(categoryName);
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
        // FIX: Pass getViewLifecycleOwner() as the first argument.
        productAdapter = new ProductAdapter(getViewLifecycleOwner(), model, this, requireContext());
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        binding.ProductLIST.setAdapter(productAdapter);
        binding.ProductLIST.setLayoutManager(layoutManager);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("SpecialProduct").document(category).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    ArrayList<String> productIdList = (ArrayList<String>) document.get("products");
                    if (productIdList != null) {
                        model.clear();
                        int numProducts = productIdList.size();
                        AtomicInteger productsProcessed = new AtomicInteger(0);
                        for (String productId : productIdList) {
                            db.collection("Product").document(productId).get().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    DocumentSnapshot productSnapshot = task1.getResult();
                                    if (productSnapshot.exists()) {
                                        ProductModel productModel = productSnapshot.toObject(ProductModel.class);
                                        if (productModel != null) {
                                            model.add(productModel);
                                        }
                                    }
                                }
                                productsProcessed.incrementAndGet();
                                if (productsProcessed.get() == numProducts) {
                                    binding.progressCircular.setVisibility(View.GONE);
                                    productAdapter.notifyDataSetChanged();
                                }
                            }).addOnFailureListener(e -> {
                                basicFun.AlertDialog(requireContext(), e.toString());
                            });
                        }
                    }
                }
            }
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

        //        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
//        Bundle bundle = new Bundle();
//        bundle.putParcelable("productDetails", productModel);
//        ProductDetailsFragment productDetailsFragment = new ProductDetailsFragment();
//        productDetailsFragment.setArguments(bundle);
//        transaction.replace(R.id.loader, productDetailsFragment, "ProductFilterFragment");
//        transaction.addToBackStack("ProductFilterFragment");
//        transaction.commit();
    }
}
