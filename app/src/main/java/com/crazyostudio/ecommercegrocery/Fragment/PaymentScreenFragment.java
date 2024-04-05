package com.crazyostudio.ecommercegrocery.Fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.crazyostudio.ecommercegrocery.Activity.OrderDetailsActivity;
import com.crazyostudio.ecommercegrocery.Adapter.OrderProductAdapter;
import com.crazyostudio.ecommercegrocery.Model.AddressModel;
import com.crazyostudio.ecommercegrocery.Model.OrderModel;
import com.crazyostudio.ecommercegrocery.Model.ProductModel;
import com.crazyostudio.ecommercegrocery.Model.UserinfoModels;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.Services.AuthService;
import com.crazyostudio.ecommercegrocery.databinding.FragmentPaymentScreenBinding;
import com.crazyostudio.ecommercegrocery.interfaceClass.OrderProductInterface;
import com.crazyostudio.ecommercegrocery.javaClasses.basicFun;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.util.TinyCartHelper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Objects;

public class PaymentScreenFragment extends Fragment implements OrderProductInterface {
    private FragmentPaymentScreenBinding binding;
    private AddressModel adders;
    private UserinfoModels userinfoModels;
    private String PaymentMode;
    private String addersMode;
//    private String id;
    private String CouponCode;
    Cart cart;
    private FirebaseDatabase firebaseDatabase;
    boolean isShippingCost = false;
    private double Subtotal;
    private double ShippingFee;
    private double total;
    private ProductModel productModel;
    BigDecimal getTotalPrice;
    double newCouponValue = 0; // Variable to hold the applied coupon value
    private boolean CouponValueIsApply = false;
    ArrayList<ProductModel> ShowProductModels;


    private static final String RupeeSymbols = "₹";
    private static final int STANDARD = 30;
    private static final int FAST_DELIVERY = 50;

