package com.ronosoft.alwarmart.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ronosoft.alwarmart.R;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.List;

public class DialogSliderAdapter extends RecyclerView.Adapter<DialogSliderAdapter.DialogSliderViewHolder> {
    private Context context;
    private List<String> imageUrls;

    public DialogSliderAdapter(Context context, List<String> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    @NonNull
    @Override
    public DialogSliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_dialog_image_slider, parent, false);
        return new DialogSliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DialogSliderViewHolder holder, int position) {
        Glide.with(context).load(imageUrls.get(position)).into(holder.photoView);
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    class DialogSliderViewHolder extends RecyclerView.ViewHolder {
        PhotoView photoView;

        DialogSliderViewHolder(@NonNull View itemView) {
            super(itemView);
            photoView = itemView.findViewById(R.id.photoView);
        }
    }
}