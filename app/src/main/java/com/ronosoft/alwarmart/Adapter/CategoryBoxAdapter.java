package com.ronosoft.alwarmart.Adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import androidx.gridlayout.widget.GridLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ronosoft.alwarmart.Model.ProductCategoryModel;
import com.ronosoft.alwarmart.R;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.List;

public class CategoryBoxAdapter {

    private final Context context;
    private final List<ProductCategoryModel> categoryList;
    private final GridLayout gridLayout;

    public CategoryBoxAdapter(Context context, List<ProductCategoryModel> categoryList, GridLayout gridLayout) {
        this.context = context;
        this.categoryList = categoryList;
        this.gridLayout = gridLayout;
    }

    public void populateGridLayout() {
        if (categoryList == null || categoryList.isEmpty()) {
            return;
        }

        gridLayout.removeAllViews();
        gridLayout.setColumnCount(4); // Set 4 columns
        int rows = (categoryList.size() + 3) / 4; // Adjust number of rows based on the number of categories
        gridLayout.setRowCount(rows);

        for (ProductCategoryModel category : categoryList) {
            View view = LayoutInflater.from(context).inflate(R.layout.category_item, gridLayout, false);

            PhotoView imageView = view.findViewById(R.id.categoryImage);
            TextView tagTextView = view.findViewById(R.id.categoryTag);

            // Handle long tags by wrapping the text properly
            String tag = category.getTag();
            tagTextView.setText(TextUtils.ellipsize(tag, tagTextView.getPaint(), tagTextView.getMaxWidth(), TextUtils.TruncateAt.END));

            // Optimized Glide image loading
            Glide.with(context)
                    .load(category.getImageUri())
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.skeleton_shape)
                    .error(R.drawable.ic_error)
                    .into(imageView);

            // GridLayout parameters (2x4 grid with margins)
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1);
            params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1);
            params.setMargins(4, 4, 4, 4); // Add margin between items
            view.setLayoutParams(params);

            gridLayout.addView(view);
        }
    }
}
