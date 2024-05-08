package com.example.demo.Dtos.ResponseDtos;

import com.example.demo.Models.Item;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDateTime;

@Getter
@Setter
public class ItemResponseDto
{
    private long id;

    private double paidPrice;

    private long quantity;

    private long order_id;

    private CarResponseDto car;

    private LocalDateTime createdOn;

    private LocalDateTime updatedOn;

    public ItemResponseDto() {}

    public ItemResponseDto(Item item) {
        this.id = item.getId();
        this.paidPrice = item.getPaidPrice();
        this.quantity = item.getQuantity();
        this.order_id = item.getOrder().getId();
        this.createdOn = item.getCreatedOn();
        this.updatedOn = item.getUpdatedOn();
        this.car = new CarResponseDto(item.getCar());
    }
}
