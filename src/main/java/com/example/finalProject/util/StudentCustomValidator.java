package com.example.finalProject.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.example.finalProject.dtos.StudentDTO;
import com.example.finalProject.entities.UserEntity;
import com.example.finalProject.repositories.UserRepository;

@Component
public class StudentCustomValidator implements Validator {

	@Autowired
	UserRepository userRepository;

	@Override
	public boolean supports(Class<?> clazz) {

		return StudentDTO.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {

		StudentDTO teacher = (StudentDTO) target;
		UserEntity user = userRepository.findByUsername(teacher.getEmail());

		if(user == null) {
			errors.reject("400", "Korisnicko ime ne postoji.");
		}

		if (user != null) {
			if (!user.getRole().getName().equals("STUDENT")) {

				errors.reject("400", "Korisnik sa unetim korisnickim imenom nema ulogu studenta.");
			}

			if (user.getStudent() != null || user.getAdmin() != null || user.getTeacher() != null
					|| user.getParent() != null) {
				errors.reject("400", "Korisnicko ime je zauzeto.");
			}
		}

	}

}
