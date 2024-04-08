package com.crazyostudio.ecommercegrocery.Fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.crazyostudio.ecommercegrocery.Adapter.SearchAdapter;
import com.crazyostudio.ecommercegrocery.Adapter.SearchAdapterInterface;
import com.crazyostudio.ecommercegrocery.Model.ProductModel;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.databinding.FragmentSearchBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class SearchFragment extends Fragment implements SearchAdapterInterface {
    private FragmentSearchBinding binding;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference productsRef = db.collection("Product");

    private ActionBar actionBar;

    private SearchAdapter adapter;
    private static final int SPEECH_REQUEST_CODE = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);


        actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            // Hide the ActionBar when the fragment is created
            actionBar.hide();
        }


        adapter = new SearchAdapter(requireContext(),this);
//
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);


        binding.searchBar.setSpeechMode(true); // Enable voice search
        binding.searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                // Handle search state changes
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                // Perform a search based on the entered text

            }

            @Override
            public void onButtonClicked(int buttonCode) {
                if (buttonCode == MaterialSearchBar.BUTTON_SPEECH) {
                    Toast.makeText(getContext(), "BUTTON_SPEECH", Toast.LENGTH_SHORT).show();

                    openVoiceRecognizer();
                }
            }
        });

        binding.searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Trigger search when text changes
                String query = s.toString();
                searchProductByName(query);

            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed
            }
        });

        return binding.getRoot();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (actionBar != null) {
            actionBar.show();
        }
    }


    private void searchProductByName(String productName) {
        // Perform search only if product name is not empty
        if (!productName.isEmpty()) {
            // Convert the product name to lowercase for case-insensitive search
            String lowercaseProductName = productName.toLowerCase();

            // Create a query to filter products by name
            Query query = productsRef.whereGreaterThanOrEqualTo("productName", lowercaseProductName)
                    .whereLessThanOrEqualTo("productName", lowercaseProductName + "\uf8ff");

            // Execute the query
            query.get().addOnSuccessListener(queryDocumentSnapshots -> {
                // Handle successful query result
                ArrayList<ProductModel> models = new ArrayList<>();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    // Retrieve and process each document
                    ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
                    models.add(productModel);
                }
                // Set data to your adapter after the loop to avoid setting data for each iteration
                adapter.setData(models);
            }).addOnFailureListener(e -> {
                // Handle query failure
                System.err.println("Error searching for products: " + e.getMessage());
            });
        }
        else {
            // If product name is empty, do nothing or show a message
            System.out.println("Product name is empty");
        }
    }

    private void openVoiceRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
// This starts the activity and populates the intent with the speech text.
        startActivityForResult(intent, SPEECH_REQUEST_CODE);


    }
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);

            binding.searchBar.setText(spokenText);

            // Set cursor position to the end of the text
//            binding.searchBar.setSelection(binding.searchBar.getText().length());
            binding.searchBar.getSearchEditText().setSelection(spokenText.length());
            // Request focus on the search bar
            binding.searchBar.requestFocus();

            // Open the search bar
            binding.searchBar.openSearch();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onclick(ProductModel productModel) {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putParcelable("productDetails", productModel);
        ProductDetailsFragment productDetailsFragment = new ProductDetailsFragment();
        productDetailsFragment.setArguments(bundle);
        transaction.replace(R.id.loader,productDetailsFragment,"productDetailsFragment");
        transaction.addToBackStack("productDetailsFragment").commit();
    }
}
