package com.quoctoan.shoestore.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.google.gson.Gson;
import com.quoctoan.shoestore.entity.*;
import com.quoctoan.shoestore.model.*;
import com.quoctoan.shoestore.respository.CategoryRepository;
import com.quoctoan.shoestore.respository.ProductItemRepository;
import com.quoctoan.shoestore.respository.ProductRepository;
import com.quoctoan.shoestore.respository.PromotionRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;


@Transactional
@Service
public class ProductService {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductItemRepository productItemRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    StorageService storageService;
    @Autowired
    PromotionRepository promotionRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    public ProductResponseModel convertToProductResponseModel(Product product ){
        ProductResponseModel productModel = new ProductResponseModel();
        productModel.setId(product.getId());
        productModel.setName(product.getName());
        productModel.setCategoryName(product.getCategory().getCategoryName());
        productModel.setDescription(product.getDescription());
        productModel.setImageUrl(product.getImageUrl());
        productModel.setCategory(product.getCategory().getCategoryName());
        productModel.setBrand(product.getBrand().getName());
        productModel.setStatus(product.getStatus());
        productModel.setRating(product.getRating());
        productModel.setSold(product.getSold());
        productModel.setRated(product.getRated());
        try {
            Map<String, String> attributesMap = objectMapper.readValue(product.getAttributes(), Map.class);
            productModel.setAttributes(attributesMap);
        } catch (Exception e) {
            productModel.setAttributes(null);
        }
        List<ProductItemModel> productItemModels = new ArrayList<>();
        product.getProductItems().forEach(productItem -> {
            ProductItemModel productItemModel = new ProductItemModel();
            BeanUtils.copyProperties(productItem, productItemModel);
            productItemModels.add(productItemModel);
        });
        productModel.setProductItems(productItemModels);
        productModel.setDiscountPercent(0);
        Optional<Integer> discount = promotionRepository.findDiscountPercentForProduct(product.getId(), LocalDate.now());
        discount.ifPresent(productModel::setDiscountPercent);
        productModel.calculateAvrPrice();
        return productModel;
    }


//    public ResponseEntity<ResponseObject> findAll() {
//        List<Product> productList = productRepository.findAllProduct();
//        List<ProductResponseModel> productResponseModelList = new ArrayList<>();
//        productList.forEach(product -> {
//            productResponseModelList.add(convertToProductResponseModel(product));
//        });
//
//        // Kiểm tra và trả về kết quả
//        if (!productResponseModelList.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.OK)
//                    .body(new ResponseObject("OK", "Successfully", productResponseModelList));
//        } else {
//            return ResponseEntity.status(HttpStatus.OK)
//                    .body(new ResponseObject("Not found", "Not found", ""));
//        }
//    }
    public ResponseEntity<ResponseObject> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.findAll(pageable); // Sử dụng Pageable ở đây

        List<ProductResponseModel> productResponseModelList = productPage.stream()
                .map(this::convertToProductResponseModel)
                .collect(Collectors.toList());

        // Kiểm tra và trả về kết quả
        if (!productResponseModelList.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("products", productResponseModelList);
            response.put("currentPage", productPage.getNumber());
            response.put("totalItems", productPage.getTotalElements());
            response.put("totalPages", productPage.getTotalPages());

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("OK", "Successfully", response));
        } else {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("Not found", "Not found", ""));
        }
    }

    public ResponseEntity<ResponseObject> findAllProduct() {
        List<Product> productList = productRepository.findAll();
        if(!productList.isEmpty()){
            List<ProductResponseModel> productResponseModelList = new ArrayList<>();
            productList.forEach(product -> {
                productResponseModelList.add(convertToProductResponseModel(product));
            });
            return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseObject("OK", "Successfully", productResponseModelList));
        } else {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("Not found", "Not found", ""));
        }
    }

    public ResponseEntity<ResponseObject> findAllWithAllStatus() {
        List<Product> productList = productRepository.findAll();
        List<ProductResponseModel> productResponseModelList = new ArrayList<>();
        productList.forEach(product -> {
            productResponseModelList.add(convertToProductResponseModel(product));
        });
        if (!productResponseModelList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("OK", "Successfully", productResponseModelList));
        } else {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("Not found", "Not found", ""));
        }
    }

    public ResponseEntity<ResponseObject> findById(Integer id) {
        Optional<Product> productOtp = productRepository.findById(id);
        if (productOtp.isPresent()) {
            Product product = productOtp.get();
            ProductResponseModel productModel = convertToProductResponseModel(product);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", productModel));
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("Not found", "Not found", ""));
        }
    }

