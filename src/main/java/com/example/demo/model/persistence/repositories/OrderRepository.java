package com.example.demo.model.persistence.repositories;

import java.util.List;

import com.example.demo.model.persistence.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.persistence.UserOrder;

public interface OrderRepository extends JpaRepository<UserOrder, Long> {
	List<UserOrder> findByUser(AppUser user);
}
