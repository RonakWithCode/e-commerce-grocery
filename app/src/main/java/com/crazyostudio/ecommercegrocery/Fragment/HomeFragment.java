package com.crazyostudio.ecommercegrocery.Fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.bumptech.glide.Glide;
import com.crazyostudio.ecommercegrocery.Adapter.HomeCategoryAdapter;
import com.crazyostudio.ecommercegrocery.Adapter.HomeProductAdapter;
import com.crazyostudio.ecommercegrocery.Model.BannerModels;
import com.crazyostudio.ecommercegrocery.Model.HomeCategoryModel;
import com.crazyostudio.ecommercegrocery.Model.HomeProductModel;
import com.crazyostudio.ecommercegrocery.Model.ProductCategoryModel;
import com.crazyostudio.ecommercegrocery.Model.ProductModel;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.Services.DatabaseService;
import com.crazyostudio.ecommercegrocery.databinding.FragmentHomeBinding;
import com.crazyostudio.ecommercegrocery.databinding.ProductViewDialogBinding;
import com.crazyostudio.ecommercegrocery.interfaceClass.CategoryAdapterInterface;
import com.crazyostudio.ecommercegrocery.interfaceClass.HomeCategoryInterface;
import com.crazyostudio.ecommercegrocery.interfaceClass.HomeProductInterface;
import com.crazyostudio.ecommercegrocery.interfaceClass.onClickProductAdapter;
import com.crazyostudio.ecommercegrocery.javaClasses.CustomSmoothScroller;
import com.crazyostudio.ecommercegrocery.javaClasses.basicFun;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.imaginativeworld.whynotimagecarousel.ImageCarousel;
import org.imaginativeworld.whynotimagecarousel.listener.CarouselListener;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

import java.util.ArrayList;
import java.util.Objects;


public class HomeFragment extends Fragment implements onClickProductAdapter, CategoryAdapterInterface {
    FragmentHomeBinding binding;
//    CategoryAdapter categoryAdapter;
    DatabaseService databaseService;
    HomeProductAdapter homeProductAdapter;
    HomeCategoryAdapter homeCategoryAdapter;
    HomeCategoryAdapter homeProductBoysSkinAdapter;
    ArrayList<HomeProductModel> homeProductModel;
    ArrayList<HomeProductModel> homeProductModelBoysSkin;
    ArrayList<HomeCategoryModel> categoryModels;
    ArrayList<HomeCategoryModel> categoryBoysSkinModels;

//805864


//    HomeCategoryAdapter
//    HomeCategoryModel

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        databaseService = new DatabaseService();
//        binding.shimmerLayout.startShimmer();
        categoryModels = new ArrayList<>();
        categoryBoysSkinModels = new ArrayList<>();
        homeCategoryAdapter = new HomeCategoryAdapter(categoryModels, requireContext(), new HomeCategoryInterface() {
            @Override
            public void onClick(String filter) {

            }
        });
        homeProductBoysSkinAdapter = new HomeCategoryAdapter(categoryBoysSkinModels, requireContext(), new HomeCategoryInterface() {
            @Override
            public void onClick(String filter) {

            }
        });

        LoadCarousel();
        LoadProductCategory();


//        LoadCategory();

//        loadMainCarousel();







        SetupAdapter();
        SetupAdapterByBoysSkin();


