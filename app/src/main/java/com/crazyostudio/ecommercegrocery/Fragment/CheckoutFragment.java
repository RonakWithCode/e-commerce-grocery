package com.crazyostudio.ecommercegrocery.Fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.crazyostudio.ecommercegrocery.Adapter.ShoppingCartsAdapter;
import com.crazyostudio.ecommercegrocery.Dialog.CustomErrorDialog;
import com.crazyostudio.ecommercegrocery.HelperClass.ShoppingCartHelper;
import com.crazyostudio.ecommercegrocery.HelperClass.ValuesHelper;
import com.crazyostudio.ecommercegrocery.Model.AddressModel;
import com.crazyostudio.ecommercegrocery.Model.Customer;
import com.crazyostudio.ecommercegrocery.Model.OrderModel;
import com.crazyostudio.ecommercegrocery.Model.Payment;
import com.crazyostudio.ecommercegrocery.Model.Shipping;
import com.crazyostudio.ecommercegrocery.Model.ShoppingCartsProductModel;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.Services.AuthService;
import com.crazyostudio.ecommercegrocery.Services.DatabaseService;
import com.crazyostudio.ecommercegrocery.databinding.FragmentCheckoutBinding;
import com.crazyostudio.ecommercegrocery.interfaceClass.ShoppingCartsInterface;
import com.crazyostudio.ecommercegrocery.javaClasses.TokenManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;


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
    double Total =  00;
    double grandTotal = 00;

    double processFee = 00;
    private double newCouponValue;
    private double couponMin;
    private AddressModel addressModel;
    NavController navController;
    int TotalUse;
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
//        productModels = new ArrayList<>();
//        recommendationsAdapter = new RecommendationsAdapter(productModels,this,requireContext());
//        binding.recommendationsProduct.setAdapter(recommendationsAdapter);
//        binding.recommendationsProduct.setLayoutManager(new LinearLayoutManager(requireContext(),LinearLayoutManager.
        binding.CardView.setAdapter(cartsAdapter);
        binding.CardView.setLayoutManager(new LinearLayoutManager(requireContext()));

        binding.orderDetailsViewBack.setOnClickListener(click->navController.popBackStack());
//        CouponFragment
        binding.couponCodeApplyBtn.setOnClickListener(view->{
            if (!Objects.requireNonNull(binding.couponCode.getEditText()).getText().toString().isEmpty()) {
                if (!CouponValueIsApply) {
                    CouponCode = Objects.requireNonNull(binding.couponCode.getEditText()).getText().toString();
                    FirebaseDatabase.getInstance().getReference().child("admin").child("Coupon").child(CouponCode).addListenerForSingleValueEvent(new ValueEventListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                // Coupon code exists, retrieve discount amount and min value
                                String couponDiscountAmount = snapshot.child("coupon_discount_amount").getValue(String.class);
                                String couponMinValue = snapshot.child("couponMinvalue").getValue(String.class);
                                TotalUse = snapshot.child("TotalUse").getValue(Integer.class);
                                boolean isActive = Boolean.TRUE.equals(snapshot.child("isActive").getValue(Boolean.class));

                                if (isActive && TotalUse > 0) { // Changed the condition to TotalUse > 0
                                    assert couponMinValue != null;
                                    couponMin = Double.parseDouble(couponMinValue);
                                    assert couponDiscountAmount != null;
                                    if (Total >= couponMin) {
                                        CouponValueIsApply = true;
                                        newCouponValue = Double.parseDouble(couponDiscountAmount); // Example coupon value
                                        binding.couponCodeApplyBtn.setText("remove");
                                        binding.discount.setText(ValuesHelper.RupeeSymbols + newCouponValue);
                                        binding.couponCode.setEnabled(!CouponValueIsApply);
                                        updateSubTotalPrice();
                                    }
                                    else {
                                        binding.couponCode.setError("Minimum subtotal value required to apply the coupon is " + ValuesHelper.RupeeSymbols + couponMinValue);
                                    }
                                }else {
                                    binding.couponCode.setError("this "+ binding.couponCode.getEditText().getText().toString()+" coupon has expired");
                                    binding.couponCode.getEditText().setText("");
                                }

                            } else {
                                // Coupon code does not exist
                                binding.couponCode.setError(CouponCode + " not valid");
                                binding.couponCode.getEditText().setText("");
                            }
                        }

                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle database error
                            binding.couponCode.setError(CouponCode + " not valid");
                            CouponValueIsApply = false;
                            newCouponValue = 00; // No coupon applied
                            binding.couponCodeApplyBtn.setText("Apply");
                            binding.discount.setText(ValuesHelper.RupeeSymbols+newCouponValue);
                            binding.couponCode.setEnabled(!CouponValueIsApply);
                        }
                    });
                }
                else {
                    newCouponValue = 0; // No coupon applied
                    updateSubTotalPrice();
                    CouponValueIsApply = false;
                    binding.discount.setText(ValuesHelper.RupeeSymbols+newCouponValue);
                    binding.couponCodeApplyBtn.setText("Apply");
                    binding.couponCode.setEnabled(!CouponValueIsApply);

                }
            }else {
                binding.couponCode.setError("fill the discount code");
            }

        });




        binding.ShippingPrice.setText(deliveryFee);

        initAddress();
        loadProductFromCart();

        binding.placeBtn.setOnClickListener(view -> {
//            TODO

            final Dialog dialog = new Dialog(requireContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.bottom_sheet_payment_options);


            RadioButton codRadioButton = dialog.findViewById(R.id.codRadioButton);
            ImageView closeButton = dialog.findViewById(R.id.closeButton);


            codRadioButton.setOnClickListener(v -> placeOrder(System.currentTimeMillis(), "cash", "pending"));
            closeButton.setOnClickListener(v -> dialog.dismiss());

            dialog.show();
            dialog.setCancelable(true);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().getAttributes().windowAnimations = R.style.Animationboy;
            dialog.getWindow().setGravity(Gravity.BOTTOM);






        });


            return binding.getRoot();
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
                updateSubTotalPrice();
