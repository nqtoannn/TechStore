package com.quoctoan.shoestore.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quoctoan.shoestore.entity.PaymentMethod;
import com.quoctoan.shoestore.model.PaymentModel;
import com.quoctoan.shoestore.model.ResponseObject;
import com.quoctoan.shoestore.respository.PaymentMethodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentMethodService {
    @Autowired
    PaymentMethodRepository paymentMethodRepository;

    public ResponseEntity<ResponseObject> findAllPaymentMethod() {
        List<PaymentMethod> paymentMethodList = paymentMethodRepository.findAll();
        List<PaymentModel> paymentModels = new ArrayList<>();
        paymentMethodList.forEach(paymentMethod -> {
            PaymentModel paymentModel = new PaymentModel(paymentMethod.getId(), paymentMethod.getPaymentMethodName(), paymentMethod.getStatus());
            paymentModels.add(paymentModel);
        });
        if(!paymentModels.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", paymentModels));
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("Not found", "Not found", ""));
        }
    }

    public ResponseEntity<ResponseObject> addPayment(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if (json == null || json.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseObject("ERROR", "Empty JSON", ""));
            }
            JsonNode jsonObject = objectMapper.readTree(json);
            String paymentName = jsonObject.get("paymentName") != null ?
                    jsonObject.get("paymentName").asText() : "";
            String status = jsonObject.get("status") != null ?
                    jsonObject.get("status").asText() : "ACTIVE";
            PaymentMethod paymentMethod = new PaymentMethod();
            paymentMethod.setPaymentMethodName(paymentName);
            paymentMethod.setStatus(status);
            paymentMethodRepository.save(paymentMethod);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("OK", "Successfully", ""));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject("ERROR", "An error occurred", e.getMessage()));
        }
    }

    public ResponseEntity<ResponseObject> updatePayment(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if (json == null || json.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseObject("ERROR", "Empty JSON", ""));
            }
            JsonNode jsonObject = objectMapper.readTree(json);
            Integer paymentId = jsonObject.get("paymentId") != null ?
                    jsonObject.get("paymentId").asInt() : 1;
            String status = jsonObject.get("status") != null ?
                    jsonObject.get("status").asText() : "ACTIVE";
            Optional<PaymentMethod> paymentMethodOtp = paymentMethodRepository.findById(paymentId);
            if (paymentMethodOtp.isPresent()) {
                PaymentMethod paymentMethod = paymentMethodOtp.get();
                paymentMethod.setStatus(status);
                paymentMethodRepository.save(paymentMethod);
            }
            else
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseObject("ERROR", "Have error when update status code order", ""));
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("OK", "Successfully", ""));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject("ERROR", "An error occurred", e.getMessage()));
        }
    }


}
