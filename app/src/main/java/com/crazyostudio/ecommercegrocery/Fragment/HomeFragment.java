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
import com.crazyostudio.ecommercegrocery.MainActivity;
import com.crazyostudio.ecommercegrocery.Model.BannerModels;
import com.crazyostudio.ecommercegrocery.Model.ProductCategoryModel;
import com.crazyostudio.ecommercegrocery.Model.ProductModel;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.Services.DatabaseService;
import com.crazyostudio.ecommercegrocery.databinding.FragmentHomeBinding;
import com.crazyostudio.ecommercegrocery.interfaceClass.CategoryAdapterInterface;
import com.crazyostudio.ecommercegrocery.interfaceClass.onClickProductAdapter;
import com.crazyostudio.ecommercegrocery.javaClasses.basicFun;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.imaginativeworld.whynotimagecarousel.ImageCarousel;
import org.imaginativeworld.whynotimagecarousel.listener.CarouselListener;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class HomeFragment extends Fragment implements onClickProductAdapter, CategoryAdapterInterface {
    FragmentHomeBinding binding;
    ProductAdapter productAdapter;
    CategoryAdapter categoryAdapter;
    ArrayList<ProductModel> model;

    DatabaseService databaseService;
//    private static final int SPEECH_REQUEST_CODE = 0;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        databaseService = new DatabaseService();
        LoadCarousel();
        LoadCategory();
        LoadProduct();
        binding.categorySeeMore.setOnClickListener(view->{
            BottomNavigationView bottomAppBar = getActivity().findViewById(R.id.bottomNavigationView);
//            View secondElement = bottomAppBar.getChildAt(1);
            bottomAppBar.setSelectedItemId(R.id.GoCategory);
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.loader,new CategoryFragment(),"CategoryFragment");
            transaction.addToBackStack("CategoryFragment");
            transaction.commit();
        });
        return binding.getRoot();
    }




//    private void openVoiceRecognizer() {
//        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//// This starts the activity and populates the intent with the speech text.
//        startActivityForResult(intent, SPEECH_REQUEST_CODE);
//
//
//    }
//
//    private void filterList(String valueOf) {
//        ArrayList<ProductModel> filter = new ArrayList<>();
//        for (ProductModel productModel : model) {
//            if (productModel.getProductName().toLowerCase().contains(valueOf.toLowerCase())) {
//                filter.add(productModel);
//            } else if (productModel.getCategory().toLowerCase().contains(valueOf.toLowerCase())) {
//                filter.add(productModel);
//            }
//
//        }
//        if (filter.isEmpty()) {
////            Toast.makeText(getContext(), "No data found", Toast.LENGTH_SHORT).show();
//        } else {
//            productAdapter.setFilerList(filter);
//        }
//    }

     void LoadCarousel() {
        ArrayList<BannerModels> models = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference bannerRef = database.getReference().child("Banner");
        ImageCarousel Image_Carousel = this.binding.carousel;
         bannerRef.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                     BannerModels banner = snapshot.getValue(BannerModels.class);
                     models.add(banner);
                     assert banner != null;
                     CarouselItem carouselItem = new CarouselItem(banner.getBannerUrl());
                     Image_Carousel.addData(carouselItem);
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {
                 // Handle potential errors
                 Log.e("TAG", "Error fetching data", databaseError.toException());
             }
         });


        Image_Carousel.setCarouselListener(new CarouselListener() {
            @Nullable
            @Override
            public ViewBinding onCreateViewHolder(@NonNull LayoutInflater layoutInflater, @NonNull ViewGroup viewGroup) {
                return null;
            }

            @Override
            public void onBindViewHolder(@NonNull ViewBinding viewBinding, @NonNull CarouselItem carouselItem, int i) {
            }

            @Override
            public void onClick(int position, @NonNull CarouselItem carouselItem) {
                Log.i("position_ImageCarousel", "position : "+position);
                Log.i("position_ImageCarousel", " ArrayList<String>  : "+models.get(position).getBannerGoto());
                Toast.makeText(getContext(), "", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(int position, @NonNull CarouselItem carouselItem) {

            }
        });

//         carousel.carouselListener
     }

    void LoadProduct() {
        model = new ArrayList<>();
        productAdapter = new ProductAdapter(model, this, requireContext(), "Main");
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        binding.productsList.setAdapter(productAdapter);
        binding.productsList.setLayoutManager(layoutManager);
        databaseService.getAllProducts(new DatabaseService.GetAllProductsCallback() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(ArrayList<ProductModel> products) {
                model.addAll(products);
                productAdapter.notifyDataSetChanged();
                if (binding.ChatsProgressBar.getVisibility() == View.VISIBLE) {
                    binding.ChatsProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(String errorMessage) {
                // Handle the error her
                basicFun.AlertDialog(requireContext(),errorMessage);
            }
        });

    }

    void LoadCategory() {
        ArrayList<ProductCategoryModel> categoryModels = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(categoryModels, requireContext(), this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.category.setLayoutManager(layoutManager);
        binding.category.setAdapter(categoryAdapter);
        databaseService.getAllCategory(new DatabaseService.GetAllCategoryCallback() {
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

    @Override
    public void onClick(ProductModel productModel) {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putParcelable("productDetails", productModel);
        bundle.putInt("backButton", 0);
        ProductDetailsFragment productDetailsFragment = new ProductDetailsFragment();
        productDetailsFragment.setArguments(bundle);
//        productDetailsFragment.setArguments(bundle);
        transaction.replace(R.id.loader, productDetailsFragment, "HomeFragment");
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
        transaction.replace(R.id.loader, fragment, "HomeFragment");
        transaction.addToBackStack("HomeFragment");
        transaction.commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode,
//                                    Intent data) {
//        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
//            List<String> results = data.getStringArrayListExtra(
//                    RecognizerIntent.EXTRA_RESULTS);
//            String spokenText = results.get(0);
//            Log.d("spokenText", "onActivityResult: "+spokenText);
////            binding.searchBar.openSearch();
////            binding.searchBar.setText(spokenText);
////            filterList(binding.searchBar.getText().toString());
//            // Do something with spokenText.
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }
//
}