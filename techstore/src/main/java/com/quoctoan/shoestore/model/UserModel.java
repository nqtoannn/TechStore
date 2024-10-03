package com.quoctoan.shoestore.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserModel {
    private Integer id;
    private String email;
    private String password;
    private String role;
    private String fullName;
    private String phoneNumber;
    private String status;
}
