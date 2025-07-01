package com.example.finalProject.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.example.finalProject.entities.RoleEntity;
import com.example.finalProject.repositories.RoleRepository;

@Component
public class RoleCustomValidator implements Validator {
	
	@Autowired
	RoleRepository roleRepository;
    

	@Override
	public boolean supports(Class<?> clazz) {
		
		return RoleEntity.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
	
		RoleEntity role = (RoleEntity) target;
		if (roleRepository.existsByName(role.getName()))
			errors.reject("400", "Ta uloga vec postoji.");
	}

}
