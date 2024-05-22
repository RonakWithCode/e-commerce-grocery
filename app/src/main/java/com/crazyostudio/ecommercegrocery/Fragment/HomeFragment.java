package com.crazyostudio.ecommercegrocery.Fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewbinding.ViewBinding;

import com.bumptech.glide.Glide;
import com.crazyostudio.ecommercegrocery.Adapter.HomeCategoryAdapter;
import com.crazyostudio.ecommercegrocery.Adapter.HomeProductAdapter;
import com.crazyostudio.ecommercegrocery.Adapter.ProductAdapter;
import com.crazyostudio.ecommercegrocery.Model.BannerModels;
import com.crazyostudio.ecommercegrocery.Model.HomeProductModel;
import com.crazyostudio.ecommercegrocery.Model.ProductModel;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.Services.DatabaseService;
import com.crazyostudio.ecommercegrocery.databinding.CategoryViewDialogBinding;
import com.crazyostudio.ecommercegrocery.databinding.FragmentHomeBinding;
import com.crazyostudio.ecommercegrocery.databinding.ProductViewDialogBinding;
import com.crazyostudio.ecommercegrocery.interfaceClass.onClickProductAdapter;
import com.crazyostudio.ecommercegrocery.javaClasses.basicFun;
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


public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private DatabaseService databaseService;
    private HomeCategoryAdapter homeCategoryAdapter;
    private HomeCategoryAdapter homeProductBoysSkinAdapter;
    private HomeProductAdapter MultiViewAdapter;

    private ArrayList<HomeProductModel> MultiViewModel;
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
        MultiViewModel = new ArrayList<>();
        homeProductModelBoysSkin = new ArrayList<>();

        homeCategoryAdapter = new HomeCategoryAdapter(homeProductModel, requireContext(), this::ViewCat);
        homeProductBoysSkinAdapter = new HomeCategoryAdapter(homeProductModelBoysSkin, requireContext(), this::ViewCat);
        MultiViewAdapter = new HomeProductAdapter(MultiViewModel, this::showProductViewDialog, requireActivity());

        setupAdapters();
        loadInitialData();





        return binding.getRoot();

    }


    private void setupAdapters() {
        LinearLayoutManager categoryLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.recyclerCategory.setLayoutManager(categoryLayoutManager);
        binding.recyclerCategory.setAdapter(homeCategoryAdapter);

        LinearLayoutManager boysSkinLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.boysSkinCareRecyclerView.setLayoutManager(boysSkinLayoutManager);
        binding.boysSkinCareRecyclerView.setAdapter(homeProductBoysSkinAdapter);


        LinearLayoutManager MultiViewManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.MultiViewAdapter.setLayoutManager(MultiViewManager);
        binding.MultiViewAdapter.setAdapter(MultiViewAdapter);

//        MultiViewAdapter.setHasStableIds(true);


    }
    private void loadInitialData() {
        LoadCarousel();
        LoadProductCategory();

        loadProductsMultiViewForLoop(new String[]{"chips and Snacks"});
        loadProductsForCategories(new String[]{"chips and Snacks", "toothpaste", "hair oil ", "drinks"});
        loadProductsForCategoriesByBoysSkin(new String[]{"toothpaste", "drinks"});
    }

    private void loadProductsMultiViewForLoop(String[] strings) {
        for (String s : strings) {
            loadProductsMultiView(s);
        }
    }


    private void loadProductsMultiView(String s) {
        databaseService.getAllProductsByCategoryOnly(s, new DatabaseService.GetAllProductsCallback() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(ArrayList<ProductModel> products) {
//                MultiViewModel.clear();
                MultiViewModel.add(new HomeProductModel(s,products));
                MultiViewAdapter.notifyDataSetChanged();

            }

            @Override
            public void onError(String errorMessage) {
                basicFun.AlertDialog(requireContext(), errorMessage);

            }
        });
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


    @SuppressLint("SetTextI18n")
    private void showProductViewDialog(ProductModel productModel,ArrayList<ProductModel> sameProducts) {
        Dialog bottomSheetDialog = new Dialog(requireContext());
        ProductViewDialogBinding productViewDialogBinding = ProductViewDialogBinding.inflate(getLayoutInflater());

        productViewDialogBinding.ProductName.setText(productModel.getProductName());
        productViewDialogBinding.closeButton.setOnClickListener(v -> bottomSheetDialog.dismiss());

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
        sameProducts.remove(productModel);
        if (sameProducts.isEmpty()){
            productViewDialogBinding.product.setVisibility(View.GONE);
            productViewDialogBinding.itemCategory.setVisibility(View.GONE);
        }else {
            LinearLayoutManager LayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
            productViewDialogBinding.itemCategory.setLayoutManager(LayoutManager);
            productViewDialogBinding.itemCategory.setAdapter(new ProductAdapter(sameProducts, (productModel1, sameProducts1) -> showProductViewDialog(productModel1, sameProducts1),requireContext(),"Main"));
        }

        bottomSheetDialog.setContentView(productViewDialogBinding.getRoot());
        bottomSheetDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        bottomSheetDialog.getWindow().getAttributes().windowAnimations = R.style.bottom_sheet_dialogAnimation;
        bottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);

        bottomSheetDialog.show();
    }

    void ViewCat(HomeProductModel model){
        Dialog bottomSheetDialog = new Dialog(requireContext());
        CategoryViewDialogBinding productViewDialogBinding = CategoryViewDialogBinding.inflate(getLayoutInflater());

        productViewDialogBinding.productsRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        productViewDialogBinding.productsRecyclerView.setAdapter(new ProductAdapter(model.getProduct(), new onClickProductAdapter() {
            @Override
            public void onClick(ProductModel productModel, ArrayList<ProductModel> sameProducts) {
                showProductViewDialog(productModel,sameProducts);
            }
        }, requireContext(), "Main"));
        productViewDialogBinding.title.setText(model.getTitle());

        productViewDialogBinding.closeButton.setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomSheetDialog.setContentView(productViewDialogBinding.getRoot());

        bottomSheetDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        bottomSheetDialog.getWindow().getAttributes().windowAnimations = R.style.bottom_sheet_dialogAnimation;
        bottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);

        bottomSheetDialog.show();





    }




}