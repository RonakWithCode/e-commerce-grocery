package com.ronosoft.alwarmart.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.ronosoft.alwarmart.R;
import com.ronosoft.alwarmart.Services.AuthService;
import com.ronosoft.alwarmart.Services.DatabaseService;
import com.ronosoft.alwarmart.databinding.FragmentAuthUserDetailsBinding;
import com.ronosoft.alwarmart.javaClasses.LoadingDialog;

// this us add to fragment

public class AuthUserDetailsFragment extends Fragment {
    FragmentAuthUserDetailsBinding binding;
    private String number;
    NavController navController;
    DatabaseService service;
    AuthService authService;
    String uid;
    LoadingDialog loadingDialog;

    public AuthUserDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            number = getArguments().getString("number");
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAuthUserDetailsBinding.inflate(inflater,container,false);
        loadingDialog = new LoadingDialog(requireActivity());
        navController = Navigation.findNavController(requireActivity(),R.id.nav_host_fragment);
        service = new DatabaseService();
        authService = new AuthService();
        uid = authService.getUserId();

        binding.number.setText("+91 "+number);
        binding.number.setEnabled(false);

//        binding.userImage.setOnClickListener(view -> ShowDialog());
        binding.nextBtn.setOnClickListener(onclick->{
            if (!binding.Name.getText().toString().isEmpty()) {
                loadingDialog.startLoadingDialog(); // Show loading dialog
                setupUser(binding.Name.getText().toString());
            }
        });
        return binding.getRoot();
    }
    void setupUser(String name){
        authService.updateName(name, new AuthService.UpdateNameListenerCallback() {
            @Override
            public void failureListener(Exception e) {
                loadingDialog.dismissDialog(); // Show loading dialog
            }

            @Override
            public void Success() {
                loadingDialog.dismissDialog(); // Show loading dialog

                requireActivity().finish();
//                startActivity(new Intent(requireContext(), MainActivity.class));
            }
        });
    }
}