        homeProductAdapter = new HomeProductAdapter(homeProductModel, this::showProductViewDialog, requireActivity());

//        offers();
//        LoadProduct("chips and Snacks");
//        LoadProduct("dairy");
//        LoadProduct("toothpaste");
//        LoadProduct("drinks");
//        String[] categories = {"chips and Snacks", "toothpaste", "hair oil ", "drinks"};
//        loadProductsForCategories(categories);


//        LoadCarousel();
//        LoadCategory();
        loadProductsForCategories(new String[]{"chips and Snacks", "toothpaste", "hair oil ", "drinks"});
        loadProductsByCat(new String[]{"chips and Snacks", "toothpaste", "hair oil ", "drinks"});
        loadProductsForCategoriesByBoysSkin(new String[]{"toothpaste", "drinks"});

//        LoadProduct("chips and Snacks");

//        binding.categorySeeMore.setOnClickListener(view -> {
//            BottomNavigationView bottomAppBar = requireActivity().findViewById(R.id.bottomNavigationView);
//            bottomAppBar.setSelectedItemId(R.id.GoCategory);
//            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
//            transaction.replace(R.id.loader, new CategoryFragment(), "CategoryFragment");
//            transaction.addToBackStack("CategoryFragment");
//            transaction.commit();
//        });
//        binding.chipsAndSnacksSeeMore.setOnClickListener(v -> {
//            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
//            Bundle bundle = new Bundle();
//            bundle.putString("filter", "chips and Snacks");
//            ProductFilterFragment fragment = new ProductFilterFragment();
//            fragment.setArguments(bundle);
//            transaction.replace(R.id.loader, fragment, "ProductFilterFragment");
//            transaction.addToBackStack("ProductFilterFragment");
//            transaction.commit();
//        });
//        binding.dairySeeMore.setOnClickListener(v -> {
//            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
//            Bundle bundle = new Bundle();
//            bundle.putString("filter", "dairy");
//            ProductFilterFragment fragment = new ProductFilterFragment();
//            fragment.setArguments(bundle);
//            transaction.replace(R.id.loader, fragment, "ProductFilterFragment");
//            transaction.addToBackStack("ProductFilterFragment");
//            transaction.commit();
//        });
//        binding.drinksSeeMore.setOnClickListener(v -> {
//            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
//            Bundle bundle = new Bundle();
//            bundle.putString("filter", "drinks");
//            ProductFilterFragment fragment = new ProductFilterFragment();
//            fragment.setArguments(bundle);
//            transaction.replace(R.id.loader, fragment, "ProductFilterFragment");
//            transaction.addToBackStack("ProductFilterFragment");
//            transaction.commit();
//        });
//        binding.forYouTeethSeeMore.setOnClickListener(v -> {
//            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
//            Bundle bundle = new Bundle();
//            bundle.putString("filter", "toothpaste");
//            ProductFilterFragment fragment = new ProductFilterFragment();
//            fragment.setArguments(bundle);
//            transaction.replace(R.id.loader, fragment, "ProductFilterFragment");
//            transaction.addToBackStack("ProductFilterFragment");
//            transaction.commit();
//        });

