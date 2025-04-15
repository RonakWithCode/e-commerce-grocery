package com.ronosoft.alwarmart.Fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ronosoft.alwarmart.Activity.BrandActivity;
import com.ronosoft.alwarmart.Activity.FragmentLoader;
import com.ronosoft.alwarmart.Adapter.HomeCategoryAdapter;
import com.ronosoft.alwarmart.Adapter.HomeProductAdapter;
import com.ronosoft.alwarmart.Adapter.ProductAdapter;
import com.ronosoft.alwarmart.Component.ProductViewCard;
import com.ronosoft.alwarmart.Model.AddressModel;
import com.ronosoft.alwarmart.Model.BannerModels;
import com.ronosoft.alwarmart.Model.HomeProductModel;
import com.ronosoft.alwarmart.Model.ProductModel;
import com.ronosoft.alwarmart.Model.UserinfoModels;
import com.ronosoft.alwarmart.R;
import com.ronosoft.alwarmart.Services.DatabaseService;
import com.ronosoft.alwarmart.databinding.CategoryViewDialogBinding;
import com.ronosoft.alwarmart.databinding.FragmentHomeBinding;
import com.ronosoft.alwarmart.javaClasses.AddressDeliveryService;
import com.ronosoft.alwarmart.javaClasses.basicFun;

