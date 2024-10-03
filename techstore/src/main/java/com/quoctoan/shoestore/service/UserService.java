package com.quoctoan.shoestore.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.quoctoan.shoestore.entity.*;
import com.quoctoan.shoestore.model.AddressResponseModel;
import com.quoctoan.shoestore.model.ResponseObject;
import com.quoctoan.shoestore.model.UserModel;
import com.quoctoan.shoestore.respository.AddressRepository;
import com.quoctoan.shoestore.respository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    AddressRepository addressRepository;
    public ResponseEntity<ResponseObject> findAllCustomer() {
        List<User> userList = new ArrayList<>();
        userList = userRepository.findAllCustomer();
        List<UserModel> userModelList = userList.stream()
                .map(user -> {
                    UserModel userModel = new UserModel();
                    userModel.setId(user.getId());
                    userModel.setEmail(user.getEmail());
                    userModel.setPassword(user.getPassword());
                    userModel.setStatus(user.getStatus());
                    userModel.setFullName(user.getFullName());
                    userModel.setPhoneNumber(user.getPhoneNumber());
                    return userModel;
                })
                .toList();
        if (!userModelList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", userModelList));
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("Not found", "Not found", ""));
        }
    }

    public ResponseEntity<ResponseObject> findAllEmployee() {
        List<User> userList = new ArrayList<>();
        userList = userRepository.findAllEmployee();
        List<UserModel> userModelList = userList.stream()
                .map(user -> {
                    UserModel userModel = new UserModel();
                    userModel.setId(user.getId());
                    userModel.setEmail(user.getEmail());
                    userModel.setRole(user.getRole().toString());
                    userModel.setPassword(user.getPassword());
                    userModel.setStatus(user.getStatus());
                    userModel.setFullName(user.getFullName());
                    userModel.setPhoneNumber(user.getPhoneNumber());
                    return userModel;
                })
                .toList();
        if (!userModelList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", userModelList));
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("Not found", "Not found", ""));
        }
    }

    public ResponseEntity<ResponseObject> findUserById(Integer customerId) {
        Optional<User> customer = userRepository.findById(customerId);
        List<UserModel> userList = new ArrayList<>();
        if (customer.isPresent()) {
            UserModel userModel = new UserModel();
            userModel.setId(customer.get().getId());
            userModel.setEmail(customer.get().getUsername());
            userModel.setStatus(customer.get().getStatus());
            userModel.setFullName(customer.get().getFullName());
            userModel.setPhoneNumber(customer.get().getPhoneNumber());
            userList.add(userModel);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", userList));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("Not found", "Not found", ""));
    }

    public ResponseEntity<ResponseObject> findEmployeeById(Integer employeeId) {
        Optional<User> user = userRepository.findById(employeeId);
        List<UserModel> userList = new ArrayList<>();
        if (user.isPresent()) {
            UserModel userModel = new UserModel();
            userModel.setId(user.get().getId());
            userModel.setEmail(user.get().getEmail());
            userModel.setRole(user.get().getRole().toString());
            userModel.setPassword(user.get().getPassword());
            userModel.setStatus(user.get().getStatus());
            userModel.setFullName(user.get().getFullName());
            userModel.setPhoneNumber(user.get().getPhoneNumber());
            userList.add(userModel);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", userList));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("Not found", "Not found", ""));
    }

    public ResponseEntity<ResponseObject> update(String json) {
        JsonNode jsonNode;
        JsonMapper jsonMapper = new JsonMapper();
        try {
            jsonNode = jsonMapper.readTree(json);
            Integer id = jsonNode.get("id") != null ? jsonNode.get("id").asInt() : -1;
            String fullName = jsonNode.get("fullName") != null ? jsonNode.get("fullName").asText() : "";
            String phoneNumber = jsonNode.get("phoneNumber") != null ? jsonNode.get("phoneNumber").asText() : "";
            Optional<User> userOptional = userRepository.findById(id);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                user.setFullName(fullName);
                user.setPhoneNumber(phoneNumber);;
                User updatedUser = userRepository.save(user);
                ObjectMapper objectMapper = new ObjectMapper();
                ArrayNode dataArray = objectMapper.createArrayNode();
                List<String> response = new ArrayList<>();
                response.add(id.toString());
                for (String value : response) {
                    ObjectNode objectNode = objectMapper.createObjectNode();
                    objectNode.put("value", value);
                    dataArray.add(objectNode);
                };
                if (updatedUser.getId() > 0) {
                    return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", dataArray));
                }
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("ERROR", "Have error when update user", ""));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("Error", e.getMessage(), ""));
        }

    }

    public ResponseEntity<Object> updateStatusUser(String json) {
        JsonNode jsonNode;
        JsonMapper jsonMapper = new JsonMapper();
        try {
            jsonNode = jsonMapper.readTree(json);
            Integer id = jsonNode.get("id") != null ? jsonNode.get("id").asInt() : -1;
            String status = jsonNode.get("status") != null ? jsonNode.get("status").asText() : "ACTIVE";
            Optional<User> userOptional = userRepository.findById(id);
            if (userOptional.isPresent()) {
                ObjectMapper objectMapper = new ObjectMapper();
                ArrayNode dataArray = objectMapper.createArrayNode();
                User user = userOptional.get();
                user.setStatus(status);
                User updatedUser = userRepository.save(user);
                List<String> response = new ArrayList<>();
                response.add(id.toString());
                response.add(status);
                for (String value : response) {
                    ObjectNode objectNode = objectMapper.createObjectNode();
                    objectNode.put("value", value);
                    dataArray.add(objectNode);
                };
                if (updatedUser.getId() > 0) {
                    return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", dataArray));
                }
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("ERROR", "Have error when update user", ""));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("Error", e.getMessage(), ""));
        }
    }

    public ResponseEntity<ResponseObject> addAddress(String json){
        JsonNode jsonNode;
        JsonMapper jsonMapper = new JsonMapper();
        try {
            jsonNode = jsonMapper.readTree(json);
            String address = jsonNode.get("address") != null ? jsonNode.get("address").asText() : "";
            String phoneNumber = jsonNode.get("phoneNumber") != null ? jsonNode.get("phoneNumber").asText() : "";
            Integer userId = jsonNode.get("userId") != null ? jsonNode.get("userId").asInt() : 1;
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                Address newAddress = new Address();
                newAddress.setAddress(address);
                newAddress.setPhoneNumber(phoneNumber);
                newAddress.setCustomer(user);
                addressRepository.save(newAddress);
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseObject("OK", "Successfully", ""));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject("ERROR", "User not found", ""));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("Error", e.getMessage(), ""));
        }
    }

    public ResponseEntity<ResponseObject> findAddressByCustomerId(Integer customerId) {
        List<Address> addressList = addressRepository.findAllAddressByCustomerId(customerId);
        List<AddressResponseModel> addressResponseModels = new ArrayList<>();
        addressList.forEach(address -> {
            AddressResponseModel addressResponseModel = new AddressResponseModel();
            addressResponseModel.setId(address.getId());
            addressResponseModel.setAddress(address.getAddress());
            addressResponseModel.setPhoneNumber(address.getPhoneNumber());
            addressResponseModels.add(addressResponseModel);
        });
        if(!addressResponseModels.isEmpty()){
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", addressResponseModels));
        }
        else {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("Not found", "Not found", ""));
        }
    }
}
