package com.crazyostudio.ecommercegrocery.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crazyostudio.ecommercegrocery.MainActivity;
import com.crazyostudio.ecommercegrocery.Model.UserinfoModels;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.databinding.FragmentAuthUserDetailsBinding;
import com.crazyostudio.ecommercegrocery.javaClasses.basicFun;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;


public class AuthUserDetailsFragment extends Fragment {
    FragmentAuthUserDetailsBinding binding;
    FirebaseDatabase db;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private String number;
    NavController navController;

    private Uri userImage;
    private boolean IsUseImage;

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
        db  = FirebaseDatabase.getInstance();
        navController = Navigation.findNavController(requireActivity(),R.id.nav_host_fragment);

        binding.number.setText(number);
        binding.userImage.setOnClickListener(view -> ImagePicker.with(requireActivity())
                .crop()
                .compress(1024)
                .maxResultSize(800, 800)
                .start(123));
        binding.signup.setOnClickListener(onclick->{
            if (basicFun.CheckField(binding.Mail)&& basicFun.CheckField(binding.Name)){
                ProgressDialog p = new ProgressDialog(requireContext());
                p.setTitle("Waiting.....");
                p.show();
                UserinfoModels UserinfoModels;
                if (IsUseImage){
                    UserinfoModels = new UserinfoModels(FirebaseAuth.getInstance().getUid(), binding.Name.getText().toString(),binding.Mail.getText().toString(),userImage.toString(),number,true);
                    db.getReference().child("UserInfo").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).setValue(UserinfoModels).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(binding.Name.getText().toString()).setPhotoUri(userImage)
                                    .build();
                            assert user != null;
                            user.updateProfile(profileUpdates).addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
//                                            navController.navigate(R.id.action_authOTP_to_authUserDetailsFragment);
                                            if (p.isShowing()){
                                                p.dismiss();
                                                startActivity(new Intent(requireContext(), MainActivity.class));
                                            }
//                                            ////////////
                                        }else {
                                            basicFun.AlertDialog(requireContext(), task1.toString());
                                        }
                                    }).addOnFailureListener(e -> basicFun.AlertDialog(requireContext(),e.toString()));

                        }else {
                            basicFun.AlertDialog(requireContext(),task.toString());
                        }
                    }).addOnFailureListener(e -> basicFun.AlertDialog(requireContext(),e.toString()));
                }else {
                    UserinfoModels = new UserinfoModels(FirebaseAuth.getInstance().getUid(), binding.Name.getText().toString(),binding.Mail.getText().toString(),number,true);
                    db.getReference().child("UserInfo").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).setValue(UserinfoModels).addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(binding.Name.getText().toString()).build();
                            assert user != null;
                            user.updateProfile(profileUpdates).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
//                                            navController.navigate(R.id.action_authOTP_to_authUserDetailsFragment);
                                    if (p.isShowing()){
                                        p.dismiss();
//                                        startActivity(new Intent(requireContext(), MainActivity.class));
                                        getActivity().finish();
                                    }
//                                            ////////////
                                }else {
                                    basicFun.AlertDialog(requireContext(), task1.toString());
                                }
                            }).addOnFailureListener(e -> basicFun.AlertDialog(requireContext(),e.toString()));

                        }else {
                            basicFun.AlertDialog(requireContext(),task.toString());
                        }

                    }).addOnFailureListener(e -> basicFun.AlertDialog(requireContext(),e.toString()));
                }
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        assert data != null;
        if (data.getData() != null && requestCode == 123) {
            userImage = data.getData();
            IsUseImage = true;
            binding.userImage.setImageURI(data.getData());
        }
    }
}