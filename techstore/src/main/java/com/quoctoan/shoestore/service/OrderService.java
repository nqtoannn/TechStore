package com.quoctoan.shoestore.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.quoctoan.shoestore.entity.*;
import com.quoctoan.shoestore.model.*;
import com.quoctoan.shoestore.payment.vnpay.PaymentService;
import com.quoctoan.shoestore.respository.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderStatusRepository orderStatusRepository;
    @Autowired
    ProductItemRepository productItemRepository;
    @Autowired
    EmailSendService emailSendService;
    @Autowired
    OrderItemRepository orderItemRepository;
    @Autowired
    PaymentMethodRepository paymentMethodRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CartRepository cartRepository;
    @Autowired
    PaymentService paymentService;
    @Autowired
    StorageService storageService;
    @Autowired
    ScheduledTaskService scheduledTaskService;

    public ResponseEntity<ResponseObject> findAllOrders() {
        List<Order> orderList = orderRepository.findAll();
        List<OrderModel> orderModelList = orderList.stream().map(order -> {
            OrderModel orderModel = new OrderModel();
            orderModel.setId(order.getId());
            orderModel.setOrderDate(order.getOrderDate());
            orderModel.setTotalPrice(order.getTotalPrice());
            orderModel.setPaymentMethod(order.getPaymentMethod().getPaymentMethodName());
            orderModel.setOrderStatus(order.getOrderStatus().getStatus());
            orderModel.setAddress(order.getAddress());
            orderModel.setNote(order.getNote());
            List<OrderItemModel> orderItemModels = order.getOrderItems().stream().map(orderItem -> {
                OrderItemModel orderItemModel = new OrderItemModel();
                orderItemModel.setOrderItemId(orderItem.getId());
                orderItemModel.setPrice(orderItem.getPrice());
                orderItemModel.setQuantity(orderItem.getQuantity());
                orderItemModel.setProductItemId(orderItem.getProductItem().getId());
                orderItemModel.setProductItemUrl(orderItem.getProductItem().getImageUrl());
                orderItemModel.setProductItemName(orderItem.getProductItem().getProductItemName());
                orderItemModel.setProductName(orderItem.getProductItem().getProduct().getName());
                return orderItemModel;
            }).collect(Collectors.toList());
            orderModel.setOrderItems(orderItemModels);
            return orderModel;
        }).collect(Collectors.toList());
        if (!orderModelList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", orderModelList));
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("Not found", "Not found", ""));
        }
    }

    public ResponseEntity<ResponseObject> findAllByStatusId(Integer id) {
        if(id == 0){
            return findAllOrders();
        }
        List<Order> orderList = new ArrayList<>();
        if(id == 4){
            orderList = orderRepository.findSuccessOrder();
        }
        else {
            orderList = orderRepository.findAllByStatusId(id);
        }
        List<OrderModel> orderModelList = orderList.stream().map(order -> {
            OrderModel orderModel = new OrderModel();
            orderModel.setId(order.getId());
            orderModel.setOrderDate(order.getOrderDate());
            orderModel.setTotalPrice(order.getTotalPrice());
            orderModel.setPaymentMethod(order.getPaymentMethod().getPaymentMethodName());
            orderModel.setOrderStatus(order.getOrderStatus().getStatus());
            orderModel.setAddress(order.getAddress());
            orderModel.setNote(order.getNote());
            List<OrderItemModel> orderItemModels = order.getOrderItems().stream().map(orderItem -> {
                OrderItemModel orderItemModel = new OrderItemModel();
                orderItemModel.setOrderItemId(orderItem.getId());
                orderItemModel.setPrice(orderItem.getPrice());
                orderItemModel.setQuantity(orderItem.getQuantity());
                orderItemModel.setProductItemId(orderItem.getProductItem().getId());
                orderItemModel.setProductItemUrl(orderItem.getProductItem().getImageUrl());
                orderItemModel.setProductItemName(orderItem.getProductItem().getProductItemName());
                orderItemModel.setProductName(orderItem.getProductItem().getProduct().getName());
                return orderItemModel;
            }).collect(Collectors.toList());
            orderModel.setOrderItems(orderItemModels);
            return orderModel;
        }).collect(Collectors.toList());
        if (!orderModelList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", orderModelList));
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("Not found", "Not found", ""));
        }
    }

    public ResponseEntity<ResponseObject> findAllByCustomerId(Integer customerId) {
        List<Order> orderList = orderRepository.findAllOrderByCustomerId(customerId);
        List<OrderModel> orderModelList = orderList.stream().map(order -> {
            OrderModel orderModel = new OrderModel();
            orderModel.setId(order.getId());
            orderModel.setOrderDate(order.getOrderDate());
            orderModel.setTotalPrice(order.getTotalPrice());
            orderModel.setPaymentMethod(order.getPaymentMethod().getPaymentMethodName());
            orderModel.setOrderStatus(order.getOrderStatus().getStatus());
            orderModel.setAddress(order.getAddress());
            orderModel.setNote(order.getNote());
            List<OrderItemModel> orderItemModels = order.getOrderItems().stream().map(orderItem -> {
                OrderItemModel orderItemModel = new OrderItemModel();
                orderItemModel.setOrderItemId(orderItem.getId());
                orderItemModel.setPrice(orderItem.getPrice());
                orderItemModel.setQuantity(orderItem.getQuantity());
                orderItemModel.setProductItemId(orderItem.getProductItem().getId());
                orderItemModel.setProductItemUrl(orderItem.getProductItem().getImageUrl());
                orderItemModel.setProductItemName(orderItem.getProductItem().getProductItemName());
                orderItemModel.setProductName(orderItem.getProductItem().getProduct().getName());
                return orderItemModel;
            }).collect(Collectors.toList());
            orderModel.setOrderItems(orderItemModels);
            return orderModel;
        }).collect(Collectors.toList());
        if (!orderModelList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", orderModelList));
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("Not found", "Not found", ""));
        }
    }

    public ResponseEntity<ResponseObject> findOrderById(Integer orderId) {
        Optional<Order> orderOptional  = orderRepository.findById(orderId);
        if(orderOptional.isPresent()){
            Order order = orderOptional.get();
            OrderModel orderModel = new OrderModel();
            orderModel.setId(order.getId());
            orderModel.setOrderDate(order.getOrderDate());
            orderModel.setTotalPrice(order.getTotalPrice());
            orderModel.setPaymentMethod(order.getPaymentMethod().getPaymentMethodName());
            orderModel.setOrderStatus(order.getOrderStatus().getStatus());
            orderModel.setAddress(order.getAddress());
            orderModel.setNote(order.getNote());
            List<OrderItemModel> orderItemModels = order.getOrderItems().stream().map(orderItem -> {
                OrderItemModel orderItemModel = new OrderItemModel();
                orderItemModel.setOrderItemId(orderItem.getId());
                orderItemModel.setPrice(orderItem.getPrice());
                orderItemModel.setQuantity(orderItem.getQuantity());
                orderItemModel.setProductItemId(orderItem.getProductItem().getId());
                orderItemModel.setProductItemUrl(orderItem.getProductItem().getImageUrl());
                orderItemModel.setProductItemName(orderItem.getProductItem().getProductItemName());
                orderItemModel.setProductName(orderItem.getProductItem().getProduct().getName());
                return orderItemModel;
            }).collect(Collectors.toList());
            orderModel.setOrderItems(orderItemModels);
            List<OrderModel> orderModelList = new ArrayList<>();
            orderModelList.add(orderModel);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", orderModelList));
        }
        else{
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("Not found", "Not found", ""));
        }
    }

    public ResponseEntity<ResponseObject> order(String json, HttpServletRequest request) {
        ObjectMapper objectMapper = new ObjectMapper();
        Order order = new Order();
        try {
            if (json == null || json.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseObject("ERROR", "Empty JSON", ""));
            }
            JsonNode jsonObjectOrder = objectMapper.readTree(json);
            Integer customerId = jsonObjectOrder.get("customerId") != null ?
                    Integer.parseInt(jsonObjectOrder.get("customerId").asText()) : 1;
            Integer payId = jsonObjectOrder.get("payId") != null ?
                    Integer.parseInt(jsonObjectOrder.get("payId").asText()) : 1;
            Double totalPrice = jsonObjectOrder.get("totalPrice") != null ?
                    Double.parseDouble(jsonObjectOrder.get("totalPrice").asText()) : 1;
            String orderDate = jsonObjectOrder.get("orderDate") != null ?
                    jsonObjectOrder.get("orderDate").asText() : "";
            String address = jsonObjectOrder.get("address") != null ?
                    jsonObjectOrder.get("address").asText() : "";
            JsonNode orderItemList = jsonObjectOrder.get("orderItemList");
            Optional<User> user = userRepository.findById(customerId);
            User newUser = new User();
            newUser.setId(user.get().getId());
            Optional<PaymentMethod> paymentMethodModel = paymentMethodRepository.findById(payId);
            PaymentMethod paymentMethod = new PaymentMethod();
            paymentMethod.setId(paymentMethodModel.get().getId());
            paymentMethod.setPaymentMethodName(paymentMethodModel.get().getPaymentMethodName());
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate parsedDate = LocalDate.parse(orderDate, dateFormatter);
            order.setCustomer(newUser);
            order.setPaymentMethod(paymentMethod);
            order.setTotalPrice(totalPrice);
            if(paymentMethodModel.get().getPaymentMethodName().equals("VN-Pay")){
                OrderStatus orderStatus = new OrderStatus();
                orderStatus.setId(6);
                order.setOrderStatus(orderStatus);
            }else {
                OrderStatus orderStatus = new OrderStatus();
                orderStatus.setId(1);
                order.setOrderStatus(orderStatus);
            }
            order.setOrderDate(parsedDate);
            order.setAddress(address);
            String[] cc = {};
            List<OrderItem> orderItems = new ArrayList<>();
            List<Integer> productItemIdList = new ArrayList<>();
            if (orderItemList.isArray()) {
                for (JsonNode orderItemJson : orderItemList) {
                    OrderItem orderItem = new OrderItem();
                    Optional<ProductItem> productItemModel = productItemRepository.findById(orderItemJson.get("productItemId").asInt());
                    ProductItem productItemSave = productItemModel.get();
                    productItemSave.setQuantityInStock(productItemSave.getQuantityInStock() - orderItemJson.get("quantity").asInt());
                    productItemRepository.save(productItemSave);
                    ProductItem productItem = new ProductItem();
                    productItem.setProductItemName(productItemModel.get().getProductItemName());
                    productItem.setId(productItemModel.get().getId());
                    productItem.setPrice(productItemModel.get().getPrice());
                    productItem.setStatus(productItemModel.get().getStatus());
                    productItem.setQuantityInStock(productItemModel.get().getQuantityInStock());
                    productItemIdList.add(productItem.getId());
                    orderItem.setProductItem(productItem);
                    orderItem.setPrice(orderItemJson.get("price").asDouble());
                    orderItem.setQuantity(orderItemJson.get("quantity").asInt());
                    orderItems.add(orderItem);
                }
            }
            Map<String, Object> model = new HashMap<>();
            orderRepository.save(order);
            Order savedOrder = orderRepository.save(order);
            model.put("orderId", savedOrder.getId());
            model.put("totalPrice", savedOrder.getTotalPrice());
            model.put("paymentMethod", savedOrder.getPaymentMethod().getPaymentMethodName());
            model.put("orderDate", savedOrder.getOrderDate());
            model.put("address", savedOrder.getAddress());
            List<Map<String, Object>> orderItemsJSON = new ArrayList<>();
            for (OrderItem orderItem : orderItems) {
                orderItem.setOrder(savedOrder);
                orderItemRepository.save(orderItem);
                Map<String, Object> orderItemJSON = new HashMap<>();
                orderItemJSON.put("price", orderItem.getPrice());
                orderItemJSON.put("quantity", orderItem.getQuantity());
                orderItemJSON.put("productItemName", orderItem.getProductItem().getProductItemName());
                orderItemsJSON.add(orderItemJSON);
            }
            System.out.println(customerId + productItemIdList.toString());
            deleteCartByCustomerProductItem(customerId,productItemIdList);
            model.put("orderItems", orderItemsJSON);
            emailSendService.sendMail(user.get().getEmail(), cc, "Thông báo đặt hàng thành công", model);
            if(paymentMethodModel.get().getPaymentMethodName().equals("VN-Pay")){
                String url = paymentService.createVnPayPaymentforOrder(totalPrice,order.getId(),request);
                scheduledTaskService.scheduleOrderCheck(order.getId());
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseObject("OK", "Successfully", url));
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("OK", "Successfully", ""));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject("ERROR", "An error occurred", e.getMessage()));
        }
    }

    public ResponseEntity<ResponseObject> getUrlVnPayById(Integer orderId, HttpServletRequest request){
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if(!optionalOrder.isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject("BAD_REQUEST", "Can not find order with this id", ""));
        }
        if(!optionalOrder.get().getPaymentMethod().getPaymentMethodName().equals("VN-Pay")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject("BAD_REQUEST", "Wrong payment method", ""));
        }
        Order order = optionalOrder.get();
        String url = paymentService.createVnPayPaymentforOrder(order.getTotalPrice(),orderId,request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseObject("OK", "Successfully", url));
    }

    public void deleteCartByCustomerProductItem(Integer customerId, List<Integer> productItemIdList) {
        productItemIdList.forEach(id -> {
            try {
                Optional<Cart> existingCartOptional = Optional.ofNullable(cartRepository.findExistCart(customerId, id));
                if (existingCartOptional.isPresent()) {
                    cartRepository.deleteCartById(existingCartOptional.get().getId());
                }
            } catch (Exception e) {
                System.err.println("Error deleting cart item with ProductItemId " + id + ": " + e.getMessage());
            }
        });
    }



    public ResponseEntity<Object> updateStatusOrder(String json) {
        JsonNode jsonNode;
        JsonMapper jsonMapper = new JsonMapper();
        try {
            jsonNode = jsonMapper.readTree(json);
            Integer orderId = jsonNode.get("orderId") != null ? jsonNode.get("orderId").asInt() : null;
            Integer statusCode = jsonNode.get("statusCode") != null ? jsonNode.get("statusCode").asInt() : -1;
            String note = jsonNode.get("note") != null ?
                    jsonNode.get("note").asText() : "";
            Optional<Order> orderOptional = orderRepository.findById(orderId);
            if (orderOptional.isPresent()) {
                Order order = orderOptional.get();
                OrderStatus orderStatus = new OrderStatus();
                orderStatus.setId(statusCode);
                order.setOrderStatus(orderStatus);
                if(!note.isEmpty()){
                    order.setNote(note);
                }
                orderRepository.save(order);
                if (statusCode == 8) {
                    List<OrderItem> orderItemList = order.getOrderItems().stream().toList();
                    for (OrderItem orderItem : orderItemList) {
                        Optional<ProductItem> productItemOptional = productItemRepository.findById(orderItem.getProductItem().getId());
                        if (productItemOptional.isPresent()){
                            ProductItem productItem = productItemOptional.get();
                            productItem.setQuantityInStock(productItem.getQuantityInStock()+orderItem.getQuantity());
                            productItemRepository.save(productItem);
                        }
                    }
                }
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", ""));
            }
            else
                return ResponseEntity.status(HttpStatus.OK)
                            .body(new ResponseObject("ERROR", "Have error when update status code order", ""));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("Error", e.getMessage(), ""));
        }
    }

    public String uploadImage(MultipartFile file, String namePath, Integer orderId) {
        String imageUrl = storageService.uploadImages(file, namePath);
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if(orderOptional.isPresent()){
            Order order = orderOptional.get();
            order.setNote(imageUrl);
            OrderStatus orderStatus = new OrderStatus();
            orderStatus.setId(4);
            order.setOrderStatus(orderStatus);
            orderRepository.save(order);
        }
        return imageUrl;
    }

}
