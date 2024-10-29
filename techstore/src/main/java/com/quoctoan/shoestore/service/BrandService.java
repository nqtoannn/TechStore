package com.quoctoan.shoestore.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quoctoan.shoestore.entity.Brand;
import com.quoctoan.shoestore.model.ResponseObject;
import com.quoctoan.shoestore.respository.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class BrandService {
    @Autowired
    private BrandRepository brandRepository;

    public ResponseEntity<ResponseObject> addBrand(String json){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if (json == null || json.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseObject("ERROR", "Empty JSON", ""));
            }
            JsonNode jsonObjectCartItem = objectMapper.readTree(json);
            String brandName = jsonObjectCartItem.get("brandName") != null ?
                    jsonObjectCartItem.get("brandName").asText() : "";
            String brandLogo = jsonObjectCartItem.get("brandLogo") != null ?
                    jsonObjectCartItem.get("brandLogo").asText() : "";
            Brand brand = new Brand();
            brand.setLogo(brandLogo);
            brand.setName(brandName);
            brandRepository.save(brand);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("OK", "Successfully", ""));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject("ERROR", "An error occurred", e.getMessage()));
        }
    }

    public ResponseEntity<ResponseObject> getAllBrands(){
        Map<String, Object> results = new TreeMap<String, Object>();
        List<Brand> brandList = brandRepository.findAll();
        results.put("brandList", brandList);
        if (results.size() > 0) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", results));
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("Not found", "Not found", ""));
        }
    }
}
