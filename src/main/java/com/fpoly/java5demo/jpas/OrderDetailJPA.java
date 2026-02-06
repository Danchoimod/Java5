package com.fpoly.java5demo.jpas;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fpoly.java5demo.entities.OrderDetail;

public interface OrderDetailJPA extends JpaRepository<OrderDetail, Integer> {

}
