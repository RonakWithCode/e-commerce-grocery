package com.crazyostudio.ecommercegrocery.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crazyostudio.ecommercegrocery.Model.ProductModel;
import com.crazyostudio.ecommercegrocery.R;

import java.util.ArrayList;

/**
 * Adapter for providing search suggestions.
 */
public class SearchSuggestionsAdapter extends androidx.cursoradapter.widget.CursorAdapter {

    private ArrayList<ProductModel> suggestions = new ArrayList<>();

    public SearchSuggestionsAdapter(Context context) {
        super(context, null, 0);
    }

    public void setSuggestions(ArrayList<ProductModel> suggestions) {
        this.suggestions = suggestions;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return suggestions.size();
    }

    @Override
    public ProductModel getItem(int position) {
        return suggestions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View newView(Context context, android.database.Cursor cursor, ViewGroup parent) {
        // Inflate a view for each suggestion item
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.item_search_result, parent, false);
    }

    @Override
    public void bindView(View view, Context context, android.database.Cursor cursor) {
        // Bind data to the suggestion item views
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        ProductModel product = getItem(cursor.getPosition());
        holder.name.setText(product.getProductName());
        holder.price.setText("INR " + product.getPrice());
    }

    // ViewHolder pattern for caching view lookups
    static class ViewHolder {
        final TextView name;
        final TextView price;

        ViewHolder(View itemView) {
            name = itemView.findViewById(R.id.productName);
            price = itemView.findViewById(R.id.productPrice);
        }
    }
}
