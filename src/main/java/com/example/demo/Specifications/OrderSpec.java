package com.example.demo.Specifications;

import com.example.demo.Enums.OrderStatus;
import com.example.demo.Models.Order;
import com.example.demo.Models.User;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class OrderSpec
{
    public static Specification<Order> statusEquals(OrderStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Order> totalEquals(double total) {
        return (root, query, cb) -> cb.equal(root.get("total"), total);
    }

    public static Specification<Order> totalGreater(double total) {
        return (root, query, cb) -> cb.greaterThan(root.get("total"), total);
    }

    public static Specification<Order> userEquals(User user) {
        return (root, query, cb) -> cb.equal(root.get("user"), user);
    }

    public static Specification<Order> totalLess(double total) {
        return (root, query, cb) -> cb.lessThan(root.get("total"), total);
    }

    public static Specification<Order> createdBefore(LocalDateTime before) {
        return (root, query, cb) -> cb.lessThan(root.get("createdOn"), before);
    }

}
