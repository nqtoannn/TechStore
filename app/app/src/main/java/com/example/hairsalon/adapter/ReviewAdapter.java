package com.example.hairsalon.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hairsalon.R;
import com.example.hairsalon.model.Review;

import java.util.List;
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private Context mContext;
    private List<Review> mReviewList;

    public ReviewAdapter(Context context, List<Review> reviewList) {
        this.mContext = context;
        this.mReviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_layout, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review currentReview = mReviewList.get(position);
        holder.userTextView.setText(currentReview.getCustomer());
        holder.ratingBar.setRating((float) currentReview.getRatingValue());
        holder.commentDate.setText(currentReview.getCreateAt());
        holder.contentTextView.setText(currentReview.getComment());

        if (!currentReview.getImageUrl().equals("null")) {
            holder.imageView.setVisibility(View.VISIBLE);
            Glide.with(holder.imageView.getContext())
                    .load(currentReview.getImageUrl())
                    .placeholder(R.drawable.placeholder_image)
                    .into(holder.imageView);
        } else {
            holder.imageView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mReviewList.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView userTextView, commentDate, contentTextView;
        RatingBar ratingBar;
        ImageView imageView;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            userTextView = itemView.findViewById(R.id.text_comment_user);
            ratingBar = itemView.findViewById(R.id.rating_bar);
            commentDate = itemView.findViewById(R.id.text_comment_date);
            contentTextView = itemView.findViewById(R.id.text_comment_content);
            imageView = itemView.findViewById(R.id.review_img);
        }
    }
}
