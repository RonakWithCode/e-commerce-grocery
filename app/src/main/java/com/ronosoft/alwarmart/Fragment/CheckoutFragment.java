package com.ronosoft.alwarmart.Fragment;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.ronosoft.alwarmart.Adapter.ShoppingCartsAdapter;
import com.ronosoft.alwarmart.Dialog.CustomErrorDialog;
import com.ronosoft.alwarmart.HelperClass.ShoppingCartHelper;
import com.ronosoft.alwarmart.HelperClass.ValuesHelper;
import com.ronosoft.alwarmart.Manager.ProductManager;
import com.ronosoft.alwarmart.Model.AddressModel;
import com.ronosoft.alwarmart.Model.CouponModel;
import com.ronosoft.alwarmart.Model.Customer;
import com.ronosoft.alwarmart.Model.Gift;
import com.ronosoft.alwarmart.Model.OrderModel;
import com.ronosoft.alwarmart.Model.Payment;
import com.ronosoft.alwarmart.Model.Shipping;
import com.ronosoft.alwarmart.Model.ShoppingCartsProductModel;
import com.ronosoft.alwarmart.R;
import com.ronosoft.alwarmart.Services.AuthService;
import com.ronosoft.alwarmart.Services.DatabaseService;
import com.ronosoft.alwarmart.databinding.FragmentCheckoutBinding;
import com.ronosoft.alwarmart.interfaceClass.ShoppingCartsInterface;
import com.ronosoft.alwarmart.javaClasses.OrderIdGenerator;
import com.ronosoft.alwarmart.javaClasses.TokenManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

public class CheckoutFragment extends Fragment implements ShoppingCartsInterface {

    private FragmentCheckoutBinding binding;
    private AuthService authService;
    private DatabaseService databaseService;
    private ShoppingCartsAdapter cartsAdapter;
    private ArrayList<ShoppingCartsProductModel> models;
    private String uid;
    private AddressModel addressModel;
    private NavController navController;

    private double subTotalPrice = 0;
    private double shippingFee = 0;
    private double processingFee = 0;
    private double donate = 0;
    private double grandTotal = 0;

    private double couponValue = 0;
    private String couponCode = "";
    private double minOderValue = 0;
    private boolean couponIsApply = false;
    private final String TAG = "CheckoutFragment";
    private Gift mainGift;

    private Handler messageHandler;
    private Runnable messageRunnable;
    private static final String[] LOADING_MESSAGES = {
            "Processing your order...",
            "Packing your items...",
            "Verifying payment...",
            "Preparing shipment...",
            "Almost done..."
    };
    private Dialog loadingDialog;  // Add this new variable
    public CheckoutFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            addressModel = getArguments().getParcelable("adders");
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCheckoutBinding.inflate(inflater, container, false);
        navController = Navigation.findNavController(requireActivity(), R.id.oder_host_fragment);
        authService = new AuthService();
        databaseService = new DatabaseService();
        uid = authService.getUserId();
        binding.shimmerLayout.startShimmer();
        models = new ArrayList<>();
        mainGift = new Gift();
        cartsAdapter = new ShoppingCartsAdapter(models, this, requireContext());
        binding.CardView.setAdapter(cartsAdapter);
        binding.CardView.setLayoutManager(new LinearLayoutManager(requireContext()));

        binding.orderDetailsViewBack.setOnClickListener(view -> navController.popBackStack());
        initAddress();
        loadProductFromCart();

        binding.couponCodeApplyBtn.setOnClickListener(view -> {
            if (couponIsApply) {
                removeCoupon();
            } else {
                String _couponCode = Objects.requireNonNull(binding.couponCode.getEditText()).getText().toString();
                if (_couponCode.isEmpty()) {
                    binding.couponCode.setError("Please Enter Coupon Code");
                } else {
                    ApplyCouponCode(_couponCode);
                }
            }
        });

        binding.placeBtn.setOnClickListener(view -> {
            final Dialog dialog = new Dialog(requireContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.bottom_sheet_payment_options);

            RadioButton codRadioButton = dialog.findViewById(R.id.codRadioButton);
            ImageView closeButton = dialog.findViewById(R.id.closeButton);

            codRadioButton.setOnClickListener(v -> {

                placeOrder("Standard", ValuesHelper.PROCESSING);
            });
            closeButton.setOnClickListener(v -> dialog.dismiss());

            dialog.show();
            dialog.setCancelable(true);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().getAttributes().windowAnimations = R.style.Animationboy;
            dialog.getWindow().setGravity(Gravity.BOTTOM);
        });

