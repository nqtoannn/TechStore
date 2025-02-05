package com.example.hairsalon.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.hairsalon.R;
import com.example.hairsalon.constants.Constant;
import com.example.hairsalon.model.CartItem;
import com.example.hairsalon.utils.Utils;

import java.lang.ref.WeakReference;
import java.util.List;

public class CartItemAdapter extends ArrayAdapter<CartItem> {
    private WeakReference<Context> mContext;
    private List<CartItem> mCartItems;

    public CartItemAdapter(Context context, List<CartItem> cartItems) {
        super(context, 0, cartItems);
        mContext = new WeakReference<>(context);
        mCartItems = cartItems;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext.get()).inflate(R.layout.list_cart_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CartItem cartItem = mCartItems.get(position);
        holder.bind(cartItem);

        holder.deleteIcon.setOnClickListener(v -> showDeleteConfirmationDialog(position));
        return convertView;
    }

    private void showDeleteConfirmationDialog(final int position) {
        Context context = mContext.get();
        if (context == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Xác nhận xóa!")
                .setMessage("Bạn có chắc chắn muốn xóa sản phẩm? " + mCartItems.get(position).getProductItemName() + "?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    Integer itemId = mCartItems.get(position).getId();
                    mCartItems.remove(position);
                    removeCartItem(itemId);
                    notifyDataSetChanged();
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .create().show();
    }

    private void removeCartItem(Integer cartItemId) {
        String apiUrl = Constant.baseUrl + "customer/deleteCartItem/" + cartItemId;
        StringRequest request = new StringRequest(Request.Method.DELETE, apiUrl,
                response -> Log.d("RemoveCartItemResponse", response),
                error -> Log.e("RemoveCartItemError", apiUrl, error)
        );
        Volley.newRequestQueue(mContext.get()).add(request);
    }

    static class ViewHolder {
        ImageView productImageView;
        TextView productNameTextView;
        TextView productPriceTextView;
        TextView productQuantityTextView;
        ImageView deleteIcon;
        TextView productItemNameTextView;
        CheckBox checkBox;

        ViewHolder(View view) {
            productImageView = view.findViewById(R.id.product_image);
            productNameTextView = view.findViewById(R.id.product_name);
            productPriceTextView = view.findViewById(R.id.product_price);
            productQuantityTextView = view.findViewById(R.id.product_quantity);
            deleteIcon = view.findViewById(R.id.delete_icon);
            productItemNameTextView = view.findViewById(R.id.product_item_name);
            checkBox = view.findViewById(R.id.product_checkbox);
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
            checkBox.setChecked(cartItem.isSelected());
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                cartItem.setSelected(isChecked);
                if (cartItem.getCheckedListener() != null) {
                    cartItem.getCheckedListener().onCheckedChanged(isChecked);
                }
            });
        }
    }
}
