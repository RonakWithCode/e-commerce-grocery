package com.ronosoft.alwarmart.Fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
//import android.widget.Toast;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ronosoft.alwarmart.Activity.BrandActivity;
import com.ronosoft.alwarmart.Adapter.HomeCategoryAdapter;
import com.ronosoft.alwarmart.Adapter.HomeProductAdapter;
import com.ronosoft.alwarmart.Adapter.ProductAdapter;
import com.ronosoft.alwarmart.Component.ProductViewCard;
import com.ronosoft.alwarmart.Model.AddressModel;
import com.ronosoft.alwarmart.Model.BannerModels;
import com.ronosoft.alwarmart.Model.BestsellersModels;
import com.ronosoft.alwarmart.Model.HomeProductModel;
import com.ronosoft.alwarmart.Model.ProductCategoryModel;
import com.ronosoft.alwarmart.Model.ProductModel;
import com.ronosoft.alwarmart.Model.UserinfoModels;
import com.ronosoft.alwarmart.R;
import com.ronosoft.alwarmart.Services.DatabaseService;
import com.ronosoft.alwarmart.databinding.CategoryViewDialogBinding;
import com.ronosoft.alwarmart.databinding.FragmentHomeBinding;
import com.ronosoft.alwarmart.javaClasses.AddressDeliveryService;
import com.ronosoft.alwarmart.javaClasses.CustomSmoothScroller;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private FragmentHomeBinding binding;
    private DatabaseService databaseService;
    private HomeCategoryAdapter homeCategoryAdapter, homeProductBoysSkinAdapter;
    private HomeProductAdapter multiViewAdapter;
    private ArrayList<HomeProductModel> multiViewModel, homeProductModel, homeProductModelBoysSkin;
    private String userId;
    private Dialog homeProductBottomSheetDialog;
    private FirebaseFirestore firestore;

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
//                Toast.makeText(requireContext(), "Default address loaded. Delivery Time: " + deliveryTime + " minutes", Toast.LENGTH_SHORT).show();
            } else {
                binding.address.setText("HOME - Add Address");
                binding.txtDeliveryTime.setText("Alwar Mart in 10 minutes");
            }
        }

        // Retrieve user info if logged in
        final UserinfoModels[] userInfo = new UserinfoModels[1];
        if (currentUser != null) {
            databaseService.getUserInfo(userId, new DatabaseService.getUserInfoCallback() {
                @Override
                public void onSuccess(UserinfoModels user) {
                    userInfo[0] = user;
                }
                @Override
                public void onError(String errorMessage) {
//                    Toast.makeText(requireContext(), "Error retrieving user info: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Set dropdown listener for addresses
        binding.arrowDropdown.setOnClickListener(view -> {
            if (currentUser == null) {
//                Toast.makeText(requireContext(), "Please log in to manage your addresses", Toast.LENGTH_SHORT).show();
                return;
            }
            ArrayList<AddressModel> addresses = new ArrayList<>();
            if (userInfo[0] != null && userInfo[0].getAddress() != null) {
                addresses.addAll(userInfo[0].getAddress());
            }
            if (addresses.isEmpty()) {
//                Toast.makeText(requireContext(), "No addresses found. Please add one.", Toast.LENGTH_SHORT).show();
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
//                Toast.makeText(requireContext(), "Default address updated. Delivery Time: " + newDeliveryTime + " minutes", Toast.LENGTH_SHORT).show();
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
        multiViewAdapter = new HomeProductAdapter(multiViewModel, (product, productList) ->
                new ProductViewCard(getActivity()).showProductViewDialog(product, productList), requireActivity());

        binding.BestsellersSee.setOnClickListener(v -> SeeAll());
        binding.boysSkinCareSee.setOnClickListener(v -> SeeAll());

        setupAdapters();
        loadInitialData();
        return binding.getRoot();
    }

    public void SeeAll() {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.loader, new ProductWithSlideCategoryFragment());
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

        LinearLayoutManager multiViewManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        binding.MultiViewAdapter.setLayoutManager(multiViewManager);
        binding.MultiViewAdapter.setAdapter(multiViewAdapter);
        multiViewManager.setSmoothScrollbarEnabled(true);
        CustomSmoothScroller smoothScroller = new CustomSmoothScroller(requireContext());
        smoothScroller.setTargetPosition(0);
        multiViewManager.startSmoothScroll(smoothScroller);
        binding.MultiViewAdapter.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy * 2);
            }
        });
    }

    private void loadInitialData() {
        LoadCarousel();
        LoadProductCategory();
        LoadFromDBBestseller();
        databaseService.getAllCategory(new DatabaseService.GetAllCategoryCallback() {
            @Override
            public void onSuccess(ArrayList<ProductCategoryModel> categories) {
                // Extract unique tags from the fetched categories.
                ArrayList<String> tags = new ArrayList<>();
                for (ProductCategoryModel model : categories) {
                    if (model.getTag() != null && !tags.contains(model.getTag())) {
                        tags.add(model.getTag());
                    }
                }
                // Shuffle the list for randomness.
                Collections.shuffle(tags);

                // Limit the result to at most 15 items.
                int limit = 15;
                if (tags.size() > limit) {
                    tags = new ArrayList<>(tags.subList(0, limit));
                }

                // Convert the list to a String array.
                String[] tagArray = tags.toArray(new String[0]);

                // Call the multi-view loader with the randomized and limited array.
                loadProductsMultiViewForLoop(tagArray);
            }

            @Override
            public void onError(String errorMessage) {
                // Handle error as needed.
            }
        });


        loadProductsForCategories(new String[]{"Dairy","snacks","BISCUITS","hair oil","Grains","Pulses" ,"Honey & Spreads"});  /// this seller
        loadProductsForCategoriesByBoysSkin(new String[]{"SKIN CARE", "hair oil"});
    }

    private void LoadFromDBBestseller() {
        DocumentReference docRef = firestore.collection("Bestsellers").document("list");
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    BestsellersModels bestsellersModels = document.toObject(BestsellersModels.class);
                    if (bestsellersModels != null && bestsellersModels.getProductIds() != null) {
                        loadProductsForBestseller(bestsellersModels);
                    } else {
                        Log.e(TAG, "Bestsellers model or productIds is null.");
                    }
                } else {
                    Log.e(TAG, "Bestseller list not found in Firestore.");
                }
            } else {
                Log.e(TAG, "Error getting bestseller document: ", task.getException());
            }
        });
    }

    private void loadProductsForBestseller(BestsellersModels bestsellersModels) {
        List<String> productIds = bestsellersModels.getProductIds();
        ArrayList<ProductModel> bestsellerProductModel = new ArrayList<>();
        ProductAdapter productAdapter = new ProductAdapter(
                getViewLifecycleOwner(),
                bestsellerProductModel,
                (product, productList) -> new ProductViewCard(getActivity()).showProductViewDialog(product, productList),
                requireContext());

        if (!productIds.isEmpty()) {
            if (productIds.size() <= 10) {
                firestore.collection("Product")
                        .whereIn("productId", productIds)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && task.getResult() != null) {
                                for (DocumentSnapshot doc : task.getResult()) {
                                    ProductModel product = doc.toObject(ProductModel.class);
                                    if (product != null && product.isAvailable()) {
                                        bestsellerProductModel.add(product);
                                    }
                                }
                                productAdapter.notifyDataSetChanged();
                            } else {
                                Log.e(TAG, "Error fetching bestseller products: ", task.getException());
                            }
                        });
            } else {
                List<List<String>> chunks = splitList(productIds, 10);
                final int[] remaining = {chunks.size()};
                for (List<String> chunk : chunks) {
                    firestore.collection("Product")
                            .whereIn("productId", chunk)
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful() && task.getResult() != null) {
                                    for (DocumentSnapshot doc : task.getResult()) {
                                        ProductModel product = doc.toObject(ProductModel.class);
                                        if (product != null && product.isAvailable()) {
                                            bestsellerProductModel.add(product);
                                        }
                                    }
                                } else {
                                    Log.e(TAG, "Error fetching chunk: ", task.getException());
                                }
                                if (--remaining[0] == 0) {
                                    productAdapter.notifyDataSetChanged();
                                }
                            });
                }
            }
        }
        binding.recyclerByDatabase.setAdapter(productAdapter);
        binding.recyclerByDatabase.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    // Helper method to split a list into chunks
    private List<List<String>> splitList(List<String> list, int chunkSize) {
        List<List<String>> chunks = new ArrayList<>();
        for (int i = 0; i < list.size(); i += chunkSize) {
            chunks.add(list.subList(i, Math.min(list.size(), i + chunkSize)));
        }
        return chunks;
    }

    // Load products for multi-view (e.g. a list of categories with products)
    private void loadProductsMultiViewForLoop(String[] categories) {
        multiViewModel.clear();
        for (String category : categories) {
            loadProductsMultiView(category);
        }
    }

    private void loadProductsMultiView(String category) {
        databaseService.getAllProductsByCategoryOnly(category, new DatabaseService.GetAllProductsCallback() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(ArrayList<ProductModel> products) {
                multiViewModel.add(new HomeProductModel(category, products));
                multiViewAdapter.notifyDataSetChanged();
            }
            @Override
            public void onError(String errorMessage) {
                basicFun.AlertDialog(requireContext(), errorMessage);
            }
        });
    }

    private void LoadProductCategory() {
        Context context = requireContext();
        Glide.with(context)
                .load("https://firebasestorage.googleapis.com/v0/b/e-commerce-11d7d.appspot.com/o/brand%2F1742976615605-download.png?alt=media&token=51e28872-3ab5-4381-ac51-1435c8a2258b")
                .placeholder(R.drawable.skeleton_shape)
                .error(R.drawable.error_placeholder)
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
                .load("https://firebasestorage.googleapis.com/v0/b/e-commerce-11d7d.appspot.com/o/brand%2F1742798121205-download.png?alt=media&token=1f2a0e9e-8af1-4471-9b26-52a0ced5185f")
                .placeholder(R.drawable.skeleton_shape)
                .error(R.drawable.error_placeholder)
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
                .load("https://firebasestorage.googleapis.com/v0/b/e-commerce-11d7d.appspot.com/o/brand%2F1742797315362-ITC_Limited-Logo.wine.png?alt=media&token=35a715ee-51de-4eeb-a56c-ae8ae47c881a")
                .placeholder(R.drawable.skeleton_shape)
                .error(R.drawable.error_placeholder)
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
                .load("https://firebasestorage.googleapis.com/v0/b/e-commerce-11d7d.appspot.com/o/brand%2F1742797426395-4ba62de6fa4103a0c4a7e0f7f34afff9.w800.h800.png?alt=media&token=43a11f24-f7c4-44a2-9bb4-3d3d640fadcf")
                .placeholder(R.drawable.skeleton_shape)
                .error(R.drawable.error_placeholder)
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
//                topCarousel.clearData();
//                bottomCarousel.clearData();
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
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
        topCarousel.setCarouselListener(new CarouselListener() {
            @Nullable
            @Override
            public ViewBinding onCreateViewHolder(@NonNull LayoutInflater layoutInflater, @NonNull ViewGroup viewGroup) {
                return null;
            }
            @Override
            public void onBindViewHolder(@NonNull ViewBinding viewBinding, @NonNull CarouselItem carouselItem, int i) { }
            @Override
            public void onClick(int i, @NonNull CarouselItem carouselItem) {
                if (topBannerModels.size() > i && carouselItem.getHeaders() != null) {
                    String id = carouselItem.getHeaders().get("bannerId");
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
            public void onLongClick(int i, @NonNull CarouselItem carouselItem) { }
        });
        bottomCarousel.setCarouselListener(new CarouselListener() {
            @Nullable
            @Override
            public ViewBinding onCreateViewHolder(@NonNull LayoutInflater layoutInflater, @NonNull ViewGroup viewGroup) {
                return null;
            }
            @Override
            public void onBindViewHolder(@NonNull ViewBinding viewBinding, @NonNull CarouselItem carouselItem, int i) { }
            @Override
            public void onClick(int i, @NonNull CarouselItem carouselItem) {
                if (bottomBannerModels.size() > i && carouselItem.getHeaders() != null) {
                    String id = carouselItem.getHeaders().get("bannerId");
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
            public void onLongClick(int i, @NonNull CarouselItem carouselItem) { }
        });
    }

    // Show category view in a bottom sheet dialog
    void ViewCat(HomeProductModel model) {
        CategoryViewDialogBinding dialogBinding = CategoryViewDialogBinding.inflate(getLayoutInflater());
        dialogBinding.productsRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        dialogBinding.productsRecyclerView.setAdapter(
                new ProductAdapter(
                        getViewLifecycleOwner(),
                        model.getProduct(),
                        (product, productList) -> new ProductViewCard(getActivity()).showProductViewDialog(product, productList),
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

    // --- Helper method: loadProduct ---
    // This method fetches products by category and adds them to a given list using a provided adapter.
    private void loadProduct(String category, ArrayList<HomeProductModel> productList, HomeCategoryAdapter adapter) {
        databaseService.getAllProductsByCategoryOnly(category, new DatabaseService.GetAllProductsCallback() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(ArrayList<ProductModel> products) {
                productList.add(new HomeProductModel(category, products));
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onError(String errorMessage) {
                basicFun.AlertDialog(requireContext(), errorMessage);
            }
        });
    }

    // Method to load products for multiple categories into homeProductModel list.
    private void loadProductsForCategories(String[] categories) {
        for (String category : categories) {
            loadProduct(category, homeProductModel, homeCategoryAdapter);
        }
    }

    // Method to load products for multiple categories into homeProductModelBoysSkin list.
    private void loadProductsForCategoriesByBoysSkin(String[] categories) {
        for (String category : categories) {
            loadProduct(category, homeProductModelBoysSkin, homeProductBoysSkinAdapter);
        }
    }
}
