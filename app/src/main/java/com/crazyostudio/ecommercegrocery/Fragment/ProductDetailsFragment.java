package com.crazyostudio.ecommercegrocery.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.crazyostudio.ecommercegrocery.Activity.AuthMangerActivity;
import com.crazyostudio.ecommercegrocery.Activity.OderActivity;
import com.crazyostudio.ecommercegrocery.Adapter.ProductAdapter;
import com.crazyostudio.ecommercegrocery.Adapter.ProductDisplayImagesAdapter;
import com.crazyostudio.ecommercegrocery.Model.ProductModel;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.Services.DatabaseService;
import com.crazyostudio.ecommercegrocery.databinding.FragmentProductDetailsBinding;
import com.crazyostudio.ecommercegrocery.interfaceClass.onClickProductAdapter;
import com.crazyostudio.ecommercegrocery.javaClasses.basicFun;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

import java.util.ArrayList;
import java.util.Objects;


@SuppressLint({"ResourceAsColor", "NotifyDataSetChanged", "SetTextI18n"})
public class ProductDetailsFragment extends Fragment implements onClickProductAdapter {
    private FragmentProductDetailsBinding binding;
    private ProductModel productModel;
    private int BACK;
    private FragmentTransaction transaction;
    private boolean IsChatsProgressBar = false;

    public ProductDetailsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            productModel = getArguments().getParcelable("productDetails");
            String BACK_KEY = "backButton";
            BACK = getArguments().getInt(BACK_KEY, 1);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProductDetailsBinding.inflate(inflater, container, false);
        transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        if (BACK == 0) {
//            bindin.viewBack.setVisibility(View.VISIBLE);
        }
        if (IsChatsProgressBar) {
            binding.recyclerViewProgressBar.setVisibility(View.GONE);
        }
        BottomAppBar bottomAppBar = getActivity().findViewById(R.id.bottomAppBar);
        if (bottomAppBar != null) {
            bottomAppBar.setVisibility(View.GONE);
        }
        //        ((AppCompatActivity) requireActivity()).getSupportActionBar().hide();
//        binding.viewBack.setOnClickListener(back-> requireActivity().onBackPressed());
        initValue();
        binding.AddTOCart.setOnClickListener(view -> AddTOCart());
        binding.plusBtn.setOnClickListener(view -> {
            int quantity = productModel.getDefaultQuantity();
            quantity++;
            if (quantity > productModel.getQuantity()) {
                Toast.makeText(requireContext(), "Max stock available: " + productModel.getQuantity(), Toast.LENGTH_SHORT).show();
            } else {
                productModel.setDefaultQuantity(quantity);
                binding.quantity.setText(String.valueOf(quantity));
            }
        });
        binding.minusBtn.setOnClickListener(view -> {
            int quantity = productModel.getDefaultQuantity();
            if (quantity > 1)
                quantity--;
            productModel.setDefaultQuantity(quantity);
            binding.quantity.setText(String.valueOf(quantity));

        });
//        binding.BuyNow.setOnClickListener(btu-> buyNow());

