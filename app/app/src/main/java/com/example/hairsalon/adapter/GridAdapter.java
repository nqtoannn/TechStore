package com.example.hairsalon.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.example.hairsalon.R;
import com.example.hairsalon.model.Product;
import com.example.hairsalon.model.ProductItem;
import com.example.hairsalon.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class GridAdapter extends BaseAdapter {

    private Context mContext;
    private List<Product> mProductList;

    public GridAdapter(Context context, List<Product> productList) {
        mContext = context;
        mProductList = productList;
    }

    @Override
    public int getCount() {
        return mProductList.size();
    }

    @Override
    public Object getItem(int position) {
        return mProductList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.grid_item, parent, false);
            holder = new ViewHolder();
            holder.imageViewProduct = convertView.findViewById(R.id.imageViewProduct);
            holder.textViewProductName = convertView.findViewById(R.id.textViewProductName);
            holder.textViewProductPrice = convertView.findViewById(R.id.textViewProductPrice);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Product product = mProductList.get(position);
        Log.d("Adapter", "Loading image for position: " + position + ", URL: " + product.getImageUrl());


        ImageRequest imageRequest = new ImageRequest(
                product.getImageUrl(),
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        holder.imageViewProduct.setImageBitmap(response);
                        Log.d("Volley", "Image loaded successfully");
                    }
                },
                0,
                0,
                ImageView.ScaleType.CENTER_INSIDE,
                Bitmap.Config.RGB_565,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Failed to load image", error);
                    }
                }
        );

        Volley.newRequestQueue(mContext).add(imageRequest);

        // Set the product name and price
        holder.textViewProductName.setText(product.getName());
        holder.textViewProductPrice.setText(product.getAvrPrice());

        return convertView;
    }

    static class ViewHolder {
        ImageView imageViewProduct;
        TextView textViewProductName;
        TextView textViewProductPrice;
    }
}
