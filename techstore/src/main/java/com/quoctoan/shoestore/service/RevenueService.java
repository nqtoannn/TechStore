package com.quoctoan.shoestore.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.quoctoan.shoestore.entity.Product;
import com.quoctoan.shoestore.entity.ProductItem;
import com.quoctoan.shoestore.model.MonthlyRevenue;
import com.quoctoan.shoestore.model.ProductItemModel;
import com.quoctoan.shoestore.model.ProductResponseModel;
import com.quoctoan.shoestore.model.ResponseObject;
import com.quoctoan.shoestore.respository.OrderRepository;
import com.quoctoan.shoestore.respository.ProductItemRepository;
import com.quoctoan.shoestore.respository.PromotionRepository;
import com.quoctoan.shoestore.respository.imp.RevenueRepositoryImp;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class RevenueService {
    @Autowired
    RevenueRepositoryImp revenueImp;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ProductItemRepository productItemRepository;
    @Autowired
    PromotionRepository promotionRepository;

    public ResponseEntity<ResponseObject> getRevenueFromProduct() {
        Double total = orderRepository.findRevenueBetweenDates();
        if (total != 0) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", total));
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("Not found", "Not found", ""));
        }
    }

    public ResponseEntity<ResponseObject> getRevenueProductBetweenDate(@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Object[]> results = orderRepository.findMonthlyRevenueBetweenDates(startDate, endDate);
        Map<String, MonthlyRevenue> revenueMap = new HashMap<>();
        for (Object[] result : results) {
            int year = (int) result[0];
            int month = (int) result[1];
            Double totalRevenue = (Double) result[2];
            revenueMap.put(year + "-" + month, new MonthlyRevenue(year, month, totalRevenue));
        }
        List<MonthlyRevenue> monthlyRevenues = new ArrayList<>();
        LocalDate current = startDate.withDayOfMonth(1);
        LocalDate end = endDate.withDayOfMonth(1);

        while (!current.isAfter(end)) {
            int year = current.getYear();
            int month = current.getMonthValue();
            String key = year + "-" + month;

            if (revenueMap.containsKey(key)) {
                monthlyRevenues.add(revenueMap.get(key));
            } else {
                monthlyRevenues.add(new MonthlyRevenue(year, month, 0.0));
            }

            current = current.plusMonths(1);
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", monthlyRevenues));
    }

    public ResponseEntity<ResponseObject> getMostPurchased(){
        List<ProductItem> productList = productItemRepository.getMostPruchased();
        List<ProductResponseModel> productResponseModelList = new ArrayList<>();
        productList.forEach(productItem -> {
            Product product = productItem.getProduct();
            ProductResponseModel productModel = new ProductResponseModel();
            productModel.setId(product.getId());
            productModel.setName(product.getName());
            productModel.setCategoryName(product.getCategory().getCategoryName());
            productModel.setDescription(product.getDescription());
            productModel.setImageUrl(product.getImageUrl());
            productModel.setCategory(product.getCategory().getCategoryName());
            productModel.setStatus(product.getStatus());
            productModel.setRating(product.getRating());
            productModel.setRated(product.getRated());
            productModel.setSold(product.getSold());
            List<ProductItem> productItemList = product.getProductItems();
            List<ProductItemModel> productItemModels = new ArrayList<>();
            productItemList.forEach(productItems -> {
                ProductItemModel productItemModel = new ProductItemModel();
                BeanUtils.copyProperties(productItems,productItemModel);
                productItemModels.add(productItemModel);
            });
            productModel.setProductItems(productItemModels);
            Optional<Integer> discount = promotionRepository.findDiscountPercentForProduct(product.getId(),LocalDate.now());
            if(discount.isPresent()){
                productModel.setDiscountPercent(discount.get());
            }
            productModel.calculateAvrPrice();
            productResponseModelList.add(productModel);
        });
        if (!productResponseModelList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", productResponseModelList));
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("Not found", "Not found", ""));
        }
    }


}
