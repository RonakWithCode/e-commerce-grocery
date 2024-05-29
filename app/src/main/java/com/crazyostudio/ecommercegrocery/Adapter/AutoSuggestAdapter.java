package com.crazyostudio.ecommercegrocery.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AutoSuggestAdapter extends ArrayAdapter<String> {
    private List<String> suggestions;

    public AutoSuggestAdapter(Context context, int resource) {
        super(context, resource);
        suggestions = new ArrayList<>();
    }

    public void setData(List<String> data) {
        suggestions.clear();
        suggestions.addAll(data);
        notifyDataSetChanged();  // Notify the adapter to update the view
    }

    @Override
    public int getCount() {
        return suggestions.size();
    }

    @Override
    public String getItem(int position) {
        return suggestions.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
        }
        TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
        tv.setText(getItem(position));
        return convertView;
    }
}
