package com.ronosoft.alwarmart.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ronosoft.alwarmart.Model.ProductModel;
import com.ronosoft.alwarmart.R;
import com.ronosoft.alwarmart.databinding.FragmentProductDetailsBinding;
import com.ronosoft.alwarmart.interfaceClass.onClickProductAdapter;

import java.util.ArrayList;


@SuppressLint({"ResourceAsColor", "NotifyDataSetChanged", "SetTextI18n"})
public class ProductDetailsFragment extends Fragment implements onClickProductAdapter {
    private FragmentProductDetailsBinding binding;
    private ProductModel productModel = null;
    private FragmentTransaction transaction;

    public ProductDetailsFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProductDetailsBinding.inflate(inflater, container, false);
//        transaction = requireActivity().getSupportFragmentManager().beginTransaction();
//        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.hide();
//        }
//        Bundle args = getArguments();
//        if (args != null) {
//            productModel = args.getParcelable("productDetails");
//            String productId = args.getString("productId");
//            if (productModel == null) {
//                // Fetch productModel from database
//                new DatabaseService().getAllProductById(productId, new DatabaseService.GetAllProductsModelCallback() {
//                    @Override
//                    public void onSuccess(ProductModel products) {
//                        productModel = products;
//                        // Call initValue() once productModel is initialized
//                        initValue();
//                    }
//
//                    @Override
//                    public void onError(String errorMessage) {
//                        CustomErrorDialog customErrorDialog = new CustomErrorDialog(requireContext());
//                        customErrorDialog.setTitle("Loading product error");
//                        customErrorDialog.setMessage(errorMessage);
//                        customErrorDialog.show();
//                    }
//                });
//            } else {
//                // Call initValue() if productModel is already available
//                initValue();
//            }
//        } else {
//            requireActivity().finish();
//            startActivity(new Intent(requireContext(), MainActivity.class));
//        }
//
//
//
//
//        binding.backButton.setOnClickListener(view-> requireActivity().onBackPressed());
//        binding.recyclerViewProgressBar.setVisibility(View.GONE);
//        BottomAppBar bottomAppBar = requireActivity().findViewById(R.id.bottomAppBar);
//        if (bottomAppBar != null) {
//            bottomAppBar.setVisibility(View.GONE);
//        }
//        //        ((AppCompatActivity) requireActivity()).getSupportActionBar().hide();
////        binding.viewBack.setOnClickListener(back-> requireActivity().onBackPressed());
////        initValue();
//        binding.AddTOCart.setOnClickListener(view -> addToCart());
//        binding.plusBtn.setOnClickListener(view -> {
//            int quantity = productModel.getDefaultQuantity();
//            quantity++;
//            if (quantity > productModel.getQuantity()) {
//                Toast.makeText(requireContext(), "Max stock available: " + productModel.getQuantity(), Toast.LENGTH_SHORT).show();
//            } else {
//                productModel.setDefaultQuantity(quantity);
//                binding.quantity.setText(String.valueOf(quantity));
//            }
//        });
//        binding.minusBtn.setOnClickListener(view -> {
//            int quantity = productModel.getDefaultQuantity();
//            if (quantity > 1)
//                quantity--;
//            productModel.setDefaultQuantity(quantity);
//            binding.quantity.setText(String.valueOf(quantity));
//
//        });
////        binding.BuyNow.setOnClickListener(btu-> buyNow());
//
//        binding.search.setOnClickListener(view ->{
//            requireActivity().getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.loader, new SearchFragment())
//                    .addToBackStack("ProductDetailsFragment")
//                    .commit();
//        });

        return binding.getRoot();
    }

    @Override
    public void onClick(ProductModel productModel, ArrayList<ProductModel> sameProducts) {

    }
