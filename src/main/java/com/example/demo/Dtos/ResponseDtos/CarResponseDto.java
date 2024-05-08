package com.example.demo.Dtos.ResponseDtos;

import com.example.demo.Enums.Brand;
import com.example.demo.Enums.Type;
import com.example.demo.Models.Car;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class CarResponseDto{

    private long id;
    private String name;

    @Enumerated(EnumType.STRING)
    private Brand brand;

    private double price;

    private long quantity;

    @Enumerated(EnumType.STRING)
    private Type type;

    private LocalDateTime createdOn;

    public CarResponseDto() {}

    public CarResponseDto(Car car) {
        this.name = car.getName();
        this.brand = car.getBrand();
        this.type = car.getType();
        this.price = car.getPrice();
        this.createdOn = car.getCreatedOn();
        this.id = car.getId();
        this.quantity = car.getQuantity();
    }
}
