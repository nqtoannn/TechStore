package com.example.hairsalon.api;

import androidx.annotation.Nullable;

import com.example.hairsalon.constants.Constant;
import com.example.hairsalon.model.AuthenticationRequest;
import com.example.hairsalon.model.ResponseAuthData;
import com.example.hairsalon.model.ResponseData;
import com.example.hairsalon.model.ResponseDataBrand;
import com.example.hairsalon.model.ResponseDataCategory;
import com.example.hairsalon.model.ResponseListData;
import com.example.hairsalon.model.ResponseProduct;
import com.example.hairsalon.model.ResponseString;
import com.example.hairsalon.model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    ApiService apiService = new Retrofit.Builder()
            .baseUrl(Constant.baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService.class);

    //Product -- Cate -- Brand
    @GET("products/findAll")
    Call<ResponseListData> getProduct();
    @GET("brand/findAll")
    Call<ResponseDataBrand> getBrands();
    @GET("category/findAll")
    Call<ResponseDataCategory> getCategories();
    @GET("products/findById/{productId}")
    Call<ResponseProduct> getProductById(@Path("productId") Integer productId);
    @GET("products/search/{productName}")
    Call<ResponseData> searchProductByName(@Path("productName") String productItemName);
    @GET("products/filter")
    Call<ResponseListData> getFilteredProducts(
            @Query("page") int page,
            @Query("size") int size,
            @Query("categoryId") @Nullable Integer categoryId,
            @Query("brandId") @Nullable Integer brandId,
            @Query("minRating") @Nullable Integer minRating,
            @Query("sortOrder") String sortOrder
    );
    @GET("payment/getAll")
    Call<ResponseData> getAllPayment();
    @GET("products/review/getAll/{productId}")
    Call<ResponseData> getAllComment(@Path("productId") Integer productId);

    //Account
    @POST("account/forgotPassword")
    Call<ResponseData> forgotPassword(@Body String email);
    @PUT("account/resetPassword")
    Call<ResponseString> changePassword(@Body JsonObject json);

    //Customer
    @GET("customer/orders/getOrderById/{orderId}")
    Call<ResponseData> getOrderById(@Path("orderId") Integer orderId, @Header("Authorization") String token);
    @GET("customer/getOrderPaymentUrl/{orderId}")
    Call<ResponseString> getPaymentUrlByOrderId(@Path("orderId") Integer orderId, @Header("Authorization") String token);
    @GET("customer/findById/{customerId}")
    Call<ResponseData> getEmployeeById(@Path("customerId") Integer customerId, @Header("Authorization") String token);
    @PUT("customer/updateUserProfile")
    Call<ResponseData> updateUserProfile(@Body JsonObject json, @Header("Authorization") String token); //done
    @GET("customer/findById/{customerId}")
    Call<ResponseData> getCustomerById(@Path("customerId") Integer customerId, @Header("Authorization") String token);
    @GET("customer/findAllCartItems/{userId}")
    Call<ResponseData> getAllCartItemsByCartId(@Path("userId") Integer userId, @Header("Authorization") String token);
    @GET("customer/orders/getAllOrdersByCustomerId/{customerId}")
    Call<ResponseData> getAllOrderByCustomerId(@Path("customerId") Integer customerId, @Header("Authorization") String token);
    @PUT("customer/uploadReviewImg")
    Call<ResponseData> uploadImgReview(@Part MultipartBody.Part file,
                                       @Query("namePath") String namePath,
                                       @Query("reviewId") Integer reviewId, @Header("Authorization") String token);

    //Employee
    @GET("employee/order/findAllWithStatus/{statusId}")
    Call<ResponseData> getOrderWithStatus(@Path("statusId") Integer statusId, @Header("Authorization") String token);
    @Multipart
    @POST("employee/order/confirmOrder") //done
    Call<Void> uploadOrderFile(@Part MultipartBody.Part file,
                          @Query("namePath") String namePath,
                          @Query("orderId") Integer orderId, @Header("Authorization") String token);

    //Auth
    @POST("auth/register")
    Call<ResponseAuthData> registerUser(@Body JsonObject json); //done
    @POST("auth/authenticate")
    Call<ResponseAuthData> authenticateUser(@Body AuthenticationRequest request); //done
    @PUT("auth/updateStatusUser")
    Call<ResponseData> updateUserStatus(@Body JsonObject json);
}
