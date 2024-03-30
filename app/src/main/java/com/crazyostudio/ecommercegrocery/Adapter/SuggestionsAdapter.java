package com.crazyostudio.ecommercegrocery.Adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.crazyostudio.ecommercegrocery.Model.ProductModel;
import com.crazyostudio.ecommercegrocery.R;

import java.util.List;

public class SuggestionsAdapter extends ArrayAdapter<ProductModel> {

    public SuggestionsAdapter(@NonNull Context context, @NonNull List<ProductModel> suggestions) {
        super(context, R.layout.item_search_result, suggestions);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.item_search_result, parent, false);
            holder = new ViewHolder();
            holder.productImageView = view.findViewById(R.id.product_image);
            holder.productNameTextView = view.findViewById(R.id.product_name);
            holder.productPriceTextView = view.findViewById(R.id.product_price);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        ProductModel suggestion = getItem(position);

        if (suggestion != null) {
            Glide.with(getContext()).load(suggestion.getImageURL().get(0)).placeholder(R.drawable.placeholder).into(holder.productImageView);
            holder.productNameTextView.setText(suggestion.getProductName());
            holder.productPriceTextView.setText("₹" + suggestion.getPrice());
        }

        return view;
    }

    static class ViewHolder {
        ImageView productImageView;
        TextView productNameTextView;
        TextView productPriceTextView;
    }
}
