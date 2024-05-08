package com.example.demo.Repos;

import com.example.demo.Enums.Brand;
import com.example.demo.Enums.Type;
import com.example.demo.Models.Car;
import com.example.demo.Models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarRepo extends JpaRepository<Car,Long>
{

    boolean existsByNameAndBrandAndType(String name, Brand brand, Type type);

    long count(Specification<Car> spec);

    Page<Car> findAll(Specification<Car> spec, Pageable pageable);

    List<Car> findAll(Specification<Car> spec);
}
