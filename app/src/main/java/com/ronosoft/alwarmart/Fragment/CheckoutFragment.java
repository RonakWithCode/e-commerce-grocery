package com.ronosoft.alwarmart.Fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.ronosoft.alwarmart.Adapter.ShoppingCartsAdapter;
import com.ronosoft.alwarmart.Dialog.CustomErrorDialog;
import com.ronosoft.alwarmart.HelperClass.ShoppingCartHelper;
import com.ronosoft.alwarmart.HelperClass.ValuesHelper;
import com.ronosoft.alwarmart.Model.AddressModel;
import com.ronosoft.alwarmart.Model.CouponModel;
import com.ronosoft.alwarmart.Model.Customer;
import com.ronosoft.alwarmart.Model.OrderModel;
import com.ronosoft.alwarmart.Model.Payment;
import com.ronosoft.alwarmart.Model.Shipping;
import com.ronosoft.alwarmart.Model.ShoppingCartsProductModel;
import com.ronosoft.alwarmart.R;
import com.ronosoft.alwarmart.Services.AuthService;
import com.ronosoft.alwarmart.Services.DatabaseService;
import com.ronosoft.alwarmart.databinding.FragmentCheckoutBinding;
import com.ronosoft.alwarmart.interfaceClass.ShoppingCartsInterface;
import com.ronosoft.alwarmart.javaClasses.TokenManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.Random;


public class CheckoutFragment extends Fragment implements ShoppingCartsInterface {
    FragmentCheckoutBinding binding;
    AuthService authService;
    DatabaseService databaseService;
    ShoppingCartsAdapter cartsAdapter;
    ArrayList<ShoppingCartsProductModel> models;
    String uid;
    AddressModel addressModel;
    NavController navController;

    private double subTotalPrice = 0; // sub total price of all items in order
    private double shippingFee = 0; // shipping fee of customer
    private double processingFee = 0; // processing fee of Platform
    private double donate = 0; // if customer donate money to Charity
    private double saveAmount = 0; // it a save amount by customer this is calculated by all product mrp - grandTotal
    private double grandTotal = 0; // the all total price of order

    private double couponValue = 0;
    String couponCode = "";
    private double minOderValue = 0;
    private boolean couponIsApply = false;
    private final String TAG = "CheckoutFragment";

    public CheckoutFragment() {}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            number = getArguments().getString("number");
            addressModel = getArguments().getParcelable("adders");

        }
    }





    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCheckoutBinding.inflate(inflater,container,false);
        navController = Navigation.findNavController(requireActivity(),R.id.oder_host_fragment);
        authService = new AuthService();
        databaseService = new DatabaseService();
        uid = authService.getUserId();
        binding.shimmerLayout.startShimmer();
        models = new ArrayList<>();
        cartsAdapter = new ShoppingCartsAdapter(models, this, requireContext());
        binding.CardView.setAdapter(cartsAdapter);
        binding.CardView.setLayoutManager(new LinearLayoutManager(requireContext()));
//        binding.orderDetailsViewBack.setOnClickListener(click->navController.popBackStack());

        binding.orderDetailsViewBack.setOnClickListener(view->{
            navController.popBackStack();

        });

        initAddress();

        loadProductFromCart();

//
        binding.couponCodeApplyBtn.setOnClickListener(view -> {
            if (couponIsApply) {
                // Remove coupon
                removeCoupon();
            } else {
                // Apply coupon
                String _couponCode = Objects.requireNonNull(binding.couponCode.getEditText()).getText().toString();
                if (_couponCode.isEmpty()) {
                    binding.couponCode.setError("Please Enter Coupon Code");
                } else {
                    ApplyCouponCode(_couponCode);
                }
            }
        });


        binding.placeBtn.setOnClickListener(view -> {
//            TODO

            final Dialog dialog = new Dialog(requireContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.bottom_sheet_payment_options);


            RadioButton codRadioButton = dialog.findViewById(R.id.codRadioButton);
            ImageView closeButton = dialog.findViewById(R.id.closeButton);



            codRadioButton.setOnClickListener(v -> placeOrder( "Standard", ValuesHelper.PROCESSING));
            closeButton.setOnClickListener(v -> dialog.dismiss());

            dialog.show();
            dialog.setCancelable(true);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().getAttributes().windowAnimations = R.style.Animationboy;
            dialog.getWindow().setGravity(Gravity.BOTTOM);






        });
