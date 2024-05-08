package com.example.demo.Dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddToCartDto
{
    @NotNull
    private long car_id;

    @NotNull
    private int qty = 0;
}