        return binding.getRoot();

    }




    private void LoadProductCategory() {
        Glide.with(requireContext()).load("https://firebasestorage.googleapis.com/v0/b/e-commerce-11d7d.appspot.com/o/all_category%2Fskin%20care.png?alt=media&token=6edf6d22-e31e-4935-abfd-6b5ddde4ea28").placeholder(R.drawable.skeleton_shape).into(binding.skin);
        Glide.with(requireContext()).load("https://firebasestorage.googleapis.com/v0/b/e-commerce-11d7d.appspot.com/o/all_category%2F6.png?alt=media&token=032ccdeb-9a48-4d2e-976d-238b20172b1a").placeholder(R.drawable.skeleton_shape).into(binding.AttaRiceDal);
        Glide.with(requireContext()).load("https://firebasestorage.googleapis.com/v0/b/e-commerce-11d7d.appspot.com/o/all_category%2F3.png?alt=media&token=30804049-0835-4197-a25c-637b75f0f5d4").placeholder(R.drawable.skeleton_shape).into(binding.milk);
        Glide.with(requireContext()).load("https://firebasestorage.googleapis.com/v0/b/e-commerce-11d7d.appspot.com/o/all_category%2F1.png?alt=media&token=4608d93f-add0-4e26-bbce-e17275024b06").placeholder(R.drawable.skeleton_shape).into(binding.oil);
        Glide.with(requireContext()).load("https://firebasestorage.googleapis.com/v0/b/e-commerce-11d7d.appspot.com/o/all_category%2F2.png?alt=media&token=fc901685-49db-4b11-a053-231c7cfa7380").placeholder(R.drawable.skeleton_shape).into(binding.Bakery);
        Glide.with(requireContext()).load("https://firebasestorage.googleapis.com/v0/b/e-commerce-11d7d.appspot.com/o/all_category%2F4.png?alt=media&token=8aad396a-a33a-4d71-94bb-b1bf9052d425").placeholder(R.drawable.skeleton_shape).into(binding.drinks);
// TODO:  More set in this
//          some are : onClicks and view category etc

        Glide.with(requireContext()).load("https://firebasestorage.googleapis.com/v0/b/e-commerce-11d7d.appspot.com/o/all_category%2Fbrand%2Fdabur.png?alt=media&token=26e97c85-3c03-47d9-8971-0b496ab5100f").placeholder(R.drawable.skeleton_shape).into(binding.Dabur);
        Glide.with(requireContext()).load("https://firebasestorage.googleapis.com/v0/b/e-commerce-11d7d.appspot.com/o/all_category%2Fbrand%2Fitc.png?alt=media&token=34307cc3-bba2-492c-9393-8306898f7757").placeholder(R.drawable.skeleton_shape).into(binding.itc);
//        Glide.with(requireContext()).load("https://firebasestorage.googleapis.com/v0/b/e-commerce-11d7d.appspot.com/o/all_category%2F4.png?alt=media&token=8aad396a-a33a-4d71-94bb-b1bf9052d425").placeholder(R.drawable.skeleton_shape).into(binding.drinks);


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

//    void LoadCarousel() {
//        ArrayList<BannerModels> models = new ArrayList<>();
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference bannerRef = database.getReference().child("Banner");
//
//        bannerRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                ArrayList<ImageSlidesModel> autoImageList = new ArrayList<>();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    BannerModels banner = snapshot.getValue(BannerModels.class);
//                    models.add(banner);
//                    assert banner != null;
//                    ImageSlidesModel slideModelModel = new ImageSlidesModel (banner.getBannerUrl(), banner.getBannerCaption());
//                    autoImageList.add(slideModelModel);
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Handle potential errors
//                Log.e("TAG", "Error fetching data", databaseError.toException());
//            }
//        });
//
//
//        binding.carousel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(requireContext(), "it onTouched", Toast.LENGTH_SHORT).show();
//
//            }
//        });
//
//
//        binding.carousel.onItemClickListener(new ItemsListener() {
//            @Override
//            public void onItemChanged(int i) {
//
//            }
//
//            @Override
//            public void onTouched(@Nullable ImageActionTypes imageActionTypes, int position) {
//                Toast.makeText(requireContext(), "it onTouched", Toast.LENGTH_SHORT).show();
//
//            }
//
//            @Override
//            public void onItemClicked(int position) {
//                Toast.makeText(requireContext(), "it clicked", Toast.LENGTH_SHORT).show();
//                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
//                Bundle bundle = new Bundle();
//                bundle.putString("filter", models.get(position).getBannerGoto());
//                ProductFilterFragment fragment = new ProductFilterFragment();
//                fragment.setArguments(bundle);
//                transaction.replace(R.id.loader, fragment, "ProductFilterFragment");
//                transaction.addToBackStack("ProductFilterFragment");
//                transaction.commit();
//            }
//        });
//
////
////        binding.carousel.setItemClickListener(new ItemClickListener() {
////            @Override
////            public void onItemSelected(int position) {
////                Toast.makeText(requireContext(), "it clicked", Toast.LENGTH_SHORT).show();
////                // Ensure the position is within the bounds of the models list
////                if (position >= 0 && position < models.size()) {
////                    FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
////                    Bundle bundle = new Bundle();
////                    bundle.putString("filter", models.get(position).getBannerGoto());
////                    ProductFilterFragment fragment = new ProductFilterFragment();
////                    fragment.setArguments(bundle);
////                    transaction.replace(R.id.loader, fragment, "ProductFilterFragment");
////                    transaction.addToBackStack("ProductFilterFragment");
////                    transaction.commit();
////                }
////            }
////
////            @Override
////            public void doubleClick(int position) {
////                // Handle double click event if needed
////                Toast.makeText(requireContext(), "it clicked", Toast.LENGTH_SHORT).show();
////
////            }
////        });
//
//
//
//    }


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
//                Log.i("position_ImageCarousel", " ArrayList<String>  : "+models.get(position).getBannerGoto());
//                Toast.makeText(getContext(), "", Toast.LENGTH_SHORT).show();
//                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
//                Bundle bundle = new Bundle();
//                bundle.putString("filter",models.get(position).getBannerGoto());
//                ProductFilterFragment fragment = new ProductFilterFragment();
//                fragment.setArguments(bundle);
//                transaction.replace(R.id.loader, fragment, "ProductFilterFragment");
//                transaction.addToBackStack("ProductFilterFragment");
//                transaction.commit();
//
//            }
//
//            @Override
//            public void onLongClick(int position, @NonNull CarouselItem carouselItem) {
//
//            }
//        });






    void LoadCarousel() {
        ArrayList<BannerModels> modelsTop = new ArrayList<>();
        ArrayList<BannerModels> modelsCenter = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference bannerRef = database.getReference().child("Banner");
        ImageCarousel Image_Carousel = this.binding.carousel;
        ImageCarousel Image_CarouselCenter = this.binding.carousel;
        bannerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    BannerModels banner = snapshot.getValue(BannerModels.class);
                    if (banner != null) {
                        if (banner.getPosition().equals("Top Position")){
                            modelsTop.add(banner);
                            if (isDarkModeEnabled(requireContext())){
                                CarouselItem carouselItem = new CarouselItem(banner.getDarkBannerImage());
                                Image_Carousel.addData(carouselItem);
                            }
                            else {
                                CarouselItem carouselItem = new CarouselItem(banner.getBannerImages());
                                Image_Carousel.addData(carouselItem);
                            }
                        }
                        else if (banner.getPosition().equals("Center Position"))
                        {
                            modelsCenter.add(banner);
                            if (isDarkModeEnabled(requireContext())){
                                CarouselItem carouselItem = new CarouselItem(banner.getDarkBannerImage());
                                Image_CarouselCenter.addData(carouselItem);
                            }
                            else {
                                CarouselItem carouselItem = new CarouselItem(banner.getBannerImages());
                                Image_CarouselCenter.addData(carouselItem);
                            }
                        }
                    }
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
//                Log.i("position_ImageCarousel", "position : "+position);
//                Log.i("position_ImageCarousel", " ArrayList<String>  : "+models.get(position).getBannerGoto());
//                Toast.makeText(getContext(), "", Toast.LENGTH_SHORT).show();

                if (modelsTop.get(position).isByCategory()) {
                    FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                    Bundle bundle = new Bundle();
                    bundle.putString("filter",modelsTop.get(position).getFilterQuery());
                    ProductFilterFragment fragment = new ProductFilterFragment();
                    fragment.setArguments(bundle);
                    transaction.replace(R.id.loader, fragment, "ProductFilterFragment");
                    transaction.addToBackStack("ProductFilterFragment");
                    transaction.commit();
                }else {
                    //TODO : ---------------------------------------------------------
                    FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                    Bundle bundle = new Bundle();
                    bundle.putString("filter",modelsTop.get(position).getFilterQuery());
                    bundle.putString("filterName",modelsTop.get(position).getBannerCaption());
                    ProductFilterByQueryFragment fragment = new ProductFilterByQueryFragment();
                    fragment.setArguments(bundle);
                    transaction.replace(R.id.loader, fragment, "ProductFilterByQueryFragment");
                    transaction.addToBackStack("ProductFilterByQueryFragment");
                    transaction.commit();

                }


            }

            @Override
            public void onLongClick(int position, @NonNull CarouselItem carouselItem) {

            }
        });
        Image_CarouselCenter.setCarouselListener(new CarouselListener() {
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
//                Log.i("position_ImageCarousel", "position : "+position);
//                Log.i("position_ImageCarousel", " ArrayList<String>  : "+models.get(position).getBannerGoto());
//                Toast.makeText(getContext(), "", Toast.LENGTH_SHORT).show();

                if (modelsCenter.get(position).isByCategory()) {
                    FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                    Bundle bundle = new Bundle();
                    bundle.putString("filter",modelsCenter.get(position).getFilterQuery());
                    ProductFilterFragment fragment = new ProductFilterFragment();
                    fragment.setArguments(bundle);
                    transaction.replace(R.id.loader, fragment, "ProductFilterFragment");
                    transaction.addToBackStack("ProductFilterFragment");
                    transaction.commit();
                }else {
                    //TODO : ---------------------------------------------------------
                    FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                    Bundle bundle = new Bundle();
                    bundle.putString("filter",modelsCenter.get(position).getFilterQuery());
                    bundle.putString("filterName",modelsCenter.get(position).getBannerCaption());
                    ProductFilterByQueryFragment fragment = new ProductFilterByQueryFragment();
                    fragment.setArguments(bundle);
                    transaction.replace(R.id.loader, fragment, "ProductFilterByQueryFragment");
                    transaction.addToBackStack("ProductFilterByQueryFragment");
                    transaction.commit();

                }


            }

            @Override
            public void onLongClick(int position, @NonNull CarouselItem carouselItem) {

            }
        });

    }


    public void SetupAdapter(){
        homeProductModel = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false);
        binding.recyclerCategory.setAdapter(homeCategoryAdapter);
        binding.boysSkinCareRecyclerView.setLayoutManager(layoutManager);
        layoutManager.setSmoothScrollbarEnabled(true);
        CustomSmoothScroller smoothScroller = new CustomSmoothScroller(requireContext());
        smoothScroller.setTargetPosition(0);
        layoutManager.startSmoothScroll(smoothScroller);
        binding.recyclerCategory.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                // Adjust the scrolling speed by multiplying dy (vertical scroll amount) with a factor
                int newDy = (int) (dy * 2); // Increase scrolling speed by multiplying with a factor (e.g., 1.5)
                super.onScrolled(recyclerView, dx, newDy);
            }
        });


    }


    private void SetupAdapterByBoysSkin() {
        homeProductModelBoysSkin = new ArrayList<>();
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false);
        binding.boysSkinCareRecyclerView.setAdapter(homeProductBoysSkinAdapter);
        binding.recyclerCategory.setLayoutManager(layoutManager1);
        layoutManager1.setSmoothScrollbarEnabled(true);
        CustomSmoothScroller smoothScroller = new CustomSmoothScroller(requireContext());
        smoothScroller.setTargetPosition(0);
        layoutManager1.startSmoothScroll(smoothScroller);

        binding.boysSkinCareRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                // Adjust the scrolling speed by multiplying dy (vertical scroll amount) with a factor
                int newDy = (int) (dy * 2); // Increase scrolling speed by multiplying with a factor (e.g., 1.5)
                super.onScrolled(recyclerView, dx, newDy);
            }
        });
    }







    public static boolean isDarkModeEnabled(Context context) {
        int currentNightMode = context.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES;
    }

    private void loadProductsByCat(String[] strings) {
        homeProductModel.clear();
        for (String category : strings) {
            LoadProductSee(category);
        }
    }

    void loadProductsForCategories(String[] categories) {
        categoryModels.clear();
        for (String category : categories) {
            LoadProduct(category);
        }
    }
    void loadProductsForCategoriesByBoysSkin(String[] categories) {
        categoryBoysSkinModels.clear();
        for (String category : categories) {
            LoadProductByBoysSkin(category);
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    void LoadProduct(String category) {
//        homeProductModel.clear();
        databaseService.getAllProductsByCategoryOnly(category,new DatabaseService.GetAllProductsCallback() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(ArrayList<ProductModel> products) {
                ArrayList<String> Images = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    Images.add(products.get(i).getImageURL().get(0));
                }
                categoryModels.add(new HomeCategoryModel(category,Images,String.valueOf(products.size()),category));
                homeProductModel.add(new HomeProductModel(category,products));
//                model.addAll(products);
                homeCategoryAdapter.notifyDataSetChanged();
//                if (binding.ChatsProgressBar.getVisibility() == View.VISIBLE) {
//                    binding.ChatsProgressBar.setVisibility(View.GONE);
//                }
//                binding.shimmerLayout.stopShimmer();
//                binding.shimmerLayout.setVisibility(View.GONE);
//                binding.ScrollView.setVisibility(View.VISIBLE);
            }


            @Override
            public void onError(String errorMessage) {
                // Handle the error her
                basicFun.AlertDialog(requireContext(),errorMessage);
            }
        });
        homeCategoryAdapter.notifyDataSetChanged();

    }

    void LoadProductByBoysSkin(String category) {
//        homeProductModel.clear();
        databaseService.getAllProductsByCategoryOnly(category,new DatabaseService.GetAllProductsCallback() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(ArrayList<ProductModel> products) {
                ArrayList<String> Images = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    Images.add(products.get(i).getImageURL().get(0));
                }
                categoryBoysSkinModels.add(new HomeCategoryModel(category,Images,String.valueOf(products.size()),category));
//                homeProductModel.add(new HomeProductModel(category,products));
//                model.addAll(products);
                homeProductBoysSkinAdapter.notifyDataSetChanged();
//                if (binding.ChatsProgressBar.getVisibility() == View.VISIBLE) {
//                    binding.ChatsProgressBar.setVisibility(View.GONE);
//                }
//                binding.shimmerLayout.stopShimmer();
//                binding.shimmerLayout.setVisibility(View.GONE);
//                binding.ScrollView.setVisibility(View.VISIBLE);
            }


            @Override
            public void onError(String errorMessage) {
                // Handle the error her
                basicFun.AlertDialog(requireContext(),errorMessage);
            }
        });
        homeProductBoysSkinAdapter.notifyDataSetChanged();

    }








