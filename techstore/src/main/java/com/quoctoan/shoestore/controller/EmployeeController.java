package com.quoctoan.shoestore.controller;

import com.quoctoan.shoestore.model.ResponseObject;
import com.quoctoan.shoestore.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @GetMapping("order/findAllWithStatus/{statusId}")
    public ResponseEntity<ResponseObject> getOrders(@PathVariable Integer statusId) {
        return orderService.findAllByStatusId(statusId);
    }

    @PutMapping("order/updateStatusOrder")
    public ResponseEntity<Object> updateStatusOrder(@RequestBody String json) {
        return orderService.updateStatusOrder(json);
    }

    @PostMapping("order/confirmOrder")
    public String uploadImageOrder(@RequestParam("namePath") String namePath, @RequestParam("file") MultipartFile file,
                                     @RequestParam("orderId") Integer orderId) {
        return orderService.uploadImage(file, namePath, orderId);
    }
}
