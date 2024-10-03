package com.quoctoan.shoestore.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quoctoan.shoestore.entity.*;
import com.quoctoan.shoestore.model.ResponseObject;
import com.quoctoan.shoestore.model.ReviewResponse;
import com.quoctoan.shoestore.respository.ProductItemRepository;
import com.quoctoan.shoestore.respository.ProductRepository;
import com.quoctoan.shoestore.respository.ReviewRepository;
import com.quoctoan.shoestore.respository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    @Autowired
    ReviewRepository reviewRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductItemRepository productItemRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    StorageService storageService;


    public ResponseEntity<ResponseObject> addReview(String json){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if (json == null || json.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseObject("ERROR", "Empty JSON", ""));
            }
            JsonNode jsonObjectPromotion = objectMapper.readTree(json);
            Integer ratingValue = jsonObjectPromotion.get("ratingValue") != null ?
                    jsonObjectPromotion.get("ratingValue").asInt() : 5;
            String comment = jsonObjectPromotion.get("comment") != null ?
                    jsonObjectPromotion.get("comment").asText() : "";
            String imageUrl = jsonObjectPromotion.get("imageUrl") != null ?
                    jsonObjectPromotion.get("imageUrl").asText() : "";
            Integer productItemId = jsonObjectPromotion.get("productItemId") != null ?
                    jsonObjectPromotion.get("productItemId").asInt() : 4;
            Integer userId = jsonObjectPromotion.get("userId") != null ?
                    jsonObjectPromotion.get("userId").asInt() : 52;
            Optional<User> optionalUser = userRepository.findById(userId);
            Optional<ProductItem> optionalProductItem = productItemRepository.findById(productItemId);
            if(optionalUser.isPresent() && optionalProductItem.isPresent()){
                Review review = new Review();
                review.setComment(comment);
                review.setCustomer(optionalUser.get());
                review.setRatingValue(ratingValue);
                review.setImageUrl(imageUrl);
                review.setProduct(optionalProductItem.get().getProduct());
                Review savedReview = reviewRepository.save(review);
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseObject("OK", "Successfully", savedReview.getId()));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject("ERROR", "Data not validate", ""));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject("ERROR", "An error occurred", e.getMessage()));
        }
    }

    public String uploadImage(MultipartFile file, String namePath, Integer reviewId) {
        String imageUrl = storageService.uploadImages(file, namePath);
        reviewRepository.updateImage(imageUrl,reviewId);
        return imageUrl;
    }

    public ResponseEntity<ResponseObject> getAllReviewByProductId(Integer productId){
        try {
            List<Review> reviewList = reviewRepository.findByProductId(productId);
            if(reviewList.isEmpty()){
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseObject("OK", "Not found", ""));
            }
            else {
                List<ReviewResponse> reviewResponses = new ArrayList<>();
                for (Review review : reviewList) {
                    ReviewResponse reviewResponse = new ReviewResponse();
                    reviewResponse.setId(review.getId());
                    reviewResponse.setCustomer(review.getCustomer().getFullName());
                    reviewResponse.setRatingValue(review.getRatingValue());
                    reviewResponse.setImageUrl(review.getImageUrl());
                    reviewResponse.setComment(review.getComment());
                    reviewResponses.add(reviewResponse);

                }
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseObject("OK", "Successfully", reviewResponses));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject("ERROR", "An error occurred", e.getMessage()));
        }
    }

}
