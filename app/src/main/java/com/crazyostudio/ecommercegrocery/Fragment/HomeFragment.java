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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.bumptech.glide.Glide;
import com.crazyostudio.ecommercegrocery.Adapter.HomeCategoryAdapter;
import com.crazyostudio.ecommercegrocery.Adapter.HomeProductAdapter;
import com.crazyostudio.ecommercegrocery.Adapter.ProductAdapter;
import com.crazyostudio.ecommercegrocery.Model.BannerModels;
import com.crazyostudio.ecommercegrocery.Model.HomeCategoryModel;
import com.crazyostudio.ecommercegrocery.Model.HomeProductModel;
import com.crazyostudio.ecommercegrocery.Model.ProductCategoryModel;
import com.crazyostudio.ecommercegrocery.Model.ProductModel;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.Services.DatabaseService;
import com.crazyostudio.ecommercegrocery.databinding.CategoryViewDialogBinding;
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
    private FragmentHomeBinding binding;
    private DatabaseService databaseService;
    private HomeCategoryAdapter homeCategoryAdapter;
    private HomeCategoryAdapter homeProductBoysSkinAdapter;
    private ArrayList<HomeProductModel> homeProductModel;
    private ArrayList<HomeProductModel> homeProductModelBoysSkin;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);
//        databaseService = new DatabaseService();
//        SetupAdapter();
//        SetupAdapterByBoysSkin();
//        homeCategoryAdapter = new HomeCategoryAdapter(homeProductModel, requireContext(), new HomeCategoryInterface() {
//            @Override
//            public void onClick(HomeProductModel filter) {
//
//            }
//        });
//        homeProductBoysSkinAdapter = new HomeCategoryAdapter(homeProductModelBoysSkin, requireContext(), new HomeCategoryInterface() {
//            @Override
//            public void onClick(HomeProductModel filter) {
//
//            }
//        });
//
//        LoadCarousel();
//        LoadProductCategory();
//        loadProductsForCategories(new String[]{"chips and Snacks"});
//        loadProductsForCategoriesByBoysSkin(new String[]{"toothpaste"});


        databaseService = new DatabaseService();

        homeProductModel = new ArrayList<>();
        homeProductModelBoysSkin = new ArrayList<>();

        homeCategoryAdapter = new HomeCategoryAdapter(homeProductModel, requireContext(), new HomeCategoryInterface() {
            @Override
            public void onClick(HomeProductModel filter) {
                ViewCat(filter);
            }
        });
        homeProductBoysSkinAdapter = new HomeCategoryAdapter(homeProductModelBoysSkin, requireContext(), new HomeCategoryInterface() {
            @Override
            public void onClick(HomeProductModel filter) {
                ViewCat(filter);

            }
        });

        setupAdapters();
        loadInitialData();





        return binding.getRoot();

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
                int newDy = dy * 2; // Increase scrolling speed by multiplying with a factor (e.g., 1.5)
                super.onScrolled(recyclerView, dx, newDy);
            }
        });


    }