//
    @SuppressLint("NotifyDataSetChanged")
    void LoadProductSee(String category) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false);
        binding.MultiViewAdapter.setAdapter(homeProductAdapter);
        binding.MultiViewAdapter.setLayoutManager(layoutManager);
        layoutManager.setSmoothScrollbarEnabled(true);
        CustomSmoothScroller smoothScroller = new CustomSmoothScroller(requireContext());
        smoothScroller.setTargetPosition(0);
        layoutManager.startSmoothScroll(smoothScroller);
        binding.MultiViewAdapter.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                // Adjust the scrolling speed by multiplying dy (vertical scroll amount) with a factor
                int newDy = (int) (dy * 2); // Increase scrolling speed by multiplying with a factor (e.g., 1.5)
                super.onScrolled(recyclerView, dx, newDy);
            }
        });

//        homeProductModel.clear();
        databaseService.getAllProductsByCategoryOnly(category,new DatabaseService.GetAllProductsCallback() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(ArrayList<ProductModel> products) {
                homeProductModel.add(new HomeProductModel(category,products));
//                model.addAll(products);
                homeProductAdapter.notifyDataSetChanged();
//                if (binding.ChatsProgressBar.getVisibility() == View.VISIBLE) {
//                    binding.ChatsProgressBar.setVisibility(View.GONE);
//                }
//                binding.shimmerLayout.stopShimmer();
//                binding.shimmerLayout.setVisibility(View.GONE);
//                binding.ScrollView.setVisibility(View.VISIBLE);
            }


            @Override
            public void onError(String errorMessage) {
                // Handle the error her
                basicFun.AlertDialog(requireContext(),errorMessage);
            }
        });
        homeProductAdapter.notifyDataSetChanged();

    }


