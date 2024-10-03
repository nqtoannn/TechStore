package com.quoctoan.shoestore.controller;

import com.quoctoan.shoestore.model.ResponseObject;
import com.quoctoan.shoestore.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
@RestController
@RequestMapping(path = "/shoestore/api/")
public class CustomerController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;
    @Autowired
    private CartService cartService;
    @Autowired
    private ReviewService reviewService;

    @PostMapping("customer/order") //Done
    public ResponseEntity<ResponseObject> order(@RequestBody String json, HttpServletRequest request) {
        return orderService.order(json,request);
    }

    @GetMapping("customer/orders/getAllOrdersByCustomerId/{customerId}")  //done
    public ResponseEntity<ResponseObject> findAllOrderByCustomerId(@PathVariable Integer customerId) {
        return orderService.findAllByCustomerId(customerId);
    }

    @PostMapping("customer/addToCart") //done
    public ResponseEntity<ResponseObject> addToCart(@RequestBody String json) {
        return cartService.addToCart(json);
    }

    @GetMapping("customer/findAllCartItems/{userId}") //done
    public ResponseEntity<ResponseObject> findAllCartItem(@PathVariable Integer userId ) {
        return cartService.findAllByUserId(userId);
    }

    @DeleteMapping("customer/deleteCartItem/{id}") //done
    public ResponseEntity<ResponseObject> deleteCartItemById(@PathVariable Integer id) {
        return cartService.deleteCart(id);
    }

    @DeleteMapping("customer/deleteAllCartByCusId/{customerId}") //done
    public ResponseEntity<ResponseObject> deleteAllCartItemByCartId(@PathVariable Integer customerId) {
        return cartService.deleteAllCartByCustomerId(customerId);
    }

    @PutMapping("customer/updateQuantityCartItem") //done
    public ResponseEntity<ResponseObject> updateQuantityCartItem(@RequestBody String json) {
        return cartService.updateQuantityItem(json);
    }

    @PutMapping("customer/updateOrderStatus") //done
    ResponseEntity<Object> updateStatusCodeOrder(@RequestBody String json) {
        return orderService.updateStatusOrder(json);
    }

    @GetMapping("customer/findById/{customerId}")
    public ResponseEntity<ResponseObject> getCustomerById(@PathVariable Integer customerId) {
        return userService.findUserById(customerId);
    }

    @GetMapping("address/getAll/{customerId}")
    public ResponseEntity<ResponseObject> getCustomerAddressById(@PathVariable Integer customerId) {
        return userService.findAddressByCustomerId(customerId);
    }

    @PostMapping("address/addAddress")
    public ResponseEntity<ResponseObject> addAddress(@RequestBody String json) {
        return userService.addAddress(json);
    }

    @PutMapping("customer/updateUserProfile")
    public ResponseEntity<ResponseObject> updateUserProfile(@RequestBody String json){
        return userService.update(json);
    }

    @PostMapping("customer/addReview")
    public ResponseEntity<ResponseObject> addReview(@RequestBody String json){
        return reviewService.addReview(json);
    }

    @PutMapping("customer/uploadReviewImg")
    public String uploadImageReview(@RequestParam("namePath") String namePath, @RequestParam("file") MultipartFile file,
                                     @RequestParam("reviewId") Integer reviewId) {
        return reviewService.uploadImage(file, namePath, reviewId);
    }
}
