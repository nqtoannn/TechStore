package com.quoctoan.shoestore.controller;

import com.quoctoan.shoestore.model.ResponseObject;
import com.quoctoan.shoestore.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
@RestController
@RequestMapping(path = "/techstore/api/transaction")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @PostMapping("add")
    public ResponseEntity<ResponseObject> createTransaction(@RequestBody String json) {
        return transactionService.addTransaction(json);
    }

    @GetMapping("getByType/{type}")
    public ResponseEntity<ResponseObject> getAllTransaction(@PathVariable String type){
        return transactionService.getTransactionByType(type);
    }


}
