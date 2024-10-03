package com.quoctoan.shoestore.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quoctoan.shoestore.entity.*;
import com.quoctoan.shoestore.model.*;
import com.quoctoan.shoestore.respository.CartRepository;
import com.quoctoan.shoestore.respository.ProductItemRepository;
import com.quoctoan.shoestore.respository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductItemRepository productItemRepository;

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<ResponseObject>   addToCart(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if (json == null || json.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseObject("ERROR", "Empty JSON", ""));
            }
            JsonNode jsonObjectCartItem = objectMapper.readTree(json);
            Integer customerId = jsonObjectCartItem.get("customerId") != null ?
                    jsonObjectCartItem.get("customerId").asInt() : 1;
            Integer productItemId = jsonObjectCartItem.get("product_item_id") != null ?
                    jsonObjectCartItem.get("product_item_id").asInt() : 1;
            Integer quantity = jsonObjectCartItem.get("quantity") != null ?
                    jsonObjectCartItem.get("quantity").asInt() : 1;
            Optional<ProductItem> productItemOptional = productItemRepository.findById(productItemId);
            Optional<User> userOptional = userRepository.findById(customerId);
            if (userOptional.isPresent() && productItemOptional.isPresent()) {
                ProductItem productItem = productItemOptional.get();
                User user = userOptional.get();
                Optional<Cart> existingCartOptional = Optional.ofNullable(cartRepository.findExistCart(user.getId(), productItem.getId()));
                if (existingCartOptional.isPresent()) {
                    Cart existingCart = existingCartOptional.get();
                    existingCart.setQuantity(existingCart.getQuantity() + quantity);
                    cartRepository.save(existingCart);
                } else {
                    Cart cart = new Cart();
                    cart.setCustomer(user);
                    cart.setProductItem(productItem);
                    cart.setQuantity(quantity);
                    cartRepository.save(cart);
                }
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseObject("OK", "Successfully", ""));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject("ERROR", "Cart or product item not found", ""));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject("ERROR", "An error occurred", e.getMessage()));
        }
    }


    public ResponseEntity<ResponseObject> findAllByUserId(Integer userId) {
        List<CartModel> cartModelList = new ArrayList<>();
        try {
            List<Cart> cartItemList = cartRepository.findAllCartByCustomerId(userId);
            cartItemList.forEach(cart -> {
                ProductItemModel productItemModel = new ProductItemModel(
                        cart.getProductItem().getId(),
                        cart.getProductItem().getProductItemName(),
                        cart.getProductItem().getPrice(),
                        cart.getProductItem().getQuantityInStock(),
                        cart.getProductItem().getStatus(),
                        cart.getProductItem().getImageUrl(),""
                );
                CartModel cartModel = new CartModel();
                cartModel.setId(cart.getId());
                cartModel.setQuantity(cart.getQuantity());
                cartModel.setProductItemModel(productItemModel);
                cartModel.setProductId(cart.getProductItem().getProduct().getId());
                cartModel.setProductName(cart.getProductItem().getProduct().getName());
                cartModelList.add(cartModel);
            });

            if (!cartModelList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", cartModelList));
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("Not found", "Not found", ""));
            }
        } catch (Exception e) {
            e.printStackTrace();  // Log lỗi ra console
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObject("Error", e.getMessage(), ""));
        }
    }

//    public ResponseEntity<ResponseObject> deleteCart(Integer cartId) {
//        Optional<Cart> cartItem = cartRepository.findById(cartId);
//        if (cartItem.isPresent()) {
//             cartRepository.deleteById(cartId);
//            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", ""));
//        }
//        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("ERROR", "Cannot found this item", ""));
//    }

    public ResponseEntity<ResponseObject> deleteCart(Integer cartId) {
        try {
            // Kiểm tra xem cartId có hợp lệ và có tồn tại trong database không
            Optional<Cart> cartItem = cartRepository.findById(cartId);
            if (cartItem.isPresent()) {
                // Xóa mục khỏi cơ sở dữ liệu
                cartRepository.deleteCartById(cartId);
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully deleted", ""));
            } else {
                // Trường hợp không tìm thấy mục giỏ hàng với cartId đã cung cấp
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject("ERROR", "Cannot find this item", ""));
            }
        } catch (Exception e) {
            // Xử lý ngoại lệ và trả về thông báo lỗi
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObject("ERROR", "An error occurred while deleting the item", e.getMessage()));
        }
    }

    public ResponseEntity<ResponseObject> deleteAllCartByCustomerId(Integer customerId) {
        try {
            cartRepository.deleteAllCartByCustomerId(customerId);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", ""));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("ERROR", e.getMessage(), ""));
        }
    }


    public ResponseEntity<ResponseObject> updateQuantityItem(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if (json == null || json.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseObject("ERROR", "Empty JSON", ""));
            }
            JsonNode jsonObjectUpdateQuantity = objectMapper.readTree(json);
            Integer cartId = jsonObjectUpdateQuantity.get("cartId") != null ?
                    Integer.parseInt(jsonObjectUpdateQuantity.get("cartId").asText()) : 1;
            Integer number = jsonObjectUpdateQuantity.get("number") != null ?
                    jsonObjectUpdateQuantity.get("number").asInt() : 1;
            Optional<Cart> cart = cartRepository.findById(cartId);
            if (cart.isPresent()) {
                cart.get().setQuantity(number);
                cartRepository.save(cart.get());
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", ""));
            }
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("ERROR", "Cannot update quantity", ""));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject("ERROR", "An error occurred", e.getMessage()));
        }
    }
}
