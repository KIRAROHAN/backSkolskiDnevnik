package com.example.finalProject.services;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.PositiveOrZero;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.finalProject.dtos.StudentDTO;
import com.example.finalProject.entities.AddressEntity;
import com.example.finalProject.entities.ParentEntity;
import com.example.finalProject.entities.StudentEntity;
import com.example.finalProject.entities.UserEntity;
import com.example.finalProject.repositories.AddressRepository;
import com.example.finalProject.repositories.ParentRepository;
import com.example.finalProject.repositories.StudentRepository;
import com.example.finalProject.repositories.UserRepository;

@Service
public class StudentService {

	@Autowired
	StudentRepository studentRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	private ParentRepository parentRepository;

	@Autowired
	AddressRepository addressRepository;

	public StudentEntity addStudent(StudentDTO newStudent) {

		try {

			StudentEntity student = new StudentEntity();
			UserEntity user = userRepository.findByUsername(newStudent.getEmail());

			student.setName(newStudent.getIme());
			student.setLastName(newStudent.getPrezime());
			student.setSchoolClass(newStudent.getRazred());
			student.setPhoneNumber(newStudent.getBrojTelefona());
			student.setUser(user);

			for (AddressEntity address : addressRepository.findAll()) {

				if (address.getStreet().equals(newStudent.getUlica()) && address.getCity().equals(newStudent.getGrad())
						&& address.getCountry().equals(newStudent.getDrzava())) {
					// && !address.isDeleted()) {

					// System.out.println("nasao istu adresu");
					student.setAddress(address);
					studentRepository.save(student);
					return student;
				}

			}

			AddressEntity newAddress = new AddressEntity();

			newAddress.setStreet(newStudent.getUlica());
			newAddress.setCity(newStudent.getGrad());
			newAddress.setCountry(newStudent.getDrzava());

			addressRepository.save(newAddress);
			student.setAddress(newAddress);

			studentRepository.save(student);

			return student;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public StudentEntity changePhone(Long id, String phone) {
		try {

			StudentEntity student = studentRepository.findById(id).get();

			student.setPhoneNumber(phone);

			studentRepository.save(student);

			return student;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public StudentEntity addAddressToUser(Long id, Long addressId) {

		try {

			StudentEntity student = studentRepository.findById(id).get();

			AddressEntity address = addressRepository.findById(addressId).get();

			student.setAddress(address);

			return studentRepository.save(student);
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public StudentEntity changeSurname(Long id, String prezime) {

		try {

			StudentEntity student = studentRepository.findById(id).get();

			student.setLastName(prezime);

			studentRepository.save(student);

			return student;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public StudentEntity changeName(Long id, String ime) {

		try {

			StudentEntity student = studentRepository.findById(id).get();

			student.setName(ime);

			studentRepository.save(student);

			return student;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public StudentEntity changeSchoolClass(Long id, String shoolClass) {

		try {

			StudentEntity student = studentRepository.findById(id).get();

			student.setSchoolClass(shoolClass);

			studentRepository.save(student);

			return student;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public StudentEntity addParentToStudent(Long idParent, Long idStudent) {

		try {

			StudentEntity student = studentRepository.findById(idStudent).get();

			ParentEntity parent = parentRepository.findById(idParent).get();

			List<ParentEntity> parents = student.getParents();

			if (parents == null) {

				parents = new ArrayList<ParentEntity>();

			}

			parents.add(parent);

			student.setParents(parents);

			studentRepository.save(student);

			return student;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public StudentEntity removeParentFromStudent(Long idParent, Long idStudent) {

		try {

			StudentEntity student = studentRepository.findById(idStudent).get();

			ParentEntity parent = parentRepository.findById(idParent).get();

			student.getParents().remove(parent);

			studentRepository.save(student);

			return student;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
