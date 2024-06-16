package com.crazyostudio.ecommercegrocery.Fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.crazyostudio.ecommercegrocery.Activity.AuthMangerActivity;
import com.crazyostudio.ecommercegrocery.Adapter.DialogSliderAdapter;
import com.crazyostudio.ecommercegrocery.Adapter.HomeCategoryAdapter;
import com.crazyostudio.ecommercegrocery.Adapter.HomeProductAdapter;
import com.crazyostudio.ecommercegrocery.Adapter.ProductAdapter;
import com.crazyostudio.ecommercegrocery.Adapter.SliderAdapter;
import com.crazyostudio.ecommercegrocery.Adapter.VariantsAdapter;
import com.crazyostudio.ecommercegrocery.Manager.ProductManager;
import com.crazyostudio.ecommercegrocery.Model.BannerModels;
import com.crazyostudio.ecommercegrocery.Model.BrandModel;
import com.crazyostudio.ecommercegrocery.Model.HomeProductModel;
import com.crazyostudio.ecommercegrocery.Model.ProductModel;
import com.crazyostudio.ecommercegrocery.Model.ShoppingCartFirebaseModel;
import com.crazyostudio.ecommercegrocery.Model.Variations;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.Services.BrandService;
import com.crazyostudio.ecommercegrocery.Services.DatabaseService;
import com.crazyostudio.ecommercegrocery.databinding.CategoryViewDialogBinding;
import com.crazyostudio.ecommercegrocery.databinding.DialogFullscreenImageBinding;
import com.crazyostudio.ecommercegrocery.databinding.FragmentHomeBinding;
import com.crazyostudio.ecommercegrocery.databinding.ProductViewDialogBinding;
import com.crazyostudio.ecommercegrocery.databinding.RemoveProductBoxAlertBinding;
import com.crazyostudio.ecommercegrocery.javaClasses.CustomSmoothScroller;
import com.crazyostudio.ecommercegrocery.javaClasses.basicFun;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.imaginativeworld.whynotimagecarousel.ImageCarousel;
import org.imaginativeworld.whynotimagecarousel.listener.CarouselListener;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private DatabaseService databaseService;
    private HomeCategoryAdapter homeCategoryAdapter;
    private HomeCategoryAdapter homeProductBoysSkinAdapter;
    private HomeProductAdapter MultiViewAdapter;

    private ArrayList<HomeProductModel> MultiViewModel;
    private ArrayList<HomeProductModel> homeProductModel;
    private ArrayList<HomeProductModel> homeProductModelBoysSkin;
    String userId;
    Dialog HomeProductBottomSheetDialog;
    public HomeFragment() {
        // Required empty public constructor
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null){
            String displayName = currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "Hi user";
            String phoneNumber = currentUser.getPhoneNumber() != null ? currentUser.getPhoneNumber() : "No phone number";
            userId = currentUser.getUid();
            binding.UserName.setText(displayName);
            binding.address.setText(phoneNumber);
        }else {
            binding.UserName.setText("Hi user");
            binding.address.setText("Alwar Mart");
        }
        HomeProductBottomSheetDialog = new Dialog(requireContext());
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

//       // Change Status Bar color
        Window window = requireActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.OrderYellowColor));

        binding.searchView.setOnClickListener(view->openSearchFragment());

        databaseService = new DatabaseService();

        homeProductModel = new ArrayList<>();
        MultiViewModel = new ArrayList<>();
        homeProductModelBoysSkin = new ArrayList<>();

        homeCategoryAdapter = new HomeCategoryAdapter(homeProductModel, requireContext(), this::ViewCat);
        homeProductBoysSkinAdapter = new HomeCategoryAdapter(homeProductModelBoysSkin, requireContext(), this::ViewCat);
        MultiViewAdapter = new HomeProductAdapter(MultiViewModel, this::showProductViewDialog, requireActivity());


        binding.BestsellersSee.setOnClickListener(v -> SeeAll());
        binding.boysSkinCareSee.setOnClickListener(v -> SeeAll());

        setupAdapters();
        loadInitialData();




        return binding.getRoot();
    }


    public void SeeAll(){
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.loader,new ProductWithSlideCategoryFragment());
        transaction.addToBackStack("SelectLanguageFragment");
        transaction.commit();
    }


    private void setupAdapters() {
        LinearLayoutManager categoryLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.recyclerCategory.setLayoutManager(categoryLayoutManager);
        binding.recyclerCategory.setAdapter(homeCategoryAdapter);

        LinearLayoutManager boysSkinLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.boysSkinCareRecyclerView.setLayoutManager(boysSkinLayoutManager);
        binding.boysSkinCareRecyclerView.setAdapter(homeProductBoysSkinAdapter);


        LinearLayoutManager MultiViewManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        binding.MultiViewAdapter.setLayoutManager(MultiViewManager);
        binding.MultiViewAdapter.setAdapter(MultiViewAdapter);
        MultiViewManager.setSmoothScrollbarEnabled(true);
        CustomSmoothScroller smoothScroller = new CustomSmoothScroller(requireContext());
        smoothScroller.setTargetPosition(0);
        MultiViewManager.startSmoothScroll(smoothScroller);
        binding.MultiViewAdapter.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                // Adjust the scrolling speed by multiplying dy (vertical scroll amount) with a factor
                int newDy = (int) (dy * 2); // Increase scrolling speed by multiplying with a factor (e.g., 1.5)
                super.onScrolled(recyclerView, dx, newDy);
            }
        });

