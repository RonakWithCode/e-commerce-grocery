package com.crazyostudio.ecommercegrocery.Fragment;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import com.crazyostudio.ecommercegrocery.Adapter.ListCategoryAdapter;
import com.crazyostudio.ecommercegrocery.Adapter.ProductAdapter;
import com.crazyostudio.ecommercegrocery.Adapter.SlideCategoryAdapter;
import com.crazyostudio.ecommercegrocery.Model.HomeProductModel;
import com.crazyostudio.ecommercegrocery.Model.ProductCategoryModel;
import com.crazyostudio.ecommercegrocery.Model.ProductModel;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.Services.DatabaseService;
import com.crazyostudio.ecommercegrocery.databinding.FragmentProductWithSlideCategoryBinding;
import com.crazyostudio.ecommercegrocery.interfaceClass.CategoryAdapterInterface;
import com.crazyostudio.ecommercegrocery.interfaceClass.onClickProductAdapter;
import com.crazyostudio.ecommercegrocery.javaClasses.basicFun;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductWithSlideCategoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductWithSlideCategoryFragment extends Fragment {
    FragmentProductWithSlideCategoryBinding binding;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ProductAdapter productAdapter;
    ArrayList<ProductModel> model;

    public ProductWithSlideCategoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProductWithSlideCategoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProductWithSlideCategoryFragment newInstance(String param1, String param2) {
        ProductWithSlideCategoryFragment fragment = new ProductWithSlideCategoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
        binding.backBtn.setOnClickListener(v -> requireActivity().onBackPressed());
        LoadCategory();
        model = new ArrayList<>();
        productAdapter = new ProductAdapter(model, (productModel, sameProducts) -> {

        }, requireContext(),"main");

        binding.Products.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.Products.setAdapter(productAdapter);

        return binding.getRoot();
    }



    void LoadCategory() {
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
                LoadProductByCategory(category.get(0).getTag());
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
        new DatabaseService().getAllProductsByCategoryOnly(s, new DatabaseService.GetAllProductsCallback() {
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




}