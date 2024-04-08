package com.crazyostudio.ecommercegrocery.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crazyostudio.ecommercegrocery.Adapter.CategoryAdapter;
import com.crazyostudio.ecommercegrocery.Adapter.ListCategoryAdapter;
import com.crazyostudio.ecommercegrocery.Model.ProductCategoryModel;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.Services.DatabaseService;
import com.crazyostudio.ecommercegrocery.databinding.FragmentCategoryBinding;
import com.crazyostudio.ecommercegrocery.interfaceClass.CategoryAdapterInterface;
import com.google.android.material.bottomappbar.BottomAppBar;

import java.util.ArrayList;

public class CategoryFragment extends Fragment implements CategoryAdapterInterface {
    FragmentCategoryBinding binding;

    public CategoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCategoryBinding.inflate(inflater,container,false);
//        BannerCategoryAdapter

        LoadCategory();
        return binding.getRoot();
    }

    @Override
    public void onClick(ProductCategoryModel productModel) {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("filter", productModel.getTag());
        ProductFilterFragment fragment = new ProductFilterFragment();
        fragment.setArguments(bundle);
        transaction.replace(R.id.loader, fragment, "ProductFilterFragment");
        transaction.addToBackStack("ProductFilterFragment");
        transaction.commit();
    }





    void LoadCategory() {
        ArrayList<ProductCategoryModel> categoryModels = new ArrayList<>();
        ListCategoryAdapter categoryAdapter = new ListCategoryAdapter(categoryModels, requireContext(), this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.getRoot().setLayoutManager(layoutManager);
        binding.getRoot().setAdapter(categoryAdapter);
        new DatabaseService().getAllCategory(new DatabaseService.GetAllCategoryCallback() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(ArrayList<ProductCategoryModel> category) {
                categoryModels.addAll(category);
                categoryAdapter.notifyDataSetChanged();

            }

            @Override
            public void onError(String errorMessage) {

            }
        });
    }

}