//        MultiViewAdapter.setHasStableIds(true);


    }
    private void loadInitialData() {
        LoadCarousel();
        LoadProductCategory();

//        loadProductsMultiViewForLoop(new String[]{"chips and Snacks"});
        loadProductsMultiViewForLoop(new String[]{"ha","chips and Snacks", "toothpaste", "hair oil ", "drinks"});
        loadProductsForCategories(new String[]{"ha","hahahah","aw","oil","this is me new \uD83D\uDC08" });
        loadProductsForCategoriesByBoysSkin(new String[]{"toothpaste", "drinks"});
    }
    private void loadProductsMultiViewForLoop(String[] strings) {
        for (String s : strings) {
            loadProductsMultiView(s);
        }
    }


    private void loadProductsMultiView(String s) {

        MultiViewModel.clear();

        databaseService.getAllProductsByCategoryOnly(s, new DatabaseService.GetAllProductsCallback() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(ArrayList<ProductModel> products) {
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
        Glide.with(requireContext()).load("https://firebasestorage.googleapis.com/v0/b/e-commerce-11d7d.appspot.com/o/all_category%2Fbrand%2Fitc.png?alt=media&token=34307cc3-bba2-492c-9393-8306898f7757").placeholder(R.drawable.skeleton_shape).into(binding.View1);
        Glide.with(requireContext()).load("https://firebasestorage.googleapis.com/v0/b/e-commerce-11d7d.appspot.com/o/all_category%2Fbrand%2Fitc.png?alt=media&token=34307cc3-bba2-492c-9393-8306898f7757").placeholder(R.drawable.skeleton_shape).into(binding.View2);
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
//        Image_CarouselCenter.setCarouselListener(new CarouselListener() {
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
//                if (modelsCenter.get(position).isByCategory()) {
//                    FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
//                    Bundle bundle = new Bundle();
//                    bundle.putString("filter",modelsCenter.get(position).getFilterQuery());
//                    ProductFilterFragment fragment = new ProductFilterFragment();
//                    fragment.setArguments(bundle);
//                    transaction.replace(R.id.loader, fragment, "ProductFilterFragment");
//                    transaction.addToBackStack("ProductFilterFragment");
//                    transaction.commit();
//                }else {
//                    //TODO : ---------------------------------------------------------
//                    FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
//                    Bundle bundle = new Bundle();
//                    bundle.putString("filter",modelsCenter.get(position).getFilterQuery());
//                    bundle.putString("filterName",modelsCenter.get(position).getBannerCaption());
//                    ProductFilterByQueryFragment fragment = new ProductFilterByQueryFragment();
//                    fragment.setArguments(bundle);
//                    transaction.replace(R.id.loader, fragment, "ProductFilterByQueryFragment");
//                    transaction.addToBackStack("ProductFilterByQueryFragment");
//                    transaction.commit();
//
//                }
//
//
//            }
//
//            @Override
//            public void onLongClick(int position, @NonNull CarouselItem carouselItem) {
//
//            }
//        });

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
//        binding.loader.setVisibility(View.GONE);
    }
    private void loadProduct(String category, ArrayList<HomeProductModel> productList, HomeCategoryAdapter adapter) {
        databaseService.getAllProductsByCategoryOnly(category, new DatabaseService.GetAllProductsCallback() {
            @SuppressLint("NotifyDataSetChanged")
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


    private void showProductViewDialog(ProductModel productModel, ArrayList<ProductModel> sameProducts) {
        ProductManager productManager = new ProductManager(requireActivity());
        List<String> imageUrls = productModel.getProductImage(); // Add your image URLs here
        Dialog bottomSheetDialog = new Dialog(requireContext());
        ProductViewDialogBinding productViewDialogBinding = ProductViewDialogBinding.inflate(getLayoutInflater());
        ViewPager2 viewPager = productViewDialogBinding.viewPager;
        SliderAdapter sliderAdapter = new SliderAdapter(requireContext(), imageUrls, position -> {
            showImageInDialog(position, imageUrls);
        });
        viewPager.setAdapter(sliderAdapter);


        productViewDialogBinding.closeButton.setOnClickListener(v -> bottomSheetDialog.dismiss());


        List<Variations> variationsList = new ArrayList<>();
        ArrayList<ProductModel> productModels = new ArrayList<>();
        VariantsAdapter variantsAdapter = new VariantsAdapter(requireContext(), variationsList, productModel.getProductId(), new VariantsAdapter.VariantsCallback() {
            @Override
            public void Product(String id) {
                boolean isTrue = false;
                for (int i = 0; i < productModels.size(); i++) {
                    if (id.equals(productModels.get(i).getProductId())) {
                        showProductViewDialog(productModels.get(i), sameProducts);
                        isTrue = true;
                    }
                }
                if (!isTrue) {
                    if (!id.equals(productModel.getProductId())) {
                        databaseService.getAllProductById(id, new DatabaseService.GetAllProductsModelCallback() {
                            @Override
                            public void onSuccess(ProductModel products) {
                                if (sameProducts != null) {
                                    showProductViewDialog(products,sameProducts);
                                }
                            }
                            @Override
                            public void onError(String errorMessage) {

                            }
                        });
                    }
                }
            }
        });


        RecyclerView variantsRecyclerView = productViewDialogBinding.variantsList;
        variantsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        variantsRecyclerView.setAdapter(variantsAdapter);
        variationsList.add(new Variations(productModel.getProductId(),productModel.getMinSelectableQuantity()+" * "+productModel.getWeight()+productModel.getWeightSIUnit(),productModel.getPrice()+""));
        variantsAdapter.notifyDataSetChanged();
//        Product Variations

        if (productModel.getVariations() != null) {
//            List<Variations> finalVariationsList = new ArrayList<>();
            for (int i = 0; i < productModel.getVariations().size(); i++) {
                int finalI = i;
                databaseService.getAllProductById(productModel.getVariations().get(i).getId(), new DatabaseService.GetAllProductsModelCallback() {
                    @Override
                    public void onSuccess(ProductModel oneProduct) {
                        variationsList.add(new Variations(oneProduct.getProductId(),productModel.getVariations().get(finalI).getWeightWithSIUnit(), ""+oneProduct.getPrice()));
                        productModels.add(oneProduct);
                        variantsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
//            variationsList.addAll(finalVariationsList);

        }

//        End Product Variations

//        Set up
        productViewDialogBinding.categoryName.setText(productModel.getCategory());
        productViewDialogBinding.ProductName.setText(productModel.getProductName());


//        Set Brand with logo
        productViewDialogBinding.brandNameInBox.setText(productModel.getBrand());
        BrandService brandService = new BrandService(requireContext());
        brandService.getAllBrandWithIconsById(productModel.getBrand(), new BrandService.addBrandsByIdListener() {
            @Override
            public void onFailure(Exception error) {

            }

            @Override
            public void onSuccess(BrandModel brandModel) {
                Glide.with(requireContext())
                        .load(brandModel.getBrandIcon())
                        .placeholder(R.drawable.product_image_shimmee_effect) // Placeholder image while loading
                        .error(R.drawable.product_image_shimmee_effect) // Error image if loading fails
                        .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) // Resize the image
                        .centerCrop() // Scale type for resizing
                        .into(productViewDialogBinding.brandIcon);
            }
        });

        productViewDialogBinding.Price.setText("₹" + productModel.getPrice());
        productViewDialogBinding.size.setText(productModel.getWeight() +" "+ productModel.getWeightSIUnit());
        productViewDialogBinding.MRP.setText(":" + productModel.getMrp());
        productViewDialogBinding.MRP.setPaintFlags(productViewDialogBinding.MRP.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        if (!productModel.getProductDescription().isEmpty())
        {
            productViewDialogBinding.Description.setVisibility(View.VISIBLE);
            productViewDialogBinding.DescriptionTextView.setVisibility(View.VISIBLE);
            productViewDialogBinding.Description.setText(productModel.getProductDescription());
        }

         if (FirebaseAuth.getInstance().getCurrentUser() !=null) {
             productManager.isProductInCart(productModel.getProductId(), new ProductManager.addListenerForIsProductInCart() {
                 @Override
                 public void FoundProduct(ShoppingCartFirebaseModel shoppingCartFirebaseModel) {
                     productViewDialogBinding.quantityBox.setVisibility(View.VISIBLE);
                     productViewDialogBinding.AddTOCart.setVisibility(View.GONE);
                     productViewDialogBinding.quantity.setText(""+shoppingCartFirebaseModel.getProductSelectQuantity());
                 }

                 @Override
                 public void notFoundInCart() {
                     productViewDialogBinding.AddTOCart.setVisibility(View.VISIBLE);
                     productViewDialogBinding.quantityBox.setVisibility(View.GONE);
                 }
             });


//             String userId = FirebaseAuth.getInstance().getUid();
//            assert userId != null;
//            DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("Cart").child(userId);
//            String productNameToFind = productModel.getProductId();
//            Query query = productsRef.orderByKey().equalTo(productNameToFind);
//            query.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    if (dataSnapshot.exists()) {
//                        productViewDialogBinding.quantityBox.setVisibility(View.VISIBLE);
//                        productViewDialogBinding.AddTOCart.setVisibility(View.GONE);
////                        productViewDialogBinding.AddTOCart.setText("Go to Cart");
//////                        productViewDialogBinding.quantityBox.setVisibility(View.GONE);
//////                        addTOCart.setText("Go to Cart");
////                        productViewDialogBinding.AddTOCart.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white));
////                        productViewDialogBinding.AddTOCart.setTextColor(ContextCompat.getColor(requireContext(), R.color.FixBlack));
//
//                    }
//                }
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//                    // Handle the error in case of a database error.
//                    Toast.makeText(getContext(), "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });
//
        }

        if (productModel.getStockCount() == 0) {
            productViewDialogBinding.OutOfStockBuyOptions.setVisibility(View.VISIBLE);
            productViewDialogBinding.quantity.setText("0");
//            productViewDialogBinding.TextInStock.setText("Out of Stock");
            productViewDialogBinding.quantityBox.setVisibility(View.GONE);
//            productViewDialogBinding.TextInStock.setTextColor(ContextCompat.getColor(requireContext(), R.color.FixRed));
            productViewDialogBinding.AddTOCart.setVisibility(View.GONE);
        }

        String diet;
        if (productModel.getProductIsFoodItem().equals("FoodVeg")) {
            diet = "Veg";

            Glide.with(requireActivity())
                    .load(R.drawable.food_green)
                    .placeholder(R.drawable.product_image_shimmee_effect) // Placeholder image while loading
                    .error(R.drawable.product_image_shimmee_effect) // Error image if loading fails
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) // Resize the image
                    .centerCrop() // Scale type for resizing
                    .into(productViewDialogBinding.FoodTypeIcon);
//            productViewDialogBinding.FoodTypeIcon.set
        }
        else if (productModel.getProductType().equals("FoodNonVeg")) {
            diet = "NonVeg";
            Glide.with(requireActivity())
                    .load(R.drawable.food_brown)
                    .placeholder(R.drawable.product_image_shimmee_effect) // Placeholder image while loading
                    .error(R.drawable.product_image_shimmee_effect) // Error image if loading fails
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) // Resize the image
                    .centerCrop() // Scale type for resizing
                    .into(productViewDialogBinding.FoodTypeIcon);
        }
        else if (productModel.getProductIsFoodItem().equals("VegetableAndFruit")) {
            diet = "VegetableAndFruit";
            Glide.with(requireActivity())
                    .load(R.drawable.food_green)
                    .placeholder(R.drawable.product_image_shimmee_effect) // Placeholder image while loading
                    .error(R.drawable.product_image_shimmee_effect) // Error image if loading fails
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) // Resize the image
                    .centerCrop() // Scale type for resizing
                    .into(productViewDialogBinding.FoodTypeIcon);
        }
        else {
            diet = "not food item.";
            productViewDialogBinding.FoodTypeIcon.setVisibility(View.GONE);
//            productViewDialogBinding.dietType.setVisibility(View.GONE);
//            productViewDialogBinding.diet.setVisibility(View.GONE);
        }
//        productViewDialogBinding.dietType.setText(diet);





//   Show the in cat
        sameProducts.remove(productModel);
        if (sameProducts.isEmpty()){
//            productViewDialogBinding.product.setVisibility(View.GONE);
//            productViewDialogBinding.itemCategory.setVisibility(View.GONE);
        }else {
            LinearLayoutManager LayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
            productViewDialogBinding.similarProductsRecyclerView.setLayoutManager(LayoutManager);
            productViewDialogBinding.similarProductsRecyclerView.setAdapter(new ProductAdapter(sameProducts, this::showProductViewDialog,requireContext(),"Main"));

        }






        productViewDialogBinding.AddTOCart.setOnClickListener(v -> {
            if (FirebaseAuth.getInstance().getCurrentUser()!=null) {
                productManager.addToBothDatabase(new ShoppingCartFirebaseModel(productModel.getProductId(), productModel.getMinSelectableQuantity()), new ProductManager.AddListenerForAddToBothInDatabase() {
                @Override
                public void added(ShoppingCartFirebaseModel shoppingCartFirebaseModel) {
                    productViewDialogBinding.AddTOCart.setVisibility(View.GONE);
                    productViewDialogBinding.quantityBox.setVisibility(View.VISIBLE);
                }
                @Override
                public void failure(Exception e) {
                    productViewDialogBinding.AddTOCart.setVisibility(View.VISIBLE);
                    productViewDialogBinding.quantityBox.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "check your network connection and try again ", Toast.LENGTH_SHORT).show();
                }
            });

//            addToCart(productModel,productViewDialogBinding.AddTOCart,bottomSheetDialog,productViewDialogBinding.quantityBox);
            }else {
                requireActivity().startActivity(new Intent(requireContext(), AuthMangerActivity.class));
            }

        });


//        end set up





        productViewDialogBinding.plusBtn.setOnClickListener(view -> {
            int quantity = productModel.getSelectableQuantity();
            quantity++;
            if (quantity > productModel.getStockCount()) {
                Toast.makeText(requireContext(), "Max stock available: " + productModel.getStockCount(), Toast.LENGTH_SHORT).show();
            }
            else {
                productModel.setSelectableQuantity(quantity);
                productViewDialogBinding.quantity.setText(String.valueOf(productModel.getSelectableQuantity()));
                productManager.UpdateCartQuantityById(userId,productModel.getProductId(),productModel.getSelectableQuantity());
            }

        });
        productViewDialogBinding.minusBtn.setOnClickListener(view -> {
            int quantity = productModel.getSelectableQuantity();
            if (quantity > 1) {
                quantity--;
                productModel.setSelectableQuantity(quantity);
                productViewDialogBinding.quantity.setText(String.valueOf(productModel.getSelectableQuantity()));
                productManager.UpdateCartQuantityById(userId, productModel.getProductId(), productModel.getSelectableQuantity());
            }else {
                Dialog removeBottomSheetDialog = new Dialog(requireContext());
                RemoveProductBoxAlertBinding boxAlertBinding = RemoveProductBoxAlertBinding.inflate(getLayoutInflater());
                removeBottomSheetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                removeBottomSheetDialog.setContentView(boxAlertBinding.getRoot());
                Window window = removeBottomSheetDialog.getWindow();
                if (window != null) {
                    window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    WindowManager.LayoutParams layoutParams = window.getAttributes();
                    layoutParams.windowAnimations = R.style.bottom_sheet_dialogAnimation;
                    layoutParams.gravity = Gravity.CENTER_VERTICAL;
                    window.setAttributes(layoutParams);
                }

                // Handle button clicks
                boxAlertBinding.confirmRemoveButton.setOnClickListener(remove -> {
                    // Handle remove action
                    productManager.RemoveCartProductById(userId,productModel.getProductId());
                    productViewDialogBinding.AddTOCart.setVisibility(View.VISIBLE);
                    productViewDialogBinding.quantityBox.setVisibility(View.GONE);
                    removeBottomSheetDialog.dismiss();
                });

                boxAlertBinding.cancelRemoveButton.setOnClickListener(remove -> removeBottomSheetDialog.dismiss());

                if (!removeBottomSheetDialog.isShowing()) {removeBottomSheetDialog.show();}
            }
//            else {
//                Dialog removeBottomSheetDialog = new Dialog(requireContext());
//                RemoveProductBoxAlertBinding boxAlertBinding = RemoveProductBoxAlertBinding.inflate(getLayoutInflater());
//                removeBottomSheetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                removeBottomSheetDialog.setContentView(binding.getRoot());
//
//                Window window = removeBottomSheetDialog.getWindow();
//                if (window != null) {
//                    window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                    WindowManager.LayoutParams layoutParams = window.getAttributes();
//                    layoutParams.windowAnimations = R.style.bottom_sheet_dialogAnimation;
//                    layoutParams.gravity = Gravity.BOTTOM;
//                    window.setAttributes(layoutParams);
//                }
//
//                // Handle button clicks
//                boxAlertBinding.confirmRemoveButton.setOnClickListener(remove -> {
//                    // Handle remove action
//                    productManager.RemoveCartProductById(userId,productModel.getProductId());
//                    removeBottomSheetDialog.dismiss();
//                });
//
//                boxAlertBinding.cancelRemoveButton.setOnClickListener(remove -> removeBottomSheetDialog.dismiss());
//
//                removeBottomSheetDialog.show();
//
////                Dialog removeBottomSheetDialog = new Dialog(requireContext());
////                RemoveProductBoxBinding removeProductBoxBinding = RemoveProductBoxBinding.inflate(getLayoutInflater());
////                removeBottomSheetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
////                removeBottomSheetDialog.setContentView(binding.getRoot());
////                Window window = removeBottomSheetDialog.getWindow();
////                if (window != null) {
////                    window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
////                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
////                    WindowManager.LayoutParams layoutParams = window.getAttributes();
////                    layoutParams.windowAnimations = R.style.bottom_sheet_dialogAnimation;
////                    layoutParams.gravity = Gravity.BOTTOM;
////                    window.setAttributes(layoutParams);
////                }
////
////
////                removeProductBoxBinding.btnRemove.setOnClickListener(v->{
////                    removeProductBoxBinding.progressCircular.setVisibility(View.VISIBLE);
////                    productManager.RemoveCartProductById(userId,productModel.getProductId());
////
////                    removeProductBoxBinding.progressCircular.setVisibility(View.GONE);
////                    removeBottomSheetDialog.dismiss();
////                });
////
////                removeProductBoxBinding.productName.setText(productModel.getProductName());
////                Glide.with(requireContext()).load(productModel.getProductImage().get(0)).into(removeProductBoxBinding.productImage);
////                removeProductBoxBinding.productQty.setText(productModel.getSelectableQuantity()+"");
////                removeProductBoxBinding.productPrice.setText("₹"+productModel.getPrice());
////                removeProductBoxBinding.productQtyUp.setOnClickListener(up->{
////                    int quantity = productModel.getSelectableQuantity();
////                    quantity++;
////                    if(quantity>model.getStockCount()) {
////                        Toast.makeText(requireContext(), "Max stock available: "+ model.getStockCount(), Toast.LENGTH_SHORT).show();
////                    } else {
////                        model.setSelectableQuantity(quantity);
////                        UpdateQuantity(model, model.getProductId());
////                        binding.productPrice.setText("₹"+model.getPrice());
////
////                    }
////                });
////                removeProductBoxBinding.productQtyDown.setOnClickListener(Down->{
////                    int quantity = model.getSelectableQuantity();
////                    if(quantity > 1) {
////                        quantity--;
////                        model.setSelectableQuantity(quantity);
////                        UpdateQuantity(model, model.getProductId());
////                        removeProductBoxBinding.productPrice.setText("₹"+model.getPrice());
////                    }
////                });
////                removeProductBoxBinding.btnCancel.setOnClickListener(v -> {
////                    removeBottomSheetDialog.dismiss();
////                });
////
////
////
////                if (!removeBottomSheetDialog.isShowing()) {
////                    removeBottomSheetDialog.show();
////
////                }
////                if (requireActivity().getSupportFragmentManager().findFragmentByTag("bottom_sheet_fragment") == null) {
////                    RemoveBottomSheetDialogFragment bottomSheet = new RemoveBottomSheetDialogFragment(uid, id, cartsAdapter, cartsProductModel);
////                    bottomSheet.show(requireActivity().getSupportFragmentManager(), "bottom_sheet_fragment");
//                }
//                shoppingCartsInterface.remove(position,model.getProductId(),model);

//
//            if(quantity > 1) {
//                quantity--;
//                model.setSelectableQuantity(quantity);
//                shoppingCartsInterface.UpdateQuantity(model, model.getProductId());
//                holder.binding.productPrice.setText("₹"+model.getPrice());
//            }else {
//            }
        });



//




        bottomSheetDialog.setContentView(productViewDialogBinding.getRoot());
        bottomSheetDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        bottomSheetDialog.getWindow().getAttributes().windowAnimations = R.style.bottom_sheet_dialogAnimation;
        bottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);

        bottomSheetDialog.show();
    }


    private void showImageInDialog(int position,List<String> imageUrls) {
        Dialog dialog = new Dialog(requireContext());
        DialogFullscreenImageBinding dialogBinding = DialogFullscreenImageBinding.inflate(LayoutInflater.from(requireContext()));
        dialog.setContentView(dialogBinding.getRoot());
//        dialogBinding.dialogViewPager
//        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false);
        DialogSliderAdapter dialogAdapter = new DialogSliderAdapter(requireContext(), imageUrls);
//        dialogViewPager.setLayoutManager(layoutManager);
        dialogBinding.getRoot().setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialogBinding.close.setOnClickListener(v->{
            dialog.dismiss();
        });
        dialogBinding.dialogViewPager.setAdapter(dialogAdapter);
        dialogBinding.dialogViewPager.setCurrentItem(position);
        // Set full screen
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.white);
        }
        Animation animation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_up);
        dialogBinding.getRoot().startAnimation(animation);


        dialog.show();

    }



//    private void showProductViewDialog(ProductModel productModel,ArrayList<ProductModel> sameProducts) {
////        ProductModel model = new ProductModel();
//        Dialog bottomSheetDialog = new Dialog(requireContext());
//        ProductViewDialogBinding productViewDialogBinding = ProductViewDialogBinding.inflate(getLayoutInflater());

//
//        productViewDialogBinding.ProductName.setText(productModel.getProductName());
//        productViewDialogBinding.closeButton.setOnClickListener(v -> bottomSheetDialog.dismiss());
//        for (String imageUrl : productModel.getProductImage()) {
//            CarouselItem carouselItem = new CarouselItem(imageUrl);
//            productViewDialogBinding.productsImages.addData(carouselItem);
//        }
//
//        if (!Objects.equals(productModel.getProductDescription(), "")) {
//            productViewDialogBinding.productDescription.setVisibility(View.VISIBLE);
//            productViewDialogBinding.Description.setVisibility(View.VISIBLE);
//            productViewDialogBinding.productDescription.setText(productModel.getProductDescription());
//        }
//
//        double mrp = productModel.getMrp();
//        double sellingPrice = productModel.getPrice();
//        double discountPercentage = mrp - sellingPrice;
//
//        productViewDialogBinding.discount.setText("₹" + discountPercentage + "% off");
//        productViewDialogBinding.Price.setText("₹" + productModel.getPrice());
//        productViewDialogBinding.quantitySmail.setText("(₹" + productModel.getPrice() + " / " + productModel.getWeight() + productModel.getWeightSIUnit() + ")");
//        productViewDialogBinding.MRP.setText(":" + productModel.getMrp());
//        productViewDialogBinding.MRP.setPaintFlags(productViewDialogBinding.MRP.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//
//        if (productModel.getStockCount() == 0) {
//            productViewDialogBinding.OutOfStockBuyOptions.setVisibility(View.VISIBLE);
//            productViewDialogBinding.quantity.setText("0");
//            productViewDialogBinding.TextInStock.setText("Out of Stock");
//            productViewDialogBinding.quantityBox.setVisibility(View.INVISIBLE);
//            productViewDialogBinding.TextInStock.setTextColor(ContextCompat.getColor(requireContext(), R.color.FixRed));
//            productViewDialogBinding.AddTOCart.setVisibility(View.INVISIBLE);
//        }
//
//        productViewDialogBinding.categoryType.setText(productModel.getCategory());
//        productViewDialogBinding.netQuantity.setText(productModel.getWeight() + productModel.getWeightSIUnit());
//
//        String diet;
//        if (productModel.getProductType().equals("FoodVeg")) {
//            diet = "Veg";
//        } else if (productModel.getProductType().equals("FoodNonVeg")) {
//            diet = "NonVeg";
//        } else {
//            diet = "not food item.";
//            productViewDialogBinding.dietType.setVisibility(View.GONE);
//            productViewDialogBinding.diet.setVisibility(View.GONE);
//        }
//        productViewDialogBinding.dietType.setText(diet);
//        productViewDialogBinding.ExpiryDate.setText(productModel.getProductLife());
//
//        productViewDialogBinding.AddTOCart.setOnClickListener(v -> {
//            addToCart(productModel,productViewDialogBinding.AddTOCart,bottomSheetDialog,productViewDialogBinding.quantityBox);
//        });
//        sameProducts.remove(productModel);
//        if (sameProducts.isEmpty()){
//            productViewDialogBinding.product.setVisibility(View.GONE);
//            productViewDialogBinding.itemCategory.setVisibility(View.GONE);
//        }else {
//            LinearLayoutManager LayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
//            productViewDialogBinding.itemCategory.setLayoutManager(LayoutManager);
//            productViewDialogBinding.itemCategory.setAdapter(new ProductAdapter(sameProducts, this::showProductViewDialog,requireContext(),"Main"));
//        }
//
//
//
//        productViewDialogBinding.plusBtn.setOnClickListener(view -> {
//            int quantity = productModel.getSelectableQuantity();
//            quantity++;
//            if (quantity > productModel.getStockCount()) {
//                Toast.makeText(requireContext(), "Max stock available: " + productModel.getStockCount(), Toast.LENGTH_SHORT).show();
//            }
//            else {
//                productModel.setSelectableQuantity(quantity);
//                productViewDialogBinding.quantity.setText(String.valueOf(productModel.getSelectableQuantity()));
//            }
//
//        });
//        productViewDialogBinding.minusBtn.setOnClickListener(view -> {
//            int quantity = productModel.getSelectableQuantity();
//            if (quantity > 1)
//                quantity--;
////            selectQTY = quantity;
//            productModel.setSelectableQuantity(quantity);
//            productViewDialogBinding.quantity.setText(String.valueOf(productModel.getSelectableQuantity()));
//
//        });
//
//
//
//
//        bottomSheetDialog.setContentView(productViewDialogBinding.getRoot());
//        bottomSheetDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        bottomSheetDialog.getWindow().getAttributes().windowAnimations = R.style.bottom_sheet_dialogAnimation;
//        bottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
//
//        bottomSheetDialog.show();
//    }


//    public void addToCart(ProductModel productModel, MaterialButton addTOCart, Dialog bottomSheetDialog, CardView quantityBox) {
//        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//            ShoppingCartFirebaseModel shoppingCartFirebaseModel = new ShoppingCartFirebaseModel(productModel.getProductId(), productModel.getSelectableQuantity());
//            DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("Cart").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
//            String productNameToFind = productModel.getProductId();
//            Query query = productsRef.orderByKey().equalTo(productNameToFind);
//            query.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    if (dataSnapshot.exists()) {
//                        // Product already exists in the cart
//                        // Handle this scenario, e.g., navigate to the cart
//                        navigateToShoppingCartFragment(bottomSheetDialog);
//                    } else {
//                        // Product doesn't exist in the cart, add it
//                        addProductToCart(productsRef, shoppingCartFirebaseModel,productModel,addTOCart,quantityBox);
//
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//                    // Handle the error in case of a database error.
//                    Log.e("DatabaseError", "Database error: " + databaseError.getMessage());
//                    Toast.makeText(getContext(), "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });
//        } else {
//            // User not authenticated, navigate to authentication screen
//            startActivity(new Intent(getContext(), AuthMangerActivity.class));
//        }
//    }

//
//    @SuppressLint("SetTextI18n")
//    private void addProductToCart(DatabaseReference productsRef, ShoppingCartFirebaseModel shoppingCartFirebaseModel, ProductModel productModel, MaterialButton addTOCart, CardView quantityBox) {
//        productsRef.child(productModel.getProductId()).setValue(shoppingCartFirebaseModel)
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
////                        addTOCart.setText("Go to Cart");
////                        addTOCart.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white));
////                        addTOCart.setTextColor(ContextCompat.getColor(requireContext(), R.color.FixBlack));
//                        addTOCart.setVisibility(View.GONE);
//                        quantityBox.setVisibility(View.VISIBLE);
//                    }
//                })
//                .addOnFailureListener(error -> basicFun.AlertDialog(requireContext(), error.toString()));
//
//    }
//
//
//    private void navigateToShoppingCartFragment(Dialog bottomSheetDialog) {
//        BottomAppBar bottomAppBar = requireActivity().findViewById(R.id.bottomAppBar);
//        if (bottomAppBar != null) {
//            bottomAppBar.setVisibility(View.VISIBLE);
//        }
//        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.show();
//        }
//        bottomSheetDialog.dismiss();
//        if (HomeProductBottomSheetDialog.isShowing()){
//            HomeProductBottomSheetDialog.dismiss();
//        }
//        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.loader, new ShoppingCartsFragment(), "ShoppingCartsFragment");
//        transaction.addToBackStack("ShoppingCartsFragment");
//        transaction.commit();
//    }


    void ViewCat(HomeProductModel model){
//        HomeProductBottomSheetDialog = new Dialog(requireContext());
        CategoryViewDialogBinding productViewDialogBinding = CategoryViewDialogBinding.inflate(getLayoutInflater());
        productViewDialogBinding.productsRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        productViewDialogBinding.productsRecyclerView.setAdapter(new ProductAdapter(model.getProduct(), this::showProductViewDialog, requireContext(), "Main"));
        productViewDialogBinding.title.setText(model.getTitle());
        productViewDialogBinding.seeMore.setOnClickListener(v -> {
            HomeProductBottomSheetDialog.dismiss();
            Bundle bundle = new Bundle();
            bundle.putString("filter" , model.getTitle());
            ProductWithSlideCategoryFragment productWithSlideCategoryFragment = new ProductWithSlideCategoryFragment();
            productWithSlideCategoryFragment.setArguments(bundle);
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.loader,productWithSlideCategoryFragment);
            transaction.addToBackStack("SelectLanguageFragment");
            transaction.commit();
        });

        productViewDialogBinding.closeButton.setOnClickListener(v -> HomeProductBottomSheetDialog.dismiss());

        HomeProductBottomSheetDialog.setContentView(productViewDialogBinding.getRoot());

        HomeProductBottomSheetDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        HomeProductBottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        HomeProductBottomSheetDialog.getWindow().getAttributes().windowAnimations = R.style.bottom_sheet_dialogAnimation;
        HomeProductBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);

        HomeProductBottomSheetDialog.show();





    }


//    void
//
    private void openSearchFragment() {
        if (isAdded() && getActivity() != null) {
            SearchFragment fragment  = new SearchFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("model",homeProductModel);
            fragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.loader,fragment)
                    .addToBackStack("HomeFragment")
                    .commit();


    }
}

}