//    void LoadProduct(String category) {
//        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
//        binding.MultiViewAdapter.setAdapter(homeProductAdapter);
//        binding.MultiViewAdapter.setLayoutManager(layoutManager);
//        layoutManager.setSmoothScrollbarEnabled(true);
//        CustomSmoothScroller smoothScroller = new CustomSmoothScroller(requireContext());
//        smoothScroller.setTargetPosition(0);
//        layoutManager.startSmoothScroll(smoothScroller);
//        binding.MultiViewAdapter.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                // Adjust the scrolling speed by multiplying dy (vertical scroll amount) with a factor
//                int newDy = (int) (dy * 2); // Increase scrolling speed by multiplying with a factor (e.g., 1.5)
//                super.onScrolled(recyclerView, dx, newDy);
//            }
//        });
//
//        // Load products for the specified category
//        databaseService.getAllProductsByCategoryOnly(category, new DatabaseService.GetAllProductsCallback() {
//            @Override
//            public void onSuccess(ArrayList<ProductModel> products) {
//                // Add the loaded products to the home product model list
//                homeProductModel.add(new HomeProductModel(category, products));
//
//                // Notify the adapter that data set has changed
//                homeProductAdapter.notifyDataSetChanged();
//
//                // Hide progress bar and shimmer effect when products are loaded
//                if (binding.ChatsProgressBar.getVisibility() == View.VISIBLE) {
//                    binding.ChatsProgressBar.setVisibility(View.GONE);
//                }
//                binding.shimmerLayout.stopShimmer();
//                binding.shimmerLayout.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onError(String errorMessage) {
//                // Handle the error here
//                basicFun.AlertDialog(requireContext(), errorMessage);
//            }
//        });
//    }


