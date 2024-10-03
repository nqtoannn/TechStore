package com.quoctoan.shoestore.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quoctoan.shoestore.entity.*;
import com.quoctoan.shoestore.model.*;
import com.quoctoan.shoestore.respository.ProductRepository;
import com.quoctoan.shoestore.respository.PromotionDetailRepository;
import com.quoctoan.shoestore.respository.PromotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PromotionService {
    @Autowired
    PromotionRepository promotionRepository;
    @Autowired
    PromotionDetailRepository promotionDetailRepository;
    @Autowired
    ProductRepository productRepository;

    public ResponseEntity<ResponseObject> addPromotion(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if (json == null || json.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseObject("ERROR", "Empty JSON", ""));
            }
            JsonNode jsonObjectPromotion = objectMapper.readTree(json);
            String promotionName = jsonObjectPromotion.get("promotionName") != null ?
                    jsonObjectPromotion.get("promotionName").asText() : "";
            String startDateStr = jsonObjectPromotion.get("startDate") != null ?
                    jsonObjectPromotion.get("startDate").asText() : "";
            String endDateStr = jsonObjectPromotion.get("endDate") != null ?
                    jsonObjectPromotion.get("endDate").asText() : "";
            Integer userId = jsonObjectPromotion.get("userId") != null ?
                    jsonObjectPromotion.get("userId").asInt() : 2;
            JsonNode promotionList = jsonObjectPromotion.get("promotionList");
            Promotion promotion = new Promotion();
            promotion.setPromotionName(promotionName);
            LocalDate startDate = null;
            LocalDate endDate = null;
            if (!startDateStr.isEmpty()) {
                startDate = LocalDate.parse(startDateStr, DateTimeFormatter.ISO_DATE);
            }
            if (!endDateStr.isEmpty()) {
                endDate = LocalDate.parse(endDateStr, DateTimeFormatter.ISO_DATE);
            }
            promotion.setStartDate(startDate);
            promotion.setEndDate(endDate);
            User user = new User();
            user.setId(userId);
            promotion.setUser(user);
            Promotion savePromotion = promotionRepository.save(promotion);
            List<PromotionDetail> promotionDetailList = new ArrayList<>();
            if (promotionList.isArray()) {
                for (JsonNode promotionItem : promotionList) {
                    Optional<Product> productOptional = productRepository.findById(promotionItem.get("productId").asInt());
                    Integer discountPercent = promotionItem.get("discountPercent").asInt();
                    PromotionDetail pd = new PromotionDetail();
                    pd.setProduct(productOptional.get());
                    pd.setPromotion(savePromotion);
                    pd.setDiscountPercent(discountPercent);
                    promotionDetailList.add(pd);
                    promotionDetailRepository.save(pd);
                }
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("OK", "Successfully", ""));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject("ERROR", "An error occurred", e.getMessage()));
        }
    }


    public ResponseEntity<ResponseObject> getAllPromotion(){
        try{
            List<Promotion> promotionList = promotionRepository.findAll();
            List<PromotionResponse> promotionResponseList = new ArrayList<>();
            promotionList.forEach( p -> {
                PromotionResponse promotionResponse = new PromotionResponse();
                promotionResponse.setId(p.getId());
                promotionResponse.setPromotionName(p.getPromotionName());
                promotionResponse.setStartDate(p.getStartDate());
                promotionResponse.setEndDate(p.getEndDate());
                List<PromotionDetail> promotionDetailList = p.getPromotionDetails();
                List<PromotionDetailResponse> promotionDetailResponseList = new ArrayList<>();
                promotionDetailList.forEach(promotionDetail -> {
                    Product product = promotionDetail.getProduct();
                    ProductModel productModel = new ProductModel();
                    productModel.setId(product.getId());
                    productModel.setName(product.getName());
                    productModel.setCategory(product.getCategory());
                    productModel.setDescription(product.getDescription());
                    productModel.setImageUrl(product.getImageUrl());
                    productModel.setStatus(product.getStatus());
                    PromotionDetailResponse promotionDetailResponse = new PromotionDetailResponse(promotionDetail.getId(),productModel,promotionDetail.getDiscountPercent());
                    promotionDetailResponseList.add(promotionDetailResponse);
                });
                promotionResponse.setPromotionDetail(promotionDetailResponseList);
                promotionResponseList.add(promotionResponse);
            });
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("OK", "Successfully", promotionResponseList));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject("ERROR", "An error occurred", e.getMessage()));
        }
    }


}
