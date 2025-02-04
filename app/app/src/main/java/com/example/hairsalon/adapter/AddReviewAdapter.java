package com.example.hairsalon.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.example.hairsalon.R;
import com.example.hairsalon.model.CartItem;
import com.example.hairsalon.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddReviewAdapter extends ArrayAdapter<CartItem> {
    private Context context;
    private List<CartItem> cartItems;
    private ReviewActionListener listener;

    public interface ReviewActionListener {
        void onRatingChanged(int position, float rating);
        void onCommentChanged(int position, String comment);
        void onPickImage(int position);
    }

    public AddReviewAdapter(@NonNull Context context, @NonNull List<CartItem> cartItems, ReviewActionListener listener) {
        super(context, 0, cartItems);
        this.context = context;
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.add_review_adapter, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CartItem cartItem = cartItems.get(position);
        holder.bind(cartItem, position, listener);

        return convertView;
    }

    static class ViewHolder {
        ImageView productImageView;
        TextView productNameTextView;
        TextView productPriceTextView;
        TextView productQuantityTextView;
        TextView productItemNameTextView;
        RatingBar productRatingBar;
        EditText reviewText;
        Button uploadImageButton;

        ViewHolder(View view) {
            productImageView = view.findViewById(R.id.product_image);
            productNameTextView = view.findViewById(R.id.product_name);
            productPriceTextView = view.findViewById(R.id.product_price);
            productQuantityTextView = view.findViewById(R.id.product_quantity);
            productItemNameTextView = view.findViewById(R.id.product_item_name);
            productRatingBar = view.findViewById(R.id.product_rating);
            reviewText = view.findViewById(R.id.review_text);
            uploadImageButton = view.findViewById(R.id.upload_image_button);
        }

        void bind(CartItem cartItem, int position, ReviewActionListener listener) {
            productNameTextView.setText(cartItem.getProductName());
            productPriceTextView.setText("Đơn giá " + Utils.formatPrice(cartItem.getPrice()));
            productQuantityTextView.setText(String.valueOf(cartItem.getQuantity()));
            productItemNameTextView.setText("Phân loại: " + cartItem.getProductItemName());

            Glide.with(productImageView.getContext())
                    .load(cartItem.getImageUrl())
                    .placeholder(R.drawable.placeholder_image)
                    .into(productImageView);

            productRatingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
                listener.onRatingChanged(position, rating);
            });

            reviewText.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    listener.onCommentChanged(position, s.toString());
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
            });

            uploadImageButton.setOnClickListener(v -> listener.onPickImage(position));
        }
    }
}