        return binding.getRoot();
    }

    private void initValue() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String userId = FirebaseAuth.getInstance().getUid();
            assert userId != null;
            CollectionReference productsRef = FirebaseFirestore.getInstance().collection("Cart").document(userId).collection("Products");
            String productNameToFind = productModel.getProductId();
            productsRef.document(productNameToFind).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        // Product exists in the cart
                        binding.AddTOCart.setText("Go to Cart");
                        binding.quantityBox.setVisibility(View.VISIBLE);
                    }
                } else {
                    // Handle errors
                    Toast.makeText(getContext(), "Firestore error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        binding.category.setText(productModel.getCategory());
        binding.ProductTitle.setText(productModel.getProductName());
        for (int i = 0; i < productModel.getImageURL().size(); i++) {
            binding.productsImages.addData(new CarouselItem(productModel.getImageURL().get(i)));
        }
        double mrp = productModel.getMrp(); // Replace with the actual MRP
        double sellingPrice = productModel.getPrice(); // Replace with the actual selling price
//
        double discountPercentage = mrp - sellingPrice;

//        float percentageSaved = (float) (((productModel.getMrp() - productModel.getPrice()) / productModel.getMrp()) * 100f);
        binding.discount.setText("₹" + discountPercentage + "% off");
        binding.Price.setText("₹" + productModel.getPrice());
        binding.quantitySmail.setText("(₹" + productModel.getPrice() + " / " + productModel.getSubUnit() + productModel.getUnit() + ")");
//        binding.MRP.setText("M.R.P.: ₹"+productModel.getMrp());
        if (productModel.getQuantity() == 0) {
            binding.OutOfStockBuyOptions.setVisibility(View.VISIBLE);
            binding.quantity.setText("0");
            binding.TextInStock.setText("Out of Stock");
            binding.quantityBox.setVisibility(View.INVISIBLE);
            binding.TextInStock.setTextColor(R.color.FixRed);
            binding.AddTOCart.setVisibility(View.INVISIBLE);
//            binding.BuyNow.setVisibility(View.INVISIBLE);
        }
        binding.categoryType.setText(productModel.getCategory());
        binding.netQuantity.setText(productModel.getSubUnit() + productModel.getUnit());
//        binding.categoryType.setText(productModel.getQuantity()+productModel.getItemUnit());
        String diet;
        if (productModel.getProductType().equals("FoodVeg")) {
            binding.ItemType.setImageResource(R.drawable.food_green);
            diet = "Veg";
        } else if (productModel.getProductType().equals("FoodNonVeg")) {
            binding.ItemType.setImageResource(R.drawable.food_brown);
            diet = "NonVeg";
        } else {
            diet = "not food item.";
            binding.ItemType.setVisibility(View.GONE);
            binding.dietType.setVisibility(View.GONE);
            binding.diet.setVisibility(View.GONE);
        }

        binding.dietType.setText(diet);
        binding.ExpiryDate.setText(productModel.getEditDate());
        ProductDisplayImagesAdapter productDisplayImagesAdapter = new ProductDisplayImagesAdapter(productModel.getImageURL(), getContext());
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.ProductItemImage.setLayoutManager(manager);
        binding.ProductItemImage.setAdapter(productDisplayImagesAdapter);
        productDisplayImagesAdapter.notifyDataSetChanged();
        LoadProduct();
    }

    void LoadProduct() {
        ArrayList<ProductModel> model = new ArrayList<>();
        ProductAdapter productAdapter = new ProductAdapter(model, this, requireContext(), "HORIZONTAL");
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.itemCategory.setAdapter(productAdapter);
        binding.itemCategory.setLayoutManager(layoutManager);

        new DatabaseService().getAllProductsByCategory(productModel.getProductId(), productModel.getCategory(), new DatabaseService.GetAllProductsCallback() {
            @Override
            public void onSuccess(ArrayList<ProductModel> products) {
                model.clear();
                model.addAll(products);
                IsChatsProgressBar = true;
                binding.recyclerViewProgressBar.setVisibility(View.GONE);
                productAdapter.notifyDataSetChanged();

            }

            @Override
            public void onError(String errorMessage) {
                model.clear();
                binding.product.setVisibility(View.GONE);
                binding.itemCategory.setVisibility(View.GONE);
            }
        });

    }

    public void buyNow() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            GoToBuy();
        } else {
            startActivity(new Intent(getContext(), AuthMangerActivity.class));
        }
    }

    public void GoToBuy() {
        Intent intent = new Intent(requireContext(), OderActivity.class);
        intent.putExtra("BuyType", "Now");
        intent.putExtra("productModel", productModel);
        startActivity(intent);
    }

    public void AddTOCart() {




        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String userId = FirebaseAuth.getInstance().getUid();
            assert userId != null;
            CollectionReference productsRef = FirebaseFirestore.getInstance().collection("Cart").document(userId).collection("Products");
            String productNameToFind = productModel.getProductId();
            productsRef.document(productNameToFind).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        // Product already exists in the cart
                        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.loader, new ShoppingCartsFragment(), "ShoppingCartsFragment");
                        transaction.addToBackStack("ShoppingCartsFragment");
                        transaction.commit();
                    } else {
                        // Product doesn't exist in the cart, add it
                        productsRef.document(productNameToFind).set(productModel).addOnCompleteListener(addTask -> {
                            if (addTask.isSuccessful()) {
                                binding.AddTOCart.setText("Go to Cart");
                            }
                        }).addOnFailureListener(error -> basicFun.AlertDialog(requireContext(), error.toString()));
                    }
                } else {
                    // Handle errors
                    Toast.makeText(getContext(), "Firestore error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            startActivity(new Intent(getContext(), AuthMangerActivity.class));
        }
    }


    @Override
    public void onClick(ProductModel productModel) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("productDetails", productModel);
        bundle.putInt("backButton", 0);
        ProductDetailsFragment productDetailsFragment = new ProductDetailsFragment();
        productDetailsFragment.setArguments(bundle);
        transaction.replace(R.id.loader,productDetailsFragment,"productDetailsFragment");
        transaction.addToBackStack("productDetailsFragment").commit();
    }

}