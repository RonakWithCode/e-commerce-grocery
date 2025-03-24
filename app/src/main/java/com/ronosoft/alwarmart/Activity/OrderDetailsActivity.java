package com.ronosoft.alwarmart.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ronosoft.alwarmart.Adapter.OrderProductAdapter;
import com.ronosoft.alwarmart.HelperClass.ShoppingCartHelper;
import com.ronosoft.alwarmart.HelperClass.ValuesHelper;
import com.ronosoft.alwarmart.Manager.ProductManager;
import com.ronosoft.alwarmart.Model.OrderModel;
import com.ronosoft.alwarmart.Model.ShoppingCartsProductModel;
import com.ronosoft.alwarmart.R;
import com.ronosoft.alwarmart.Services.DatabaseService;
import com.ronosoft.alwarmart.databinding.ActivityOrderDetailsBinding;
import com.ronosoft.alwarmart.interfaceClass.OrderProductInterface;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderDetailsActivity extends AppCompatActivity implements OrderProductInterface {
    private ActivityOrderDetailsBinding binding;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private OrderModel orderModel;
    private DatabaseService databaseService;
    private ProductManager productManager;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupActionBar();
        initializeFirebase();
//        binding.download.setVisibility();
        binding.orderDetailsViewBack.setOnClickListener(view -> onBackPressed());
        binding.download.setOnClickListener(v -> downloadBill());
        binding.ContinueShopping.setOnClickListener(v -> finish());

        String orderID = getIntent().getStringExtra("orderID");
        if (orderID != null && !orderID.isEmpty()) {
            fetchOrderDetails(orderID);
        }
        else {
            Toast.makeText(this, "Order ID is missing", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    private void initializeFirebase() {
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        databaseService = new DatabaseService();
        productManager = new ProductManager(this);
        userId = auth.getUid();
    }

    private void fetchOrderDetails(String orderID) {
        database.getReference().child("Order").child(userId).child(orderID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            orderModel = snapshot.getValue(OrderModel.class);
                            binding.progressCircular.setVisibility(View.GONE);
                            if (orderModel != null) {
                                displayOrderDetails();
                            } else {
                                Toast.makeText(OrderDetailsActivity.this, "Failed to retrieve order details", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(OrderDetailsActivity.this, "Order not found", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(OrderDetailsActivity.this, "Failed to retrieve order details", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private void downloadBill() {
        try {
            // Create PDF document optimized for thermal paper (72mm printable width)
            PdfDocument document = new PdfDocument();
            // 72mm = ~204 points width, height will be dynamic based on content
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(204, 800, 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            Canvas canvas = page.getCanvas();
            Paint paint = new Paint();
            
            // Starting position - minimal margins
            int y = 10;
            int leftMargin = 5;
            int rightMargin = 199; // 204 - 5
            int centerX = 204 / 2;

            // Logo - smaller size
            Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.transparent_logo);
            if (logo != null) {
                float aspectRatio = (float) logo.getWidth() / logo.getHeight();
                int targetWidth = 60; // Smaller logo
                int targetHeight = (int) (targetWidth / aspectRatio);
                Bitmap resizedLogo = Bitmap.createScaledBitmap(logo, targetWidth, targetHeight, true);
                canvas.drawBitmap(resizedLogo, centerX - (targetWidth / 2), y, paint);
                y += targetHeight + 5;
            }

            // Company Name - compact
            paint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));
            paint.setTextSize(12);
            String companyName = "ALWAR MART";
            float textWidth = paint.measureText(companyName);
            canvas.drawText(companyName, centerX - (textWidth / 2), y, paint);
            y += 15;

            // Business Details - smaller and more compact
            paint.setTextSize(7);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
            String[] businessDetails = {
                "Email: contact@alwarmart.in",
                "Website: alwarmart.in",
                "Phone: +91 7023941072"
            };
            
            for (String detail : businessDetails) {
                textWidth = paint.measureText(detail);
                canvas.drawText(detail, centerX - (textWidth / 2), y, paint);
                y += 10;
            }

            // Separator Line
            y += 3;
            paint.setStrokeWidth(0.5f);
            canvas.drawLine(leftMargin, y, rightMargin, y, paint);
            y += 10;

            // Order Details - compact layout
            paint.setTextSize(8);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));
            canvas.drawText("TAX INVOICE", leftMargin, y, paint);
            y += 12;

            paint.setTextSize(7);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault());
            
            // Compact key-value pairs
            drawKeyValuePair(canvas, "Invoice:", orderModel.getOrderId(), leftMargin, y, paint);
            y += 10;
            drawKeyValuePair(canvas, "Date:", dateFormat.format(orderModel.getOrderDate()), leftMargin, y, paint);
            y += 10;
            
            if (ValuesHelper.DELIVERED.equals(orderModel.getOrderStatus())) {
                drawKeyValuePair(canvas, "Delivered:", 
                    dateFormat.format(orderModel.getShipping().getDeliveredData()), leftMargin, y, paint);
                y += 10;
            }

            // Customer Details - compact
            y += 5;
            paint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));
            canvas.drawText("Customer Details:", leftMargin, y, paint);
            y += 10;
            
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
            canvas.drawText(orderModel.getCustomer().getFullName(), leftMargin, y, paint);
            y += 10;
            canvas.drawText("Ph: " + orderModel.getShipping().getShippingAddress().getMobileNumber(), 
                leftMargin, y, paint);
            y += 10;
            
            // Address with optimized word wrap
            String address = orderModel.getShipping().getShippingAddress().getFlatHouse() + ", " + 
                orderModel.getShipping().getShippingAddress().getAddress();
            String[] addressLines = splitTextIntoLines(address, 40);
            for (String line : addressLines) {
                canvas.drawText(line, leftMargin, y, paint);
                y += 10;
            }

            // Items Table - optimized for thermal paper
            y += 5;
            drawThermalItemsTable(canvas, paint, y, leftMargin, rightMargin);

            document.finishPage(page);

            // Save and share PDF
            String fileName = "AlwarMart_" + orderModel.getOrderId() + ".pdf";
            File file = new File(getExternalFilesDir(null), fileName);
            FileOutputStream fos = new FileOutputStream(file);
            document.writeTo(fos);
            document.close();
            fos.close();

            Toast.makeText(this, "Invoice downloaded successfully", Toast.LENGTH_SHORT).show();
            openPDF(file);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to generate invoice", Toast.LENGTH_SHORT).show();
        }
    }

    private void drawThermalItemsTable(Canvas canvas, Paint paint, int startY, int leftMargin, int rightMargin) {
        // Optimized column widths for thermal paper
        int[] columnWidths = {85, 30, 35, 40}; // Item, Qty, Price, Total
        
        // Headers - compact
        paint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));
        paint.setTextSize(7);
        
        int x = leftMargin;
        canvas.drawText("Item", x, startY, paint);
        x += columnWidths[0];
        canvas.drawText("Qty", x, startY, paint);
        x += columnWidths[1];
        canvas.drawText("Price", x, startY, paint);
        x += columnWidths[2];
        canvas.drawText("Total", x, startY, paint);

        // Separator
        startY += 3;
        paint.setStrokeWidth(0.5f);
        canvas.drawLine(leftMargin, startY, rightMargin, startY, paint);

        // Items - compact layout
        startY += 8;
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(7);
        
        double subtotal = 0;
        for (ShoppingCartsProductModel item : orderModel.getOrderItems()) {
            x = leftMargin;
            
            // Compact item name with weight
            String itemText = item.getProductName() + " (" + item.getWeight() + item.getWeightSIUnit() + ")";
            String[] itemLines = splitTextIntoLines(itemText, 22);
            for (String line : itemLines) {
                canvas.drawText(line, x, startY, paint);
                startY += 8;
            }
            startY -= 8;
            
            x += columnWidths[0];
            canvas.drawText(String.valueOf(item.getSelectableQuantity()), x, startY, paint);
            
            x += columnWidths[1];
            canvas.drawText("₹" + formatPrice(item.getPrice()), x, startY, paint);
            
            x += columnWidths[2];
            double itemTotal = item.getPrice() * item.getSelectableQuantity();
            canvas.drawText("₹" + formatPrice(itemTotal), x, startY, paint);
            
            subtotal += itemTotal;
            startY += 8;
        }

        // Totals section - compact
        startY += 3;
        paint.setStrokeWidth(0.5f);
        canvas.drawLine(leftMargin, startY, rightMargin, startY, paint);
        startY += 8;

        // Compact totals layout
        int totalsX = rightMargin - 70;
        drawThermalTotalLine(canvas, paint, "Subtotal:", formatPrice(subtotal), totalsX, startY);
        startY += 8;

        if (orderModel.getCouponCodeValue() > 0) {
            drawThermalTotalLine(canvas, paint, "Discount:", 
                "-" + formatPrice(orderModel.getCouponCodeValue()), totalsX, startY);
            startY += 8;
        }

        double shippingFee = orderModel.getShipping().getShippingFee();
        drawThermalTotalLine(canvas, paint, "Shipping:", formatPrice(shippingFee), totalsX, startY);
        startY += 8;

        if (orderModel.getDonate() > 0) {
            drawThermalTotalLine(canvas, paint, "Donation:", formatPrice(orderModel.getDonate()), 
                totalsX, startY);
            startY += 8;
        }

        // Final total
        paint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));
        drawThermalTotalLine(canvas, paint, "Total:", formatPrice(orderModel.getOrderTotalPrice()), 
            totalsX, startY);
    }

    private void drawThermalTotalLine(Canvas canvas, Paint paint, String label, String amount, int x, int y) {
        canvas.drawText(label, x, y, paint);
        float amountWidth = paint.measureText(amount);
        canvas.drawText(amount, x + 70 - amountWidth, y, paint);
    }

    private void drawKeyValuePair(Canvas canvas, String key, String value, int x, int y, Paint paint) {
        paint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));
        canvas.drawText(key, x, y, paint);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        canvas.drawText(value, x + 100, y, paint);
    }

    private String formatPrice(double price) {
        return String.format(Locale.getDefault(), "%.2f", price);
    }

    private String[] splitTextIntoLines(String text, int maxCharsPerLine) {
        List<String> lines = new ArrayList<>();
        int start = 0;
        while (start < text.length()) {
            int end = Math.min(start + maxCharsPerLine, text.length());
            if (end < text.length()) {
                int lastSpace = text.lastIndexOf(' ', end);
                if (lastSpace > start) {
                    end = lastSpace;
                }
            }
            lines.add(text.substring(start, end).trim());
            start = end;
        }
        return lines.toArray(new String[0]);
    }

    private void openPDF(File file) {
        Uri uri = FileProvider.getUriForFile(this,
            getApplicationContext().getPackageName() + ".provider", file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    private void displayOrderDetails() {
        if (orderModel.getCustomer() != null) {
            binding.orderContactName.setText(orderModel.getCustomer().getFullName());
            binding.orderContactPhone.setText(orderModel.getShipping().getShippingAddress().getMobileNumber());
            binding.orderShippingAddress.setText(orderModel.getShipping().getShippingAddress().getFlatHouse() + " " + orderModel.getShipping().getShippingAddress().getAddress());
            binding.orderShippingMethod.setText(orderModel.getShipping().getShippingMethod());
            binding.orderPaymentMethod.setText(orderModel.getPayment().getPaymentMethod());

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String formattedDateTime = dateFormat.format(orderModel.getOrderDate());
            binding.orderDate.setText(formattedDateTime);
            binding.orderDeliveryStatus.setText(orderModel.getOrderStatus());

            if (ValuesHelper.DELIVERED.equals(orderModel.getOrderStatus())) {
                binding.orderDeliveryOnBox.setVisibility(View.VISIBLE);
                String formattedDateOfDeliveredOrder = dateFormat.format(orderModel.getShipping().getDeliveredData());
                binding.orderDeliveryDate.setText(formattedDateOfDeliveredOrder);
                binding.download.setVisibility(View.VISIBLE);
            }



            OrderProductAdapter orderProductAdapter = new OrderProductAdapter(orderModel.getOrderItems(), this, this::onOrder);
            binding.orderItems.setAdapter(orderProductAdapter);
            binding.orderItems.setLayoutManager(new LinearLayoutManager(this));
            orderProductAdapter.notifyDataSetChanged();

            binding.discount.setText("₹"+orderModel.getCouponCodeValue());
            binding.subtotal.setText("₹" + ShoppingCartHelper.calculateTotalPrices(orderModel.getOrderItems()));
            binding.shippingFee.setText("₹" + orderModel.getShipping().getShippingFee());
            binding.grandTotal.setText("₹" + orderModel.getOrderTotalPrice());

        }
        else {
            Toast.makeText(this, "Customer information is missing", Toast.LENGTH_SHORT).show();
            this.finish();
        }
    }

    @Override
    public void onOrder(ShoppingCartsProductModel model) {
    }
}
