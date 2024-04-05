    package com.crazyostudio.ecommercegrocery.Fragment;

    import android.app.AlertDialog;
    import android.os.Bundle;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.Toast;

    import androidx.annotation.NonNull;
    import androidx.fragment.app.Fragment;
    import androidx.fragment.app.FragmentTransaction;
    import androidx.recyclerview.widget.LinearLayoutManager;

    import com.crazyostudio.ecommercegrocery.Adapter.AddressAdapter;
    import com.crazyostudio.ecommercegrocery.Adapter.newAddressFragment;
    import com.crazyostudio.ecommercegrocery.Model.AddressModel;
    import com.crazyostudio.ecommercegrocery.Model.UserinfoModels;
    import com.crazyostudio.ecommercegrocery.R;
    import com.crazyostudio.ecommercegrocery.Services.DatabaseService;
    import com.crazyostudio.ecommercegrocery.databinding.FragmentUserAccountBinding;
    import com.crazyostudio.ecommercegrocery.interfaceClass.AddressInterface;
    import com.crazyostudio.ecommercegrocery.javaClasses.basicFun;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.auth.FirebaseUser;
    import com.google.firebase.firestore.DocumentSnapshot;
    import com.google.firebase.firestore.FieldValue;
    import com.google.firebase.firestore.FirebaseFirestore;

    import java.util.ArrayList;

    public class UserAccountFragment extends Fragment implements AddressInterface {
        private FragmentUserAccountBinding binding;
        private AddressAdapter addressAdapter;
        private ArrayList<AddressModel> adderes;
        private FirebaseAuth auth;
        DatabaseService databaseService;
        public UserAccountFragment(){}
//                   <com.google.android.material.textfield.TextInputLayout
//        android:id="@+id/emailField"
//        android:layout_marginTop="@dimen/_5sdp"
//        android:layout_width="match_parent"
//        android:theme="@style/FilledBoxStyle"
//        android:layout_height="wrap_content"
//        android:hint="@string/e_mail"
//        style="@style/Widget.MaterialComponents.TextInputLayout.FilledBox"
//        app:hintTextColor="@color/HintMainTextColor">
//
//                <com.google.android.material.textfield.TextInputEditText
//        android:id="@+id/email"
//        android:layout_width="match_parent"
//        android:background="@color/MainBackgroundColor"
//        android:layout_height="wrap_content"
//        android:autofillHints="emailAddress"
//        android:inputType="textPersonName"
//        android:textColor="@color/MainTextColor"
//        android:textColorHint="@color/MainTextColor" />
//            </com.google.android.material.textfield.TextInputLayout>


        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            binding = FragmentUserAccountBinding.inflate(inflater,container,false);
            auth = FirebaseAuth.getInstance();
            databaseService =   new DatabaseService();
            binding.backButton.setOnClickListener(back->getActivity().finish());

            binding.saveBtn.setOnClickListener(save->{
                showConfirmationDialog();
            });

            binding.AddAddress.setOnClickListener(address->{
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container,new newAddressFragment(),"newAddress");
                transaction.addToBackStack("newAddress");
                transaction.commit();
            });
            init();


            return binding.getRoot();
        }




      private void init() {
            binding.progressCircular.setVisibility(View.VISIBLE);
            adderes = new ArrayList<>();
            addressAdapter = new AddressAdapter(adderes,this,requireContext());
            LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false);
            binding.UserAddress.setLayoutManager(layoutManager);
            binding.UserAddress.setAdapter(addressAdapter);
           databaseService.getUserInfoByDocumentSnapshot(auth.getUid(), new DatabaseService.getUserInfoDocumentSnapshotCallback() {
                @Override

                public void onSuccess(DocumentSnapshot user) {
                    UserinfoModels models = user.toObject(UserinfoModels.class);
                    if (models != null) {
//                        binding.email.setText(models.getEmailAddress());
                        binding.Name.setText(models.getUsername());
                        if (models.getAddress() != null && !models.getAddress().isEmpty()) {
                            adderes.clear();
                            adderes.addAll(models.getAddress());
                            addressAdapter.notifyDataSetChanged();
                        }
                    }
                    binding.progressCircular.setVisibility(View.GONE);
                }

                @Override
                public void onError(String errorMessage) {

                }
            });

        }

        private void showConfirmationDialog() {

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Save Changes");
            builder.setMessage("Do you want to save the changes?");
            builder.setPositiveButton("Yes", (dialog, which) -> save());
            builder.setNegativeButton("No", ((dialog, which) -> dialog.dismiss()));
            builder.show();
        }

        private void save(){
            new DatabaseService().UpdateUserInfo(auth.getUid(), binding.Name.getText().toString(), new DatabaseService.UpdateUserInfoCallback() {
                @Override
                public void onSuccess() {
                    requireActivity().finish();
                }

                @Override
                public void onError(String errorMessage) {
                    binding.hintView.setText(errorMessage);
//                    binding.hintView.setText((CharSequence) ColorStateList.valueOf(Color.RED));
                    binding.Name.setError(errorMessage);
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
        @Override
        public void addersSelect(AddressModel adders) {}
    //    TODO make the function in database class for removing user AddressModel
        @Override
        public void remove(AddressModel address, int pos) {
            binding.progressCircular.setVisibility(View.VISIBLE);
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String userId = currentUser.getUid();
                databaseService.removeAdders(userId, address, new DatabaseService.removeAddersCallback() {
                    @Override
                    public void onSuccess() {
                        binding.progressCircular.setVisibility(View.INVISIBLE);
                        addressAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        basicFun.AlertDialog(requireContext(), "Failed to remove address: " + errorMessage);

                    }
                });
            }

            else {
                basicFun.AlertDialog(requireContext(), "User not authenticated.");
            }
        }



    }