//
//    private void initValue() {
//        if (FirebaseAuth.getInstance().getCurrentUser() !=null) {
//            String userId = FirebaseAuth.getInstance().getUid();
//            assert userId != null;
//            DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("Cart").child(userId);
//            String productNameToFind = productModel.getProductId();
//            Query query = productsRef.orderByKey().equalTo(productNameToFind);
//            query.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    if (dataSnapshot.exists()) {
//                        binding.AddTOCart.setText("Go to Cart");
//                        binding.quantityBox.setVisibility(View.VISIBLE);
//                    }
//                }
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//                    // Handle the error in case of a database error.
//                    Toast.makeText(getContext(), "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//
//        binding.category.setText(productModel.getCategory());
//        binding.ProductTitle.setText(productModel.getProductName());
//        for (int i = 0; i < productModel.getImageURL().size(); i++) {
//            String imageUrl = productModel.getImageURL().get(i);
//            CarouselItem carouselItem = new CarouselItem(imageUrl);
//            binding.productsImages.addData(carouselItem);
//        }
//
//        if (!Objects.equals(productModel.getProductDescription(), "")){
//            binding.productDescription.setVisibility(View.VISIBLE);
//            binding.Description .setVisibility(View.VISIBLE);
//            binding.productDescription.setText(productModel.getProductDescription());
//        }
//
//        double mrp = productModel.getMrp(); // Replace with the actual MRP
//        double sellingPrice = productModel.getPrice(); // Replace with the actual selling price
////
//        double discountPercentage = mrp - sellingPrice;
//
////        float percentageSaved = (float) (((productModel.getMrp() - productModel.getPrice()) / productModel.getMrp()) * 100f);
//        binding.discount.setText("₹" + discountPercentage + "% off");
//
//        binding.Price.setText("₹" + productModel.getPrice());
//        binding.quantitySmail.setText("(₹" + productModel.getPrice() + " / " + productModel.getSubUnit() + productModel.getUnit() + ")");
//        binding.MRP.setText(":"+productModel.getMrp());
//        binding.MRP.setPaintFlags(binding.MRP.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//
//
//        if (productModel.getQuantity() == 0) {
//            binding.OutOfStockBuyOptions.setVisibility(View.VISIBLE);
//            binding.quantity.setText("0");
//            binding.TextInStock.setText("Out of Stock");
//            binding.quantityBox.setVisibility(View.INVISIBLE);
//            binding.TextInStock.setTextColor(R.color.FixRed);
//            binding.AddTOCart.setVisibility(View.INVISIBLE);
////            binding.BuyNow.setVisibility(View.INVISIBLE);
//        }
//        binding.categoryType.setText(productModel.getCategory());
//        binding.netQuantity.setText(productModel.getSubUnit() + productModel.getUnit());
////        binding.categoryType.setText(productModel.getQuantity()+productModel.getItemUnit());
//        String diet;
//        if (productModel.getProductType().equals("FoodVeg")) {
//            binding.ItemType.setImageResource(R.drawable.food_green);
//            diet = "Veg";
//        } else if (productModel.getProductType().equals("FoodNonVeg")) {
//            binding.ItemType.setImageResource(R.drawable.food_brown);
//            diet = "NonVeg";
//        } else {
//            diet = "not food item.";
//            binding.ItemType.setVisibility(View.GONE);
//            binding.dietType.setVisibility(View.GONE);
//            binding.diet.setVisibility(View.GONE);
//        }
//        binding.dietType.setText(diet);
//
//        binding.ExpiryDate.setText(productModel.getEditDate());
//        ProductDisplayImagesAdapter productDisplayImagesAdapter = new ProductDisplayImagesAdapter(productModel.getImageURL(), getContext());
//        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
//        binding.ProductItemImage.setLayoutManager(manager);
//        binding.ProductItemImage.setAdapter(productDisplayImagesAdapter);
//        productDisplayImagesAdapter.notifyDataSetChanged();
//        LoadProduct();
//    }
//
//    void LoadProduct() {
//        ArrayList<ProductModel> model = new ArrayList<>();
//        ProductAdapter productAdapter = new ProductAdapter(model, this, requireContext(), "HORIZONTAL");
//        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
//        binding.itemCategory.setAdapter(productAdapter);
//        binding.itemCategory.setLayoutManager(layoutManager);
//
//        new DatabaseService().getAllProductsByCategory(productModel.getProductId(), productModel.getCategory(), new DatabaseService.GetAllProductsCallback() {
//            @Override
//            public void onSuccess(ArrayList<ProductModel> products) {
//                model.clear();
//                model.addAll(products);
//                binding.recyclerViewProgressBar.setVisibility(View.GONE);
//                productAdapter.notifyDataSetChanged();
//
//            }
//
//            @Override
//            public void onError(String errorMessage) {
//                model.clear();
//                binding.product.setVisibility(View.GONE);
//                binding.itemCategory.setVisibility(View.GONE);
//            }
//        });
//
//    }
//
////    public void buyNow() {
////        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
////            GoToBuy();
////        } else {
////            startActivity(new Intent(getContext(), AuthMangerActivity.class));
////        }
////    }
//
////    public void GoToBuy() {
////        Intent intent = new Intent(requireContext(), OderActivity.class);
////        intent.putExtra("BuyType", "Now");
////        intent.putExtra("productModel", productModel);
////        startActivity(intent);
////    }
//
//
//    public void addToCart() {
//        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//            ShoppingCartFirebaseModel shoppingCartFirebaseModel = new ShoppingCartFirebaseModel(productModel.getProductId(), productModel.getDefaultQuantity());
//            DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("Cart").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
//            String productNameToFind = productModel.getProductId();
//            Query query = productsRef.orderByKey().equalTo(productNameToFind);
//            query.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    if (dataSnapshot.exists()) {
//                        // Product already exists in the cart
//                        // Handle this scenario, e.g., navigate to the cart
//                        navigateToShoppingCartFragment();
//                    } else {
//                        // Product doesn't exist in the cart, add it
//                        addProductToCart(productsRef, shoppingCartFirebaseModel);
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
//    private void navigateToShoppingCartFragment() {
//        BottomAppBar bottomAppBar = requireActivity().findViewById(R.id.bottomAppBar);
//        if (bottomAppBar != null) {
//            bottomAppBar.setVisibility(View.VISIBLE);
//        }
//        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.show();
//        }
//        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.loader, new ShoppingCartsFragment(), "ShoppingCartsFragment");
//        transaction.addToBackStack("ShoppingCartsFragment");
//        transaction.commit();
//    }
//
//    private void addProductToCart(DatabaseReference productsRef, ShoppingCartFirebaseModel shoppingCartFirebaseModel) {
//        productsRef.child(productModel.getProductId()).setValue(shoppingCartFirebaseModel)
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        binding.AddTOCart.setText("Go to Cart");
//                    }
//                })
//                .addOnFailureListener(error -> basicFun.AlertDialog(requireContext(), error.toString()));
//    }
//
//
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        BottomAppBar bottomAppBar = requireActivity().findViewById(R.id.bottomAppBar);
//        if (bottomAppBar != null) {
//            bottomAppBar.setVisibility(View.VISIBLE);
//        }
//        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.show();
//        }
//    }
//
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.hide();
//        }
//    }
//
//    @Override
//    public void onClick(ProductModel productModel, ArrayList<ProductModel> sameProducts) {
//        Bundle bundle = new Bundle();
//        bundle.putParcelable("productDetails", productModel);
//        bundle.putInt("backButton", 0);
//        ProductDetailsFragment productDetailsFragment = new ProductDetailsFragment();
//        productDetailsFragment.setArguments(bundle);
//        transaction.replace(R.id.loader,productDetailsFragment,"productDetailsFragment");
//        transaction.addToBackStack("productDetailsFragment").commit();
//    }
}