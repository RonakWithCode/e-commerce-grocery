package com.ronosoft.alwarmart.Activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.FirebaseDatabase;
import com.ronosoft.alwarmart.R;
import com.ronosoft.alwarmart.databinding.ActivityOderBinding;

public class OderActivity extends AppCompatActivity {

    private ActivityOderBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Hide the action bar safely
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        } else {
            // Log error if action bar is unexpectedly null
            FirebaseDatabase.getInstance()
                    .getReference().child("Error")
                    .push().setValue("ActionBar is null in OderActivity");
        }

        // All further navigation and order flow is handled by the Navigation Component.
    }
}
