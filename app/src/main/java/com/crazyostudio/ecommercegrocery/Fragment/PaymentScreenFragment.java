package com.crazyostudio.ecommercegrocery.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.crazyostudio.ecommercegrocery.Model.ProductModel;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.databinding.FragmentPaymentScreenBinding;
import com.google.firebase.database.FirebaseDatabase;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.util.TinyCartHelper;

import java.math.BigDecimal;
import java.util.ArrayList;

public class PaymentScreenFragment extends Fragment {
    FragmentPaymentScreenBinding binding;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private String adders;
    Cart cart;
    ArrayList<ProductModel> models;
    FirebaseDatabase firebaseDatabase;
    boolean isShippingCost = false;
    public PaymentScreenFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            adders = getArguments().getString("adders");
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
        firebaseDatabase = FirebaseDatabase.getInstance();
        cart = TinyCartHelper.getCart();

        BigDecimal totalPrice = cart.getTotalPrice();
        BigDecimal threshold = new BigDecimal("150.0"); // Define the threshold as a BigDecimal

        int comparisonResult = totalPrice.compareTo(threshold);

        if (comparisonResult < 0) {
            isShippingCost = true;
            binding.radiaId1.setText("₹50 Standard delivery if your buy more than 150 free delivery");
            binding.SubTotalPrice.setText("₹"+cart.getTotalPrice());
            binding.ShippingPrice.setText("₹50");
            double Total = Double.parseDouble(cart.getTotalPrice().toString())+50;
            binding.TotalPrice.setText("₹"+Total);
            // totalPrice is less than 150
            // Add your logic here
        } else {
            binding.SubTotalPrice.setText("₹"+cart.getTotalPrice());
            binding.ShippingPrice.setText("0");
            double Total = Double.parseDouble(cart.getTotalPrice().toString());
            binding.TotalPrice.setText("₹"+Total);

        }




        binding.Options.setOnCheckedChangeListener((radioGroup, i) -> {
            RadioButton radioButton = requireActivity().findViewById(i);
            if (radioButton.getId()==R.id.radia_id1){
                if (isShippingCost) {
                    binding.SubTotalPrice.setText("₹"+cart.getTotalPrice());
                    binding.ShippingPrice.setText("₹50");
                    double Total = Double.parseDouble(cart.getTotalPrice().toString())+50;
                    binding.TotalPrice.setText("₹"+Total);

                }else {
                    binding.SubTotalPrice.setText("₹"+cart.getTotalPrice());
                    binding.ShippingPrice.setText("₹0");
                    double Total = Double.parseDouble(cart.getTotalPrice().toString());
                    binding.TotalPrice.setText("₹"+Total);

                }
            }
            else {
                binding.SubTotalPrice.setText("₹"+cart.getTotalPrice());
                binding.ShippingPrice.setText("₹60");
                double Total = Double.parseDouble(cart.getTotalPrice().toString())+60;
                binding.TotalPrice.setText("₹"+Total);

            }
        });


        binding.Change.setOnClickListener(Change-> requireActivity().onBackPressed());
        binding.DeliveryAddress.setText(adders);
//        binding.COD.setOnClickListener(COD->{
//
//        });

        binding.makePayment.setOnClickListener(view -> {
//            binding.payment.getslec

            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            Fragment fragment = new PlaceOderFragment();
            Bundle bundle = new Bundle();
            bundle.putString("adders",adders);

            bundle.putString("PaymentMode","COD");
            fragment.setArguments(bundle);
            transaction.replace(R.id.fragment_container,fragment,"PlaceOderFragment");
            transaction.addToBackStack("PlaceOderFragment");
            transaction.commit();

        });



        return binding.getRoot();
    }
}