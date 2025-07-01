package com.example.finalProject.services;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.finalProject.dtos.UserDTO;
import com.example.finalProject.dtos.UserDTOforFRONT;
import com.example.finalProject.entities.AddressEntity;
import com.example.finalProject.entities.AdminEntity;
import com.example.finalProject.entities.GradeEntity;
import com.example.finalProject.entities.ParentEntity;
import com.example.finalProject.entities.RoleEntity;
import com.example.finalProject.entities.StudentEntity;
import com.example.finalProject.entities.SubjectEntity;
import com.example.finalProject.entities.TeacherEntity;
import com.example.finalProject.entities.UserEntity;
import com.example.finalProject.repositories.AddressRepository;
import com.example.finalProject.repositories.AdminRepository;
import com.example.finalProject.repositories.GradeRepository;
import com.example.finalProject.repositories.ParentRepository;
import com.example.finalProject.repositories.RoleRepository;
import com.example.finalProject.repositories.StudentRepository;
import com.example.finalProject.repositories.SubjectRepository;
import com.example.finalProject.repositories.TeacherRepository;
import com.example.finalProject.repositories.UserRepository;
import com.example.finalProject.util.Encryption;

@Service
public class UserService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	TeacherRepository teacherRepository;

	@Autowired
	StudentRepository studentRepository;

	@Autowired
	ParentRepository parentRepository;

	@Autowired
	AdminRepository adminRepository;

	@Autowired
	AddressRepository addressRepository;

	@Autowired
	GradeRepository gradeRepository;

	@Autowired
	SubjectRepository subjectRepository;

	public UserEntity addUser(UserDTOforFRONT newUser) {

		try {

			UserEntity user = new UserEntity();
			RoleEntity role = roleRepository.findByName(newUser.getUloga());

			user.setUsername(newUser.getEmail());
			user.setPassword(Encryption.getPassEncoded(newUser.getSifra()));
			user.setRole(role);

			userRepository.save(user);

			return user;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public UserEntity changePassword(Long id, String novaSifra) {

		try {

			UserEntity user = userRepository.findById(id).get();

			user.setPassword(Encryption.getPassEncoded(novaSifra));

			userRepository.save(user);

			return user;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public UserEntity changeUsername(Long id, String email) {

		try {

			UserEntity user = userRepository.findById(id).get();

			user.setUsername(email);

			userRepository.save(user);

			return user;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public UserEntity changeRole(Long id, String newRole) {
		try {

			UserEntity user = userRepository.findById(id).get();
			RoleEntity role = roleRepository.findByName(newRole);

			user.setRole(role);

			userRepository.save(user);

			return user;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public UserEntity deleteUser(Long id) {

		try {

			UserEntity deletedUser = userRepository.findById(id).get();
			String role = deletedUser.getRole().getName();

			if (role.equals("ADMIN")) {

				AdminEntity admin = deletedUser.getAdmin();

				if (admin != null) {

					AddressEntity address = admin.getAddress();

					if (address != null) {

						int count = 0;

						// proveravam da li jos neki user ima adresu istu kao user kojeg zelim da
						// obrisem
						for (TeacherEntity t : teacherRepository.findAll()) {

							if (address.equals(t.getAddress())) {

								count++;
							}
						}

						for (AdminEntity a : adminRepository.findAll()) {

							if (address.equals(a.getAddress())) {

								count++;
							}
						}

						for (StudentEntity s : studentRepository.findAll()) {

							if (address.equals(s.getAddress())) {

								count++;
							}
						}

						for (ParentEntity p : parentRepository.findAll()) {

							if (address.equals(p.getAddress())) {

								count++;
							}
						}

						// ako ima samo taj user, onda brisem adresu
						if (count == 1) {

							admin.setAddress(null);
							addressRepository.delete(address);

						}

					}

					adminRepository.delete(admin);
				}
			}

			if (role.equals("TEACHER")) {

				TeacherEntity teacher = deletedUser.getTeacher();

				if (teacher != null) {

					AddressEntity address = teacher.getAddress();

					if (address != null) {

						int count = 0;

						// proveravam da li jos neki user ima adresu istu kao user kojeg zelim da
						// obrisem
						for (TeacherEntity t : teacherRepository.findAll()) {

							if (address.equals(t.getAddress())) {

								count++;
							}
						}

						for (AdminEntity a : adminRepository.findAll()) {

							if (address.equals(a.getAddress())) {

								count++;
							}
						}

						for (StudentEntity s : studentRepository.findAll()) {

							if (address.equals(s.getAddress())) {

								count++;
							}
						}

						for (ParentEntity p : parentRepository.findAll()) {

							if (address.equals(p.getAddress())) {

								count++;
							}
						}

						// ako ima samo taj user, onda brisem adresu
						if (count == 1) {

							teacher.setAddress(null);
							addressRepository.delete(address);

						}

					}

					for (SubjectEntity s : teacher.getSubjects()) {

						s.setTeacher(null);
						subjectRepository.save(s);
					}

					for (GradeEntity g : teacher.getGrades()) {

						g.setTeacher(null);
						gradeRepository.save(g);
					}

					teacherRepository.delete(teacher);
				}
			}

			if (role.equals("PARENT")) {

				ParentEntity parent = deletedUser.getParent();

				if (parent != null) {

					AddressEntity address = parent.getAddress();
					if (address != null) {

						int count = 0;

						// proveravam da li jos neki user ima adresu istu kao user kojeg zelim da
						// obrisem
						for (TeacherEntity t : teacherRepository.findAll()) {

							if (address.equals(t.getAddress())) {
								count++;

							}
						}

						for (AdminEntity a : adminRepository.findAll()) {

							if (address.equals(a.getAddress())) {
								count++;
							}

						}

						for (StudentEntity s : studentRepository.findAll()) {

							if (address.equals(s.getAddress())) {
								count++;

							}
						}

						for (ParentEntity p : parentRepository.findAll()) {

							if (address.equals(p.getAddress())) {
								count++;

							}
						}

						// ako ima samo taj user, onda brisem adresu
						if (count == 1) {
							parent.setAddress(null);
							addressRepository.delete(address);
						}
					}

					for (StudentEntity s : parent.getStudents()) {

						s.getParents().remove(parent);
						studentRepository.save(s);
					}

					parentRepository.delete(parent);
				}
			}

			if (role.equals("STUDENT")) {

				StudentEntity student = deletedUser.getStudent();

				if (student != null) {
					AddressEntity address = student.getAddress();

					if (address != null) {
						int count = 0;

						// proveravam da li jos neki user ima adresu istu kao user kojeg zelim da
						// obrisem
						for (TeacherEntity t : teacherRepository.findAll()) {
							if (address.equals(t.getAddress())) {
								count++;
							}
						}

						for (AdminEntity a : adminRepository.findAll()) {
							if (address.equals(a.getAddress())) {
								count++;
							}
						}

						for (StudentEntity s : studentRepository.findAll()) {
							if (address.equals(s.getAddress())) {
								count++;
							}
						}

						for (ParentEntity p : parentRepository.findAll()) {
							if (address.equals(p.getAddress())) {
								count++;
							}
						}

						// ako ima samo taj user, onda brisem adresu
						if (count == 1) {
							student.setAddress(null);
							addressRepository.delete(address);
						}

					}
					for (SubjectEntity s : student.getSubjects()) {

						s.getStudents().remove(student);
						subjectRepository.save(s);
					}

					gradeRepository.deleteAll(student.getGrades());

					for (ParentEntity p : student.getParents()) {

						if (p.getStudents().size() > 1) {

							p.getStudents().remove(student);
							parentRepository.save(p);
						}

						else {
							parentRepository.deleteAll(student.getParents());

						}
					}

					studentRepository.delete(student);
				}
			}

			userRepository.delete(deletedUser);

			return deletedUser;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public UserEntity changeUser(Long id, UserDTOforFRONT newUser) {
		try {

			UserEntity user = userRepository.findById(id).get();
			RoleEntity role = roleRepository.findByName(newUser.getUloga());

			user.setUsername(newUser.getEmail());
			user.setPassword(Encryption.getPassEncoded(newUser.getSifra()));
			user.setRole(role);

			userRepository.save(user);

			return user;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