//    private void SetupAdapterByBoysSkin() {
//        homeProductModelBoysSkin = new ArrayList<>();
//        LinearLayoutManager layoutManager1 = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false);
//        binding.boysSkinCareRecyclerView.setAdapter(homeProductBoysSkinAdapter);
//        binding.recyclerCategory.setLayoutManager(layoutManager1);
//        layoutManager1.setSmoothScrollbarEnabled(true);
//        CustomSmoothScroller smoothScroller = new CustomSmoothScroller(requireContext());
//        smoothScroller.setTargetPosition(0);
//        layoutManager1.startSmoothScroll(smoothScroller);
//
//        binding.boysSkinCareRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                // Adjust the scrolling speed by multiplying dy (vertical scroll amount) with a factor
//                int newDy = (int) (dy * 2); // Increase scrolling speed by multiplying with a factor (e.g., 1.5)
//                super.onScrolled(recyclerView, dx, newDy);
//            }
//        });
//    }
//
    private void setupAdapters() {
        LinearLayoutManager categoryLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.recyclerCategory.setLayoutManager(categoryLayoutManager);
        binding.recyclerCategory.setAdapter(homeCategoryAdapter);

        LinearLayoutManager boysSkinLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.boysSkinCareRecyclerView.setLayoutManager(boysSkinLayoutManager);
        binding.boysSkinCareRecyclerView.setAdapter(homeProductBoysSkinAdapter);

    }
    private void loadInitialData() {
        LoadCarousel();
        LoadProductCategory();

//        loadProductsForCategories(new String[]{"chips and Snacks"});
        loadProductsForCategories(new String[]{"chips and Snacks", "toothpaste", "hair oil ", "drinks"});
        loadProductsForCategoriesByBoysSkin(new String[]{"toothpaste", "drinks"});

//        loadProductsForCategoriesByBoysSkin(new String[]{"toothpaste"});
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










    public static boolean isDarkModeEnabled(Context context) {
        int currentNightMode = context.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES;
    }

    private void loadProductsForCategories(String[] categories) {
        for (String category : categories) {
            loadProduct(category, homeProductModel, homeCategoryAdapter);
        }
    }

    private void loadProductsForCategoriesByBoysSkin(String[] categories) {
        for (String category : categories) {
            loadProduct(category, homeProductModelBoysSkin, homeProductBoysSkinAdapter);
        }
    }
    private void loadProduct(String category, ArrayList<HomeProductModel> productList, HomeCategoryAdapter adapter) {
        databaseService.getAllProductsByCategoryOnly(category, new DatabaseService.GetAllProductsCallback() {
            @Override
            public void onSuccess(ArrayList<ProductModel> products) {
//                productList.clear();
                productList.add(new HomeProductModel(category, products));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String errorMessage) {
                basicFun.AlertDialog(requireContext(), errorMessage);
            }
        });
    }



//    @SuppressLint("NotifyDataSetChanged")
//    void LoadProduct(String category) {
////        homeProductModel.clear();
//        databaseService.getAllProductsByCategoryOnly(category,new DatabaseService.GetAllProductsCallback() {
//            @SuppressLint("NotifyDataSetChanged")
//            @Override
//            public void onSuccess(ArrayList<ProductModel> products) {
//                homeProductModel.clear();
//                homeProductModel.add(new HomeProductModel(category,products));
//                homeCategoryAdapter.notifyDataSetChanged();
//            }
//
//
//            @Override
//            public void onError(String errorMessage) {
//                // Handle the error her
//                basicFun.AlertDialog(requireContext(),errorMessage);
//            }
//        });
//        homeCategoryAdapter.notifyDataSetChanged();
//
//    }
//
//    @SuppressLint("NotifyDataSetChanged")
//    void LoadProductByBoysSkin(String category) {
////        homeProductModel.clear();
//        databaseService.getAllProductsByCategoryOnly(category,new DatabaseService.GetAllProductsCallback() {
//            @SuppressLint("NotifyDataSetChanged")
//            @Override
//            public void onSuccess(ArrayList<ProductModel> products) {
//
////                categoryBoysSkinModels.add(new HomeCategoryModel(category,Images,String.valueOf(products.size()),category));
//                homeProductModelBoysSkin.clear();
//                homeProductModelBoysSkin.add(new HomeProductModel(category,products));
////                model.addAll(products);
//                homeProductBoysSkinAdapter.notifyDataSetChanged();
//
//            }
//
//
//            @Override
//            public void onError(String errorMessage) {
//                // Handle the error her
//                basicFun.AlertDialog(requireContext(),errorMessage);
//            }
//        });
//        homeProductBoysSkinAdapter.notifyDataSetChanged();
//
//    }
//
//






    @Override
    public void onClick(ProductModel productModel) {
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

//
//    @SuppressLint("SetTextI18n")
//    private void showProductViewDialog(ProductModel productModel) {
//        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
//        ProductViewDialogBinding productViewDialogBinding = ProductViewDialogBinding.inflate(getLayoutInflater());
////        productViewDialogBinding.getRoot().setBackgroundResource(R.drawable.rounded_corners);
//
//        // Set data and click listeners
////        productViewDialogBinding.productsImages.setImageResource(R.drawable.sample_product_image); // Replace with actual image resource
//        productViewDialogBinding.ProductName.setText(productModel.getProductName());
//
//        for (int i = 0; i < productModel.getImageURL().size(); i++) {
//            String imageUrl = productModel.getImageURL().get(i);
//            CarouselItem carouselItem = new CarouselItem(imageUrl);
//            productViewDialogBinding.productsImages.addData(carouselItem);
//        }
//
//        if (!Objects.equals(productModel.getProductDescription(), "")){
//            productViewDialogBinding.productDescription.setVisibility(View.VISIBLE);
//            productViewDialogBinding.Description .setVisibility(View.VISIBLE);
//            productViewDialogBinding.productDescription.setText(productModel.getProductDescription());
//        }
//        double mrp = productModel.getMrp(); // Replace with the actual MRP
//        double sellingPrice = productModel.getPrice(); // Replace with the actual selling price
////
//        double discountPercentage = mrp - sellingPrice;
//
////        float percentageSaved = (float) (((productModel.getMrp() - productModel.getPrice()) / productModel.getMrp()) * 100f);
//        productViewDialogBinding.discount.setText("₹" + discountPercentage + "% off");
//        productViewDialogBinding.Price.setText("₹" + productModel.getPrice());
//        productViewDialogBinding.quantitySmail.setText("(₹" + productModel.getPrice() + " / " + productModel.getSubUnit() + productModel.getUnit() + ")");
//        productViewDialogBinding.MRP.setText(":"+productModel.getMrp());
//        productViewDialogBinding.MRP.setPaintFlags(productViewDialogBinding.MRP.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//
//
//        if (productModel.getQuantity() == 0) {
//            productViewDialogBinding.OutOfStockBuyOptions.setVisibility(View.VISIBLE);
//            productViewDialogBinding.quantity.setText("0");
//            productViewDialogBinding.TextInStock.setText("Out of Stock");
//            productViewDialogBinding.quantityBox.setVisibility(View.INVISIBLE);
//            productViewDialogBinding.TextInStock.setTextColor(ContextCompat.getColor(requireContext(), R.color.FixRed));
//            productViewDialogBinding.AddTOCart.setVisibility(View.INVISIBLE);
////            binding.BuyNow.setVisibility(View.INVISIBLE);
//        }
//        productViewDialogBinding.categoryType.setText(productModel.getCategory());
//        productViewDialogBinding.netQuantity.setText(productModel.getSubUnit() + productModel.getUnit());
//        String diet;
//        if (productModel.getProductType().equals("FoodVeg")) {
////            binding.ItemType.setImageResource(R.drawable.food_green);
//            diet = "Veg";
//        } else if (productModel.getProductType().equals("FoodNonVeg")) {
////            binding.ItemType.setImageResource(R.drawable.food_brown);
//            diet = "NonVeg";
//        } else {
//            diet = "not food item.";
////            binding.ItemType.setVisibility(View.GONE);
//            productViewDialogBinding.dietType.setVisibility(View.GONE);
//            productViewDialogBinding.diet.setVisibility(View.GONE);
//        }
//        productViewDialogBinding.dietType.setText(diet);
//        productViewDialogBinding.ExpiryDate.setText(productModel.getEditDate());
//
//
//
//        productViewDialogBinding.AddTOCart.setOnClickListener(v -> {
//            // Handle add to cart action
//            bottomSheetDialog.dismiss();
//        });
//
//        bottomSheetDialog.setContentView(productViewDialogBinding.getRoot());
//        bottomSheetDialog.show();
//    }
//

    @SuppressLint("SetTextI18n")
    private void showProductViewDialog(ProductModel productModel) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        ProductViewDialogBinding productViewDialogBinding = ProductViewDialogBinding.inflate(getLayoutInflater());

        productViewDialogBinding.ProductName.setText(productModel.getProductName());

        for (String imageUrl : productModel.getImageURL()) {
            CarouselItem carouselItem = new CarouselItem(imageUrl);
            productViewDialogBinding.productsImages.addData(carouselItem);
        }

        if (!Objects.equals(productModel.getProductDescription(), "")) {
            productViewDialogBinding.productDescription.setVisibility(View.VISIBLE);
            productViewDialogBinding.Description.setVisibility(View.VISIBLE);
            productViewDialogBinding.productDescription.setText(productModel.getProductDescription());
        }

        double mrp = productModel.getMrp();
        double sellingPrice = productModel.getPrice();
        double discountPercentage = mrp - sellingPrice;

        productViewDialogBinding.discount.setText("₹" + discountPercentage + "% off");
        productViewDialogBinding.Price.setText("₹" + productModel.getPrice());
        productViewDialogBinding.quantitySmail.setText("(₹" + productModel.getPrice() + " / " + productModel.getSubUnit() + productModel.getUnit() + ")");
        productViewDialogBinding.MRP.setText(":" + productModel.getMrp());
        productViewDialogBinding.MRP.setPaintFlags(productViewDialogBinding.MRP.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        if (productModel.getQuantity() == 0) {
            productViewDialogBinding.OutOfStockBuyOptions.setVisibility(View.VISIBLE);
            productViewDialogBinding.quantity.setText("0");
            productViewDialogBinding.TextInStock.setText("Out of Stock");
            productViewDialogBinding.quantityBox.setVisibility(View.INVISIBLE);
            productViewDialogBinding.TextInStock.setTextColor(ContextCompat.getColor(requireContext(), R.color.FixRed));
            productViewDialogBinding.AddTOCart.setVisibility(View.INVISIBLE);
        }

        productViewDialogBinding.categoryType.setText(productModel.getCategory());
        productViewDialogBinding.netQuantity.setText(productModel.getSubUnit() + productModel.getUnit());

        String diet;
        if (productModel.getProductType().equals("FoodVeg")) {
            diet = "Veg";
        } else if (productModel.getProductType().equals("FoodNonVeg")) {
            diet = "NonVeg";
        } else {
            diet = "not food item.";
            productViewDialogBinding.dietType.setVisibility(View.GONE);
            productViewDialogBinding.diet.setVisibility(View.GONE);
        }
        productViewDialogBinding.dietType.setText(diet);
        productViewDialogBinding.ExpiryDate.setText(productModel.getEditDate());

        productViewDialogBinding.AddTOCart.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.setContentView(productViewDialogBinding.getRoot());
        bottomSheetDialog.show();
    }

    void ViewCat(HomeProductModel model){
        Toast.makeText(requireContext(), "ww", Toast.LENGTH_SHORT).show();
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        CategoryViewDialogBinding productViewDialogBinding = CategoryViewDialogBinding.inflate(getLayoutInflater());

        RecyclerView recyclerView;
        ImageButton closeButton;
//        Animation slideUp, slideDown;
//        slideUp = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up);
//        slideDown = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_down);

        recyclerView = productViewDialogBinding.productsRecyclerView;
        closeButton = productViewDialogBinding.closeButton;

        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        recyclerView.setAdapter(new ProductAdapter(model.getProduct(), productModel -> {

        },requireContext(),"Main"));

        closeButton.setOnClickListener(v -> bottomSheetDialog.dismiss());


        bottomSheetDialog.setContentView(productViewDialogBinding.getRoot());
        bottomSheetDialog.show();





    }
}