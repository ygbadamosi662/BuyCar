package com.example.demo.Models;

import com.example.demo.Enums.Brand;
import com.example.demo.Enums.Type;
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
public class Car
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column( name="car_id")
    private long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private Brand brand;

    private double price;

    private long quantity;

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> items;

    @Enumerated(EnumType.STRING)
    private Type type;

    @CreationTimestamp
    private LocalDateTime createdOn;

    @UpdateTimestamp
    private LocalDateTime updatedOn;
}
