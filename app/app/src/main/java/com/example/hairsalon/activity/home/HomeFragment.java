package com.example.hairsalon.activity.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hairsalon.R;
import com.example.hairsalon.activity.cart.CartActivity;
import com.example.hairsalon.activity.product.ListProductSearchActivity;
import com.example.hairsalon.adapter.GridAdapter;
import com.example.hairsalon.adapter.ProductItemAdapter;
import com.example.hairsalon.api.ApiService;
import com.example.hairsalon.databinding.FragmentHomeBinding;
import com.example.hairsalon.model.Brand;
import com.example.hairsalon.model.Category;
import com.example.hairsalon.model.PaginationData;
import com.example.hairsalon.model.Product;
import com.example.hairsalon.model.ProductItem;
import com.example.hairsalon.model.ResponseData;
import com.example.hairsalon.model.ResponseDataBrand;
import com.example.hairsalon.model.ResponseDataCategory;
import com.example.hairsalon.model.ResponseListData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;
    List<Product> productList = new ArrayList<>();
    String searchKey;
    Integer selectedCategoryId, selectedBrandId, selectedRating;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        binding.filter.setVisibility(View.GONE);
        binding.cartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.filter.getVisibility() == View.VISIBLE) {
                    binding.filter.setVisibility(View.GONE);
                } else {
                    binding.filter.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.searchBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (binding.searchBar.getRight() - binding.searchBar.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        String searchQuery = binding.searchBar.getText().toString();
                        Log.e("search", searchQuery);
                        performSearch(searchQuery);
                        return true;
                    }
                }
                return false;
            }
        });

        binding.searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                    searchKey = binding.searchBar.getText().toString().trim();
                    if (!searchKey.isEmpty()) {
                        if(searchKey.equals("") || searchKey == null ){
                            loadProducts();
                        } else {
                            performSearch(searchKey);
                        }
                    } else {
                        Toast.makeText(requireContext(), "Vui lòng nhập từ khóa tìm kiếm", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                return false;
            }
        });

        binding.txtApplyFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchKey = binding.searchBar.getText().toString();
                loadFilteredProducts(0,selectedCategoryId,selectedBrandId,selectedRating,searchKey);
            }
        });

        loadProducts();
        loadCategories();
        loadBrands();
        setupRatingSpinner();
        setupSortSpinner();
        return view;
    }


    private void performSearch(String query) {
        ApiService.apiService.searchProductByName(query).enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if (response.isSuccessful()) {
                    ResponseData responseData = response.body();
                    if (responseData != null && responseData.getStatus().equals("OK")) {
                        List<Map<String, Object>> productItemList = responseData.getData();
                        productList.clear();
                        for (Map<String, Object> map : productItemList) {
                            Product product = new Product();
                            product.setId(((Number) map.get("id")).intValue());
                            product.setName((String) map.get("name"))      ;
                            product.setDescription((String) map.get("description"));
                            product.setCategoryName((String) map.get("categoryName"));
                            product.setImageUrl((String) map.get("imageUrl"));
                            product.setCategory((String) map.get("category"));
                            product.setBrand((String) map.get("brand"));
                            product.setStatus((String) map.get("status"));
                            product.setRated(((Number) map.get("rated")).intValue());
                            product.setRating((double) map.get("rating"));
                            product.setSold(((Number) map.get("sold")).intValue());
                            product.setAvrPrice((String) map.get("avrPrice"));
                            product.setAttributes((Map<String, String>) map.get("attributes"));
                            product.setProductItems((List<ProductItem>) map.get("productItems"));
                            product.setDiscountPercent(((Number) map.get("discountPercent")).intValue());
                            productList.add(product);
                        }
                        if (productList != null) {
                            ProductItemAdapter adapter = new ProductItemAdapter(productList);
                            GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2);
                            binding.recyclerView.setLayoutManager(gridLayoutManager);
                            binding.recyclerView.setAdapter(adapter);
                        } else {
                            Log.e("Error", "No products found in pagination data");
                        }
                    } else {
                        Log.e("Error", "Unexpected status");
                    }
                } else {
                    Log.e("Error", "API call failed with error code: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                Log.e("Error", "API call failed: " + t.getMessage());
            }
        });
    }

    private void loadProducts() {
        ApiService.apiService.getProduct().enqueue(new Callback<ResponseListData>() {
            @Override
            public void onResponse(Call<ResponseListData> call, Response<ResponseListData> response) {
                if (response.isSuccessful()) {
                    ResponseListData responseData = response.body();
                    if (responseData != null && "OK".equals(responseData.getStatus())) {
                        PaginationData paginationData = responseData.getData();
                        if (paginationData != null) {
                            productList = paginationData.getProducts();
                            if (productList != null) {
                                ProductItemAdapter adapter = new ProductItemAdapter(productList);
                                GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2);
                                binding.recyclerView.setLayoutManager(gridLayoutManager);
                                binding.recyclerView.setAdapter(adapter);
                            } else {
                                Log.e("Error", "No products found in pagination data");
                            }
                        } else {
                            Log.e("Error", "No pagination data found in response");
                        }
                    } else {
                        Log.e("Error", "Invalid response status or no data");
                    }
                } else {
                    Log.e("Error", "API call failed with error code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseListData> call, Throwable t) {
                Log.e("Error", "API call failed: " + t.getMessage());
            }
        });
    }

    private void loadCategories() {
        ApiService.apiService.getCategories().enqueue(new Callback<ResponseDataCategory>() {
            @Override
            public void onResponse(Call<ResponseDataCategory> call, Response<ResponseDataCategory> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseDataCategory responseData = response.body();

                    List<Category> categoryList = responseData.getData().getCategoryList();
                    List<String> categoryNames = new ArrayList<>();
                    final Map<String, Integer> categoryMap = new HashMap<>();

                    // Thêm mục mặc định
                    categoryNames.add("--Chọn Phân loại--");
                    categoryMap.put("--Chọn Phân loại--", -1);

                    for (Category category : categoryList) {
                        categoryNames.add(category.getCategoryName());
                        categoryMap.put(category.getCategoryName(), category.getId());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categoryNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerCategory.setAdapter(adapter);

                    binding.spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String selectedCategoryName = parent.getItemAtPosition(position).toString();
                            selectedCategoryId = categoryMap.get(selectedCategoryName);

                            if (selectedCategoryId != -1) {
                                Log.d("Category", "Selected Category ID: " + selectedCategoryId);
                            } else {
                                Log.d("Category", "No valid category selected.");
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            // Không làm gì khi không có mục nào được chọn
                        }
                    });
                } else {
                    Log.e("Error", "Response not successful or body is null.");
                }
            }

            @Override
            public void onFailure(Call<ResponseDataCategory> call, Throwable t) {
                Log.e("Error", "Failed to load categories: " + t.getMessage());
            }
        });
    }

    private void loadBrands() {
        ApiService.apiService.getBrands().enqueue(new Callback<ResponseDataBrand>() {
            @Override
            public void onResponse(Call<ResponseDataBrand> call, Response<ResponseDataBrand> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseDataBrand responseData = response.body();

                    List<Brand> brandList = responseData.getData().getBrandList();
                    List<String> brandNames = new ArrayList<>();
                    final Map<String, Integer> brandMap = new HashMap<>();

                    // Thêm mục mặc định
                    brandNames.add("--Chọn Thương hiệu--");
                    brandMap.put("--Chọn Thương hiệu--", -1);

                    for (Brand brand : brandList) {
                        brandNames.add(brand.getName());
                        brandMap.put(brand.getName(), brand.getId());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, brandNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerBrand.setAdapter(adapter);

                    binding.spinnerBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String selectedBrandName = parent.getItemAtPosition(position).toString();
                            selectedBrandId = brandMap.get(selectedBrandName);

                            if (selectedBrandId != -1) {
                                Log.d("Brand", "Selected Brand ID: " + selectedBrandId);
                            } else {
                                Log.d("Brand", "No valid brand selected.");
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            // Không làm gì khi không có mục nào được chọn
                        }
                    });
                } else {
                    Log.e("Error", "Response not successful or body is null.");
                }
            }

            @Override
            public void onFailure(Call<ResponseDataBrand> call, Throwable t) {
                Log.e("Error", "Failed to load brands: " + t.getMessage());
            }
        });
    }


    private void setupRatingSpinner() {
        List<String> ratings = Arrays.asList("--Chọn đánh giá--","1", "2", "3", "4", "5");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, ratings);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerRating.setAdapter(adapter);
        binding.spinnerRating.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRating = binding.spinnerRating.getSelectedItemPosition();
                Log.e("rating", selectedRating.toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setupSortSpinner() {
        List<String> sorts = Arrays.asList("--Sắp xếp theo--", "Giá cao đến thấp", "Giá thấp đến cao");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, sorts);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerSort.setAdapter(adapter);
    }

    private void loadFilteredProducts(int page, Integer selectedCategoryId, Integer selectedBrandId, Integer minRating, String query) {
        String sort;
        if (binding.spinnerSort.getSelectedItem().toString().equals("Giá cao đến thấp")) {
            sort = "DESC";
        } else if (binding.spinnerSort.getSelectedItem().toString().equals("Giá thấp đến cao")) {
            sort = "ASC";
        } else {
            sort = null; // Không áp dụng sắp xếp
        }

        // Kiểm tra và đặt null nếu các giá trị <= 0
        Integer categoryId = (selectedCategoryId != null && selectedCategoryId > 0) ? selectedCategoryId : null;
        Integer brandId = (selectedBrandId != null && selectedBrandId > 0) ? selectedBrandId : null;
        Integer rating = (minRating != null && minRating > 0) ? minRating : null;

        ApiService.apiService.getFilteredProducts(page, 20, categoryId, brandId, rating, sort)
                .enqueue(new Callback<ResponseListData>() {
                    @Override
                    public void onResponse(Call<ResponseListData> call, Response<ResponseListData> response) {
                        productList.clear();
                        if (response.isSuccessful()) {
                            ResponseListData responseData = response.body();
                            if (responseData != null && "OK".equals(responseData.getStatus())) {
                                PaginationData paginationData = responseData.getData();
                                if (paginationData != null) {
                                    productList = paginationData.getProducts();
                                    if (productList != null) {
                                        ProductItemAdapter adapter = new ProductItemAdapter(productList);
                                        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2);
                                        binding.recyclerView.setLayoutManager(gridLayoutManager);
                                        binding.recyclerView.setAdapter(adapter);
                                        Log.e("Success", paginationData.toString());
                                    } else {
                                        Log.e("Error", "No products found in pagination data");
                                    }
                                } else {
                                    Log.e("Error", "No pagination data found in response");
                                }
                            } else {
                                Log.e("Error", "Invalid response status or no data");
                            }
                        } else {
                            Log.e("Error", "API call failed with error code: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseListData> call, Throwable t) {
                        // Xử lý lỗi
                        Log.e("API_FAILURE", "Failed to load products: " + t.getMessage());
                    }
                });
    }



}
