package com.quoctoan.shoestore.controller;

import com.quoctoan.shoestore.model.AuthenticationResponse;
import com.quoctoan.shoestore.model.ResponseObject;
import com.quoctoan.shoestore.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;


@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping(path = "/techstore/api/management/")
public class ManagementController {

    @Autowired
    private UserService userService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private RevenueService revenueService;
    @Autowired
    PromotionService promotionService;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private BrandService brandService;

    //###############Employee/Customer################
    @PostMapping("addEmployee") //done
    public ResponseEntity<AuthenticationResponse> addEmployee(@RequestBody String json) {
        return ResponseEntity.ok(authenticationService.addEmployee(json));
    }
    @GetMapping("customer/findAll") //done
    public ResponseEntity<ResponseObject> getAllCustomer() {
        return userService.findAllCustomer();
    }
    @GetMapping("employee/findAll") //done
    public ResponseEntity<ResponseObject> findAllEmployee() {
        return userService.findAllEmployee();
    }
    @GetMapping("employee/findById/{employeeId}") //done
    public ResponseEntity<ResponseObject> getEmployeeById(@PathVariable Integer employeeId) {
        return userService.findEmployeeById(employeeId);
    }
    @PutMapping ("employee/updateStatusUser")
    public ResponseEntity<Object> updateStatusUser(@RequestBody String json) {
        return userService.updateStatusUser(json);
    }

    //##################### Order ######################
    @GetMapping("order/findAll")
    public ResponseEntity<ResponseObject> getAllOrders() {
        return orderService.findAllOrders();
    }
    @PutMapping("order/updateStatusOrder")
    public ResponseEntity<Object> updateStatusOrder(@RequestBody String json) {
        return orderService.updateStatusOrder(json);
    }

    //#################### Promotion ###################
    @PostMapping("promotion/add")
    ResponseEntity<ResponseObject> addPromotion(@RequestBody String json) {
        return promotionService.addPromotion(json);
    }

    @GetMapping("promotion/getAll")
    ResponseEntity<ResponseObject> getAllPromotion(){
        return promotionService.getAllPromotion();
    }

    //#################### Revenue #####################
    @GetMapping("revenueFromProduct")
    public ResponseEntity<ResponseObject> getTotalRevenueFromProduct() {
        return revenueService.getRevenueFromProduct();
    }
    @GetMapping("revenueProduct/between")
    public ResponseEntity<ResponseObject> findRevenueProductBetweenDates(@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                                  @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return revenueService.getRevenueProductBetweenDate(startDate, endDate);
    }

    @GetMapping("revenueByProduct")
    public ResponseEntity<ResponseObject> findRevenueByProduct() {
        return revenueService.getRevenueByProduct();
    }

    @GetMapping("getProductSalesByDate")
    public ResponseEntity<ResponseObject> getProductSalesByDate(@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                               @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return revenueService.getProductSalesByDate(startDate,endDate);
    }

    @GetMapping("revenueProductByDate/between")
    public ResponseEntity<ResponseObject> findRevenueByDates(@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                             @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return revenueService.getRevenueProductBetweenByDate(startDate, endDate);
    }
    //#################### Brand #####################
    @PostMapping("brand/add")
    public ResponseEntity<ResponseObject> addBrand(@RequestBody String json){
        return brandService.addBrand(json);
    }
    
}
