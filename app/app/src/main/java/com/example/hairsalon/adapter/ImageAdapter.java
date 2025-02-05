package com.example.hairsalon.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hairsalon.R;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private List<String> imageUrls;
    private OnImageClickListener listener;

    public interface OnImageClickListener {
        void onImageClick(String imageUrl);
    }

    public ImageAdapter(List<String> imageUrls, OnImageClickListener listener) {
        this.imageUrls = imageUrls;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_small, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);
        Glide.with(holder.itemView.getContext()).load(imageUrl).into(holder.imageView);
        holder.itemView.setOnClickListener(v -> listener.onImageClick(imageUrl));
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
        }
    }
}

