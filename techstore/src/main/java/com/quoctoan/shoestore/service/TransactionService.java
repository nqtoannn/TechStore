package com.quoctoan.shoestore.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.quoctoan.shoestore.entity.*;
import com.quoctoan.shoestore.model.ResponseObject;
import com.quoctoan.shoestore.model.TransactionDetailResponseModel;
import com.quoctoan.shoestore.model.TransactionResponseModel;
import com.quoctoan.shoestore.respository.ProductItemRepository;
import com.quoctoan.shoestore.respository.TransactionDetailRepository;
import com.quoctoan.shoestore.respository.TransactionRepository;
import com.quoctoan.shoestore.respository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class TransactionService {
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ProductItemRepository productItemRepository;
    @Autowired
    TransactionDetailRepository transactionDetailRepository;

    public TransactionResponseModel convertToTransactionResponseModel(Transaction transaction){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        List<TransactionDetail> transactionDetails = transaction.getTransactionDetails();
        TransactionResponseModel transactionResponseModel = new TransactionResponseModel();
        transactionResponseModel.setCreatedAt((transaction.getCreatedAt()).format(formatter));
        transactionResponseModel.setTransaction_id(transaction.getTransaction_id());
        transactionResponseModel.setUsername(transaction.getUser().getFullName());
        transactionResponseModel.setType(transaction.getType());
        transactionResponseModel.setStatus(transaction.getStatus());
        transactionResponseModel.setTotal_price(transaction.getTotal_price());
        transactionResponseModel.setTotal_quantity(transaction.getTotal_quantity());
        transactionResponseModel.setUserId(transaction.getUser().getId());
        List<TransactionDetailResponseModel> detailResponseModels = new ArrayList<>();
        transactionDetails.forEach(transactionDetail -> {
            TransactionDetailResponseModel transactionDetailResponseModel = new TransactionDetailResponseModel();
            transactionDetailResponseModel.setTrans_detail_id(transactionDetail.getTrans_detail_id());
            transactionDetailResponseModel.setNote(transactionDetail.getNote());
            transactionDetailResponseModel.setQuantity(transactionDetail.getQuantity());
            transactionDetailResponseModel.setProductItemName(transactionDetail.getProduct_item().getProduct().getName() + " Phân loại: " +transactionDetail.getProduct_item().getProductItemName());
            transactionDetailResponseModel.setProductItemImg(transactionDetail.getProduct_item().getImageUrl());
            transactionDetailResponseModel.setPrice(transactionDetail.getPrice());
            transactionDetailResponseModel.setProduct_item_id(transactionDetail.getProduct_item().getId());
            detailResponseModels.add(transactionDetailResponseModel);
        });
        transactionResponseModel.setTransactionDetails(detailResponseModels);
        return transactionResponseModel;
    }

public ResponseEntity<ResponseObject> addTransaction(String json) {
    try {
        // Parse JSON input
        JsonNode jsonNode = new JsonMapper().readTree(json);

        // Extract fields from JSON with default values
        String status = jsonNode.path("status").asText("COMPLETED");
        String type = jsonNode.path("type").asText("IMPORT");
        Integer userId = jsonNode.path("userId").asInt(1);
        Integer totalQuantity = jsonNode.path("total_quantity").asInt(0);
        Double totalPrice = jsonNode.path("total_price").asDouble(0.0);
        JsonNode transactionDetailsNode = jsonNode.path("transactionDetails");

        // Validate user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User is not present"));

        // Create and save transaction
        Transaction transaction = createTransaction(type, status, totalQuantity, totalPrice, user);
        transactionRepository.save(transaction);

        // Validate and process transaction details
        if (!transactionDetailsNode.isArray() || !transactionDetailsNode.iterator().hasNext()) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Transaction detail is not present");
        }
        processTransactionDetails(transaction, transactionDetailsNode, type);

        return buildSuccessResponse("Transaction added successfully");
    } catch (IllegalArgumentException e) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    } catch (Exception e) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", e.getMessage());
    }
}

    private Transaction createTransaction(String type, String status, Integer totalQuantity, Double totalPrice, User user) {
        Transaction transaction = new Transaction();
        transaction.setType(type);
        transaction.setStatus(status);
        transaction.setTotal_price(totalPrice);
        transaction.setTotal_quantity(totalQuantity);
        transaction.setUser(user);
        return transaction;
    }

    private void processTransactionDetails(Transaction transaction, JsonNode transactionDetailsNode, String type) {
        for (JsonNode itemNode : transactionDetailsNode) {
            Integer productItemId = itemNode.path("product_item_id").asInt(0);
            Integer quantity = itemNode.path("quantity").asInt(0);
            Double price = itemNode.path("price").asDouble(0.0);
            String note = itemNode.path("note").asText("");

            // Validate product item
            ProductItem productItem = productItemRepository.findById(productItemId)
                    .orElseThrow(() -> new IllegalArgumentException("Product item with ID " + productItemId + " is not present"));

            // Create and save transaction detail
            TransactionDetail transactionDetail = createTransactionDetail(transaction, productItem, quantity, price, note);
            transactionDetailRepository.save(transactionDetail);

            // Update product stock
            updateProductStock(productItem, quantity, type);
        }
    }

    private TransactionDetail createTransactionDetail(Transaction transaction, ProductItem productItem, Integer quantity, Double price, String note) {
        TransactionDetail detail = new TransactionDetail();
        detail.setTransaction(transaction);
        detail.setTransaction_id(transaction.getTransaction_id());
        detail.setProduct_item(productItem);
        detail.setQuantity(quantity);
        detail.setPrice(price);
        detail.setNote(note);
        return detail;
    }

    private void updateProductStock(ProductItem productItem, Integer quantity, String type) {
        int updatedQuantity = type.equals("IMPORT")
                ? productItem.getQuantityInStock() + quantity
                : productItem.getQuantityInStock() - quantity;
        productItem.setQuantityInStock(updatedQuantity);
        productItemRepository.save(productItem);
    }

    private ResponseEntity<ResponseObject> buildSuccessResponse(String message) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", message, ""));
    }

    private ResponseEntity<ResponseObject> buildErrorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new ResponseObject("ERROR", message, ""));
    }

    private ResponseEntity<ResponseObject> buildErrorResponse(HttpStatus status, String message, String details) {
        return ResponseEntity.status(status).body(new ResponseObject("ERROR", message, details));
    }


    public ResponseEntity<ResponseObject> getTransactionByType(String type){
        if(type.equals("ALL")){
            List<Transaction> transactionList = transactionRepository.findAll();
            if(transactionList.isEmpty()){
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseObject("OK", "Transaction list empty",""));
            }
            List<TransactionResponseModel> transactionResponseModels = new ArrayList<>();
            transactionList.forEach(transaction -> {
                transactionResponseModels.add(convertToTransactionResponseModel(transaction));
            });
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("OK", "Successfully",transactionResponseModels));
        }
        else {
            List<Transaction> transactionList = transactionRepository.findTransactionByType(type);
            if(transactionList.isEmpty()){
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseObject("OK", "Transaction list empty",""));
            }
            List<TransactionResponseModel> transactionResponseModels = new ArrayList<>();
            transactionList.forEach(transaction -> {
                transactionResponseModels.add(convertToTransactionResponseModel(transaction));
            });
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("OK", "Successfully",transactionResponseModels));
        }
    }


}
