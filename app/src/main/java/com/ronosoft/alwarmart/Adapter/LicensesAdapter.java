package com.ronosoft.alwarmart.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ronosoft.alwarmart.Model.ProductModel;
import com.ronosoft.alwarmart.R;

import java.util.ArrayList;

public class LicensesAdapter extends ArrayAdapter<ProductModel.License> {
    private final Activity context;
    private final ArrayList<ProductModel.License> licenses;

    public LicensesAdapter(Activity context, ArrayList<ProductModel.License> licenses) {
        super(context, R.layout.license_item, licenses);
        this.context = context;
        this.licenses = licenses;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = convertView;

        if (rowView == null) {
            rowView = inflater.inflate(R.layout.license_item, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.licenseName = rowView.findViewById(R.id.licenseName);
            viewHolder.licenseCode = rowView.findViewById(R.id.licenseCode);
            rowView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();
        ProductModel.License license = licenses.get(position);

        holder.licenseName.setText(license.getName());
        holder.licenseCode.setText(license.getCode());

        return rowView;
    }

    private class ViewHolder {
        TextView licenseName;
        TextView licenseCode;
    }
}
