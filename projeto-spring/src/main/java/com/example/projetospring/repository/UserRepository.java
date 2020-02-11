package com.example.projetospring.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.projetospring.entity.User;

public interface UserRepository extends JpaRepository<User, Long>{
	
	User findByEmail(String email);
	
}
