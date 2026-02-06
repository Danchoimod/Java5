package com.fpoly.java5demo.jpas;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fpoly.java5demo.entities.User;

public interface UserJPA extends JpaRepository<User, Integer> {

}
