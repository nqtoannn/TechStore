package com.quoctoan.shoestore.controller;


import com.quoctoan.shoestore.model.ResponseObject;
import com.quoctoan.shoestore.service.ProductItemService;
import com.quoctoan.shoestore.service.RevenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(path = "/techstore/api/")
public class ProductItemController {

    @Autowired
    private ProductItemService productItemService;
    @Autowired
    private RevenueService revenueService;
    @GetMapping("productItem/findAll")
    public ResponseEntity<ResponseObject> findAll() {
        return productItemService.findAll();
    }
    @PostMapping("management/productItem/add")
    public ResponseEntity<Object> addProductItem(@RequestBody String json) {
        return productItemService.add(json);
    }
    @PutMapping("management/productItem/update")
    public ResponseEntity<Object> updateProductItem(@RequestBody String json) {
        return productItemService.update(json);
    }
    @PutMapping("management/productItem/updateStatus")
    public ResponseEntity<Object> updateProductItemStatus(@RequestBody String json) {
        return productItemService.updateStatus(json);
    }
    @GetMapping("productItem/findById/{productItemId}")
    public ResponseEntity<ResponseObject> findProductItemById(@PathVariable Integer productItemId) {
        return productItemService.findProductItemById(productItemId);
    }

    @PostMapping("management/productItem/uploadImageProductItem")
    public String uploadImageProductItem(@RequestParam("namePath") String namePath, @RequestParam("file") MultipartFile file,
                                         @RequestParam("productItemId") Integer productItemId) {
        return productItemService.uploadImage(file, namePath, productItemId);
    }

}
