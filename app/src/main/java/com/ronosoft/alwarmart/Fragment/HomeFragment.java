
package com.ronosoft.alwarmart.Fragment;
//cqJlUK9cRtSbCGivXDBJXc:APA91bGlBmDiSDZqLnmg93PFituWHrqYtr_CPsHDqRCNxgUh0Ma7UOHM8E42cchp0_AVH0tJcjDjI5kN-yZOFG_Uyh33vKizIAnQT6-mBruJjtOFlfZJPyo
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
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
import com.google.firebase.auth.GetTokenResult;
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
import com.ronosoft.alwarmart.javaClasses.CustomSmoothScroller;
import com.ronosoft.alwarmart.javaClasses.DeliveryManagement;
import com.ronosoft.alwarmart.javaClasses.basicFun;
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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private DatabaseService databaseService;
    private HomeCategoryAdapter homeCategoryAdapter;
    private HomeCategoryAdapter homeProductBoysSkinAdapter;
    private HomeProductAdapter MultiViewAdapter;
    private ArrayList<HomeProductModel> MultiViewModel; // multiply product
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

        // Initialize DatabaseService early to avoid NPE
        databaseService = new DatabaseService();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        // Set greeting and header based on login state
        if (currentUser != null) {
            userId = currentUser.getUid();
            String displayName = currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "User";
            binding.UserName.setText("Hello, " + displayName.toUpperCase(Locale.ROOT));
        } else {
            binding.UserName.setText("Hello, Guest");
            binding.address.setVisibility(View.GONE);
            binding.arrowDropdown .setVisibility(View.GONE);

            binding.txtDeliveryTime.setText("Alwar Mart in 10 minutes");
        }

        // Load default address from SharedPreferences using AddressDeliveryService if user is logged in
        AddressDeliveryService addressDeliveryService = new AddressDeliveryService();
        if (currentUser != null) {
            AddressModel defaultAddress = addressDeliveryService.getDefaultAddress(requireContext());
            if (defaultAddress != null) {
                int deliveryTime = addressDeliveryService.calculateDeliveryTime(defaultAddress);
                binding.txtDeliveryTime.setText("Alwar Mart in " + deliveryTime + " minutes");

                String addrText = "HOME - " + defaultAddress.getFlatHouse() + ", " + defaultAddress.getAddress();
                int maxLength = 30;
                if (addrText.length() > maxLength) {
                    addrText = addrText.substring(0, maxLength) + "...";
                }
                binding.address.setText(addrText);

                Toast.makeText(requireContext(), "Default address loaded. Delivery Time: " + deliveryTime + " minutes", Toast.LENGTH_SHORT).show();
            } else {
                binding.address.setText("HOME - Add Address");
                binding.txtDeliveryTime.setText("Alwar Mart in 10 minutes");
            }
        }

        // Retrieve user info from Firestore if user is logged in
        final UserinfoModels[] userInfo = new UserinfoModels[1];
        if (currentUser != null) {
            databaseService.getUserInfo(userId, new DatabaseService.getUserInfoCallback() {
                @Override
                public void onSuccess(UserinfoModels user) {
                    userInfo[0] = user;
                }
                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(requireContext(), "Error retrieving user info: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Set arrow dropdown click listener to show default address bottom sheet
        binding.arrowDropdown.setOnClickListener(view -> {
            if (currentUser == null) {
                Toast.makeText(requireContext(), "Please log in to manage your addresses", Toast.LENGTH_SHORT).show();
                return;
            }
            ArrayList<AddressModel> addresses = new ArrayList<>();
            if (userInfo[0] != null && userInfo[0].getAddress() != null) {
                addresses.addAll(userInfo[0].getAddress());
            }
            if (addresses.isEmpty()) {
                Toast.makeText(requireContext(), "No addresses found. Please add one.", Toast.LENGTH_SHORT).show();
                return;
            }
            DefaultAddressBottomSheetFragment bottomSheet = new DefaultAddressBottomSheetFragment(addresses);
            bottomSheet.setOnAddressSelectedListener(selectedAddress -> {
                addressDeliveryService.saveDefaultAddress(requireContext(), selectedAddress);
                int newDeliveryTime = addressDeliveryService.calculateDeliveryTime(selectedAddress);

                String addrText = "HOME - " + selectedAddress.getFlatHouse() + ", " + selectedAddress.getAddress();


                int maxLength = 30;
                if (addrText.length() > maxLength) {
                    addrText = addrText.substring(0, maxLength) + "...";
                }
                binding.address.setText(addrText);

                binding.txtDeliveryTime.setText("Alwar Mart in " + newDeliveryTime + " minutes");
                Toast.makeText(requireContext(), "Default address updated. Delivery Time: " + newDeliveryTime + " minutes", Toast.LENGTH_SHORT).show();
            });
            bottomSheet.show(getChildFragmentManager(), "DefaultAddressBottomSheet");
        });

        // Set up the status bar and search view
        HomeProductBottomSheetDialog = new Dialog(requireContext());
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        Window window = requireActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.OrderYellowColor));
        binding.searchView.setOnClickListener(view -> openSearchFragment());

        // Initialize product lists and adapters
        homeProductModel = new ArrayList<>();
        MultiViewModel = new ArrayList<>();
        homeProductModelBoysSkin = new ArrayList<>();

        homeCategoryAdapter = new HomeCategoryAdapter(homeProductModel, requireContext(), this::ViewCat);
        homeProductBoysSkinAdapter = new HomeCategoryAdapter(homeProductModelBoysSkin, requireContext(), this::ViewCat);
        MultiViewAdapter = new HomeProductAdapter(MultiViewModel, (productModel, sameProducts) ->
                new ProductViewCard(getActivity()).showProductViewDialog(productModel, sameProducts), requireActivity());

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
                int newDy = dy * 2; // Increase scrolling speed by multiplying with a factor (e.g., 1.5)
                super.onScrolled(recyclerView, dx, newDy);
            }
        });



    }
    private void loadInitialData() {
        LoadCarousel();
        LoadProductCategory();

//        loadProductsMultiViewForLoop(new String[]{"chips and Snacks"});
        loadProductsMultiViewForLoop(new String[]{"hair oil","SKIN CARE", "Edible Oils", "Honey & Spreads", "Digestive Care"});
        loadProductsForCategories(new String[]{"hair oil","SKIN CARE", "Edible Oils", "Honey & Spreads", "Digestive Care"});
//        loadProductsForCategories(new String[]{"Detergent Powder & Bars","Soaps & Body Care", "snacks", "Dishwashing Bars & Tubs", "BISCUITS"});
//        loadProductsForCategories(new String[]{"ha","hahahah","aw","oil","this is me new \uD83D\uDC08" });
//        loadProductsForCategoriesByBoysSkin(new String[]{"toothpaste", "drinks"});
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
        Context context = requireContext();

        Glide.with(context)
                .load("https://firebasestorage.googleapis.com/v0/b/e-commerce-11d7d.appspot.com/o/brand%2F1742447881857-download.png?alt=media&token=af2fe592-67cb-45b6-9de1-4c37adc87d6c")
                .placeholder(R.drawable.skeleton_shape)
                .error(R.drawable.error_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .override(300, 300) // Adjust these values based on your ImageView size
                .into(binding.Dabur);

        Glide.with(context)
                .load("https://firebasestorage.googleapis.com/v0/b/e-commerce-11d7d.appspot.com/o/brand%2F1742798121205-download.png?alt=media&token=1f2a0e9e-8af1-4471-9b26-52a0ced5185f")
                .placeholder(R.drawable.skeleton_shape)
                .error(R.drawable.error_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .override(300, 300)
                .into(binding.Colgate);

        Glide.with(context)
                .load("https://firebasestorage.googleapis.com/v0/b/e-commerce-11d7d.appspot.com/o/brand%2F1742797315362-ITC_Limited-Logo.wine.png?alt=media&token=35a715ee-51de-4eeb-a56c-ae8ae47c881a")
                .placeholder(R.drawable.skeleton_shape)
                .error(R.drawable.error_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .override(300, 300)
                .into(binding.itc);

        Glide.with(context)
                .load("https://firebasestorage.googleapis.com/v0/b/e-commerce-11d7d.appspot.com/o/brand%2F1742797426395-4ba62de6fa4103a0c4a7e0f7f34afff9.w800.h800.png?alt=media&token=43a11f24-f7c4-44a2-9bb4-3d3d640fadcf")
                .placeholder(R.drawable.skeleton_shape)
                .error(R.drawable.error_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .override(300, 300)
                .into(binding.Aashirvaad);
    }







    void LoadCarousel() {
        ArrayList<BannerModels> TopBannerModels = new ArrayList<>();
        ImageCarousel TopCarousel = this.binding.carousel;
        ImageCarousel BottomCarousel = this.binding.BottomCarousel;

        ArrayList<BannerModels> BottomBannerModels = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference bannerRef = database.getReference().child("Banner");
        bannerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    BannerModels banner = snapshot.getValue(BannerModels.class);
                    if (banner != null && banner.isActive()) {
                        if (banner.getPosition().equals("Top Position")) {
                            TopBannerModels.add(banner);
                            Map<String,String> map = new HashMap<>();
                            map.put("bannerId",banner.getBannerId());
                            TopCarousel.addData(new CarouselItem(banner.getBannerImages(),map));
                        }
                        else {
                            BottomBannerModels.add(banner);
//                            BottomCarousel
                            Map<String,String> map = new HashMap<>();
                            map.put("bannerId",banner.getBannerId());
                            BottomCarousel.addData(new CarouselItem(banner.getBannerImages(),map));

                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        TopCarousel.setCarouselListener(new CarouselListener() {
            @Nullable
            @Override
            public ViewBinding onCreateViewHolder(@NonNull LayoutInflater layoutInflater, @NonNull ViewGroup viewGroup) {
                return null;
            }

            @Override
            public void onBindViewHolder(@NonNull ViewBinding viewBinding, @NonNull CarouselItem carouselItem, int i) {

            }

            @Override
            public void onClick(int i, @NonNull CarouselItem carouselItem) {
                assert carouselItem.getHeaders() != null;
                String id = carouselItem.getHeaders().get("bannerId");
                if (TopBannerModels.get(i).getBannerId().equals(id)) {
                    if (TopBannerModels.get(i).getFilterByCategory()) {
                        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                        Bundle bundle = new Bundle();
                        bundle.putString("filter", TopBannerModels.get(i).getQuery());
                        ProductFilterFragment fragment = new ProductFilterFragment();
                        fragment.setArguments(bundle);
                        transaction.replace(R.id.loader, fragment, "ProductFilterFragment");
                        transaction.addToBackStack("ProductFilterFragment");
                        transaction.commit();
                    } else {
                        //TODO : ---------------------------------------------------------
                        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                        Bundle bundle = new Bundle();
                        bundle.putString("filter", TopBannerModels.get(i).getQuery());
                        bundle.putString("filterName", TopBannerModels.get(i).getBannerCaption());
                        ProductFilterByQueryFragment fragment = new ProductFilterByQueryFragment();
                        fragment.setArguments(bundle);
                        transaction.replace(R.id.loader, fragment, "ProductFilterByQueryFragment");
                        transaction.addToBackStack("ProductFilterByQueryFragment");
                        transaction.commit();
                    }
                }
            }

            @Override
            public void onLongClick(int i, @NonNull CarouselItem carouselItem) {


            }
        });
        BottomCarousel.setCarouselListener(new CarouselListener() {
            @Nullable
            @Override
            public ViewBinding onCreateViewHolder(@NonNull LayoutInflater layoutInflater, @NonNull ViewGroup viewGroup) {
                return null;
            }

            @Override
            public void onBindViewHolder(@NonNull ViewBinding viewBinding, @NonNull CarouselItem carouselItem, int i) {

            }

            @Override
            public void onClick(int i, @NonNull CarouselItem carouselItem) {
                assert carouselItem.getHeaders() != null;
                String id = carouselItem.getHeaders().get("bannerId");
                if (BottomBannerModels.get(i).getBannerId().equals(id)) {
                    if (BottomBannerModels.get(i).getFilterByCategory()) {
                        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                        Bundle bundle = new Bundle();
                        bundle.putString("filter", BottomBannerModels.get(i).getQuery());
                        ProductFilterFragment fragment = new ProductFilterFragment();
                        fragment.setArguments(bundle);
                        transaction.replace(R.id.loader, fragment, "ProductFilterFragment");
                        transaction.addToBackStack("ProductFilterFragment");
                        transaction.commit();
                    } else {
                        //TODO : ---------------------------------------------------------
                        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                        Bundle bundle = new Bundle();
                        bundle.putString("filter", BottomBannerModels.get(i).getQuery());
                        bundle.putString("filterName", BottomBannerModels.get(i).getBannerCaption());
                        ProductFilterByQueryFragment fragment = new ProductFilterByQueryFragment();
                        fragment.setArguments(bundle);
                        transaction.replace(R.id.loader, fragment, "ProductFilterByQueryFragment");
                        transaction.addToBackStack("ProductFilterByQueryFragment");
                        transaction.commit();
                    }
                }
            }

            @Override
            public void onLongClick(int i, @NonNull CarouselItem carouselItem) {


            }
        });





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

// this card not viewCat
    void ViewCat(HomeProductModel model){
//        HomeProductBottomSheetDialog = new Dialog(requireContext());
        CategoryViewDialogBinding productViewDialogBinding = CategoryViewDialogBinding.inflate(getLayoutInflater());
        productViewDialogBinding.productsRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
//        productViewDialogBinding.productsRecyclerView.setAdapter(new ProductAdapter(model.getProduct(), (productModel, sameProducts) -> new ProductViewCard(getActivity()).showProductViewDialog(productModel,sameProducts), requireContext(), "Main"));
        productViewDialogBinding.productsRecyclerView.setAdapter(
                new ProductAdapter(
                        getViewLifecycleOwner(),
                        model.getProduct(),
                        (ProductModel product, ArrayList<ProductModel> productList) ->
                                new ProductViewCard(getActivity()).showProductViewDialog(product, productList),
                        requireContext()
                )
        );


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

        Objects.requireNonNull(HomeProductBottomSheetDialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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