import org.imaginativeworld.whynotimagecarousel.ImageCarousel;
import org.imaginativeworld.whynotimagecarousel.listener.CarouselListener;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private DatabaseService databaseService;
    private HomeCategoryAdapter homeCategoryAdapter, homeProductBoysSkinAdapter;
    private HomeProductAdapter multiViewAdapter;
    private ArrayList<HomeProductModel> multiViewModel, homeProductModel, homeProductModelBoysSkin;
    private String userId;
    private Dialog homeProductBottomSheetDialog;
    private boolean isLoadingSecondList = false;
    private boolean secondListLoaded = false;

    // Categories to meet minimum requirements
    private final String[] firstList = {"Dry Fruits", "Toilet & Bathroom Cleaners"};
    private final String[] secondList = {"Health drink"};
    private final String[] fixedCategories = {"hair oil", "snacks", "Dairy", "Grains"};
    private final String[] boysSkinCategories = {"Toothpaste", "Soaps & Body Care", "Edible Oils"};

    // Pagination variables
    private static final int PAGE_SIZE = 4;

    // Fun facts and animation handler
    private String[] funFacts;
    private Handler handler;
    private Runnable skeletonRunnable, funFactRunnable;
    private final long FUN_FACT_INTERVAL = 2000;

    // Track initial loading state
    private AtomicBoolean isCarouselLoaded = new AtomicBoolean(false);
    private AtomicBoolean areCategoriesLoaded = new AtomicBoolean(false);
    private AtomicBoolean areBoysSkinCategoriesLoaded = new AtomicBoolean(false);
    private AtomicBoolean areMultiViewFirstListLoaded = new AtomicBoolean(false);

    // Listener for banners
    private ValueEventListener bannerListener;
    private DatabaseReference bannerRef;

    // Executor for async tasks
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    public HomeFragment() {}

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        // Initialize services
        databaseService = new DatabaseService();
        homeProductBottomSheetDialog = new Dialog(requireContext());
        handler = new Handler(Looper.getMainLooper());

        // Load fun facts
        funFacts = getResources().getStringArray(R.array.fun_facts);

        // Show initial loading animation
        binding.fullScreenLoadingView.setVisibility(View.VISIBLE);
        binding.mainContent.setVisibility(View.GONE);
        binding.skeletonLayout.setVisibility(View.GONE);

        // Start Lottie animation
        binding.loadingAnimation.setAnimation("loading_animation.lottie");
        binding.loadingAnimation.playAnimation();
        startFunFactCycle();

        // Schedule skeleton screen after 2 seconds
        skeletonRunnable = () -> {
            if (binding != null && binding.fullScreenLoadingView.getVisibility() == View.VISIBLE) {
                binding.fullScreenLoadingView.setVisibility(View.GONE);
                binding.skeletonLayout.setVisibility(View.VISIBLE);
                binding.shimmerLayout.startShimmer();
            }
        };
        handler.postDelayed(skeletonRunnable, 2000);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            String displayName = (currentUser.getDisplayName() != null) ? currentUser.getDisplayName() : "User";
            binding.UserName.setText("Hello, " + displayName.toUpperCase(Locale.ROOT));
        } else {
            binding.UserName.setText("Hello, Guest");
            binding.address.setVisibility(View.GONE);
            binding.arrowDropdown.setVisibility(View.GONE);
            binding.txtDeliveryTime.setText("Alwar Mart in 10 minutes");
        }

        // Load default address
        AddressDeliveryService addressDeliveryService = new AddressDeliveryService();
        if (currentUser != null) {
            AddressModel defaultAddress = addressDeliveryService.getDefaultAddress(requireContext());
            if (defaultAddress != null) {
                int deliveryTime = addressDeliveryService.calculateDeliveryTime(defaultAddress);
                binding.txtDeliveryTime.setText("Alwar Mart in " + deliveryTime + " minutes");
                String addrText = "HOME - " + defaultAddress.getFlatHouse() + ", " + defaultAddress.getAddress();
                if (addrText.length() > 30) {
                    addrText = addrText.substring(0, 30) + "...";
                }
                binding.address.setText(addrText);
            } else {
                binding.address.setText("HOME - Add Address");
                binding.txtDeliveryTime.setText("Alwar Mart in 10 minutes");
            }
        }

        // Retrieve user info asynchronously
        final UserinfoModels[] userInfo = new UserinfoModels[1];
        if (currentUser != null) {
            executorService.execute(() -> {
                databaseService.getUserInfo(userId, new DatabaseService.getUserInfoCallback() {
                    @Override
                    public void onSuccess(UserinfoModels user) {
                        if (isAdded()) {
                            userInfo[0] = user;
                        }
                    }
                    @Override
                    public void onError(String errorMessage) {
                        if (isAdded()) {
                            handler.post(() -> Toast.makeText(requireContext(), "Failed to load user info", Toast.LENGTH_SHORT).show());
                        }
                    }
                });
            });
        }

        // Set dropdown listener for addresses
        binding.arrowDropdown.setOnClickListener(view -> {
            if (currentUser == null) return;
            ArrayList<AddressModel> addresses = new ArrayList<>();
            if (userInfo[0] != null && userInfo[0].getAddress() != null) {
                addresses.addAll(userInfo[0].getAddress());
            }
            if (addresses.isEmpty()) {
                Intent intent = new Intent(requireContext(), FragmentLoader.class);
                intent.putExtra("LoadID", "UserAccountFragment");
                startActivity(intent);
                return;
            }
            DefaultAddressBottomSheetFragment bottomSheet = new DefaultAddressBottomSheetFragment(addresses);
            bottomSheet.setOnAddressSelectedListener(selectedAddress -> {
                addressDeliveryService.saveDefaultAddress(requireContext(), selectedAddress);
                int newDeliveryTime = addressDeliveryService.calculateDeliveryTime(selectedAddress);
                String addrText = "HOME - " + selectedAddress.getFlatHouse() + ", " + selectedAddress.getAddress();
                if (addrText.length() > 30) {
                    addrText = addrText.substring(0, 30) + "...";
                }
                binding.address.setText(addrText);
                binding.txtDeliveryTime.setText("Alwar Mart in " + newDeliveryTime + " minutes");
            });
            bottomSheet.show(getChildFragmentManager(), "DefaultAddressBottomSheet");
        });

        // Set up status bar and search view
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) actionBar.hide();
        Window window = requireActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.OrderYellowColor));
        binding.searchView.setOnClickListener(view -> openSearchFragment());

        // Initialize product lists and adapters
        homeProductModel = new ArrayList<>();
        multiViewModel = new ArrayList<>();
        homeProductModelBoysSkin = new ArrayList<>();
        homeCategoryAdapter = new HomeCategoryAdapter(homeProductModel, requireContext(), this::ViewCat);
        homeProductBoysSkinAdapter = new HomeCategoryAdapter(homeProductModelBoysSkin, requireContext(), this::ViewCat);
        multiViewAdapter = new HomeProductAdapter(multiViewModel, (product, productList) -> {
            if (isAdded() && getActivity() != null) {
                new ProductViewCard(getActivity()).showProductViewDialog(product, productList);
            }
        }, requireActivity());

        binding.BestsellersSee.setOnClickListener(v -> SeeAll());
        binding.CategoryTextViewSee.setOnClickListener(v -> SeeAll());
        binding.boysSkinCareSee.setOnClickListener(v -> SeeAll());

        setupAdapters();
        loadInitialData();

        return binding.getRoot();
    }

    public void SeeAll() {
        try {
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.loader, new ProductWithSlideCategoryFragment());
            transaction.addToBackStack("SelectLanguageFragment");
            transaction.commit();
        } catch (IllegalStateException e) {
            Toast.makeText(requireContext(), "Unable to load category", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupAdapters() {
        LinearLayoutManager categoryLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.recyclerCategory.setLayoutManager(categoryLayoutManager);
        binding.recyclerCategory.setAdapter(homeCategoryAdapter);
        binding.recyclerCategory.setHasFixedSize(true);

        LinearLayoutManager boysSkinLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.boysSkinCareRecyclerView.setLayoutManager(boysSkinLayoutManager);
        binding.boysSkinCareRecyclerView.setAdapter(homeProductBoysSkinAdapter);
        binding.boysSkinCareRecyclerView.setHasFixedSize(true);

        LinearLayoutManager multiViewManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        binding.MultiViewAdapter.setLayoutManager(multiViewManager);
        binding.MultiViewAdapter.setAdapter(multiViewAdapter);
        binding.MultiViewAdapter.setHasFixedSize(true);

        binding.MultiViewAdapter.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoadingSecondList && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 2) {
                    if (!secondListLoaded) {
                        isLoadingSecondList = true;
                        showBottonLoading();
                        loadSecondList(secondList);
                    }
                }
            }
        });
    }

    private void loadInitialData() {
        binding.recyclerCategory.setVisibility(View.VISIBLE);
        LoadCarousel();
        setupOnclick();
        // Load critical data first
        loadProductsForCategories(fixedCategories);
        // Defer non-critical loads
        handler.postDelayed(() -> {
            loadProductsForCategoriesByBoysSkin(boysSkinCategories);
            loadProductsForMultiView(firstList, secondList);
            LoadProductCategory();
        }, 1000);
    }

    private void setupOnclick() {
        binding.layoutAata.setOnClickListener(v -> openCategory("Grains"));
        binding.layoutDairy.setOnClickListener(v -> openCategory("Dairy"));
        binding.layoutMasala.setOnClickListener(v -> openCategory("Masala"));
        binding.layoutOil.setOnClickListener(v -> openCategory("Edible Oils"));
    }

    private void openCategory(String category) {
        Bundle bundle = new Bundle();
        bundle.putString("filter", category);
        ProductWithSlideCategoryFragment fragment = new ProductWithSlideCategoryFragment();
        fragment.setArguments(bundle);
        try {
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.loader, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } catch (IllegalStateException e) {
            Toast.makeText(requireContext(), "Unable to load category", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadProductsForMultiView(String[] firstCategory, String[] secondCategory) {
        Set<String> categories = new HashSet<>();
        for (String category : firstCategory) {
            categories.add(category);
            loadProductsMultiView(category, PAGE_SIZE);
        }
    }

    private void loadSecondList(String[] secondCategory) {
        for (String category : secondCategory) {
            loadProductsMultiView(category, PAGE_SIZE);
        }
        secondListLoaded = true;
        isLoadingSecondList = false;
        hideBottonLoading();
    }

    private void loadProductsMultiView(String category, int limit) {
        executorService.execute(() -> {
            databaseService.getAllProductsByCategoryOnlyForHomeFragment(category, new DatabaseService.GetAllProductsCallback() {
                @Override
                public void onSuccess(ArrayList<ProductModel> products) {
                    if (isAdded() && binding != null) {
                        handler.post(() -> {
                            HomeProductModel existingModel = null;
                            for (HomeProductModel model : multiViewModel) {
                                if (model.getTitle().equals(category)) {
                                    existingModel = model;
                                    break;
                                }
                            }
                            if (existingModel != null) {
                                existingModel.getProduct().addAll(products);
                                multiViewAdapter.notifyItemChanged(multiViewModel.indexOf(existingModel));
                            } else {
                                multiViewModel.add(new HomeProductModel(category, products));
                                multiViewAdapter.notifyItemInserted(multiViewModel.size() - 1);
                            }
                            areMultiViewFirstListLoaded.set(true);
                            checkIfInitialLoadingComplete();
                        });
                    }
                }
                @Override
                public void onError(String errorMessage) {
                    if (isAdded()) {
                        handler.post(() -> {
                            Toast.makeText(requireContext(), "Failed to load products: " + errorMessage, Toast.LENGTH_SHORT).show();
                            areMultiViewFirstListLoaded.set(true);
                            checkIfInitialLoadingComplete();
                        });
                    }
                }
            });
        });
    }

    private void LoadProductCategory() {
        Context context = requireContext();
        Glide.with(context)
                .load(R.drawable.dabur)
                .thumbnail(0.25f)
                .placeholder(R.drawable.skeleton_shape)
                .error(R.drawable.skeleton_shape)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(100, 100)
                .centerCrop()
                .into(binding.Dabur);

        binding.Dabur.setOnClickListener(view -> {
            Intent intent = new Intent(context, BrandActivity.class);
            intent.putExtra("brand", "DABUR");
            context.startActivity(intent);
        });

        Glide.with(context)
                .load(R.drawable.itc)
                .thumbnail(0.25f)
                .placeholder(R.drawable.skeleton_shape)
                .error(R.drawable.skeleton_shape)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(100, 100)
                .centerCrop()
                .into(binding.itc);

        binding.itc.setOnClickListener(view -> {
            Intent intent = new Intent(context, BrandActivity.class);
            intent.putExtra("brand", "ITC");
            context.startActivity(intent);
        });

        Glide.with(context)
                .load(R.drawable.colgate)
                .thumbnail(0.25f)
                .placeholder(R.drawable.skeleton_shape)
                .error(R.drawable.skeleton_shape)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(100, 100)
                .centerCrop()
                .into(binding.Colgate);

        binding.Colgate.setOnClickListener(view -> {
            Intent intent = new Intent(context, BrandActivity.class);
            intent.putExtra("brand", "Colgate");
            context.startActivity(intent);
        });

        Glide.with(context)
                .load(R.drawable.aashirvaad)
                .thumbnail(0.25f)
                .placeholder(R.drawable.skeleton_shape)
                .error(R.drawable.skeleton_shape)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(100, 100)
                .centerCrop()
                .into(binding.Aashirvaad);

        binding.Aashirvaad.setOnClickListener(view -> {
            Intent intent = new Intent(context, BrandActivity.class);
            intent.putExtra("brand", "Aashirvaad");
            context.startActivity(intent);
        });
    }

    void LoadCarousel() {
        ArrayList<BannerModels> topBannerModels = new ArrayList<>();
        ArrayList<BannerModels> bottomBannerModels = new ArrayList<>();
        ImageCarousel topCarousel = binding.carousel;
        ImageCarousel bottomCarousel = binding.BottomCarousel;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        bannerRef = database.getReference().child("Banner");
        bannerListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                topBannerModels.clear();
                bottomBannerModels.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    BannerModels banner = snapshot.getValue(BannerModels.class);
                    if (banner != null && banner.isActive()) {
                        Map<String, String> map = new HashMap<>();
                        map.put("bannerId", banner.getBannerId());
                        if ("Top Position".equals(banner.getPosition())) {
                            topBannerModels.add(banner);
                            topCarousel.addData(new CarouselItem(banner.getBannerImages(), map));
                        } else {
                            bottomBannerModels.add(banner);
                            bottomCarousel.addData(new CarouselItem(banner.getBannerImages(), map));
                        }
                    }
                }
                isCarouselLoaded.set(true);
                checkIfInitialLoadingComplete();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                isCarouselLoaded.set(true);
                checkIfInitialLoadingComplete();
                if (isAdded()) {
                    Toast.makeText(requireContext(), "Failed to load banners", Toast.LENGTH_SHORT).show();
                }
            }
        };
        bannerRef.addValueEventListener(bannerListener);
    }

    void ViewCat(HomeProductModel model) {
        CategoryViewDialogBinding dialogBinding = CategoryViewDialogBinding.inflate(getLayoutInflater());
        dialogBinding.productsRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        dialogBinding.productsRecyclerView.setAdapter(
                new ProductAdapter(
                        getViewLifecycleOwner(),
                        model.getProduct(),
                        (product, productList) -> {
                            if (isAdded() && getActivity() != null) {
                                new ProductViewCard(getActivity()).showProductViewDialog(product, productList);
                            }
                        },
                        requireContext()
                )
        );

        dialogBinding.title.setText(model.getTitle());
        dialogBinding.seeMore.setOnClickListener(v -> {
            homeProductBottomSheetDialog.dismiss();
            openCategory(model.getTitle());
        });
        dialogBinding.seeMoreBottom.setOnClickListener(v -> {
            homeProductBottomSheetDialog.dismiss();
            openCategory(model.getTitle());
        });
        dialogBinding.closeButton.setOnClickListener(v -> homeProductBottomSheetDialog.dismiss());
        homeProductBottomSheetDialog.setContentView(dialogBinding.getRoot());
        Objects.requireNonNull(homeProductBottomSheetDialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        homeProductBottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        homeProductBottomSheetDialog.getWindow().getAttributes().windowAnimations = R.style.bottom_sheet_dialogAnimation;
        homeProductBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        homeProductBottomSheetDialog.show();
    }

    private void openSearchFragment() {
        if (isAdded() && getActivity() != null) {
            SearchFragment fragment = new SearchFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("model", homeProductModel);
            fragment.setArguments(bundle);
            try {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.loader, fragment)
                        .addToBackStack("HomeFragment")
                        .commit();
            } catch (IllegalStateException e) {
                Toast.makeText(requireContext(), "Unable to open search", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadProduct(String category, ArrayList<HomeProductModel> productList, HomeCategoryAdapter adapter, AtomicBoolean loadingFlag) {
        executorService.execute(() -> {
            databaseService.getAllProductsByCategoryOnlyForHomeFragment(category, new DatabaseService.GetAllProductsCallback() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onSuccess(ArrayList<ProductModel> products) {
                    if (isAdded()) {
                        handler.post(() -> {
                            productList.add(new HomeProductModel(category, products));
                            adapter.notifyItemInserted(productList.size() - 1);
                            loadingFlag.set(true);
                            checkIfInitialLoadingComplete();
                        });
                    }
                }
                @Override
                public void onError(String errorMessage) {
                    if (isAdded()) {
                        handler.post(() -> {
                            Toast.makeText(requireContext(), "Failed to load products: " + errorMessage, Toast.LENGTH_SHORT).show();
                            loadingFlag.set(true);
                            checkIfInitialLoadingComplete();
                        });
                    }
                }
            });
        });
    }

    private void loadProductsForCategories(String[] categories) {
        for (String category : categories) {
            loadProduct(category, homeProductModel, homeCategoryAdapter, areCategoriesLoaded);
        }
    }

    private void loadProductsForCategoriesByBoysSkin(String[] categories) {
        for (String category : categories) {
            loadProduct(category, homeProductModelBoysSkin, homeProductBoysSkinAdapter, areBoysSkinCategoriesLoaded);
        }
    }

    private void showBottonLoading() {
        binding.customLoadingView.setVisibility(View.VISIBLE);
        ImageView truck = binding.loadingTruck;
        TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0f,
                Animation.RELATIVE_TO_PARENT, 1f,
                Animation.RELATIVE_TO_PARENT, 0f,
                Animation.RELATIVE_TO_PARENT, 0f
        );
        animation.setDuration(2000);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.RESTART);
        truck.startAnimation(animation);

        funFactRunnable = new Runnable() {
            @Override
            public void run() {
                if (binding != null && binding.customLoadingView.getVisibility() == View.VISIBLE) {
                    Random random = new Random();
                    int index = random.nextInt(funFacts.length);
                    binding.loadingFunFact.setText(funFacts[index]);
                    handler.postDelayed(this, FUN_FACT_INTERVAL);
                }
            }
        };
        handler.post(funFactRunnable);
    }

    private void hideBottonLoading() {
        if (binding != null) {
            binding.customLoadingView.setVisibility(View.GONE);
            binding.loadingTruck.clearAnimation();
            handler.removeCallbacks(funFactRunnable);
        }
    }

    private void startFunFactCycle() {
        AlphaAnimation fadeInPrimary = new AlphaAnimation(0f, 1f);
        fadeInPrimary.setDuration(1000);
        binding.loadingPrimaryText.startAnimation(fadeInPrimary);

        funFactRunnable = new Runnable() {
            @Override
            public void run() {
                if (binding != null && binding.fullScreenLoadingView.getVisibility() == View.VISIBLE) {
                    Random random = new Random();
                    int index = random.nextInt(funFacts.length);
                    TextView secondaryText = binding.loadingSecondaryText;
                    AlphaAnimation fadeOut = new AlphaAnimation(1f, 0f);
                    fadeOut.setDuration(500);
                    fadeOut.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {}
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            if (binding != null) {
                                secondaryText.setText(funFacts[index]);
                                AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
                                fadeIn.setDuration(500);
                                secondaryText.startAnimation(fadeIn);
                            }
                        }
                        @Override
                        public void onAnimationRepeat(Animation animation) {}
                    });
                    secondaryText.startAnimation(fadeOut);
                    handler.postDelayed(this, FUN_FACT_INTERVAL);
                }
            }
        };
        handler.post(funFactRunnable);
    }

    private void checkIfInitialLoadingComplete() {
        if (isCarouselLoaded.get() && areCategoriesLoaded.get() && areBoysSkinCategoriesLoaded.get() &&
                areMultiViewFirstListLoaded.get()) {
            if (binding != null) {
                if (binding.loadingAnimation != null && binding.loadingAnimation.isAnimating()) {
                    binding.loadingAnimation.cancelAnimation();
                }
                binding.fullScreenLoadingView.setVisibility(View.GONE);
                binding.shimmerLayout.stopShimmer();
                binding.skeletonLayout.setVisibility(View.GONE);
                binding.mainContent.setVisibility(View.VISIBLE);
                handler.removeCallbacks(skeletonRunnable);
                handler.removeCallbacks(funFactRunnable);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (bannerRef != null && bannerListener != null) {
            bannerRef.removeEventListener(bannerListener);
            bannerRef = null;
            bannerListener = null;
        }
        if (binding != null) {
            if (binding.loadingAnimation != null && binding.loadingAnimation.isAnimating()) {
                binding.loadingAnimation.cancelAnimation();
            }
            binding.loadingTruck.clearAnimation();
            handler.removeCallbacksAndMessages(null);
            binding = null;
        }
        if (homeProductBottomSheetDialog != null && homeProductBottomSheetDialog.isShowing()) {
            homeProductBottomSheetDialog.dismiss();
        }
        executorService.shutdown();
    }
}