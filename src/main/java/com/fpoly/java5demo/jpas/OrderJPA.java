package com.fpoly.java5demo.jpas;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fpoly.java5demo.entities.Order;

public interface OrderJPA extends JpaRepository<Order, Integer> {

}
