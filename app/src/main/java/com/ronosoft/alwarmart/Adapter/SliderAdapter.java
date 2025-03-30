package com.ronosoft.alwarmart.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ronosoft.alwarmart.R;

import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {
    private List<String> imageUrls;
    private Context context;
    private OnImageClickListener onImageClickListener;

    public interface OnImageClickListener {
        void onImageClick(int position);
    }

    public SliderAdapter(Context context, List<String> imageUrls, OnImageClickListener onImageClickListener) {
        this.context = context;
        this.imageUrls = imageUrls;
        this.onImageClickListener = onImageClickListener;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image_slider, parent, false);
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        // If the position is within the original list, load the image URL.
        if (position < imageUrls.size()) {
            Glide.with(context)
                    .load(imageUrls.get(position))
                    .into(holder.imageView);
        } else if (position == imageUrls.size()) {
            // First extra item: load contact info image.
            Glide.with(context)
                    .load(R.drawable.contact_info)
                    .into(holder.imageView);
        } else if (position == imageUrls.size() + 1) {
            // Second extra item: load disclaimer image.
            Glide.with(context)
                    .load(R.drawable.disclaimer)
                    .into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        // Total count = original images + 2 extra images.
        return imageUrls.size() + 2;
    }

    class SliderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;

        SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onImageClickListener != null) {
                onImageClickListener.onImageClick(getAdapterPosition());
            }
        }
    }
}