    public PaymentScreenFragment(){}

    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            id = getArguments().getString("id");
            adders = getArguments().getParcelable("adders");
            userinfoModels = getArguments().getParcelable("user");
        }
        else {
            requireActivity().onBackPressed();
        }


    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPaymentScreenBinding.inflate(inflater,container,false);
        PaymentMode = "";
        addersMode = "";
        firebaseDatabase = FirebaseDatabase.getInstance();
        ShowProductModels = new ArrayList<>();
        binding.progressCircular.setVisibility(View.VISIBLE);
        getProduct();
        getTotalPrice = BigDecimal.ZERO;

        cart = TinyCartHelper.getCart();
        getTotalPrice = cart.getTotalPrice();


        binding.couponCodeApplyBtn.setOnClickListener(view->{
            if (!CouponValueIsApply) {
                CouponCode = binding.couponCode.getEditText().getText().toString();
                firebaseDatabase.getReference().child("admin").child("Coupon").child(CouponCode).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // Coupon code exists, retrieve discount amount and min value
                            String couponDiscountAmount = snapshot.child("coupon_discount_amount").getValue(String.class);
                            String couponMinValue = snapshot.child("couponMinvalue").getValue(String.class);
                            assert couponMinValue != null;
                            double couponMin = Double.parseDouble(couponMinValue);
                            assert couponDiscountAmount != null;
                            if (Subtotal >= couponMin) {
                                // Subtotal is not less than couponMinValue
                                // Proceed with your logic here
                                CouponValueIsApply = true;
                                newCouponValue = Double.parseDouble(couponDiscountAmount); // Example coupon value
                                binding.couponCodeApplyBtn.setText("remove");
                                binding.discount.setText(""+newCouponValue);
                                binding.couponCode.setEnabled(!CouponValueIsApply);
                                total = total - newCouponValue;
                                binding.TotalPrice.setText(RupeeSymbols + total);
                            } else {
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
                        newCouponValue = 00; // No coupon applied
                        binding.couponCodeApplyBtn.setText("Apply");
                        binding.discount.setText(""+newCouponValue);
                        binding.couponCode.setEnabled(!CouponValueIsApply);
                    }
                });
            }
            else {
                total = total + newCouponValue;
                binding.TotalPrice.setText(RupeeSymbols + total);
                newCouponValue = 0; // No coupon applied
                CouponValueIsApply = false;
                binding.discount.setText(""+newCouponValue);
                binding.couponCodeApplyBtn.setText("Apply");
                binding.couponCode.setEnabled(!CouponValueIsApply);
            }
        });
        Subtotal = Double.parseDouble(getTotalPrice.toString());
        BigDecimal threshold = new BigDecimal("150.0"); // Define the threshold as a BigDecimal
        int comparisonResult = getTotalPrice.compareTo(threshold);
        if (comparisonResult < 0) {
            isShippingCost = true;
            binding.radiaId1.setText(RupeeSymbols+"50 Standard delivery if your buy more than 150 free delivery");
            binding.SubTotalPrice.setText(RupeeSymbols+getTotalPrice);
            binding.ShippingPrice.setText(RupeeSymbols + "50");
            ShippingFee = 50;
            addersMode = "Standard delivery";
            total = Double.parseDouble(getTotalPrice.toString())+ShippingFee-newCouponValue;
            binding.TotalPrice.setText(RupeeSymbols+total);
            // totalPrice is less than 150
            // Add your logic here
        } else {
            binding.SubTotalPrice.setText(RupeeSymbols+getTotalPrice);
            binding.ShippingPrice.setText("0");
            ShippingFee = 0;
            addersMode = "Standard delivery";
            total = Double.parseDouble(getTotalPrice.toString())-newCouponValue;

            binding.TotalPrice.setText(RupeeSymbols+total);

        }
        binding.payment.setOnCheckedChangeListener((radioGroup, i) -> {
            RadioButton radioButton = requireActivity().findViewById(i);
            if (radioButton.getId()==R.id.COD) {
                PaymentMode = "COD";
            }
            else if (radioButton.getId()==R.id.OnlinePay){
                PaymentMode = "Online";
            }
        });
        binding.orderDetailsViewBack.setOnClickListener(back->requireActivity().onBackPressed());
        binding.Options.setOnCheckedChangeListener((radioGroup, i) -> {
            RadioButton radioButton = requireActivity().findViewById(i);
            if (radioButton.getId()==R.id.radia_id1){
                if (isShippingCost) {
                    binding.SubTotalPrice.setText(RupeeSymbols+getTotalPrice);
                    binding.ShippingPrice.setText(RupeeSymbols+"50");
                    ShippingFee = 50;
                    addersMode = "Standard delivery";
                    total = Double.parseDouble(String.valueOf(getTotalPrice))+ShippingFee-newCouponValue;
                    binding.TotalPrice.setText(RupeeSymbols+total);

                }else {
                    binding.SubTotalPrice.setText(RupeeSymbols+getTotalPrice);
                    binding.ShippingPrice.setText(RupeeSymbols+"0");
                    ShippingFee = 0;
                    addersMode = "Standard delivery";
                    total = Double.parseDouble(getTotalPrice.toString())-newCouponValue;
                    binding.TotalPrice.setText(RupeeSymbols+total);
                }
            }
            else {
                binding.SubTotalPrice.setText(RupeeSymbols+getTotalPrice);
                binding.ShippingPrice.setText(RupeeSymbols+"60");
                addersMode = " Fast delivery";
                ShippingFee = 60;
                total = Double.parseDouble(String.valueOf(getTotalPrice))+ShippingFee - newCouponValue;
                binding.TotalPrice.setText(RupeeSymbols+total);
            }
        });
        binding.Change.setOnClickListener(Change-> requireActivity().onBackPressed());
        binding.orderShippingAddress.setText(adders.getAddress());
        binding.orderContactName.setText(adders.getName());
        binding.orderContactPhone.setText(adders.getPhone());
        binding.orderContactEmail.setText(userinfoModels.getEmailAddress());
        binding.orderDetailsPayBtn.setOnClickListener(view -> {
            long time = System.currentTimeMillis();
//            binding.payment.getslec
            if (PaymentMode.equals("COD")){

                PlaceOrder(PaymentMode,userinfoModels,time);

            }
            else if (PaymentMode.equals("Online")){
//                Write a Code for Hindering Online Payments
                Toast.makeText(requireContext(), "Sorry we are not hindering Online payment now ", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(requireContext(), "Select a Payment Method", Toast.LENGTH_SHORT).show();
            }
        });
        return binding.getRoot();
    }

    private void getProduct() {
        firebaseDatabase.getReference().child("Cart").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    ProductModel productModel = snapshot1.getValue(ProductModel.class);
                    if (productModel != null) {
                        ShowProductModels.add(productModel);

                    }
                }
                ShowProduct();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void PlaceOrder(String PaymentMode, UserinfoModels userinfoModels, long time) {
        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle("We are placing Order ");
        progressDialog.show();
        String orderId = time + FirebaseAuth.getInstance().getUid();
        ArrayList<ProductModel> models = new ArrayList<>();

            firebaseDatabase.getReference().child("Cart").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).addValueEventListener(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    BigDecimal save = BigDecimal.ZERO;
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        ProductModel productModel = snapshot1.getValue(ProductModel.class);
                        if (productModel != null) {
                            models.add(productModel);
                            save = save.add(BigDecimal.valueOf(productModel.getMrp()).multiply(BigDecimal.valueOf(productModel.getDefaultQuantity())));

                            if (models.size()==cart.getAllItemsWithQty().size()) {
                                double userSave = Double.parseDouble(String.valueOf(save)) - Subtotal;
                                String email = new AuthService().getUserEmail();
                                if (email.isEmpty()) {
                                    email = "Email is found or Valid";
                                }

                                OrderModel orderModel = new OrderModel(adders.getName(),orderId,addersMode,models,"shipped",adders.getAddress(),adders.getPhone(),Subtotal,ShippingFee,total,"Padding",PaymentMode,time,FirebaseAuth.getInstance().getUid(),userinfoModels.getToken(),0,userSave,email);
                                firebaseDatabase.getReference().child("Order").child(FirebaseAuth.getInstance().getUid()).child(orderId).setValue(orderModel).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        if (progressDialog.isShowing()) {
                                            progressDialog.dismiss();
                                        }
                                        cart.clearCart();
                                        Intent i = new Intent(requireContext(), OrderDetailsActivity.class);
                                        i.putExtra("Type","placeOrder");
                                        i.putExtra("userModel",userinfoModels);
                                        i.putExtra("orderModel",orderModel);
                                        requireActivity().finish();
                                        startActivity(i);
                                    }
                                }).addOnFailureListener(e -> {
                                    if (progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }
                                    basicFun.AlertDialog(requireContext(),e.toString());
                                });
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    basicFun.AlertDialog(requireContext(),error.toString());
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
            });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void ShowProduct(){
        OrderProductAdapter orderProductAdapter = new OrderProductAdapter(ShowProductModels,requireContext(),this);
        binding.ProductView.setAdapter(orderProductAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false);
        binding.ProductView.setLayoutManager(layoutManager);
        orderProductAdapter.notifyDataSetChanged();
        binding.progressCircular.setVisibility(View.GONE);
    }

    @Override
    public void onOrder(ProductModel model) {
    }
}