//    void offers(){
//        databaseService.getOffers(new DatabaseService.getOffer() {
//            @Override
//            public void onSuccess(ArrayList<OffersModel> offers) {
//                final Dialog dialog = new Dialog(requireContext());
//                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                dialog.setContentView(R.layout.sheet_offers);
//                PhotoView photoView = dialog.findViewById(R.id.photo_view);
//                ImageView closeButton = dialog.findViewById(R.id.closeButton);
//
//                Glide.with(requireContext()).load(offers.get(0).getOfferImage()).placeholder(R.drawable.placeholder).into(photoView);
////        codRadioButton.setOnClickListener(v -> placeOrder(System.currentTimeMillis(), "cash", "pending"));
//                closeButton.setOnClickListener(v -> dialog.dismiss());
//
//                dialog.show();
//                dialog.setCancelable(true);
//                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
//                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                dialog.getWindow().getAttributes().windowAnimations = R.style.Animationboy;
//                dialog.getWindow().setGravity(Gravity.CENTER);
//
//
//            }
//
//            @Override
//            public void onError(DatabaseError errorMessage) {
//
//            }
//        });
//
//
//
//
//
//
//
//    }



//    void LoadProduct() {
//        model = new ArrayList<>();
//        productAdapter = new ProductAdapter(model, this, requireContext(), "Main");
//        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
//        binding.productsList.setAdapter(productAdapter);
//        binding.productsList.setLayoutManager(layoutManager);
//
//        // Load initial set of products
//        loadNextProducts();
//
//        // Set up RecyclerView scroll listener
//        binding.productsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                int visibleItemCount = layoutManager.getChildCount();
//                int totalItemCount = layoutManager.getItemCount();
//                int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();
//
//                if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
//                    // User is near the end of the list, load more products
//                    loadNextProducts();
//                }
//            }
//        });
//    }

