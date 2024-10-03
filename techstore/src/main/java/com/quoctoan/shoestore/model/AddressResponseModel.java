package com.quoctoan.shoestore.model;

import com.quoctoan.shoestore.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
public class AddressResponseModel {
    private Integer id;
    private String address;
    private String phoneNumber;
}
