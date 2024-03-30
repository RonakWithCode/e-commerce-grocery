package com.crazyostudio.ecommercegrocery.Fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.crazyostudio.ecommercegrocery.Adapter.SearchAdapter;
import com.crazyostudio.ecommercegrocery.Model.ProductModel;
import com.crazyostudio.ecommercegrocery.databinding.FragmentSearchBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
public class SearchFragment extends Fragment {
    private FragmentSearchBinding binding;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference productsRef = db.collection("Product");

    private ActionBar actionBar;

    private SearchAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
//        View view = binding.getRoot();

//        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar())
//                .hide();

        actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            // Hide the ActionBar when the fragment is created
            actionBar.hide();
        }


        adapter = new SearchAdapter(requireContext());
//
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);



//        // Find the search bar from the toolbar's custom view
//        MaterialSearchBar searchBar = requireActivity().findViewById(R.id.toolbar).findViewById(R.id.searchBar);
//        searchBar.requestFocus();
//        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//        if (imm != null) {
//            imm.showSoftInput(searchBar, InputMethodManager.SHOW_IMPLICIT);
//        }
//        // Listen for text changes in the search EditText
        binding.searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Trigger search when text changes
                searchProductByName(s.toString());
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

        // Show the ActionBar when the fragment is destroyed (when navigating back)
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
        } else {
            // If product name is empty, do nothing or show a message
            System.out.println("Product name is empty");
        }
    }
}
