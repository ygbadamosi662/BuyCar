package com.example.demo.Repos;

import com.example.demo.Enums.OrderStatus;
import com.example.demo.Models.Order;
import com.example.demo.Models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepo extends JpaRepository<Order, Long>
{
    long count(Specification<Order> spec);

    Page<Order> findAll(Specification<Order> spec, Pageable pageable);

    List<Order> findAll(Specification<Order> spec);

    Optional<Order> findByUserAndStatus(User user, OrderStatus status);

    Optional<Order> findByIdAndUser(long id, User user);
}
