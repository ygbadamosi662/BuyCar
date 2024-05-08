package com.example.demo.Dtos;

import com.example.demo.Enums.Brand;
import com.example.demo.Enums.Type;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class CarsDto
{
    private boolean count = false;

    private String name;

    @Enumerated(EnumType.STRING)
    private Brand brand;

    private Map<String, Object> price;

    private Map<String, Object> quantity;

    @Enumerated(EnumType.STRING)
    private Type type;

    private int lastHours = 0;

    private int page = 1;

    private int size = 3;

    public boolean getCount() {
        return this.count;
    }
}