//    private void loadNextProducts() {
//        FirebaseFirestore database = FirebaseFirestore.getInstance();
//        binding.ChatsProgressBar.setVisibility(View.VISIBLE);
//
//        Query query;
//        if (lastVisibleProduct != null) {
//            // Load next set of products after the last visible product
//            query = database.collection("Product")
//                    .startAfter(lastVisibleProduct)
//                    .limit(PAGE_SIZE);
//        } else {
//            // Initial load
//            query = database.collection("Product")
//                    .limit(PAGE_SIZE);
//        }
//
//        query.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                ArrayList<ProductModel> products = new ArrayList<>();
//                for (DocumentSnapshot document : task.getResult()) {
//                    ProductModel product = document.toObject(ProductModel.class);
//                    if (product != null && product.isAvailable()) {
//                        products.add(product);
//                    }
//                }
//                model.addAll(products);
//                productAdapter.notifyDataSetChanged();
//
//                // Update last visible product
//                if (!products.isEmpty()) {
//                    lastVisibleProduct = task.getResult().getDocuments()
//                            .get(task.getResult().size() - 1);
//                }
//
//                binding.ChatsProgressBar.setVisibility(View.GONE);
//            } else {
//                // Handle the error
//                basicFun.AlertDialog(requireContext(), task.getException().toString());
//                binding.ChatsProgressBar.setVisibility(View.GONE);
//            }
//        });
//    }






//    void LoadCategory() {
//        ArrayList<ProductCategoryModel> categoryModels = new ArrayList<>();
//        categoryAdapter = new CategoryAdapter(categoryModels, requireContext(), this);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
//        binding.category.setLayoutManager(layoutManager);
//        binding.category.setAdapter(categoryAdapter);
//        databaseService.getAllCategory(new DatabaseService.GetAllCategoryCallback() {
//            @SuppressLint("NotifyDataSetChanged")
//            @Override
//            public void onSuccess(ArrayList<ProductCategoryModel> category) {
//                categoryModels.addAll(category);
//                categoryAdapter.notifyDataSetChanged();
//
//            }
//
//            @Override
//            public void onError(String errorMessage) {
//
//            }
//        });
//    }

    @Override
    public void onClick(ProductModel productModel) {
//        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
//        Bundle bundle = new Bundle();
//        bundle.putParcelable("productDetails", productModel);

//        ProductDetailsFragment productDetailsFragment = new ProductDetailsFragment();
//        productDetailsFragment.setArguments(bundle);
////        productDetailsFragment.setArguments(bundle);
//        transaction.replace(R.id.loader, productDetailsFragment, "HomeFragment");
//        transaction.addToBackStack(null);
//        transaction.commit();
    }

    @Override
    public void onClick(ProductCategoryModel productModel) {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("filter", productModel.getTag());
        ProductFilterFragment fragment = new ProductFilterFragment();
        fragment.setArguments(bundle);
        transaction.replace(R.id.loader, fragment, "ProductFilterFragment");
        transaction.addToBackStack(null);
        transaction.commit();

    }


    @SuppressLint("SetTextI18n")
    private void showProductViewDialog(ProductModel productModel) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        ProductViewDialogBinding productViewDialogBinding = ProductViewDialogBinding.inflate(getLayoutInflater());
