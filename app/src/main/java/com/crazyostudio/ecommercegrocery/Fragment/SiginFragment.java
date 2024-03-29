package com.crazyostudio.ecommercegrocery.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.databinding.FragmentSiginBinding;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SiginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SiginFragment extends Fragment {
    FragmentSiginBinding binding;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SiginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SiginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SiginFragment newInstance(String param1, String param2) {
        SiginFragment fragment = new SiginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSiginBinding.inflate(inflater,container,false);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().hide(); // For Activity with AppCompatActivity


        NavController navController = Navigation.findNavController(requireActivity(),R.id.nav_host_fragment );

//        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//                                                  state_focused
//        binding.Number.setOnClickListener(v -> binding.indan.);
//        }

        binding.button.setOnClickListener(sigin->{
            if (binding.checkBox.isChecked()){
//                if (binding.Number.getText().toString().length()==10) {
                    Bundle bundle = new Bundle();
                    bundle.putString("number", binding.Number.getText().toString());
                    navController.navigate(R.id.action_siginFragment_to_authOTP,bundle);

//                }
            }
        });


        return binding.getRoot();
    }
}