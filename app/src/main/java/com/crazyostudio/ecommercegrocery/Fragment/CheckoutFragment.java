package com.crazyostudio.ecommercegrocery.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.crazyostudio.ecommercegrocery.Adapter.ShoppingCartsAdapter;
import com.crazyostudio.ecommercegrocery.HelperClass.ShoppingCartHelper;
import com.crazyostudio.ecommercegrocery.Model.ShoppingCartsProductModel;
import com.crazyostudio.ecommercegrocery.Services.AuthService;
import com.crazyostudio.ecommercegrocery.Services.DatabaseService;
import com.crazyostudio.ecommercegrocery.databinding.FragmentCheckoutBinding;
import com.crazyostudio.ecommercegrocery.interfaceClass.ShoppingCartsInterface;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class CheckoutFragment extends Fragment implements ShoppingCartsInterface {
    FragmentCheckoutBinding binding;
    AuthService authService;
    DatabaseService databaseService;
//    Show product in recycle view
    ShoppingCartsAdapter cartsAdapter;
    ArrayList<ShoppingCartsProductModel> models;
    String uid;
    String deliveryFee = "free";
    private boolean CouponValueIsApply = false;
    private String CouponCode;
    private double Total =  00.0;
    private double grandTotal = 00.0;
    private static final String RupeeSymbols = "₹";
    private double newCouponValue;
    private double couponMin;
    public CheckoutFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCheckoutBinding.inflate(inflater,container,false);
        authService = new AuthService();
        databaseService = new DatabaseService();
        uid = authService.getUserId();
        models = new ArrayList<>();
        cartsAdapter = new ShoppingCartsAdapter(models, this, requireContext());
        binding.CardView.setAdapter(cartsAdapter);
        binding.CardView.setLayoutManager(new LinearLayoutManager(requireContext()));

        binding.couponCodeApplyBtn.setOnClickListener(view->{
            if (!CouponValueIsApply) {
                CouponCode = binding.couponCode.getEditText().getText().toString();
                FirebaseDatabase.getInstance().getReference().child("admin").child("Coupon").child(CouponCode).addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // Coupon code exists, retrieve discount amount and min value
                            String couponDiscountAmount = snapshot.child("coupon_discount_amount").getValue(String.class);
                            String couponMinValue = snapshot.child("couponMinvalue").getValue(String.class);
                            assert couponMinValue != null;
                            couponMin = Double.parseDouble(couponMinValue);
                            assert couponDiscountAmount != null;
                            if (Total >= couponMin) {
                                CouponValueIsApply = true;
                                newCouponValue = Double.parseDouble(couponDiscountAmount); // Example coupon value
                                binding.couponCodeApplyBtn.setText("remove");
                                binding.discount.setText(RupeeSymbols+newCouponValue);
                                binding.couponCode.setEnabled(!CouponValueIsApply);
                                updateSubTotalPrice();
                            }
                            else {
                                binding.couponCode.setError("Minimum subtotal value required to apply the coupon is "+RupeeSymbols + couponMinValue);
                            }

                        } else {
                            // Coupon code does not exist
                            binding.couponCode.setError(CouponCode + " not valid");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle database error
                        binding.couponCode.setError(CouponCode + " not valid");
                        CouponValueIsApply = false;
                        newCouponValue = 00.0; // No coupon applied
                        binding.couponCodeApplyBtn.setText("Apply");
                        binding.discount.setText(""+newCouponValue);
                        binding.couponCode.setEnabled(!CouponValueIsApply);
                    }
                });
            }
            else {
                newCouponValue = 0; // No coupon applied
                updateSubTotalPrice();
                CouponValueIsApply = false;
                binding.discount.setText(""+newCouponValue);
                binding.couponCodeApplyBtn.setText("Apply");
                binding.couponCode.setEnabled(!CouponValueIsApply);

            }
        });




        binding.ShippingPrice.setText(deliveryFee);

        Bundle arguments = getArguments();
        if (arguments != null) {
            String couponPrice = arguments.getString("couponPrice");
            String couponTotal = arguments.getString("couponTotal");
            boolean isValidCoupon = arguments.getBoolean("isValidCoupon");

            // Use the coupon details as needed
            if (isValidCoupon) {
                Log.d("isValidCouponTAG", "onCreateView: "+ couponPrice);
                Log.d("isValidCouponTAG", "couponTotal: "+ couponTotal);
                Log.d("isValidCouponTAG", "isValidCoupon: "+ isValidCoupon);
                // Coupon is valid, do something
            } else {
                // Coupon is not valid, do something else
                Log.d("isValidCouponTAGelse", "onCreateView: "+ couponPrice);
                Log.d("isValidCouponTAGelse", "couponTotal: "+ couponTotal);
                Log.d("isValidCouponTAGelse", "isValidCoupon: "+ isValidCoupon);
            }
        }


        loadProductFromCart();

        return binding.getRoot();
    }




