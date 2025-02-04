package com.example.hairsalon.model;

public class Brand {
    Integer id;
    String name;
    String logo;

    public Brand(Integer id, String name, String logo) {
        this.id = id;
        this.name = name;
        this.logo = logo;
    }

    public Brand() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
}
