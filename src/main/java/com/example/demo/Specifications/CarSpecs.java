package com.example.demo.Specifications;

import com.example.demo.Enums.*;
import com.example.demo.Models.Car;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

public class CarSpecs
{
    public static Specification<Car> nameEquals(String name) {
        return (root, query, cb) -> cb.equal(root.get("name"), name);
    }

    public static Specification<Car> typeEquals(Type type) {
        return (root, query, cb) -> cb.equal(root.get("type"), type);
    }

    public static Specification<Car> brandEquals(Brand brand) {
        return (root, query, cb) -> cb.equal(root.get("brand"), brand);
    }

    public static Specification<Car> quantityEquals(double qty) {
        return (root, query, cb) -> cb.equal(root.get("quantity"), qty);
    }

    public static Specification<Car> quantityGreater(double qty) {
        return (root, query, cb) -> cb.greaterThan(root.get("quantity"), qty);
    }

    public static Specification<Car> quantityLess(double qty) {
        return (root, query, cb) -> cb.lessThan(root.get("quantity"), qty);
    }

    public static Specification<Car> priceEquals(double price) {
        return (root, query, cb) -> cb.equal(root.get("price"), price);
    }

    public static Specification<Car> priceGreater(double price) {
        return (root, query, cb) -> cb.greaterThan(root.get("price"), price);
    }

    public static Specification<Car> priceLess(double price) {
        return (root, query, cb) -> cb.lessThan(root.get("price"), price);
    }

    public static Specification<Car> createdBefore(LocalDateTime before) {
        return (root, query, cb) -> cb.lessThan(root.get("createdOn"), before);
    }

    public static Specification<Car> searchCars(String search) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(search)) {
                return null;
            }
            String pattern = "%" + search + "%";
            return cb.or(cb.like(cb.lower(root.get("name")), pattern.toLowerCase()));
        };
    }
}
