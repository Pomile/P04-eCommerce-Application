package com.example.demo.model.persistence.repositories;

import com.example.demo.model.persistence.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.persistence.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {
	Cart findByUser(AppUser user);
}
