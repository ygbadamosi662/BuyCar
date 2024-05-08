package com.example.demo.Dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RemoveFromCartDto
{
    @NotNull
    private long item_id;

    @NotNull
    private int qty;
}
