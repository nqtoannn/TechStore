package com.quoctoan.shoestore.controller;

import com.quoctoan.shoestore.model.ResponseObject;
import com.quoctoan.shoestore.service.PaymentMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
@RestController
@RequestMapping(path = "/techstore/api/")
public class PaymentMethodController {
    @Autowired
    PaymentMethodService paymentMethodService;

    @PostMapping("management/payment/add")
    public ResponseEntity<ResponseObject> addPayment(@RequestBody String json){
        return paymentMethodService.addPayment(json);
    }

    @PutMapping("management/payment/update")
    public ResponseEntity<ResponseObject> updatePayment(@RequestBody String json){
        return paymentMethodService.updatePayment(json);
    }

    @GetMapping("payment/getAll")
    public ResponseEntity<ResponseObject> getAllPayment(){
        return paymentMethodService.findAllPaymentMethod();
    }

}