//    public ResponseEntity<ResponseObject> findAllByCateId(Integer categoryId) {
//        List<Product> productList = productRepository.findByCategoryId(categoryId);
//        List<ProductResponseModel> productResponseModelList = new ArrayList<>();
//        productList.forEach(product -> {
//            productResponseModelList.add(convertToProductResponseModel(product));
//        });
//        if (!productResponseModelList.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", productResponseModelList));
//        } else {
//            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("Not found", "Not found", ""));
//        }
//    }

    public ResponseEntity<ResponseObject> findAllByFilters(
            Integer categoryId, Integer brandId, Double minRating, String sortOrder, int page, int size) {
        if( sortOrder.toUpperCase().isEmpty()){
            Pageable pageable = PageRequest.of(page, size);
            Page<Product> productPage = productRepository.findByFilters(categoryId, brandId, minRating, pageable);

            List<ProductResponseModel> productResponseModelList = productPage.stream()
                    .map(this::convertToProductResponseModel)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("products", productResponseModelList);
            response.put("currentPage", productPage.getNumber());
            response.put("totalItems", productPage.getTotalElements());
            response.put("totalPages", productPage.getTotalPages());

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("OK", "Successfully", response));
        }
        else {
            if (sortOrder.toUpperCase().equals("DESC")) {
                Sort sort = Sort.by("sold").descending();
                Pageable pageable = PageRequest.of(page, size, sort);

                Page<Product> productPage = productRepository.findByFiltersDesc(categoryId, brandId, minRating, pageable);

                List<ProductResponseModel> productResponseModelList = productPage.stream()
                        .map(this::convertToProductResponseModel)
                        .collect(Collectors.toList());

                Map<String, Object> response = new HashMap<>();
                response.put("products", productResponseModelList);
                response.put("currentPage", productPage.getNumber());
                response.put("totalItems", productPage.getTotalElements());
                response.put("totalPages", productPage.getTotalPages());

                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseObject("OK", "Successfully", response));
            } else {
                Sort sort = Sort.by("sold").ascending();
                Pageable pageable = PageRequest.of(page, size, sort);

                Page<Product> productPage = productRepository.findByFiltersAsc(categoryId,brandId, minRating, pageable);

                List<ProductResponseModel> productResponseModelList = productPage.stream()
                        .map(this::convertToProductResponseModel)
                        .collect(Collectors.toList());

                Map<String, Object> response = new HashMap<>();
                response.put("products", productResponseModelList);
                response.put("currentPage", productPage.getNumber());
                response.put("totalItems", productPage.getTotalElements());
                response.put("totalPages", productPage.getTotalPages());

                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseObject("OK", "Successfully", response));
            }
        }
    }




    public ResponseEntity<ResponseObject> findAllByCateId(Integer categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.findByCategoryId(categoryId, pageable);

        List<ProductResponseModel> productResponseModelList = productPage.stream()
                .map(this::convertToProductResponseModel)
                .collect(Collectors.toList());

        // Tạo đối tượng phản hồi có thêm thông tin phân trang
        if (!productResponseModelList.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("products", productResponseModelList);
            response.put("currentPage", productPage.getNumber());
            response.put("totalItems", productPage.getTotalElements());
            response.put("totalPages", productPage.getTotalPages());

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("OK", "Successfully", response));
        } else {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("Not found", "Not found", ""));
        }
    }


    public ResponseEntity<ResponseObject> findByName(String productName) {
        List<Product> productList = productRepository.findByProductName(productName);
        List<ProductResponseModel> productResponseModelList = new ArrayList<>();
        productList.forEach(product -> {
            productResponseModelList.add(convertToProductResponseModel(product));
        });
        if (!productResponseModelList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", productResponseModelList));
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("Not found", "Not found", ""));
        }
    }


    public ResponseEntity<Object> add(String json) {
        JsonNode jsonNode;
        JsonMapper jsonMapper = new JsonMapper();
        try {
            jsonNode = jsonMapper.readTree(json);
            String productName = jsonNode.get("productName") != null ? jsonNode.get("productName").asText() : "null";
            String description = jsonNode.get("description") != null ? jsonNode.get("description").asText() : "null";
            String imageUrl = jsonNode.get("imageUrl") != null ? jsonNode.get("imageUrl").asText() : "null";
            Integer categoryId = jsonNode.get("categoryId") != null ? jsonNode.get("categoryId").asInt() : -1;
            Product product = new Product();
            product.setName(productName);
            product.setCategory(categoryRepository.getById(categoryId));
            product.setImageUrl(imageUrl);
            product.setDescription(description);
            product.setStatus("ACTIVE");
            productRepository.save(product);

        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("Error", e.getMessage(), ""));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", ""));
    }

    public ResponseEntity<Object> addProduct(String json) {
        JsonNode jsonNode;
        JsonMapper jsonMapper = new JsonMapper();
        Gson gson = new Gson();
        try {
            jsonNode = jsonMapper.readTree(json);
            String productName = jsonNode.path("productName").asText("null");
            String description = jsonNode.path("description").asText("null");
            String imageUrl = jsonNode.path("imageUrl").asText("null");
            JsonNode attributesNode = jsonNode.path("attributes");
            Map<String, String> attributesMap = new HashMap<>();
            attributesNode.fields().forEachRemaining(entry -> attributesMap.put(entry.getKey(), entry.getValue().asText()));
            String attributes = gson.toJson(attributesMap);
            Integer categoryId = jsonNode.path("categoryId").asInt(1);
            Integer brandId = jsonNode.path("brandId").asInt(1);
            Brand brand = new Brand();
            brand.setId(brandId);
            Product product = new Product();
            product.setName(productName);
            product.setBrand(brand);
            product.setCategory(categoryRepository.getById(categoryId));
            product.setImageUrl(imageUrl);
            product.setDescription(description);
            product.setStatus("ACTIVE");
            product.setAttributes(attributes);
            product.setRating(0.0);
            product.setSold(0);
            product.setRated(0);
            product = productRepository.save(product);
            List<ProductItem> productItems = new ArrayList<>();
            JsonNode productItemsNode = jsonNode.path("productItems");
            if (productItemsNode.isArray()) {
                for (JsonNode itemNode : productItemsNode) {
                    String productItemName = itemNode.path("productItemName").asText("null");
                    Double price = itemNode.path("price").asDouble(0.0);
                    Integer quantityInStock = itemNode.path("quantityInStock").asInt(0);
                    String itemImageUrl = itemNode.path("imageUrl").asText("null");
                    ProductItem productItem = new ProductItem();
                    productItem.setProductItemName(productItemName);
                    productItem.setPrice(price);
                    productItem.setQuantityInStock(quantityInStock);
                    productItem.setImageUrl(itemImageUrl);
                    productItem.setProduct(product);
                    productItem.setStatus("ACTIVE");
                    productItems.add(productItem);
                }
            }
            System.out.println("Saving ProductItems: " + productItems);
            productItemRepository.saveAll(productItems);
            List<Integer> idResponse = new ArrayList<>();
            idResponse.add(product.getId());
            for (ProductItem item : productItems) {
                idResponse.add(item.getId());
            }
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", idResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("Error", e.getMessage(), ""));
        }
    }

    public String uploadImage(MultipartFile file, String namePath, Integer productId) {
        String imageUrl = storageService.uploadImages(file, namePath);
        productRepository.updateImage(imageUrl, productId);
        return imageUrl;
    }

    public ResponseEntity<Object> addCategory(String json) {
        JsonNode jsonNode;
        JsonMapper jsonMapper = new JsonMapper();
        try {
            jsonNode = jsonMapper.readTree(json);
            String categoryName = jsonNode.get("categoryName") != null ? jsonNode.get("categoryName").asText() : "null";
            Category category = new Category();
            category.setCategoryName(categoryName);
            categoryRepository.save(category);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("Error", e.getMessage(), ""));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", ""));
    }

    public ResponseEntity<ResponseObject> findAllCategory() {
        Map<String, Object> results = new TreeMap<String, Object>();
        List<Category> categoryList = categoryRepository.findAll();
        results.put("categoryList", categoryList);
        if (results.size() > 0) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", results));
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("Not found", "Not found", ""));
        }
    }

    public ResponseEntity<Object> update(String json) {
        JsonNode jsonNode;
        JsonMapper jsonMapper = new JsonMapper();
        try {
            jsonNode = jsonMapper.readTree(json);
            Integer id = jsonNode.get("id") != null ? jsonNode.get("id").asInt() : -1;
            String productName = jsonNode.get("productName") != null ? jsonNode.get("productName").asText() : "";
            String description = jsonNode.get("description") != null ? jsonNode.get("description").asText() : "";
            String imageUrl = jsonNode.get("imageUrl") != null ? jsonNode.get("imageUrl").asText() : "";
            String attributes = jsonNode.get("attributes") != null ? jsonNode.get("attributes").asText() : "";
            Integer categoryId = jsonNode.get("categoryId") != null ? jsonNode.get("categoryId").asInt() : -1;
            Optional<Product> productOptional = productRepository.findById(id);
            if (productOptional.isPresent()) {
                Product product = productOptional.get();
                product.setId(id);
                product.setName(productName);
                Category category = new Category();
                category.setId(categoryId);
                product.setCategory(category);
                product.setImageUrl(imageUrl);
                product.setDescription(description);
                product.setAttributes(attributes);
                productRepository.save(product);
            }
            else {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseObject("ERROR", "Have error when update product", ""));
            }
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("Error", e.getMessage(), ""));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", ""));
    }

    public ResponseEntity<Object> updateStatus(String json) {
        JsonNode jsonNode;
        JsonMapper jsonMapper = new JsonMapper();
        try {
            jsonNode = jsonMapper.readTree(json);
            Integer id = jsonNode.get("id") != null ? jsonNode.get("id").asInt() : 4;
            String status = jsonNode.get("status") != null ? jsonNode.get("status").asText() : "ACTIVE";
            Optional<Product> productOptional = productRepository.findById(id);

            if (productOptional.isPresent()) {
                Product product = productOptional.get();
                product.setStatus(status);
                productRepository.save(product);
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", ""));
            }
            else {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseObject("ERROR", "Have error when update product", ""));
            }
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("Error", e.getMessage(), ""));
        }
    }


}