//                LoadRecommendations();
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
        if (Total < ValuesHelper.MIN_TOTAL_PRICE) {
            processFee = ValuesHelper.MIN_TOTAL_PRICE_VALUE;
            binding.processFee.setText(ValuesHelper.RupeeSymbols + processFee);
        }else {
            processFee = 00;
            binding.processFee.setText(ValuesHelper.RupeeSymbols + processFee);
        }

        binding.SubTotalPrice.setText("₹" + Total);

        grandTotal = Total - newCouponValue + processFee;
        binding.TotalPrice.setText(ValuesHelper.RupeeSymbols  + grandTotal);

    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    @Override
    public void remove(int pos,String id,ShoppingCartsProductModel cartsProductModel) {
        if (requireActivity().getSupportFragmentManager().findFragmentByTag("bottom_sheet_fragment") == null) {
            RemoveBottomSheetDialogFragment bottomSheet = new RemoveBottomSheetDialogFragment(uid,id,cartsAdapter,cartsProductModel);

            bottomSheet.show(requireActivity().getSupportFragmentManager(), "bottom_sheet_fragment");
        }
        updateSubTotalPrice();
        if (Total < couponMin) {
            // Total is less than the minimum required value, remove the coupon
            CouponValueIsApply = false;
            newCouponValue = 00;
            binding.couponCodeApplyBtn.setText("Apply");
            binding.couponCode.setEnabled(true);
            binding.discount.setText(ValuesHelper.RupeeSymbols + newCouponValue);
            updateSubTotalPrice();


        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void UpdateQuantity(ShoppingCartsProductModel UpdateModel, String id) {
        binding.progressCircular.setVisibility(View.VISIBLE);
        databaseService.UpdateCartQuantityById(uid,id,UpdateModel.getSelectableQuantity());
        binding.progressCircular.setVisibility(View.GONE);
        updateSubTotalPrice();

        // Check if the total becomes less than the minimum required value after updating quantity
        if (Total < couponMin) {
            // Total is less than the minimum required value, remove the coupon
            CouponValueIsApply = false;
            newCouponValue = 00;
            binding.couponCodeApplyBtn.setText("Apply");
            binding.couponCode.setEnabled(true);
            binding.discount.setText(ValuesHelper.RupeeSymbols + newCouponValue);
            updateSubTotalPrice();
        }




    }



    public void placeOrder(long time,String paymentMethod,String paymentStatus){
        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle("Placing Order");
        progressDialog.setMessage("Please wait while we process your order...");
        progressDialog.setCancelable(false); // Set if dialog is cancelable or not
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        String orderId = time + FirebaseAuth.getInstance().getUid();
        double totalSavings = ShoppingCartHelper.calculateTotalSavings(models);
        final double finalTotal = ShoppingCartHelper.calculateTotalPrices(models) - newCouponValue;

//    public OrderModel(String orderId, Customer customer, ArrayList< ShoppingCartsProductFirebaseModel > orderItems, double orderTotalPrice, String couponCode, String orderStatus, Payment
//        payment, Shipping shipping, Date orderDate, String notes, String token) {
        String deliveryState = ValuesHelper.PROCESSING;
//        String defaultUserName  = ValuesHelper.DEFAULT_USER_NAME;

        Customer customer = new Customer(authService.getUserId(), authService.getUserName(), addressModel.getMobileNumber() , authService.getUserPhoneNumber());
        Payment payment = new Payment(paymentMethod,paymentStatus);
        Shipping shipping = new Shipping("Standing","free",null,addressModel, deliveryState);
        Date currentDate = new Date();
        String couponCode = "NoCouponCode";
        String token = TokenManager.getInstance(requireContext()).getToken();
        if (CouponValueIsApply) {
            couponCode = Objects.requireNonNull(binding.couponCode.getEditText()).getText().toString();
            int newTotalUse = TotalUse - 1;
            FirebaseDatabase.getInstance().getReference().child("admin").child("Coupon").child(CouponCode).child("TotalUse").setValue(newTotalUse);
        }

        OrderModel orderModel = new OrderModel(orderId,customer,models,finalTotal,couponCode,deliveryState,payment, shipping
                ,currentDate , Objects.requireNonNull(binding.note.getEditText()).getText().toString(),token);
        databaseService.PlaceOder(orderModel, new DatabaseService.PlaceOrderCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess() {
                databaseService.removeCartItems(getContext(),customer.getCustomerId());
                String totalSaving = ValuesHelper.RupeeSymbols + totalSavings;
                PlaceOrderFragment placeOrderFragment = new PlaceOrderFragment(orderId,totalSaving);
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

}