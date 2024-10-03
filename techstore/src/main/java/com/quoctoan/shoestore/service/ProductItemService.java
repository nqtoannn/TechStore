package com.quoctoan.shoestore.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.quoctoan.shoestore.entity.Product;
import com.quoctoan.shoestore.entity.ProductItem;
import com.quoctoan.shoestore.model.ResponseObject;
import com.quoctoan.shoestore.entity.User;
import com.quoctoan.shoestore.model.ProductItemModel;
import com.quoctoan.shoestore.respository.ProductItemRepository;
import com.quoctoan.shoestore.respository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductItemService {
    @Autowired
    private ProductItemRepository productItemRepository;

    @Autowired
    private EmailSendService emailSendService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StorageService storageService;


    public ResponseEntity<ResponseObject> findAll() {
        List<ProductItem> productItemList = productItemRepository.findAll();
        List<ProductItemModel> productItemModelList = productItemList.stream().map(
                productItem -> {
                    ProductItemModel productItemModel = new ProductItemModel();
                    productItemModel.setId(productItem.getId());
                    productItemModel.setProductItemName(productItem.getProductItemName());
                    productItemModel.setPrice(productItem.getPrice());
                    productItemModel.setImageUrl(productItem.getImageUrl());
                    productItemModel.setStatus(productItem.getStatus());
                    productItemModel.setQuantityInStock(productItem.getQuantityInStock());
                    return productItemModel;
                }).collect(Collectors.toList());
        if (!productItemModelList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", productItemModelList));
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("Not found", "Not found", ""));
        }
    }

    public ResponseEntity<Object> add(String json) {
        JsonNode jsonNode;
        JsonMapper jsonMapper = new JsonMapper();
        try {
            jsonNode = jsonMapper.readTree(json);
            Double price = jsonNode.get("price") != null ? jsonNode.get("price").asDouble() : null;
            String productItemName = jsonNode.get("productItemName") != null ? jsonNode.get("productItemName").asText() : "";
            String imageUrl = jsonNode.get("imageUrl") != null ? jsonNode.get("imageUrl").asText() : "";
            Integer quantity = jsonNode.get("quantity") != null ? jsonNode.get("quantity").asInt() : -1;
            Integer productId = jsonNode.get("productId") != null ? jsonNode.get("productId").asInt() : -1;

            ProductItem productItem = new ProductItem();
            productItem.setProductItemName(productItemName);
            Product product = new Product();
            product.setId(productId);
            productItem.setProduct(product);
            productItem.setPrice(price);
            productItem.setStatus("OK");
            productItem.setQuantityInStock(quantity);
            productItem.setImageUrl(imageUrl);
            ProductItem saveProduct = productItemRepository.save(productItem);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("ERROR", "Have error when add product item", ""));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("Error", e.getMessage(), ""));
        }

    }

    public ResponseEntity<Object> update(String json) {
        JsonNode jsonNode;
        JsonMapper jsonMapper = new JsonMapper();
        try {
            jsonNode = jsonMapper.readTree(json);
            String productItemName = jsonNode.get("productItemName") != null ? jsonNode.get("productItemName").asText() : "";
            String imageUrl = jsonNode.get("imageUrl") != null ? jsonNode.get("imageUrl").asText() : "";
            Double price = jsonNode.get("price") != null ? jsonNode.get("price").asDouble() : null;
            Integer quantity = jsonNode.get("quantity") != null ? jsonNode.get("quantity").asInt() : -1;
            Integer productItemId = jsonNode.get("productItemId") != null ? jsonNode.get("productItemId").asInt() : -1;
            String color = jsonNode.get("color") != null ? jsonNode.get("color").asText() : "";
            Optional<ProductItem> productItemOptional = productItemRepository.findById(productItemId);
            if (productItemOptional.isPresent()) {
                ProductItem productItem = productItemOptional.get();
                productItem.setProductItemName(productItemName);
                productItem.setPrice(price);
                productItem.setQuantityInStock(quantity);
                productItem.setImageUrl(imageUrl);
                productItemRepository.save(productItem);
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", productItem.getId()));
            } else {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseObject("ERROR", "Have error when update product item", ""));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("Error", e.getMessage(), ""));
        }

    }

    public String uploadImage(MultipartFile file, String namePath, Integer serviceHairId) {
        String imageUrl = storageService.uploadImages(file, namePath);
        productItemRepository.updateImage(imageUrl, serviceHairId);
        return imageUrl;
    }

    public ResponseEntity<Object> updateStatus(String json) {
        JsonNode jsonNode;
        JsonMapper jsonMapper = new JsonMapper();
        try {
            jsonNode = jsonMapper.readTree(json);
            Integer productItemId = jsonNode.get("productItemId") != null ? jsonNode.get("productItemId").asInt() : -1;
            String status = jsonNode.get("status") != null ? jsonNode.get("status").asText() : null;
            Optional<ProductItem> productItemOptional = productItemRepository.findById(productItemId);
            if (productItemOptional.isPresent()) {
                ProductItem productItem = productItemOptional.get();
                productItem.setStatus(status);
                productItemRepository.save(productItem);
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", productItem.getId()));
            } else {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseObject("ERROR", "Have error when update product item status", ""));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("Error", e.getMessage(), ""));
        }

    }


//    public String uploadImage(MultipartFile file, String namePath, Integer serviceHairId) {
//        String imageUrl = storageService.uploadImages(file, namePath);
//        productItemRepository.updateImage(imageUrl, serviceHairId);
//        return imageUrl;
//    }

//    public ResponseEntity<ResponseObject> findByProductItemName(String productItemName) {
//        try {
//            List<ProductItem> productItemList = productItemRepository.findByProductName(productItemName);
//            List<ProductItemModel> productItemModelList = productItemList.stream().map(
//                    productItem -> {
//                        ProductItemModel productItemModel = new ProductItemModel();
//                        productItemModel.setId(productItem.getId());
//                        productItemModel.setProductItemName(productItem.getProductItemName());
//                        productItemModel.setPrice(productItem.getPrice());
//                        productItemModel.setImageUrl(productItem.getImageUrl());
//                        productItemModel.setStatus(productItem.getStatus());
//                        productItemModel.setQuantityInStock(productItem.getQuantityInStock());
//                        productItemModel.setWarrantyTime(productItem.getWarrantyTime());
//                        productItemModel.setDescription(productItem.getDescription());
//                        return productItemModel;
//                    }).collect(Collectors.toList());
//            if (!productItemModelList.isEmpty()) {
//                return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", productItemModelList));
//            } else {
//                return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("Not found", "Not found", ""));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(new ResponseObject("ERROR", "Have error:", e.getMessage()));
//        }
//    }

    public ResponseEntity<ResponseObject> findProductItemById(Integer productItemId) {
        Optional<ProductItem> productItemOptional = productItemRepository.findById(productItemId);
        if (productItemOptional.isPresent()) {
            ProductItem productItem = productItemOptional.get();
            ProductItemModel productItemModel = new ProductItemModel();
            productItemModel.setId(productItem.getId());
            productItemModel.setProductItemName(productItem.getProductItemName());
            productItemModel.setPrice(productItem.getPrice());
            productItemModel.setImageUrl(productItem.getImageUrl());
            productItemModel.setStatus(productItem.getStatus());
            productItemModel.setQuantityInStock(productItem.getQuantityInStock());
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", productItemModel));
        }
        else {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("Not found", "Not found", ""));
        }
    }



}