//

        return binding.getRoot();
    }





    private void placeOrder(String shippingMethod, String shippingStatus) {
        Date orderDate = new Date();
        Log.d(TAG, "placeOrder: " + orderDate);

        Shipping shipping = new Shipping(shippingMethod, "00", new Date(), addressModel, shippingStatus);
        Payment payment = new Payment("cash", "Pending");
        String userID = authService.getUserId();

        // Get phone number asynchronously before creating the order
        authService.getUserPhoneNumber()
            .addOnSuccessListener(phoneNumber -> {
                // Create customer with retrieved phone number
                Customer customer = new Customer(
                    userID, 
                    addressModel.getFullName(), 
                    addressModel.getMobileNumber(), 
                    phoneNumber
                );

                String orderId = generateUniqueOrderId();

                OrderModel orderModel = new OrderModel(
                    orderId,
                    customer,
                    models,
                    grandTotal,
                    couponCode,
                    shippingStatus,
                    payment,
                    shipping,
                    orderDate,
                    binding.note.getEditText().getText().toString(),
                    TokenManager.getInstance(requireContext()).getToken(),
                    couponValue,
                    donate,
                    processingFee
                );

                placeOrder(orderModel);
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error getting phone number", e);
                // Show error to user
                Toast.makeText(requireContext(), 
                    "Error: Unable to get phone number", 
                    Toast.LENGTH_SHORT
                ).show();
            });
    }


    private static String generateUniqueOrderId() {
        Random random = new Random();
        int orderId = 100000 + random.nextInt(900000); // Generates a random number between 100000 and 999999
        return String.valueOf(orderId);
    }

    private void removeCoupon() {
        couponIsApply = false;
        couponCode = "";
        couponValue = 0;
        minOderValue = 0;

        // Clear the coupon input
        Objects.requireNonNull(binding.couponCode.getEditText()).setText("");
        binding.couponCode.setError(null);
        binding.couponCode.setHelperText(null);

        // Update UI
        updateCouponButtonState();

        // Recalculate values
        CalculateValue();
    }

    private void updateCouponButtonState() {
        if (couponIsApply) {
            // Change to remove state
            binding.couponCodeApplyBtn.setText("Remove");
            binding.couponCodeApplyBtn.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), R.color.greyColor)));

            // Disable editing of coupon input
            Objects.requireNonNull(binding.couponCode.getEditText()).setEnabled(false);
        } else {
            // Change to apply state
            binding.couponCodeApplyBtn.setText("Apply");
            binding.couponCodeApplyBtn.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), R.color.green_primary)));

            // Enable editing of coupon input
            Objects.requireNonNull(binding.couponCode.getEditText()).setEnabled(true);
        }
    }

    private void ApplyCouponCode(String couponCode) {
        binding.progressCircular.setVisibility(View.VISIBLE);
        binding.couponCode.setError(null); // Clear any previous errors

        FirebaseDatabase.getInstance().getReference()
                .child("admin")
                .child("Coupon")
                .child(couponCode)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        binding.progressCircular.setVisibility(View.GONE);

                        if (!snapshot.exists()) {
                            binding.couponCode.setError("Invalid coupon code");
                            return;
                        }

                        CouponModel couponModel = snapshot.getValue(CouponModel.class);
                        if (couponModel == null) {
                            binding.couponCode.setError("Error loading coupon details");
                            return;
                        }

                        // Validate coupon
                        if (!couponModel.isActive()) {
                            binding.couponCode.setError("This coupon has expired");
                            return;
                        }

                        if (couponModel.getTotalUse() <= 0) {
                            binding.couponCode.setError("This coupon has reached its usage limit");
                            return;
                        }

                        if (subTotalPrice < couponModel.getMinOderValue()) {
                            binding.couponCode.setError("Minimum order value of ₹" + couponModel.getMinOderValue() + " required");
                            return;
                        }

                        // Apply valid coupon
                        couponIsApply = true;
                        CheckoutFragment.this.couponCode = couponModel.getCouponCode();
                        couponValue = couponModel.getCouponValue();
                        minOderValue = couponModel.getMinOderValue();

                        // Recalculate totals
                        CalculateValue();

                        // Show success message
                        binding.couponCode.setHelperText("Coupon applied successfully!");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        binding.progressCircular.setVisibility(View.GONE);
                        binding.couponCode.setError("Error validating coupon");
                    }
                });
    }


    @SuppressLint("SetTextI18n")
    private void initAddress(){
        String type;
        if (addressModel.isHomeSelected()) {
            type = "Home";
            Glide.with(requireContext()).load(R.drawable.home_shipping).into(binding.AddressType);
        }else {
            type = "Work";
            Glide.with(requireContext()).load(R.drawable.office_building).into(binding.AddressType);
        }
        binding.deliveryTo.setText("Delivering to "+type);
        binding.deliveryAddress.setText(addressModel.getFullName()+"\n" +addressModel.getMobileNumber()+"\n" +addressModel.getFlatHouse()+addressModel.getAddress());

        binding.Change.setOnClickListener(view-> navController.popBackStack());
    }


    private void loadProductFromCart() {
        binding.progressCircular.setVisibility(View.GONE);
        databaseService.getUserCartById(authService.getUserId(), new DatabaseService.GetUserCartByIdCallback() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(ArrayList<ShoppingCartsProductModel> cartsProductModels) {
                models.clear();
                models.addAll(cartsProductModels);
                cartsAdapter.notifyDataSetChanged();
                binding.shimmerLayout.stopShimmer();
                binding.shimmerLayout.setVisibility(View.GONE);
                binding.ScrollView.setVisibility(View.VISIBLE);
                binding.relativeLayout.setVisibility(View.VISIBLE);
                setupLayout();
//                updateSubTotalPrice();
//                LoadRecommendations();
            }

            @Override
            public void onError(String errorMessage) {
                // Handle error here
            }
        });
    }

    private void setupLayout() {
//        setupPricing();

        CalculateValue();

    }

    private void CalculateValue() {
        // Calculate subtotal
        subTotalPrice = ShoppingCartHelper.calculateTotalPrices(models);

        // Calculate fees
        if (subTotalPrice < ValuesHelper.MIN_TOTAL_PRICE_FOR_DELIVERY) {
            processingFee = ValuesHelper.processingFee;
            shippingFee = ValuesHelper.DeliveryFees;
        } else {
            processingFee = ValuesHelper.processingFee;
            shippingFee = ValuesHelper.StandardDeliveryFees;
        }
        donate = ValuesHelper.MinDonate;

        // Apply coupon if valid
        if (couponIsApply && subTotalPrice >= minOderValue) {
            grandTotal = subTotalPrice + processingFee + shippingFee + donate - couponValue;
        } else {
            // Reset coupon if minimum order value not met
            if (couponIsApply) {
                couponIsApply = false;
                couponCode = "";
                couponValue = 0;
                binding.couponCode.setError("Minimum order value not met for this coupon");
            }
            grandTotal = subTotalPrice + processingFee + shippingFee + donate;
        }
        updateCouponButtonState();
        setupPricing();
    }

    private void setupPricing() {
        binding.SubTotalPrice.setText(ValuesHelper.RupeeSymbols + subTotalPrice);
        binding.processFee.setText(ValuesHelper.RupeeSymbols + processingFee);
        binding.discount.setText(ValuesHelper.RupeeSymbols + couponValue);
        binding.ShippingPrice.setText(ValuesHelper.RupeeSymbols + shippingFee);
        binding.TotalPrice.setText(ValuesHelper.RupeeSymbols + grandTotal);
        if (couponIsApply){
            Objects.requireNonNull(binding.couponCode.getEditText()).getText().toString();
        }
        binding.progressCircular.setVisibility(View.GONE);


    }

    @Override
    public void remove(int pos,String id,ShoppingCartsProductModel cartsProductModel) {
        if (requireActivity().getSupportFragmentManager().findFragmentByTag("bottom_sheet_fragment") == null) {
            RemoveBottomSheetDialogFragment bottomSheet = new RemoveBottomSheetDialogFragment(uid,id,cartsAdapter,cartsProductModel);

            bottomSheet.show(requireActivity().getSupportFragmentManager(), "bottom_sheet_fragment");
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void UpdateQuantity(ShoppingCartsProductModel UpdateModel, String id) {
        binding.progressCircular.setVisibility(View.VISIBLE);
        databaseService.UpdateCartQuantityById(uid,id,UpdateModel.getSelectableQuantity());
        binding.progressCircular.setVisibility(View.GONE);



    }





    public void placeOrder(OrderModel orderModel){
        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle("Placing Order");
        progressDialog.setMessage("Please wait while we process your order...");
        progressDialog.setCancelable(false); // Set if dialog is cancelable or not
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        databaseService.PlaceOder(orderModel, new DatabaseService.PlaceOrderCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess() {
                databaseService.removeCartItems(getContext(),orderModel.getCustomer().getCustomerId());
                String totalSaving = ValuesHelper.RupeeSymbols + saveAmount;
                PlaceOrderFragment placeOrderFragment = new PlaceOrderFragment(orderModel.getOrderId(),totalSaving);
                placeOrderFragment.show(requireActivity().getSupportFragmentManager(), "place_order_dialog");
                progressDialog.dismiss();

            }
            @Override
            public void onError(Exception errorMessage) {
                progressDialog.dismiss();
                CustomErrorDialog customErrorDialog = new CustomErrorDialog(requireContext());
                customErrorDialog.setTitle("Place order error");
                customErrorDialog.setTitle(errorMessage.getLocalizedMessage());
                customErrorDialog.show();
            }
        });
    }





//    public void placeOrder(long time,String paymentMethod,String paymentStatus){
//        ProgressDialog progressDialog = new ProgressDialog(requireContext());
//        progressDialog.setTitle("Placing Order");
//        progressDialog.setMessage("Please wait while we process your order...");
//        progressDialog.setCancelable(false); // Set if dialog is cancelable or not
//        progressDialog.setCanceledOnTouchOutside(false);
//        progressDialog.show();
//        String orderId = time + FirebaseAuth.getInstance().getUid();
//        double totalSavings = ShoppingCartHelper.calculateTotalSavings(models);
//        final double finalTotal = ShoppingCartHelper.calculateTotalPrices(models) - newCouponValue;
//
////    public OrderModel(String orderId, Customer customer, ArrayList< ShoppingCartsProductFirebaseModel > orderItems, double orderTotalPrice, String couponCode, String orderStatus, Payment
////        payment, Shipping shipping, Date orderDate, String notes, String token) {
//        String deliveryState = ValuesHelper.PROCESSING;
////        String defaultUserName  = ValuesHelper.DEFAULT_USER_NAME;
//
//        Customer customer = new Customer(authService.getUserId(), authService.getUserName(), addressModel.getMobileNumber() , authService.getUserPhoneNumber());
//        Payment payment = new Payment(paymentMethod,paymentStatus);
//        Shipping shipping = new Shipping("Standing","00",null,addressModel, deliveryState);
//        Date currentDate = new Date();
//        String couponCode = "NoCouponCode";
//        String token = TokenManager.getInstance(requireContext()).getToken();
//        if (CouponValueIsApply) {
//            couponCode = Objects.requireNonNull(binding.couponCode.getEditText()).getText().toString();
//            int newTotalUse = TotalUse - 1;
//            FirebaseDatabase.getInstance().getReference().child("admin").child("Coupon").child(CouponCode).child("TotalUse").setValue(newTotalUse);
//        }
//
//        OrderModel orderModel = new OrderModel(orderId,customer,models,finalTotal,couponCode,deliveryState,payment, shipping
//                ,currentDate , Objects.requireNonNull(binding.note.getEditText()).getText().toString(),token,newCouponValue,00,00);
//        databaseService.PlaceOder(orderModel, new DatabaseService.PlaceOrderCallback() {
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onSuccess() {
//                databaseService.removeCartItems(getContext(),customer.getCustomerId());
//                String totalSaving = ValuesHelper.RupeeSymbols + totalSavings;
//                PlaceOrderFragment placeOrderFragment = new PlaceOrderFragment(orderId,totalSaving);
//                placeOrderFragment.show(requireActivity().getSupportFragmentManager(), "place_order_dialog");
//                progressDialog.dismiss();
//
//            }
//            @Override
//            public void onError(Exception errorMessage) {
//                progressDialog.dismiss();
//                CustomErrorDialog customErrorDialog = new CustomErrorDialog(requireContext());
//                customErrorDialog.setTitle("Place order error");
//                customErrorDialog.setTitle(errorMessage.getLocalizedMessage());
//                customErrorDialog.show();
//            }
//        });
//    }

}