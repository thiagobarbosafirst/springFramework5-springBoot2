package com.example.projetospring.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.projetospring.entity.Usuario;

public interface UserRepository extends JpaRepository<Usuario, Long>{
	
}
