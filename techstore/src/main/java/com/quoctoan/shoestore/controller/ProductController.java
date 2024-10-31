package com.quoctoan.shoestore.controller;

import com.quoctoan.shoestore.model.ResponseObject;
import com.quoctoan.shoestore.service.BrandService;
import com.quoctoan.shoestore.service.ProductService;
import com.quoctoan.shoestore.service.RevenueService;
import com.quoctoan.shoestore.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
@RestController
@RequestMapping(path = "/techstore/api/")
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private RevenueService revenueService;
    @Autowired
    private BrandService brandService;
    @PostMapping("category/add")
    public ResponseEntity<Object> addCategory(@RequestBody String json){
        return productService.addCategory(json);
    }
    @GetMapping("category/findAll")
    public ResponseEntity<ResponseObject> findAllCategory() {
        return productService.findAllCategory();
    }
    @GetMapping("products/findAll")
    public ResponseEntity<ResponseObject> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return productService.findAll(page, size);
    }

    @GetMapping("brand/findAll")
    public ResponseEntity<ResponseObject> getAllBrand(){
        return brandService.getAllBrands();
    }

    @GetMapping("products/findAllWithAllStatus")
    public ResponseEntity<ResponseObject> findAllProducts() {
        return productService.findAllWithAllStatus();
    }

    @GetMapping("/products/filter")
    public ResponseEntity<ResponseObject> findAllByFilters(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false, defaultValue = "desc") String sortOrder, // Default to descending
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return productService.findAllByFilters(categoryId, minRating, sortOrder, page, size);
    }



    @GetMapping("products/category/{id}")
    public ResponseEntity<ResponseObject> findAllByCateId(
            @PathVariable Integer id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return productService.findAllByCateId(id, page, size);
    }


    @GetMapping("products/findById/{id}")
    public ResponseEntity<ResponseObject> findById(@PathVariable Integer id) {
        return productService.findById(id);
    }
    @GetMapping("products/search/{productName}")
    public ResponseEntity<ResponseObject> findAllByProductItemName(@PathVariable String productName) {
        return productService.findByName(productName);
    }
    @PostMapping("management/products/add")
    public ResponseEntity<Object> addProduct(@RequestBody String json) {
        return productService.addProduct(json);
    }
    @PutMapping("management/products/update")
    public ResponseEntity<Object> updateProduct(@RequestBody String json) {
        return productService.update(json);
    }

    @PutMapping("management/products/updateStatus")
    public ResponseEntity<Object> updateProductStatus(@RequestBody String json) {
        return productService.updateStatus(json);
    }

    @PostMapping("management/products/uploadImageProduct")
    public String uploadImageProduct(@RequestParam("namePath") String namePath, @RequestParam("file") MultipartFile file,
                                         @RequestParam("productId") Integer productId) {
        return productService.uploadImage(file, namePath, productId);
    }
    @GetMapping("products/review/getAll/{id}")
    public ResponseEntity<ResponseObject> getAllReviewByProductId(@PathVariable Integer id){
        return reviewService.getAllReviewByProductId(id);
    }

    @GetMapping("products/getMostPurchased")
    private ResponseEntity<ResponseObject> getMostPurchase(){
        return revenueService.getMostPurchased();
    }

}
