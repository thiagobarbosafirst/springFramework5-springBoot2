package com.example.projetospring.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.example.projetospring.entity.User;
import com.example.projetospring.repository.UserRepository;

@Component
public class DataInitializr implements ApplicationListener<ContextRefreshedEvent>{

	@Autowired
	UserRepository userRepository;
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		List<User> users = userRepository.findAll();
		
		if(users.isEmpty()) {
			User user = new User();
			
			user.setName("Thiago Barbosa");
			user.setEmail("thiago.barbosa@bbts.com.br");
			
			userRepository.save(user);
		}
		
		User user = userRepository.findByEmail("thiago.barbosa@bbts");
		
		System.out.println(user.getName());
		
	}

}
