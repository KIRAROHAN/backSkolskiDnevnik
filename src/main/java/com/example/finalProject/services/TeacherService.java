package com.example.finalProject.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.finalProject.dtos.TeacherDTO;
import com.example.finalProject.entities.AddressEntity;
import com.example.finalProject.entities.TeacherEntity;
import com.example.finalProject.entities.UserEntity;
import com.example.finalProject.repositories.AddressRepository;
import com.example.finalProject.repositories.TeacherRepository;
import com.example.finalProject.repositories.UserRepository;

@Service
public class TeacherService {
	
	@Autowired
	TeacherRepository teacherRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	AddressRepository addressRepository;

	public TeacherEntity addTeacher(TeacherDTO newTeacher) {


			try {

				TeacherEntity teacher = new TeacherEntity();
				UserEntity user = userRepository.findByUsername(newTeacher.getEmail());
				
				teacher.setName(newTeacher.getIme());
				teacher.setLastName(newTeacher.getPrezime());
				teacher.setWeeklyFond(newTeacher.getNedeljniFondCasova());
				teacher.setPhoneNumber(newTeacher.getBrojTelefona());
				teacher.setUser(user);
				
				for(AddressEntity address : addressRepository.findAll()) {
					
					if(address.getStreet().equals(newTeacher.getUlica()) && address.getCity().equals(newTeacher.getGrad())
							&& address.getCountry().equals(newTeacher.getDrzava())) { 
							//&& !address.isDeleted()) {
						
						//System.out.println("nasao istu adresu");
						teacher.setAddress(address);
						teacherRepository.save(teacher);
						return teacher;
					}
					
				}
				
				AddressEntity newAddress = new AddressEntity();
				
				newAddress.setStreet(newTeacher.getUlica());
				newAddress.setCity(newTeacher.getGrad());
				newAddress.setCountry(newTeacher.getDrzava());
				
				addressRepository.save(newAddress);
				teacher.setAddress(newAddress);
				
				teacherRepository.save(teacher);

				return teacher;
			}

			catch (Exception e) {
				e.printStackTrace();
				return null;
			}

		
	}

	public TeacherEntity changePhone(Long id, String phone) {
		try {

			TeacherEntity teacher = teacherRepository.findById(id).get();

			teacher.setPhoneNumber(phone);

			teacherRepository.save(teacher);

			return teacher;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public TeacherEntity changeWeeklyFond(Long id, Integer fond) {
		try {

			TeacherEntity teacher = teacherRepository.findById(id).get();

			teacher.setWeeklyFond(fond);

			teacherRepository.save(teacher);

			return teacher;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public TeacherEntity addAddressToUser(Long id, Long addressId) {
		
		try {
		
				TeacherEntity teacher = teacherRepository.findById(id).get();
				
				AddressEntity address = addressRepository.findById(addressId).get();
				
				teacher.setAddress(address);
				
				return teacherRepository.save(teacher);
		}
		
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}

	public TeacherEntity changeSurname(Long id, String prezime) {
		
		try {

			TeacherEntity teacher = teacherRepository.findById(id).get();

			teacher.setLastName(prezime);

			teacherRepository.save(teacher);

			return teacher;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public TeacherEntity changeName(Long id, String ime) {
		
		try {

			TeacherEntity teacher = teacherRepository.findById(id).get();

			teacher.setName(ime);

			teacherRepository.save(teacher);

			return teacher;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
