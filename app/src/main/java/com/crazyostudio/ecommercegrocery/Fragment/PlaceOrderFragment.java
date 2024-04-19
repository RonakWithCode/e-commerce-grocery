package com.crazyostudio.ecommercegrocery.Fragment;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.crazyostudio.ecommercegrocery.Activity.OrderDetailsActivity;
import com.crazyostudio.ecommercegrocery.Dialog.DialogUtils;
import com.crazyostudio.ecommercegrocery.R;

import java.time.Instant;

public class PlaceOrderFragment extends DialogFragment {
    String orderId;
    String saveUPTO;

    public PlaceOrderFragment(String orderId, String saveUPTO) {
        this.orderId = orderId;
        this.saveUPTO = saveUPTO;
    }


    @SuppressLint("SetTextI18n")
    @Override @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_place_oder, null);
        builder.setView(view);
        builder.setCancelable(true);
        TextView saveTextView = view.findViewById(R.id.orderSave);
        saveTextView.setText("you save unto "+saveUPTO);
        Button orderStatusButton = view.findViewById(R.id.orderStatusButton);
        orderStatusButton.setOnClickListener(v -> {
            // TODO: Handle order status button click
            requireActivity().finish();
            Intent intent = new Intent(requireContext(), OrderDetailsActivity.class);
            intent.putExtra("orderID",orderId);
//            intent.putExtra("DialogUtils",true);
            startActivity(intent);
        });
        return builder.create();
    }
    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        requireActivity().finish();
        // Handle the dismiss event here
        // For example, you can send a callback to the parent fragment or activity
        // or perform any other necessary operations
    }
}
