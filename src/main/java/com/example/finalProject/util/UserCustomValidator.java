package com.example.finalProject.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.example.finalProject.dtos.UserDTO;
import com.example.finalProject.entities.UserEntity;
import com.example.finalProject.repositories.UserRepository;

@Component
public class UserCustomValidator implements Validator {
	
	@Autowired
	UserRepository userRepository;
    

	@Override
	public boolean supports(Class<?> clazz) {
		
		return UserDTO.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
	
		UserDTO user = (UserDTO) target;
		if (userRepository.existsByUsername(user.getEmail()))
			errors.reject("400", "Korisnicko ime vec postoji.");
	}

}
