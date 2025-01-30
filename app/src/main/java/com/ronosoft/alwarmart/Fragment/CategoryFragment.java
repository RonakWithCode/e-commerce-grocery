package com.ronosoft.alwarmart.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ronosoft.alwarmart.Adapter.ListCategoryAdapter;
import com.ronosoft.alwarmart.Model.ProductCategoryModel;
import com.ronosoft.alwarmart.R;
import com.ronosoft.alwarmart.Services.DatabaseService;
import com.ronosoft.alwarmart.databinding.FragmentCategoryBinding;
import com.ronosoft.alwarmart.interfaceClass.CategoryAdapterInterface;

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
        binding.shimmerLayout.startShimmer();

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
        binding.recyclerCategory.setLayoutManager(layoutManager);
        binding.recyclerCategory.setAdapter(categoryAdapter);
        new DatabaseService().getAllCategory(new DatabaseService.GetAllCategoryCallback() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(ArrayList<ProductCategoryModel> category) {
                categoryModels.addAll(category);
                categoryAdapter.notifyDataSetChanged();
                binding.shimmerLayout.stopShimmer();
                binding.shimmerLayout.setVisibility(View.GONE);
                binding.recyclerCategory.setVisibility(View.VISIBLE);

            }

            @Override
            public void onError(String errorMessage) {

            }
        });
    }

}