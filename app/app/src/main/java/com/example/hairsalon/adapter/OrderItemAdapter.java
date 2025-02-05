package com.example.hairsalon.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.example.hairsalon.R;
import com.example.hairsalon.model.CartItem;
import com.example.hairsalon.utils.Utils;

import java.lang.ref.WeakReference;
import java.util.List;

public class OrderItemAdapter extends ArrayAdapter<CartItem> {
    private WeakReference<Context> mContext;
    private List<CartItem> mCartItems;

    public OrderItemAdapter(Context context, List<CartItem> cartItems) {
        super(context, 0, cartItems);
        mContext = new WeakReference<>(context);
        mCartItems = cartItems;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext.get()).inflate(R.layout.list_order_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CartItem cartItem = mCartItems.get(position);
        holder.bind(cartItem);

        return convertView;
    }

    static class ViewHolder {
        ImageView productImageView;
        TextView productNameTextView;
        TextView productPriceTextView;
        TextView productQuantityTextView;
        TextView productItemNameTextView;

        ViewHolder(View view) {
            productImageView = view.findViewById(R.id.product_image);
            productNameTextView = view.findViewById(R.id.product_name);
            productPriceTextView = view.findViewById(R.id.product_price);
            productQuantityTextView = view.findViewById(R.id.product_quantity);
            productItemNameTextView = view.findViewById(R.id.product_item_name);
        }

        void bind(CartItem cartItem) {
            productNameTextView.setText(cartItem.getProductName());
            productPriceTextView.setText("Đơn giá "+Utils.formatPrice(cartItem.getPrice()));
            productQuantityTextView.setText(String.valueOf(cartItem.getQuantity()));
            productItemNameTextView.setText( "Phân loại: " + cartItem.getProductItemName());
            Glide.with(productImageView.getContext())
                    .load(cartItem.getImageUrl())
                    .placeholder(R.drawable.placeholder_image)
                    .into(productImageView);
        }
    }
}
