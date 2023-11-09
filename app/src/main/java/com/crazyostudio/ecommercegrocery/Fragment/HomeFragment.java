package com.crazyostudio.ecommercegrocery.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewbinding.ViewBinding;

import com.crazyostudio.ecommercegrocery.Adapter.CategoryAdapter;
import com.crazyostudio.ecommercegrocery.Adapter.ProductAdapter;
import com.crazyostudio.ecommercegrocery.Model.ProductCategoryModel;
import com.crazyostudio.ecommercegrocery.Model.ProductModel;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.databinding.FragmentHomeBinding;
import com.crazyostudio.ecommercegrocery.interfaceClass.CategoryAdapterInterface;
import com.crazyostudio.ecommercegrocery.interfaceClass.onClickProductAdapter;
import com.crazyostudio.ecommercegrocery.javaClasses.basicFun;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.imaginativeworld.whynotimagecarousel.ImageCarousel;
import org.imaginativeworld.whynotimagecarousel.listener.CarouselListener;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements onClickProductAdapter, CategoryAdapterInterface {
    FragmentHomeBinding binding;
    ProductAdapter productAdapter;
    CategoryAdapter categoryAdapter;
    ArrayList<ProductModel> model;
    FirebaseDatabase firebaseDatabase;
    boolean IsChatsProgressBar = false;
    public HomeFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater,container,false);
        firebaseDatabase = FirebaseDatabase.getInstance();

        if (!IsChatsProgressBar) {
            binding.ChatsProgressBar.setVisibility(View.VISIBLE);
            IsChatsProgressBar = true;
        }
//        LoadCarousel();
        LoadCategory();
        LoadProduct();
        return binding.getRoot();
    }

//     void LoadCarousel() {
//        ArrayList<Integer> carousel = new ArrayList<>();
//        firebaseDatabase.getReference().child("carousel").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Integer i = snapshot.getValue(Integer.class);
//                carousel.add(i);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//            }
//        });
//        ImageCarousel Image_Carousel = this.binding.carousel;
//         for (int i = 0; i < carousel.size(); i++) {
//             CarouselItem carouselItem = new CarouselItem(carousel.get(i));
//             Image_Carousel.addData(carouselItem);
//         }
//
//        Image_Carousel.setCarouselListener(new CarouselListener() {
//            @Nullable
//            @Override
//            public ViewBinding onCreateViewHolder(@NonNull LayoutInflater layoutInflater, @NonNull ViewGroup viewGroup) {
//                return null;
//            }
//
//            @Override
//            public void onBindViewHolder(@NonNull ViewBinding viewBinding, @NonNull CarouselItem carouselItem, int i) {
//            }
//
//            @Override
//            public void onClick(int position, @NonNull CarouselItem carouselItem) {
//                Log.i("position_ImageCarousel", "position : "+position);
//                Log.i("position_ImageCarousel", " ArrayList<String>  : "+carousel.get(position));
//                Toast.makeText(getContext(), "", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onLongClick(int position, @NonNull CarouselItem carouselItem) {
//
//            }
//        });
//
////         carousel.carouselListener
//     }

    void LoadProduct(){
        model = new ArrayList<>();
        productAdapter = new ProductAdapter(model,this,requireContext(),"Main");
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        binding.productsList.setAdapter(productAdapter);
        binding.productsList.setLayoutManager(layoutManager);
        firebaseDatabase.getReference().child("Product").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                model.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    ProductModel productModel = snapshot1.getValue(ProductModel.class);
                    if (productModel != null && productModel.isLive()) {
                        model.add(productModel);
                        if (IsChatsProgressBar) {
                            binding.ChatsProgressBar.setVisibility(View.GONE);
                        }
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
    void LoadCategory(){
        ArrayList<ProductCategoryModel> categoryModels = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(categoryModels,requireContext(),this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.category.setLayoutManager(layoutManager);
        binding.category.setAdapter(categoryAdapter);
        firebaseDatabase.getReference().child("Category").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryModels.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    ProductCategoryModel categoryModel = snapshot1.getValue(ProductCategoryModel.class);
                    if (categoryModel != null) {
                        categoryModels.add(categoryModel);
                        categoryAdapter.notifyDataSetChanged();
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
        bundle.putInt("backButton",0);
        ProductDetailsFragment productDetailsFragment = new ProductDetailsFragment();
        productDetailsFragment.setArguments(bundle);
//        productDetailsFragment.setArguments(bundle);
        transaction.replace(R.id.loader,productDetailsFragment,"HomeFragment");
        transaction.addToBackStack("HomeFragment");
        transaction.commit();
    }

    @Override
    public void onClick(ProductCategoryModel productModel) {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("filter", productModel.getTag());
        ProductFilterFragment fragment = new ProductFilterFragment();
        fragment.setArguments(bundle);
        transaction.replace(R.id.loader,fragment,"HomeFragment");
        transaction.addToBackStack("HomeFragment");
        transaction.commit();
    }
}