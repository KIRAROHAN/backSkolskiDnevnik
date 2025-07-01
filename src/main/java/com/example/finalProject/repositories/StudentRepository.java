package com.example.finalProject.repositories;

import org.springframework.data.repository.CrudRepository;


import com.example.finalProject.entities.StudentEntity;
import com.example.finalProject.entities.UserEntity;

public interface StudentRepository extends CrudRepository<StudentEntity, Long> {

	Iterable<StudentEntity> findBySchoolClass(String schoolClass);

	boolean existsByUser(UserEntity userEntity);

	StudentEntity findByUser(UserEntity userEntity);

}
