package com.example.demo.Repos;

import com.example.demo.Models.Item;
import com.example.demo.Models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemRepo extends JpaRepository<Item, Long>
{

    Optional<Item> findByIdAndOrder(long id, Order order);
}
