package com.example.demo.Models;

import com.example.demo.Models.Order;
import com.example.demo.Models.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
public class Item
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column( name="item_id")
    private long id;

    private double paidPrice;

    private long quantity = 0;

    @ManyToOne
    @JoinColumn(name = "car")
    private Car car;

    @ManyToOne
    @JoinColumn(name = "`order`")
    private Order order;

    @CreationTimestamp
    private LocalDateTime createdOn;

    @UpdateTimestamp
    private LocalDateTime updatedOn;
}
