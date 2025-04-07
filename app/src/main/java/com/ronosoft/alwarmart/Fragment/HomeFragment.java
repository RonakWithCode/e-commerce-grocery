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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private DatabaseService databaseService;
    private HomeCategoryAdapter homeCategoryAdapter, homeProductBoysSkinAdapter;
    private HomeProductAdapter multiViewAdapter;
    private ArrayList<HomeProductModel> multiViewModel, homeProductModel, homeProductModelBoysSkin;
    private String userId;
    private Dialog homeProductBottomSheetDialog;
    private FirebaseFirestore firestore;
    private boolean isLoadingSecondList = false;
    private boolean secondListLoaded = false;

    private final String[] firstList = {"Dry Fruits", "SKIN CARE", "Namkeen", "Noodles"};
    private final String[] secondList = {"Chocolate", "Candies", "Toilet & Bathroom Cleaners"};
    private final String[] fixedCategories = {"hair oil", "snacks", "Dairy"};
    private final String[] boysSkinCategories = {"Edible Oils", "Soaps & Body Care", "Toothpaste"};

    // Pagination variables
    private static final int PAGE_SIZE = 5;
    private Map<String, DocumentSnapshot> lastDocuments = new HashMap<>();
    private Map<String, Boolean> hasMoreData = new HashMap<>();

    // Fun facts and animation handler
    private String[] funFacts;
    private Handler handler;
    private Runnable skeletonRunnable, funFactRunnable;
    private final long FUN_FACT_INTERVAL = 2000; // Change fact every 2 seconds

    // Track initial loading state
    private AtomicBoolean isCarouselLoaded = new AtomicBoolean(false);
    private AtomicBoolean areCategoriesLoaded = new AtomicBoolean(false);
    private AtomicBoolean areBoysSkinCategoriesLoaded = new AtomicBoolean(false);
    private AtomicBoolean areMultiViewFirstListLoaded = new AtomicBoolean(false);

    public HomeFragment() {
        // Required empty public constructor
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        // Initialize services
        databaseService = new DatabaseService();
        firestore = FirebaseFirestore.getInstance();
        homeProductBottomSheetDialog = new Dialog(requireContext());
        handler = new Handler(Looper.getMainLooper());

        // Load fun facts
        funFacts = getResources().getStringArray(R.array.fun_facts);

        // Show initial loading animation immediately
        binding.fullScreenLoadingView.setVisibility(View.VISIBLE);
        binding.mainContent.setVisibility(View.GONE);
        binding.skeletonLayout.setVisibility(View.GONE);

        // Start Lottie animation
        binding.loadingAnimation.setAnimation("loading_animation.lottie");
        binding.loadingAnimation.playAnimation();
        startFunFactCycle();

        // Schedule skeleton screen after 4.5 seconds
        skeletonRunnable = () -> {
            if (binding != null && binding.fullScreenLoadingView.getVisibility() == View.VISIBLE) {
                binding.fullScreenLoadingView.setVisibility(View.GONE);
                binding.skeletonLayout.setVisibility(View.VISIBLE);
                binding.shimmerLayout.startShimmer();
            }
        };
        handler.postDelayed(skeletonRunnable, 4500); // 3 seconds delay

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

        // Load default address if logged in
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

        // Retrieve user info if logged in (background task)
        final UserinfoModels[] userInfo = new UserinfoModels[1];
        if (currentUser != null) {
            databaseService.getUserInfo(userId, new DatabaseService.getUserInfoCallback() {
                @Override
                public void onSuccess(UserinfoModels user) {
                    if (isAdded()) {
                        userInfo[0] = user;
                    }
                }
                @Override
                public void onError(String errorMessage) {
                    // Silent fail
                }
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
        } catch (Exception e) {
            // Silent fail
        }
    }

    private void setupAdapters() {
        LinearLayoutManager categoryLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.recyclerCategory.setLayoutManager(categoryLayoutManager);
        binding.recyclerCategory.setAdapter(homeCategoryAdapter);

        LinearLayoutManager boysSkinLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.boysSkinCareRecyclerView.setLayoutManager(boysSkinLayoutManager);
        binding.boysSkinCareRecyclerView.setAdapter(homeProductBoysSkinAdapter);

        LinearLayoutManager multiViewManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        binding.MultiViewAdapter.setLayoutManager(multiViewManager);
        binding.MultiViewAdapter.setAdapter(multiViewAdapter);

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
                    } else {
                        for (HomeProductModel model : multiViewModel) {
                            String category = model.getTitle();
                            if (Boolean.TRUE.equals(hasMoreData.getOrDefault(category, true))) {
                                showBottonLoading();
                                loadProductsMultiView(category, PAGE_SIZE);
                            }
                        }
                    }
                }
            }
        });
    }

    private void loadInitialData() {
        binding.recyclerCategory.setVisibility(View.VISIBLE);
        LoadCarousel();
        loadProductsForCategories(fixedCategories);
        loadProductsForCategoriesByBoysSkin(boysSkinCategories);
        LoadProductCategory();
        setupOnclick();
        loadProductsForMultiView(firstList, secondList);
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
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.loader, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void loadProductsForMultiView(String[] firstCategory, String[] secondCategory) {
        Set<String> categories = new HashSet<>();
        for (String category : firstCategory) {
            categories.add(category);
            hasMoreData.put(category, true);
            loadProductsMultiView(category, PAGE_SIZE);
        }
    }

    private void loadSecondList(String[] secondCategory) {
        for (String category : secondCategory) {
            hasMoreData.put(category, true);
            loadProductsMultiView(category, PAGE_SIZE);
        }
        secondListLoaded = true;
        isLoadingSecondList = false;
        hideBottonLoading();
    }

    private void loadProductsMultiView(String category, int limit) {
        Query query = firestore.collection("Product")
                .whereEqualTo("category", category)
                .orderBy("available", Query.Direction.DESCENDING)
                .limit(limit);

        DocumentSnapshot lastDoc = lastDocuments.get(category);
        if (lastDoc != null) {
            query = query.startAfter(lastDoc);
        }

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<ProductModel> products = new ArrayList<>();
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null) {
                    List<DocumentSnapshot> documents = querySnapshot.getDocuments();
                    for (DocumentSnapshot document : documents) {
                        ProductModel product = document.toObject(ProductModel.class);
                        if (product != null && product.isAvailable()) {
                            products.add(product);
                        }
                    }
                    if (isAdded() && binding != null) {
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

                        if (!documents.isEmpty()) {
                            lastDocuments.put(category, documents.get(documents.size() - 1));
                            hasMoreData.put(category, documents.size() == limit);
                        } else {
                            hasMoreData.put(category, false);
                        }
                    }
                }
                areMultiViewFirstListLoaded.set(true);
                checkIfInitialLoadingComplete();
            }
            hideBottonLoading();
        });
    }

    private void LoadProductCategory() {
        Context context = requireContext();
        Glide.with(context)
                .load(R.drawable.dabur)
                .placeholder(R.drawable.skeleton_shape)
                .error(R.drawable.skeleton_shape)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .override(300, 300)
                .into(binding.Dabur);

        binding.Dabur.setOnClickListener(view -> {
            Intent intent = new Intent(context, BrandActivity.class);
            intent.putExtra("brand", "DABUR");
            context.startActivity(intent);
        });

        Glide.with(context)
                .load(R.drawable.itc)
                .placeholder(R.drawable.skeleton_shape)
                .error(R.drawable.skeleton_shape)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .override(300, 300)
                .into(binding.itc);

        binding.itc.setOnClickListener(view -> {
            Intent intent = new Intent(context, BrandActivity.class);
            intent.putExtra("brand", "ITC");
            context.startActivity(intent);
        });

        Glide.with(context)
                .load(R.drawable.colgate)
                .placeholder(R.drawable.skeleton_shape)
                .error(R.drawable.skeleton_shape)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .override(300, 300)
                .into(binding.Colgate);

        binding.Colgate.setOnClickListener(view -> {
            Intent intent = new Intent(context, BrandActivity.class);
            intent.putExtra("brand", "Colgate");
            context.startActivity(intent);
        });

        Glide.with(context)
                .load(R.drawable.aashirvaad)
                .placeholder(R.drawable.skeleton_shape)
                .error(R.drawable.skeleton_shape)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .override(300, 300)
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
        DatabaseReference bannerRef = database.getReference().child("Banner");
        bannerRef.addValueEventListener(new ValueEventListener() {
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
            }
        });
        topCarousel.setCarouselListener(new CarouselListener() {
            @Nullable
            @Override
            public ViewBinding onCreateViewHolder(@NonNull LayoutInflater layoutInflater, @NonNull ViewGroup viewGroup) {
                return null;
            }
            @Override
            public void onBindViewHolder(@NonNull ViewBinding viewBinding, @NonNull CarouselItem carouselItem, int i) {}
            @Override
            public void onClick(int i, @NonNull CarouselItem carouselItem) {
                if (topBannerModels.size() > i && carouselItem.getHeaders() != null) {
                    BannerModels banner = topBannerModels.get(i);
                    FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                    Bundle bundle = new Bundle();
                    bundle.putString("filter", banner.getQuery());
                    if (banner.getFilterByCategory()) {
                        ProductFilterFragment fragment = new ProductFilterFragment();
                        fragment.setArguments(bundle);
                        transaction.replace(R.id.loader, fragment, "ProductFilterFragment");
                    } else {
                        bundle.putString("filterName", banner.getBannerCaption());
                        ProductFilterByQueryFragment fragment = new ProductFilterByQueryFragment();
                        fragment.setArguments(bundle);
                        transaction.replace(R.id.loader, fragment, "ProductFilterByQueryFragment");
                    }
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }
            @Override
            public void onLongClick(int i, @NonNull CarouselItem carouselItem) {}
        });
        bottomCarousel.setCarouselListener(new CarouselListener() {
            @Nullable
            @Override
            public ViewBinding onCreateViewHolder(@NonNull LayoutInflater layoutInflater, @NonNull ViewGroup viewGroup) {
                return null;
            }
            @Override
            public void onBindViewHolder(@NonNull ViewBinding viewBinding, @NonNull CarouselItem carouselItem, int i) {}
            @Override
            public void onClick(int i, @NonNull CarouselItem carouselItem) {
                if (bottomBannerModels.size() > i && carouselItem.getHeaders() != null) {
                    BannerModels banner = bottomBannerModels.get(i);
                    FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                    Bundle bundle = new Bundle();
                    bundle.putString("filter", banner.getQuery());
                    if (banner.getFilterByCategory()) {
                        ProductFilterFragment fragment = new ProductFilterFragment();
                        fragment.setArguments(bundle);
                        transaction.replace(R.id.loader, fragment, "ProductFilterFragment");
                    } else {
                        bundle.putString("filterName", banner.getBannerCaption());
                        ProductFilterByQueryFragment fragment = new ProductFilterByQueryFragment();
                        fragment.setArguments(bundle);
                        transaction.replace(R.id.loader, fragment, "ProductFilterByQueryFragment");
                    }
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }
            @Override
            public void onLongClick(int i, @NonNull CarouselItem carouselItem) {}
        });
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
            Bundle bundle = new Bundle();
            bundle.putString("filter", model.getTitle());
            ProductWithSlideCategoryFragment fragment = new ProductWithSlideCategoryFragment();
            fragment.setArguments(bundle);
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.loader, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
        dialogBinding.seeMoreBottom.setOnClickListener(v -> {
            homeProductBottomSheetDialog.dismiss();
            Bundle bundle = new Bundle();
            bundle.putString("filter", model.getTitle());
            ProductWithSlideCategoryFragment fragment = new ProductWithSlideCategoryFragment();
            fragment.setArguments(bundle);
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.loader, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
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
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.loader, fragment)
                    .addToBackStack("HomeFragment")
                    .commit();
        }
    }

    private void loadProduct(String category, ArrayList<HomeProductModel> productList, HomeCategoryAdapter adapter, AtomicBoolean loadingFlag) {
        databaseService.getAllProductsByCategoryOnlyForHomeFragment(category, new DatabaseService.GetAllProductsCallback() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(ArrayList<ProductModel> products) {
                if (isAdded()) {
                    productList.add(new HomeProductModel(category, products));
                    adapter.notifyItemInserted(productList.size() - 1);
                    loadingFlag.set(true);
                    checkIfInitialLoadingComplete();
                }
            }
            @Override
            public void onError(String errorMessage) {
                basicFun.AlertDialog(requireContext(), errorMessage);
                loadingFlag.set(true);
                checkIfInitialLoadingComplete();
            }
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
        if (binding != null) {
            if (binding.loadingAnimation != null && binding.loadingAnimation.isAnimating()) {
                binding.loadingAnimation.cancelAnimation();
            }
            binding.loadingTruck.clearAnimation();
            handler.removeCallbacksAndMessages(null);
            binding = null;
        }
    }
}