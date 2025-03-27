package com.ronosoft.alwarmart.javaClasses;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.DataSnapshot;
import java.util.Locale;

public class OrderIdGenerator {

    /**
     * Generates a sequential order ID by atomically incrementing the "orderSequence" node
     * in Firebase Realtime Database.
     *
     * <p>It returns the order ID as a zero-padded 6-digit string (e.g., "000001").</p>
     *
     * @return A Task that resolves to the new order ID as a String.
     */
    public static Task<String> generateUniqueOrderId() {
        // Reference to the "orderSequence" node in Firebase Realtime Database.
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("orderSequence");

        // Create a TaskCompletionSource to convert the asynchronous transaction into a Task<String>
        TaskCompletionSource<String> tcs = new TaskCompletionSource<>();

        ref.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                Integer currentValue = currentData.getValue(Integer.class);
                if (currentValue == null) {
                    // If not initialized, start at 1
                    currentData.setValue(1);
                } else {
                    // Otherwise, increment by 1
                    currentData.setValue(currentValue + 1);
                }
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                if (error != null) {
                    tcs.setException(error.toException());
                } else if (!committed) {
                    tcs.setException(new Exception("Transaction not committed"));
                } else {
                    Integer orderId = currentData.getValue(Integer.class);
                    if (orderId == null) {
                        tcs.setException(new Exception("Order ID is null"));
                    } else {
                        // Format the order ID as a zero-padded 6-digit string.
                        String formattedOrderId = String.format(Locale.getDefault(), "%06d", orderId);
                        tcs.setResult(formattedOrderId);
                    }
                }
            }
        });

        return tcs.getTask();
    }
}
