package com.crazyostudio.ecommercegrocery.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.crazyostudio.ecommercegrocery.Model.Variations;
import com.crazyostudio.ecommercegrocery.R;

import java.util.List;



public class VariantsAdapter extends RecyclerView.Adapter<VariantsAdapter.ViewHolder> {
    public interface VariantsCallback{
        void Product(String id);
    }
    private Context context;
    private List<Variations> variationsList;
    VariantsCallback callback;

    private String Id;
    public VariantsAdapter(Context context, List<Variations> variationsList,String id,VariantsCallback callback) {
        this.context = context;
        this.variationsList = variationsList;
        this.Id = id;
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_variant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Variations variation = variationsList.get(position);
        if (variation.getId().equals(Id)) {
            holder.itemView.setBackgroundResource(R.drawable.border_background);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.border_background_black);
        }
        holder.variantName.setText(variation.getName());
        holder.variantWeight.setText("₹"+variation.getWeightWithSIUnit());
        holder.itemView.setOnClickListener(v -> callback.Product(variation.getId()));
    }

    @Override
    public int getItemCount() {
        return variationsList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView variantName;
        TextView variantWeight;

        ViewHolder(View itemView) {
            super(itemView);
            variantName = itemView.findViewById(R.id.variation_name);
            variantWeight = itemView.findViewById(R.id.variation_weight);
        }
    }
}
