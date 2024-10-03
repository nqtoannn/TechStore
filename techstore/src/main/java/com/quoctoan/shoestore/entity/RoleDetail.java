package com.quoctoan.shoestore.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Id;

public class RoleDetail extends BaseEntity{
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "role_id")
    private Integer roleId;
    @Column(name = "permission_id")
    private Integer permissionId;
    @Column(name = "option_id")
    private Integer optionId;
}
