package com.example.hairsalon.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.example.hairsalon.R;
import com.example.hairsalon.activity.order.PayActivity;
import com.example.hairsalon.activity.product.DetailProductActivity;
import com.example.hairsalon.model.Product;
import java.util.List;

public class ProductItemAdapter extends RecyclerView.Adapter<ProductItemAdapter.ProductItemViewHolder> {
    private List<Product> productList;

    public ProductItemAdapter(List<Product> productList) {
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ProductItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductItemViewHolder holder, int position) {
        Product currentItem = productList.get(position);
        ImageRequest imageRequest = new ImageRequest(
                currentItem.getImageUrl(),
                response -> {
                    holder.imageView.setImageBitmap(response);
                    Log.d("Volley", "Image loaded successfully");
                },
                0, 0, ImageView.ScaleType.CENTER_INSIDE,
                Bitmap.Config.RGB_565,
                error -> Log.e("Volley", "Failed to load image", error)
        );

        Volley.newRequestQueue(holder.itemView.getContext()).add(imageRequest);
        holder.tvName.setText(currentItem.getName());
        holder.tvPrice.setText(currentItem.getAvrPrice() + "VNĐ");
        holder.ratingBar.setRating((float) currentItem.getRating());
        holder.tvSoldQuantity.setText("("+ String.valueOf(currentItem.getSold()) + " đã bán)");

        holder.imageView.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();
            Intent intent = new Intent(context, DetailProductActivity.class);
            intent.putExtra("productId", currentItem.getId());
            context.startActivity(intent);
        });

        holder.buyButton.setOnClickListener(v -> {
            Context context = holder.buyButton.getContext();
            Intent intent = new Intent(context, PayActivity.class);
            intent.putExtra("productId", currentItem.getId());
            intent.putExtra("detailName", currentItem.getName());
            intent.putExtra("detailPrice", currentItem.getAvrPrice());
            intent.putExtra("detailDescription", currentItem.getDescription());
            intent.putExtra("imageUrl", currentItem.getImageUrl());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView tvName;
        TextView tvPrice,tvSoldQuantity;
        Button buyButton;
        RatingBar ratingBar;

        public ProductItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_image);
            tvName = itemView.findViewById(R.id.tvProductItemName);
            tvPrice = itemView.findViewById(R.id.tvProuctItemPrice);
            buyButton = itemView.findViewById(R.id.buy_button);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            tvSoldQuantity = itemView.findViewById(R.id.tvSoldQuantity);

        }
    }
}
