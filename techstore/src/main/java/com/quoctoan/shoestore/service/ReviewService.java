package com.quoctoan.shoestore.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.quoctoan.shoestore.entity.*;
import com.quoctoan.shoestore.model.ResponseObject;
import com.quoctoan.shoestore.model.ReviewResponse;
import com.quoctoan.shoestore.respository.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;;
import java.text.ParsePosition;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
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
    @Autowired
    OrderRepository orderRepository;


    public ResponseEntity<ResponseObject> getAllReview(){
        List<Review> reviewList = reviewRepository.findAll();
        List<ReviewResponse> reviewResponses = new ArrayList<>();
        for (Review review : reviewList) {
            reviewResponses.add(convertToReviewResponse(review));
        }
        if(reviewResponses.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject("ERROR", "Review not found", ""));
        } else {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("OK", "Successfully", reviewResponses));
        }
    }

    public ResponseEntity<ResponseObject> updateStatusReview(String json){
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            if (json == null || json.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseObject("ERROR", "Empty JSON", ""));
            }
            JsonNode rootNode = objectMapper.readTree(json);
            Integer reviewId = rootNode.get("reviewId") != null ? rootNode.get("reviewId").asInt() : -1;
            String status = rootNode.get("status") != null ? rootNode.get("status").asText() : "show";
            Optional<Review> optionalReview = reviewRepository.findById(reviewId);
            if(optionalReview.isEmpty()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseObject("ERROR", "Review not found", ""));
            }
            Review review = optionalReview.get();
            if(status.equals("show")){
                review.setStatus("hidden");
            } else {
                review.setStatus("show");
            }
            reviewRepository.save(review);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("OK", "Successfully", ""));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject("ERROR", "An error occurred", e.getMessage()));
        }
    }

    public ResponseEntity<ResponseObject> addReview(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if (json == null || json.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseObject("ERROR", "Empty JSON", ""));
            }
            JsonNode rootNode = objectMapper.readTree(json);
            Integer orderId = rootNode.get("orderId") != null ? rootNode.get("orderId").asInt() : -1;
            JsonNode reviews = rootNode.get("reviews");
            if (orderId == -1 || reviews == null || !reviews.isArray()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseObject("ERROR", "Invalid JSON format", ""));
            }
            Optional<Order> optionalOrder = orderRepository.findById(orderId);
            if (!optionalOrder.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseObject("ERROR", "Order not found", ""));
            }
            Order order = optionalOrder.get();
            OrderStatus orderStatus = new OrderStatus();
            orderStatus.setId(5);
            order.setOrderStatus(orderStatus);
            orderRepository.save(order);
            User user = order.getCustomer();
            List<Integer> savedReviewIds = new ArrayList<>();
            for (JsonNode reviewNode : reviews) {
                Integer productItemId = reviewNode.get("productItemId") != null ? reviewNode.get("productItemId").asInt() : null;
                Integer ratingValue = reviewNode.get("ratingValue") != null ? reviewNode.get("ratingValue").asInt() : 5;
                String comment = reviewNode.get("comment") != null ? reviewNode.get("comment").asText() : "";
                String imageUrl = reviewNode.get("imageUrl") != null ? reviewNode.get("imageUrl").asText() : "";
                if (productItemId == null) {
                    continue;
                }
                Optional<ProductItem> optionalProductItem = productItemRepository.findById(productItemId);
                if (optionalProductItem.isPresent()) {
                    ProductItem productItem = optionalProductItem.get();
                    Product product = productItem.getProduct();
                    Review review = new Review();
                    review.setComment(comment);
                    review.setCustomer(user);
                    review.setRatingValue(ratingValue);
                    review.setImageUrl(imageUrl);
                    review.setProduct(product);
                    review.setStatus("show");
                    Review savedReview = reviewRepository.save(review);
                    predictText(review.getComment(), review.getId());
                    savedReviewIds.add(savedReview.getId());
                    product.setRating((product.getRated() * product.getRating() + ratingValue) / (product.getRated() + 1));
                    product.setRated(product.getRated() + 1);
                    productRepository.save(product);
                }
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("OK", "Reviews added successfully", savedReviewIds));
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
                    if(review.getStatus().equals("show")){
                        reviewResponses.add(convertToReviewResponse(review));
                    }
                }
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseObject("OK", "Successfully", reviewResponses));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject("ERROR", "An error occurred", e.getMessage()));
        }
    }

    public ReviewResponse convertToReviewResponse(Review review){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        ReviewResponse reviewResponse = new ReviewResponse();
        reviewResponse.setId(review.getId());
        reviewResponse.setCustomer(review.getCustomer().getFullName());
        reviewResponse.setRatingValue(review.getRatingValue());
        reviewResponse.setImageUrl(review.getImageUrl());
        reviewResponse.setComment(review.getComment());
        reviewResponse.setResponses(review.getResponses());
        reviewResponse.setStatus(review.getStatus());
        reviewResponse.setCreateAt(review.getCreatedAt().format(formatter));
        reviewResponse.setProductId(review.getProduct().getId());
        reviewResponse.setProductName(review.getProduct().getName());
        return reviewResponse;
    }

    public void predictText(String text, Integer reviewId) {
        String url = "http://localhost:5000/predict";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HashMap<String, String> request = new HashMap<>();
        request.put("text", text);
        HttpEntity<HashMap<String, String>> entity = new HttpEntity<>(request, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        JsonObject jsonObject = JsonParser.parseString(response.getBody()).getAsJsonObject();
        String label = jsonObject.get("label").getAsString();
        System.out.println(label);
        System.out.println(text);
        if(label.equals("clean")){
            return;
        }
        if(label.equals("offensive")){
            updateReviewStatus(reviewId,label);
        }
        if(label.equals("hate")){
            updateReviewStatus(reviewId,label);
        }
    }

    public void updateReviewStatus(Integer reviewId, String status){
        Optional<Review> optionalReview = reviewRepository.findById(reviewId);
        if(optionalReview.isPresent()){
            Review review = optionalReview.get();
            review.setStatus(status);
            reviewRepository.save(review);
        }
    }

}
