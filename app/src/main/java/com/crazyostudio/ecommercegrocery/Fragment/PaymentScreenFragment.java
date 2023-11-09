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

import com.crazyostudio.ecommercegrocery.Activity.OrderDetailsActivity;
import com.crazyostudio.ecommercegrocery.Model.AddressModel;
import com.crazyostudio.ecommercegrocery.Model.OrderModel;
import com.crazyostudio.ecommercegrocery.Model.ProductModel;
import com.crazyostudio.ecommercegrocery.Model.UserinfoModels;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.databinding.FragmentPaymentScreenBinding;
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

public class PaymentScreenFragment extends Fragment {
    private FragmentPaymentScreenBinding binding;
    private AddressModel adders;
    private UserinfoModels userinfoModels;
    private String PaymentMode;
    private String addersMode;
    private String id;
    private String BuyType;
    Cart cart;
    private FirebaseDatabase firebaseDatabase;
    boolean isShippingCost = false;
    private double Subtotal;
    private double ShippingFee;
    private double total;
    private ProductModel productModel;
    BigDecimal getTotalPrice;
    public PaymentScreenFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            BuyType = getArguments().getString("BuyType");
            id = getArguments().getString("id");
            adders = getArguments().getParcelable("adders");
            userinfoModels = getArguments().getParcelable("user");
            if (BuyType.equals("Now")) {
                productModel = getArguments().getParcelable("productModel");
            }
        }else {
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
        getTotalPrice = BigDecimal.ZERO;
        if (BuyType.equals("Now")) {
            getTotalPrice = BigDecimal.valueOf(productModel.getPrice() * productModel.getSelectProductQuantity());
        }else if (BuyType.equals("Cart"))
        {
            cart = TinyCartHelper.getCart();
            getTotalPrice = cart.getTotalPrice();
        }

        Subtotal = Double.parseDouble(getTotalPrice.toString());
        BigDecimal threshold = new BigDecimal("150.0"); // Define the threshold as a BigDecimal
        int comparisonResult = getTotalPrice.compareTo(threshold);
        if (comparisonResult < 0) {
            isShippingCost = true;
            binding.radiaId1.setText("₹50 Standard delivery if your buy more than 150 free delivery");
            binding.SubTotalPrice.setText("₹"+getTotalPrice);
            binding.ShippingPrice.setText("₹50");
            ShippingFee = 50;
            addersMode = "Standard delivery";
            total = Double.parseDouble(getTotalPrice.toString())+ShippingFee;
            binding.TotalPrice.setText("₹"+total);
            // totalPrice is less than 150
            // Add your logic here
        } else {
            binding.SubTotalPrice.setText("₹"+getTotalPrice);
            binding.ShippingPrice.setText("0");
            ShippingFee = 0;
            addersMode = "Standard delivery";
            total = Double.parseDouble(getTotalPrice.toString());
            binding.TotalPrice.setText("₹"+total);

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
                    binding.SubTotalPrice.setText("₹"+getTotalPrice);
                    binding.ShippingPrice.setText("₹50");
                    ShippingFee = 50;
                    addersMode = "Standard delivery";
                    total = Double.parseDouble(String.valueOf(getTotalPrice))+ShippingFee;
                    binding.TotalPrice.setText("₹"+total);

                }else {
                    binding.SubTotalPrice.setText("₹"+getTotalPrice);
                    binding.ShippingPrice.setText("₹0");
                    ShippingFee = 0;
                    addersMode = "Standard delivery";
                    total = Double.parseDouble(getTotalPrice.toString());
                    binding.TotalPrice.setText("₹"+total);
                }
            }
            else {
                binding.SubTotalPrice.setText("₹"+getTotalPrice);
                binding.ShippingPrice.setText("₹60");
                addersMode = " Fast delivery";
                ShippingFee = 60;
                total = Double.parseDouble(String.valueOf(getTotalPrice))+60;
                binding.TotalPrice.setText("₹"+total);
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
            }
            else {
                Toast.makeText(requireContext(), "Select a Payment Method", Toast.LENGTH_SHORT).show();
            }
        });
        return binding.getRoot();
    }

    private void PlaceOrder(String PaymentMode, UserinfoModels userinfoModels, long time) {
        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle("We are placing Order ");
        progressDialog.show();
        String orderId = time + FirebaseAuth.getInstance().getUid();
        ArrayList<ProductModel> models = new ArrayList<>();
        if (BuyType.equals("Cart")) {
            firebaseDatabase.getReference().child("Cart").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                BigDecimal save = BigDecimal.ZERO;
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    ProductModel productModel = snapshot1.getValue(ProductModel.class);
                    if (productModel != null) {
                        models.add(productModel);
                        save = save.add(BigDecimal.valueOf(productModel.getMRP()).multiply(BigDecimal.valueOf(productModel.getSelectProductQuantity())));

                        if (models.size()==cart.getAllItemsWithQty().size()) {
                            double userSave = Double.parseDouble(String.valueOf(save)) - Subtotal;

                            OrderModel orderModel = new OrderModel(adders.getName(),orderId,addersMode,models,"in padding",adders.getAddress(),adders.getPhone(),Subtotal,ShippingFee,total,"Padding",PaymentMode,time,FirebaseAuth.getInstance().getUid(),0,userSave);
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
        else if (BuyType.equals("Now")){
            models.add(productModel);
            BigDecimal save = BigDecimal.ZERO;
            save = save.add(BigDecimal.valueOf(productModel.getMRP()).multiply(BigDecimal.valueOf(productModel.getSelectProductQuantity())));
             double userSave = Double.parseDouble(String.valueOf(save)) - Subtotal;
                OrderModel orderModel = new OrderModel(
                        adders.getName(),orderId,
                        addersMode,models,"in padding",adders.getAddress(),
                        adders.getPhone(),Subtotal,ShippingFee,total,"Padding",PaymentMode,time,FirebaseAuth.getInstance().getUid(),0,userSave);
                firebaseDatabase.getReference().child("Order").child(FirebaseAuth.getInstance().getUid()).child(orderId).setValue(orderModel).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        Intent i = new Intent(requireContext(), OrderDetailsActivity.class);
                        i.putExtra("Type","placeOrder");
                        i.putExtra("userModel",userinfoModels);
                        i.putExtra("orderModel",orderModel);
                        requireActivity().finish();
                        startActivity(i);
                    }
                }).addOnFailureListener(e -> {
                    if (progressDialog.isShowing())progressDialog.dismiss();
                    basicFun.AlertDialog(requireContext(),e.toString());
                });
        }

    }

}