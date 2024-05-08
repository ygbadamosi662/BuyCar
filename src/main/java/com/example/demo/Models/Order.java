package com.example.demo.Models;


import com.example.demo.Enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Table(name = "`order`")
@Entity
public class Order
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column( name="order_id")
    private long id;

    private OrderStatus status;

    private double total = 0;

    private int totalQty = 0;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<Item> items;

    @ManyToOne
    @JoinColumn(name = "user")
    private User user;

    @OneToOne(mappedBy = "orderr", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Transaction transaction;

    @CreationTimestamp
    private LocalDateTime createdOn;

    @UpdateTimestamp
    private LocalDateTime updatedOn;
}