        // Initialize the loading dialog
        setupLoadingDialog();
        startMessageUpdater();

        return binding.getRoot();
    }
    private void setupLoadingDialog() {
        loadingDialog = new Dialog(requireContext());
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadingDialog.setContentView(R.layout.dialog_custom_loading);

        LottieAnimationView animationView = loadingDialog.findViewById(R.id.loadingAnimation);
        animationView.setAnimation(R.raw.loading_animation);
        animationView.setRepeatCount(ValueAnimator.INFINITE);
        animationView.playAnimation(); // Ensure animation starts

        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loadingDialog.getWindow().setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        loadingDialog.setCancelable(false);
    }

    private void startMessageUpdater() {
        messageHandler = new Handler(Looper.getMainLooper());
        messageRunnable = new Runnable() {
            @Override
            public void run() {
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    TextView loadingText = loadingDialog.findViewById(R.id.loadingText);
                    if (loadingText != null) {
                        Random random = new Random();
                        String randomMessage = LOADING_MESSAGES[random.nextInt(LOADING_MESSAGES.length)];
                        loadingText.setText(randomMessage);
                    }
                    messageHandler.postDelayed(this, 2000); // Update every 2 seconds
                }
            }
        };
        messageHandler.post(messageRunnable);
    }

    private void stopMessageUpdater() {
        if (messageHandler != null && messageRunnable != null) {
            messageHandler.removeCallbacks(messageRunnable);
        }
    }


    private void initAddress() {
        String type;
        if (addressModel.isHomeSelected()) {
            type = "Home";
            Glide.with(requireContext()).load(R.drawable.home_shipping).into(binding.AddressType);
        } else {
            type = "Work";
            Glide.with(requireContext()).load(R.drawable.office_building).into(binding.AddressType);
        }
        binding.deliveryTo.setText("Delivering to " + type);
        binding.deliveryAddress.setText(addressModel.getFullName() + "\n" +
                addressModel.getMobileNumber() + "\n" +
                addressModel.getFlatHouse() + addressModel.getAddress());
        binding.Change.setOnClickListener(view -> navController.popBackStack());
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
            }
            @Override
            public void onError(String errorMessage) {
                // Handle error if necessary.
            }
        });
    }

    private void setupLayout() {
        CalculateValue();
    }

    private void CalculateValue() {
        subTotalPrice = ShoppingCartHelper.calculateTotalPrices(models);
        if (subTotalPrice < ValuesHelper.MIN_TOTAL_PRICE_FOR_DELIVERY) {
            processingFee = ValuesHelper.processingFee;
            shippingFee = ValuesHelper.DeliveryFees;
        } else {
            processingFee = ValuesHelper.processingFee;
            shippingFee = ValuesHelper.StandardDeliveryFees;
        }
        donate = ValuesHelper.MinDonate;
        if (couponIsApply && subTotalPrice >= minOderValue) {
            grandTotal = subTotalPrice + processingFee + shippingFee + donate - couponValue;
        } else {
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
        setupGift();
    }

    private void setupPricing() {
        binding.SubTotalPrice.setText(ValuesHelper.RupeeSymbols + subTotalPrice);
        binding.processFee.setText(ValuesHelper.RupeeSymbols + processingFee);
        binding.discount.setText(ValuesHelper.RupeeSymbols + couponValue);
        binding.ShippingPrice.setText(ValuesHelper.RupeeSymbols + shippingFee);
        binding.TotalPrice.setText(ValuesHelper.RupeeSymbols + grandTotal);
        binding.progressCircular.setVisibility(View.GONE);
    }

    /**
     * Free Gift logic:
     * Checks if the user has redeemed the free gift fewer than the number defined
     * in the gift’s eligibleUserGroups field. The eligibleUserGroups field now holds a numeric string (e.g., "3").
     * If the user's current gift usage (from Firebase) is less than that number,
     * the code iterates over available free gifts and checks eligibility based on order subtotal and validity.
     */
    private void setupGift() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference giftUsageRef = firebaseDatabase.getReference("UserInfo")
                .child(authService.getUserId())
                .child("giftUsage");

        giftUsageRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot dataSnapshot = task.getResult();
                Long giftUsageValue = (dataSnapshot.exists()) ? dataSnapshot.getValue(Long.class) : 0L;
                if (giftUsageValue == null) giftUsageValue = 0L;
                final long usage = giftUsageValue; // Make it final for use in inner classes.

                // Instead of a fixed value, each gift's eligibleUserGroups field defines max allowed redemptions.
                DatabaseReference giftRef = firebaseDatabase.getReference("freeGifts");
                giftRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!isAdded()) return;
                        long now = System.currentTimeMillis();
                        boolean eligibleGiftFound = false;
                        boolean eligibleGiftShowFreeText = false;
                        double minAdditionalNeeded = Double.MAX_VALUE;

                        for (DataSnapshot giftSnapshot : snapshot.getChildren()) {
                            Gift gift = giftSnapshot.getValue(Gift.class);
                            if (gift != null) {
                                Log.i(TAG, "Found Gift: " + gift.getGiftId());
                                int allowedRedemptions = 0;
                                try {
                                    allowedRedemptions = Integer.parseInt(gift.getEligibleUserGroups());
                                } catch (NumberFormatException e) {
                                    Log.e(TAG, "EligibleUserGroups not numeric for gift: " + gift.getGiftId());
                                    continue;
                                }
                                if (usage < allowedRedemptions && gift.isEligible(subTotalPrice, now)) {
                                    eligibleGiftFound = true;
                                    binding.freeGiftCard.setVisibility(View.VISIBLE);
                                    binding.freeText.setVisibility(View.GONE);
                                    Glide.with(requireContext())
                                            .load(gift.getImageUrl())
                                            .placeholder(R.drawable.placeholder)
                                            .into(binding.giftImage);
                                    binding.title.setText(gift.getTitle());
                                    binding.subTitle.setText(gift.getSubtitle());
                                    binding.freeType.setText(gift.getCategory());
                                    mainGift = gift;
                                    break;
                                }
                                else {
                                    if (usage < allowedRedemptions) {
                                        eligibleGiftShowFreeText = true;
                                    }

                                    if (subTotalPrice < gift.getMinOrderValue()) {
                                        double additionalNeeded = gift.getMinOrderValue() - subTotalPrice;
                                        if (additionalNeeded < minAdditionalNeeded) {
                                            minAdditionalNeeded = additionalNeeded;
                                        }
                                    }
                                }
                            }
                        }
                        if (!eligibleGiftFound) {
                            binding.freeGiftCard.setVisibility(View.GONE);
                            if (minAdditionalNeeded != Double.MAX_VALUE) {
                                if (eligibleGiftShowFreeText) {
                                    binding.freeText.setVisibility(View.VISIBLE);
                                    binding.freeText.setText("Add products worth ₹" + (int) minAdditionalNeeded + " more to get a free gift");
                                }
                            } else {
                                binding.freeText.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        binding.freeGiftCard.setVisibility(View.GONE);
                        binding.freeText.setVisibility(View.GONE);
                        Log.e(TAG, "Gift retrieval cancelled: " + error.getMessage());
                    }
                });
            } else {
                Log.e(TAG, "Failed to get gift usage: " + task.getException().getMessage());
            }
        });
    }

    private void updateUsageAfterOrder() {
        // Update coupon usage if a coupon was applied.
        if (couponIsApply && couponCode != null && !couponCode.trim().isEmpty()) {
            DatabaseReference couponRef = FirebaseDatabase.getInstance()
                    .getReference("admin")
                    .child("Coupon")
                    .child(couponCode);
            couponRef.runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                    CouponModel coupon = mutableData.getValue(CouponModel.class);
                    if (coupon == null) {
                        return Transaction.success(mutableData);
                    }
                    int currentTotalUse = coupon.getTotalUse();
                    if (currentTotalUse > 0) {
                        coupon.setTotalUse(currentTotalUse - 1);
                        mutableData.setValue(coupon);
                        Log.d(TAG, "Decrementing coupon stock from " + currentTotalUse + " to " + (currentTotalUse - 1));
                    }
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                    if (databaseError != null) {
                        Log.e(TAG, "Coupon update failed: " + databaseError.getMessage());
                    } else {
                        Log.d(TAG, "Coupon totalUse decremented successfully.");
                    }
                }
            });

            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference couponUseRef = firebaseDatabase.getReference()
                    .child("UserInfo")
                    .child(authService.getUserId())
                    .child("couponUse");
            couponUseRef.runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                    Long couponUsage = mutableData.getValue(Long.class);
                    if (couponUsage == null) {
                        couponUsage = 0L;
                    }
                    mutableData.setValue(couponUsage + 1);
                    Log.d(TAG, "Incrementing coupon usage from " + couponUsage + " to " + (couponUsage + 1));
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                    if (databaseError != null) {
                        Log.e(TAG, "Failed to update coupon usage: " + databaseError.getMessage());
                    } else {
                        Log.d(TAG, "Coupon usage incremented successfully.");
                    }
                }
            });
        } else {
            Log.d(TAG, "No coupon applied; skipping coupon usage update.");
        }

        // Update gift stock and gift usage if a gift is applied.
        if (mainGift != null && mainGift.getGiftId() != null && !mainGift.getGiftId().trim().isEmpty()
                && mainGift.getStockLimit() != null) {

            // Decrement gift stock.
            DatabaseReference giftRef = FirebaseDatabase.getInstance()
                    .getReference("freeGifts")
                    .child(mainGift.getGiftId());
            giftRef.runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                    Gift gift = mutableData.getValue(Gift.class);
                    if (gift == null) {
                        Log.e(TAG, "Gift not found in transaction.");
                        return Transaction.success(mutableData);
                    }
                    Integer currentStock = gift.getStockLimit();
                    if (currentStock != null && currentStock > 0) {
                        gift.setStockLimit(currentStock - 1);
                        Log.d(TAG, "Decrementing gift stock from " + currentStock + " to " + (currentStock - 1));
                    } else {
                        Log.d(TAG, "Gift stock is already 0 or null.");
                    }
                    mutableData.setValue(gift);
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                    if (databaseError != null) {
                        Log.e(TAG, "Gift stock update failed: " + databaseError.getMessage());
                    } else {
                        Log.d(TAG, "Gift stock decremented successfully.");
                    }
                }
            });

            // Increment gift usage.
            DatabaseReference giftUsageRef = FirebaseDatabase.getInstance()
                    .getReference("UserInfo")
                    .child(authService.getUserId())
                    .child("giftUsage");
            giftUsageRef.runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                    Long giftUsage = mutableData.getValue(Long.class);
                    if (giftUsage == null) {
                        giftUsage = 0L;
                    }
                    mutableData.setValue(giftUsage + 1);
                    Log.d(TAG, "Incrementing gift usage from " + giftUsage + " to " + (giftUsage + 1));
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                    if (databaseError != null) {
                        Log.e(TAG, "Failed to update gift usage: " + databaseError.getMessage());
                    } else {
                        Log.d(TAG, "Gift usage incremented successfully.");
                    }
                }
            });
        } else {
            Log.d(TAG, "No gift applied or gift key is missing; skipping gift stock update.");
        }
    }

    private void removeCoupon() {
        couponIsApply = false;
        couponCode = "";
        couponValue = 0;
        minOderValue = 0;
        Objects.requireNonNull(binding.couponCode.getEditText()).setText("");
        binding.couponCode.setError(null);
        binding.couponCode.setHelperText(null);
        updateCouponButtonState();
        CalculateValue();
    }

    private void updateCouponButtonState() {
        if (!isAdded()) return;
        if (couponIsApply) {
            binding.couponCodeApplyBtn.setText("Remove");
            binding.couponCodeApplyBtn.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), R.color.greyColor)));
            Objects.requireNonNull(binding.couponCode.getEditText()).setEnabled(false);
        } else {
            binding.couponCodeApplyBtn.setText("Apply");
            binding.couponCodeApplyBtn.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), R.color.green_primary)));
            Objects.requireNonNull(binding.couponCode.getEditText()).setEnabled(true);
        }
    }

    private void ApplyCouponCode(String couponCode) {
        binding.progressCircular.setVisibility(View.VISIBLE);
        binding.couponCode.setError(null);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference couponUseRef = firebaseDatabase.getReference()
                .child("UserInfo")
                .child(authService.getUserId())
                .child("couponUse");
        couponUseRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot dataSnapshot = task.getResult();
                if (dataSnapshot.exists()) {
                    Long couponUsage = dataSnapshot.getValue(Long.class);
                    if (couponUsage != null && couponUsage == 1) {
                        binding.progressCircular.setVisibility(View.GONE);
                        binding.couponCode.setError("Coupon already used");
                        return;
                    }
                }
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
                                couponIsApply = true;
                                CheckoutFragment.this.couponCode = couponModel.getCouponCode();
                                couponValue = couponModel.getCouponValue();
                                minOderValue = couponModel.getMinOderValue();
                                CalculateValue();
                                binding.couponCode.setHelperText("Coupon applied successfully!");
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                binding.progressCircular.setVisibility(View.GONE);
                                binding.couponCode.setError("Error validating coupon");
                            }
                        });
            } else {
                binding.progressCircular.setVisibility(View.GONE);
                Log.e(TAG, "Error checking coupon usage: " + task.getException().getMessage());
            }
        });
    }

    @Override
    public void remove(int pos, String id, ShoppingCartsProductModel cartsProductModel) {
        if (models.size() == 1 && cartsProductModel.getSelectableQuantity() <= 1) {
            new ProductManager(requireActivity()).RemoveCartProductById(id);
            cartsAdapter.notifyDataSetChanged();
            Toast.makeText(requireContext(), "Your cart is now empty.", Toast.LENGTH_SHORT).show();
            if (getActivity() != null) {
                getActivity().finish();
            }
        } else {
            if (requireActivity().getSupportFragmentManager().findFragmentByTag("bottom_sheet_fragment") == null) {
                RemoveBottomSheetDialogFragment bottomSheet =
                        new RemoveBottomSheetDialogFragment(uid, id, cartsAdapter, cartsProductModel,
                                new RemoveBottomSheetDialogFragment.OnCartUpdatedListener() {
                                    @Override
                                    public void onCartUpdated() {
                                        // Optionally refresh cart data here.
                                    }
                                });
                bottomSheet.show(requireActivity().getSupportFragmentManager(), "bottom_sheet_fragment");
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void UpdateQuantity(ShoppingCartsProductModel UpdateModel, String id) {
        binding.progressCircular.setVisibility(View.VISIBLE);
        databaseService.UpdateCartQuantityById(uid, id, UpdateModel.getSelectableQuantity());
        binding.progressCircular.setVisibility(View.GONE);
    }

    // Overload that accepts shippingMethod and shippingStatus, builds OrderModel, then calls placeOrder(OrderModel)
//    private void placeOrder(String shippingMethod, String shippingStatus) {
//        if (models == null || models.isEmpty() || grandTotal <= 0) {
//            Toast.makeText(requireContext(), "Your cart is empty. Please add some products.", Toast.LENGTH_SHORT).show();
//            requireActivity().finish();
//            return;
//        }
//        Date orderDate = new Date();
//        Log.d(TAG, "placeOrder: " + orderDate);
//        Shipping shipping = new Shipping(shippingMethod, shippingFee, new Date(), addressModel, shippingStatus);
//        Payment payment = new Payment("cash", "Pending");
//        String userID = authService.getUserId();
//        authService.getUserPhoneNumber().addOnSuccessListener(phoneNumber -> {
//            Customer customer = new Customer(
//                    userID,
//                    addressModel.getFullName(),
//                    addressModel.getMobileNumber(),
//                    phoneNumber
//            );
//            OrderIdGenerator.generateUniqueOrderId().addOnSuccessListener(orderId -> {
//                OrderModel orderModel = new OrderModel(
//                        orderId,
//                        customer,
//                        models,
//                        grandTotal,
//                        couponCode,
//                        shippingStatus,
//                        payment,
//                        shipping,
//                        orderDate,
//                        binding.note.getEditText().getText().toString(),
//                        TokenManager.getInstance(requireContext()).getToken(),
//                        couponValue,
//                        donate,
//                        processingFee,
//                        mainGift
//                );
//                placeOrder(orderModel);
//            }).addOnFailureListener(e -> {
//                Toast.makeText(requireContext(), "Failed to generate order ID: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//            });
//        }).addOnFailureListener(e -> {
//            Log.e(TAG, "Error getting phone number", e);
//            Toast.makeText(requireContext(), "Error: Unable to get phone number", Toast.LENGTH_SHORT).show();
//        });
//    }



    // Existing placeOrder(OrderModel) method.


    private void placeOrder(String shippingMethod, String shippingStatus) {
        if (models == null || models.isEmpty() || grandTotal <= 0) {
            Toast.makeText(requireContext(), "Your cart is empty. Please add some products.",
                    Toast.LENGTH_SHORT).show();
            requireActivity().finish();
            return;
        }

        loadingDialog.show();
        startMessageUpdater(); // Start random message updates

        Date orderDate = new Date();
        Log.d(TAG, "placeOrder: " + orderDate);
        Shipping shipping = new Shipping(shippingMethod, shippingFee, new Date(),
                addressModel, shippingStatus);
        Payment payment = new Payment("cash", "Pending");
        String userID = authService.getUserId();

        authService.getUserPhoneNumber()
                .addOnSuccessListener(phoneNumber -> {
                    Customer customer = new Customer(
                            userID,
                            addressModel.getFullName(),
                            addressModel.getMobileNumber(),
                            phoneNumber
                    );

                    OrderIdGenerator.generateUniqueOrderId()
                            .addOnSuccessListener(orderId -> {
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
                                        processingFee,
                                        mainGift
                                );
                                placeOrderSaveInFirebase(orderModel);
                            })
                            .addOnFailureListener(e -> {
                                stopMessageUpdater();
                                handleOrderFailure("Failed to generate order ID: " + e.getMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    stopMessageUpdater();
                    handleOrderFailure("Error getting phone number: " + e.getMessage());
                });
    }

    private void placeOrderSaveInFirebase(OrderModel orderModel) {
        databaseService.PlaceOder(orderModel, new DatabaseService.PlaceOrderCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess() {
                stopMessageUpdater();

                LottieAnimationView animationView = loadingDialog.findViewById(R.id.loadingAnimation);
                TextView loadingText = loadingDialog.findViewById(R.id.loadingText);

                // Clear current animation and load success animation
                animationView.cancelAnimation();
                animationView.setAnimation(R.raw.success_animation);
                animationView.setRepeatCount(0);
                animationView.playAnimation(); // Explicitly start success animation
                loadingText.setText("Order Placed Successfully!");

                double totalSavingInDouble = ShoppingCartHelper.calculateTotalSavings(orderModel.getOrderItems())
                        - ShoppingCartHelper.calculateTotalPrices(orderModel.getOrderItems());
                String totalSaving = ValuesHelper.RupeeSymbols + totalSavingInDouble;

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    loadingDialog.dismiss();
                    databaseService.removeCartItems(getContext(), orderModel.getCustomer().getCustomerId());
                    updateUsageAfterOrder();

                    PlaceOrderFragment placeOrderFragment = new PlaceOrderFragment(
                            orderModel.getOrderId(), totalSaving);
                    placeOrderFragment.show(requireActivity().getSupportFragmentManager(),
                            "place_order_dialog");
                }, 2000); // Increased to 2 seconds to ensure animation visibility
            }

            @Override
            public void onError(Exception errorMessage) {
                stopMessageUpdater();
                handleOrderFailure("Order processing failed: " + errorMessage.getMessage());
            }
        });
    }

    private void handleOrderFailure(String errorMessage) {
        LottieAnimationView animationView = loadingDialog.findViewById(R.id.loadingAnimation);
        TextView loadingText = loadingDialog.findViewById(R.id.loadingText);

        animationView.cancelAnimation();
        animationView.setAnimation(R.raw.error_animation);
        animationView.setRepeatCount(0);
        animationView.playAnimation();
        loadingText.setText("Order Failed!");

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            loadingDialog.dismiss();
            CustomErrorDialog customErrorDialog = new CustomErrorDialog(requireContext());
            customErrorDialog.setTitle("Place order error");
            customErrorDialog.setMessage(errorMessage);
            customErrorDialog.show();
        }, 1500);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopMessageUpdater();
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
        binding = null;
    }
}
