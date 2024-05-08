package com.example.demo.Dtos;

import com.example.demo.Enums.Brand;
import com.example.demo.Enums.Type;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarDto
{
    @NotBlank
    @NotNull
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Brand brand;

    @NotNull
    private double price;

    @NotNull
    private long quantity;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Type type;
}
