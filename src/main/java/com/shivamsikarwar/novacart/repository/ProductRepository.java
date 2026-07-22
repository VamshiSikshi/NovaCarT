package com.shivamsikarwar.novacart.repository;

import com.shivamsikarwar.novacart.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product,Long> {

}
