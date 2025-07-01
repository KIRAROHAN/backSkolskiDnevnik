package com.example.finalProject.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.example.finalProject.dtos.AddressDTO;
import com.example.finalProject.dtos.UserDTO;
import com.example.finalProject.entities.UserEntity;
import com.example.finalProject.repositories.AddressRepository;
import com.example.finalProject.repositories.UserRepository;

@Component
public class AddressCustomValidator implements Validator {
	
	@Autowired
	AddressRepository addressRepository;
    

	@Override
	public boolean supports(Class<?> clazz) {
		
		return AddressDTO.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
	
		AddressDTO address = (AddressDTO) target;
		if (addressRepository.existsByStreetAndCityAndCountry(address.getUlica(),address.getGrad(),address.getDrzava())) {
			errors.reject("400", "Adresa vec postoji u bazi.");
		}
	}

}