//        productViewDialogBinding.getRoot().setBackgroundResource(R.drawable.rounded_corners);

        // Set data and click listeners
//        productViewDialogBinding.productsImages.setImageResource(R.drawable.sample_product_image); // Replace with actual image resource
        productViewDialogBinding.ProductName.setText(productModel.getProductName());

        for (int i = 0; i < productModel.getImageURL().size(); i++) {
            String imageUrl = productModel.getImageURL().get(i);
            CarouselItem carouselItem = new CarouselItem(imageUrl);
            productViewDialogBinding.productsImages.addData(carouselItem);
        }

        if (!Objects.equals(productModel.getProductDescription(), "")){
            productViewDialogBinding.productDescription.setVisibility(View.VISIBLE);
            productViewDialogBinding.Description .setVisibility(View.VISIBLE);
            productViewDialogBinding.productDescription.setText(productModel.getProductDescription());
        }
        double mrp = productModel.getMrp(); // Replace with the actual MRP
        double sellingPrice = productModel.getPrice(); // Replace with the actual selling price
//
        double discountPercentage = mrp - sellingPrice;

//        float percentageSaved = (float) (((productModel.getMrp() - productModel.getPrice()) / productModel.getMrp()) * 100f);
        productViewDialogBinding.discount.setText("₹" + discountPercentage + "% off");
        productViewDialogBinding.Price.setText("₹" + productModel.getPrice());
        productViewDialogBinding.quantitySmail.setText("(₹" + productModel.getPrice() + " / " + productModel.getSubUnit() + productModel.getUnit() + ")");
        productViewDialogBinding.MRP.setText(":"+productModel.getMrp());
        productViewDialogBinding.MRP.setPaintFlags(productViewDialogBinding.MRP.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


        if (productModel.getQuantity() == 0) {
            productViewDialogBinding.OutOfStockBuyOptions.setVisibility(View.VISIBLE);
            productViewDialogBinding.quantity.setText("0");
            productViewDialogBinding.TextInStock.setText("Out of Stock");
            productViewDialogBinding.quantityBox.setVisibility(View.INVISIBLE);
            productViewDialogBinding.TextInStock.setTextColor(ContextCompat.getColor(requireContext(), R.color.FixRed));
            productViewDialogBinding.AddTOCart.setVisibility(View.INVISIBLE);
//            binding.BuyNow.setVisibility(View.INVISIBLE);
        }
        productViewDialogBinding.categoryType.setText(productModel.getCategory());
        productViewDialogBinding.netQuantity.setText(productModel.getSubUnit() + productModel.getUnit());
        String diet;
        if (productModel.getProductType().equals("FoodVeg")) {
//            binding.ItemType.setImageResource(R.drawable.food_green);
            diet = "Veg";
        } else if (productModel.getProductType().equals("FoodNonVeg")) {
//            binding.ItemType.setImageResource(R.drawable.food_brown);
            diet = "NonVeg";
        } else {
            diet = "not food item.";
//            binding.ItemType.setVisibility(View.GONE);
            productViewDialogBinding.dietType.setVisibility(View.GONE);
            productViewDialogBinding.diet.setVisibility(View.GONE);
        }
        productViewDialogBinding.dietType.setText(diet);
        productViewDialogBinding.ExpiryDate.setText(productModel.getEditDate());



//        productViewDialogBinding.productDescription.setText("This is a description of the sample product.");
//        productViewDialogBinding.quantity.setText("1");

//        binding.minusBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int currentQuantity = Integer.parseInt(binding.quantity.getText().toString());
//                if (currentQuantity > 1) {
//                    binding.quantity.setText(String.valueOf(currentQuantity - 1));
//                }
//            }
//        });
//
//        binding.plusBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int currentQuantity = Integer.parseInt(binding.quantity.getText().toString());
//                binding.quantity.setText(String.valueOf(currentQuantity + 1));
//            }
//        });

        productViewDialogBinding.AddTOCart.setOnClickListener(v -> {
            // Handle add to cart action
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.setContentView(productViewDialogBinding.getRoot());
        bottomSheetDialog.show();
    }
}