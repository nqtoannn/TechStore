package com.quoctoan.shoestore.controller;

import com.quoctoan.shoestore.model.ResponseObject;
import com.quoctoan.shoestore.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping(path = "/techstore/api/employee/")
public class EmployeeController {
    @Autowired
    OrderService orderService;

    @GetMapping("order/findAll")
    public ResponseEntity<ResponseObject> getAllOrders() {
        return orderService.findAllOrders();
    }
    @PutMapping("order/updateStatusOrder")
    public ResponseEntity<Object> updateStatusOrder(@RequestBody String json) {
        return orderService.updateStatusOrder(json);
    }
}
