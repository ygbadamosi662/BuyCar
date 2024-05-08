package com.example.demo.Dtos.ResponseDtos;


import com.example.demo.Enums.OrderStatus;
import com.example.demo.Models.Item;
import com.example.demo.Models.Order;
import com.example.demo.Models.Transaction;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class OrderResponseDto
{
    private long id;

    private OrderStatus status;

    private double total;

    private List<ItemResponseDto> items = new ArrayList<>();

    private long user_id;

    private long transaction_id;

    private int totalQty;

    private LocalDateTime createdOn;

    private LocalDateTime updatedOn;

    public OrderResponseDto () {}

    public OrderResponseDto (Order order) {
        this.createdOn = order.getCreatedOn();
        this.updatedOn = order.getUpdatedOn();
        this.id = order.getId();
        this.user_id = order.getUser().getId();
        Transaction trans = order.getTransaction();
        if(trans != null) {
            this.transaction_id = trans.getId();
        }
        this.id = order.getId();
        this.total = order.getTotal();
        this.status = order.getStatus();
        this.totalQty = order.getTotalQty();
        for (Item item: order.getItems()) {
            ItemResponseDto itemResponseDto = new ItemResponseDto(item);
            this.items.add(itemResponseDto);
        }
    }
}
