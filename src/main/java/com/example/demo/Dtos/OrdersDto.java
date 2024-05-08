package com.example.demo.Dtos;

import com.example.demo.Dtos.ResponseDtos.ItemResponseDto;
import com.example.demo.Enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class OrdersDto
{
    private boolean count = false;

    private OrderStatus status;

    private Map<String, Object> total;

    private int lastHours = 0;

    private int page = 1;

    private int size = 3;

    public boolean getCount() {
        return this.count;
    }
}
