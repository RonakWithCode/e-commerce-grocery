package com.crazyostudio.ecommercegrocery.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.crazyostudio.ecommercegrocery.Adapter.ProductAdapter;
import com.crazyostudio.ecommercegrocery.Adapter.SlideCategoryAdapter;
import com.crazyostudio.ecommercegrocery.Component.ProductViewCard;
import com.crazyostudio.ecommercegrocery.Model.ProductCategoryModel;
import com.crazyostudio.ecommercegrocery.Model.ProductModel;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.Services.DatabaseService;
import com.crazyostudio.ecommercegrocery.databinding.FragmentProductWithSlideCategoryBinding;
import com.crazyostudio.ecommercegrocery.interfaceClass.CategoryAdapterInterface;
import com.crazyostudio.ecommercegrocery.javaClasses.basicFun;

import java.util.ArrayList;

public class ProductWithSlideCategoryFragment extends Fragment {
    FragmentProductWithSlideCategoryBinding binding;

    ProductAdapter productAdapter;
    ArrayList<ProductModel> model;
    String filter;
//    ArrayList<Hom>
    private DatabaseService databaseService;


    public ProductWithSlideCategoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProductWithSlideCategoryBinding.inflate(inflater,container,false);
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        databaseService = new DatabaseService();

        binding.backBtn.setOnClickListener(v -> {
            AppCompatActivity activity = (AppCompatActivity) requireActivity();
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().show();
            }

            // Navigate back
            requireActivity().onBackPressed();
        });
        if (getArguments() != null) {
            filter = getArguments().getString("filter");
            LoadCategory(filter);
        }else {
            LoadCategory("no");
        }
        binding.searchBta.setOnClickListener(view->openSearchFragment());
        model = new ArrayList<>();
        productAdapter = new ProductAdapter(model, (productModel, sameProducts) -> {

            new ProductViewCard(getActivity()).showProductViewDialog(productModel,sameProducts);

        }, requireContext(),"main");

        binding.Products.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.Products.setAdapter(productAdapter);

        return binding.getRoot();
    }



    void LoadCategory(String filter) {
        ArrayList<ProductCategoryModel> categoryModels = new ArrayList<>();
        SlideCategoryAdapter categoryAdapter = new SlideCategoryAdapter(categoryModels, requireContext(), new CategoryAdapterInterface() {
            @Override
            public void onClick(ProductCategoryModel productModel) {
                LoadProductByCategory(productModel.getTag());

            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.slideView.setLayoutManager(layoutManager);
        binding.slideView.setAdapter(categoryAdapter);

//        Drawable scrollbarThumb = ContextCompat.getDrawable(requireContext(), R.drawable.scrollbar_thumb);
//        Drawable scrollbarTrack = ContextCompat.getDrawable(requireContext(), R.drawable.scrollbar_track);
//
//        binding.slideView.setVerticalScrollBarEnabled(true);
//        binding.slideView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
////
////        binding.slideView.setScrollBarThumbDrawable(scrollbarThumb);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            binding.slideView.setVerticalScrollbarThumbDrawable(scrollbarThumb);
//            binding.slideView.setVerticalScrollbarTrackDrawable(scrollbarTrack);
//        }
//        binding.slideView.setScrollBarTrackDrawable(scrollbarTrack);


        new DatabaseService().getAllCategory(new DatabaseService.GetAllCategoryCallback() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(ArrayList<ProductCategoryModel> category) {
                categoryModels.addAll(category);
                categoryAdapter.notifyDataSetChanged();
                if (filter.equals("no")) {
                    LoadProductByCategory(category.get(0).getTag());
                }else {
                    LoadProductByCategory(filter);
                }
//                binding.shimmerLayout.stopShimmer();
//                binding.shimmerLayout.setVisibility(View.GONE);
                binding.slideView.setVisibility(View.VISIBLE);

            }

            @Override
            public void onError(String errorMessage) {

            }
        });
    }

    private void LoadProductByCategory(String s) {
        databaseService.getAllProductsByCategoryOnly(s, new DatabaseService.GetAllProductsCallback() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(ArrayList<ProductModel> products) {
                model.clear();
                model.addAll(products);
                binding.title.setText(s);
                productAdapter.notifyDataSetChanged();
//                MultiViewModel.clear();
//                MultiViewModel.add(new HomeProductModel(s,products));
//                MultiViewAdapter.notifyDataSetChanged();

            }

            @Override
            public void onError(String errorMessage) {
                basicFun.AlertDialog(requireContext(), errorMessage);

            }
        });
    }


    private void openSearchFragment() {
        if (isAdded() && getActivity() != null) {
            SearchFragment fragment  = new SearchFragment();
//            Bundle bundle = new Bundle();
//            bundle.putParcelableArrayList("model",model);
//            fragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.loader,fragment)
                    .addToBackStack("HomeFragment")
                    .commit();


        }
    }






//    CARD'S

// Component of ProductViewCard




}