//    private void OpenCouponCode() {
//        // Check if CheckoutFragment is already added to the back stack
//        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
//        CheckoutFragment checkoutFragment = (CheckoutFragment) fragmentManager.findFragmentByTag("CheckoutFragment");
//
//        if (checkoutFragment != null) {
//            // CheckoutFragment already exists, show it
//            fragmentManager.beginTransaction().show(checkoutFragment).commit();
//        } else {
//            // CheckoutFragment does not exist, create and add it
//            checkoutFragment = new CheckoutFragment();
//            fragmentManager.beginTransaction()
//                    .replace(R.id.fragment_container, checkoutFragment, "CheckoutFragment")
//                    .addToBackStack(null)
//                    .commit();
//        }
//    }

    private void loadProductFromCart() {
        databaseService.getUserCartById(authService.getUserId(), new DatabaseService.GetUserCartByIdCallback() {
            @Override
            public void onSuccess(ArrayList<ShoppingCartsProductModel> cartsProductModels) {
                models.clear();
                models.addAll(cartsProductModels);
                cartsAdapter.notifyDataSetChanged();
                updateSubTotalPrice();
                binding.progressCircular.setVisibility(View.GONE);
            }

            @Override
            public void onError(String errorMessage) {
                // Handle error here
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void updateSubTotalPrice() {
        Total = ShoppingCartHelper.calculateTotalPrices(models);
        binding.SubTotalPrice.setText("₹" + Total);
        grandTotal = Total - newCouponValue;
        binding.TotalPrice.setText("₹" + grandTotal);
    }
    private void LoadRecommendations(){

    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void remove(int pos,String id,ShoppingCartsProductModel cartsProductModel) {
        RemoveBottomSheetDialogFragment bottomSheet = new RemoveBottomSheetDialogFragment(uid,id,cartsAdapter,cartsProductModel);
        bottomSheet.show(requireActivity().getSupportFragmentManager(), bottomSheet.getTag());
        updateSubTotalPrice();
        if (Total < couponMin) {
            // Total is less than the minimum required value, remove the coupon
            CouponValueIsApply = false;
            newCouponValue = 00.0;
            binding.couponCodeApplyBtn.setText("Apply");
            binding.couponCode.setEnabled(true);
            binding.discount.setText(RupeeSymbols + newCouponValue);
            updateSubTotalPrice();
        }
    }

    @Override
    public void UpdateQuantity(ShoppingCartsProductModel UpdateModel, String id) {
        binding.progressCircular.setVisibility(View.VISIBLE);
        databaseService.UpdateCartQuantityById(uid,id,UpdateModel.getDefaultQuantity());
        binding.progressCircular.setVisibility(View.GONE);
        updateSubTotalPrice();

        // Check if the total becomes less than the minimum required value after updating quantity
        if (Total < couponMin) {
            // Total is less than the minimum required value, remove the coupon
            CouponValueIsApply = false;
            newCouponValue = 00.0;
            binding.couponCodeApplyBtn.setText("Apply");
            binding.couponCode.setEnabled(true);
            binding.discount.setText(RupeeSymbols + newCouponValue);
            updateSubTotalPrice();
        }
    }

}