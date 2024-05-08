package com.example.demo.Dtos;

import com.example.demo.Enums.Brand;
import com.example.demo.Enums.Type;
import com.example.demo.Models.Car;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarUpdateDto
{
    @NotNull
    private long id;
    private String name;

    @Enumerated(EnumType.STRING)
    private Brand brand;

    private double price = 0;

    private long quantity = 0;

    @Enumerated(EnumType.STRING)
    private Type type;

    private String message;

    public Car updateCar(Car car)
    {
        if(this.name == null && this.brand == null
                && this.type == null && this.quantity == 0 && this.price == 0)
        {
            this.message = "There is nothing to update";
            return null;
        }

        if(this.name != null) {
            car.setName(this.name);
        }
        if(this.brand != null) {
            car.setBrand(this.brand);
        }
        if(this.type != null) {
            car.setType(this.type);
        }
        if(this.price != 0) {
            car.setPrice(this.price);
        }
        if(this.quantity != 0) {
            car.setQuantity(this.quantity);
        }

        return car;
    }

}
