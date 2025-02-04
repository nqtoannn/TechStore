package com.example.hairsalon.adapter;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.example.hairsalon.R;
import com.example.hairsalon.activity.employee.OrderDetailEmployee;
import com.example.hairsalon.activity.order.OrderDetail;
import com.example.hairsalon.model.Order;
import com.example.hairsalon.model.OrderItem;
import com.example.hairsalon.utils.Utils;

import java.util.List;

public class OrderHistoryAdapter extends ArrayAdapter<Order> {

    private Context mContext;

    public OrderHistoryAdapter(Context context, List<Order> objects) {
        super(context, 0, objects);
        this.mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(R.layout.order_history_item, parent, false);
            holder = new ViewHolder();
            holder.productImageView = view.findViewById(R.id.imageView);
            holder.textProductName = view.findViewById(R.id.textProductName);
            holder.textPrice = view.findViewById(R.id.textPrice);
            holder.textQuantity = view.findViewById(R.id.textQuantity);
            holder.textOrderDate = view.findViewById(R.id.textOrderDate);
            holder.textStatus = view.findViewById(R.id.textStatus);
            holder.productItemName = view.findViewById(R.id.product_item_name);
            holder.textTotalPrice = view.findViewById(R.id.totalPriceLabel);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Order order = getItem(position);

        if (order != null) {
            OrderItem orderItem = order.getOrderItems().get(0);
            Integer totalQuantity = order.getOrderItems().size();
            holder.textProductName.setText(orderItem.getProductName());
            holder.productItemName.setText("Phân loại :"+orderItem.getProductItemName());
            holder.textPrice.setText("Giá: " + Utils.formatPrice(orderItem.getPrice()));
            holder.textQuantity.setText("SL: " + orderItem.getQuantity());
            holder.textOrderDate.setText("Đặt ngày: " + order.getOrderDate());
            holder.textTotalPrice.setText("Tổng("+totalQuantity+"sp): "+ Utils.formatPrice(order.getTotalPrice()));
            switch (order.getOrderStatus()) {
                case "PROCESSING":
                    holder.textStatus.setText("Đơn hàng đang chờ xác nhận");
                    break;
                case "ACCEPTED":
                    holder.textStatus.setText("Người bán đang chuẩn bị hàng");
                    break;
                case "CANCEL":
                    holder.textStatus.setText("Đơn hàng đã bị hủy");
                    break;
                case "DELIVERY":
                    holder.textStatus.setText("Đơn hàng đang được vận chuyển");
                    break;
                case "SUCCESS":
                case "REVIEWED":
                    holder.textStatus.setText("Đơn hàng đã hoàn thành");
                    break;
                case "PAID":
                    holder.textStatus.setText("Đơn hàng đã được thanh toán");
                    break;
                case "WAITING PAYMENT":
                    holder.textStatus.setText("Đơn hàng đang chờ thanh toán");
                    break;
                default:
                    holder.textStatus.setText(order.getOrderStatus());

            }
            ImageRequest imageRequest = new ImageRequest(
                    orderItem.getProductItemUrl(),
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {
                            holder.productImageView.setImageBitmap(response);
                            Log.d("Volley", "Image loaded successfully");
                        }
                    },
                    0,
                    0,
                    ImageView.ScaleType.CENTER_CROP,
                    Bitmap.Config.RGB_565,
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Volley", "Failed to load image", error);
                        }
                    }
            );
            RequestQueue requestQueue = Volley.newRequestQueue(mContext);
            requestQueue.add(imageRequest);
        }

        holder.productImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = mContext.getSharedPreferences("User", Context.MODE_PRIVATE);
                String role = sharedPreferences.getString("role", "CUSTOMER");
                if(role.equals("CUSTOMER")){
                    Intent intent = new Intent(mContext, OrderDetail.class);
                    intent.putExtra("orderId", order.getId());
                    mContext.startActivity(intent);
                }
                else {
                    Intent intent = new Intent(mContext, OrderDetailEmployee.class);
                    intent.putExtra("orderId", order.getId());
                    mContext.startActivity(intent);
                }
            }
        });


        return view;
    }

    static class ViewHolder {
        ImageView productImageView;
        TextView textProductName;
        TextView textPrice;
        TextView textQuantity;
        TextView textOrderDate;
        TextView textStatus;
        TextView productItemName;
        TextView textTotalPrice;

    }
}
