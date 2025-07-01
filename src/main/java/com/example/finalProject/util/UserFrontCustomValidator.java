package com.example.finalProject.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.example.finalProject.dtos.UserDTOforFRONT;
import com.example.finalProject.repositories.UserRepository;

@Component
public class UserFrontCustomValidator implements Validator {

	@Autowired
	UserRepository userRepository;

	@Override
	public boolean supports(Class<?> clazz) {

		return UserDTOforFRONT.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {

		UserDTOforFRONT user = (UserDTOforFRONT) target;
		// ukoliko korisnik koji je pronadjen ima razlicit id od user-a, a isti username
		// ne dozvoljava promenu
		if (userRepository.existsByUsername(user.getEmail())) {
			
			
			if (user.getId() != null) {
				

				if (userRepository.findByUsername(user.getEmail()).getId().compareTo(user.getId()) != 0) {
					errors.reject("400", "Korisnicko ime vec postoji.");
				}				
			}
			
			else {
				
				errors.reject("400", "Korisnicko ime vec postoji.");
			}
		}
	}

}
