package com.example.finalProject.repositories;

import org.springframework.data.repository.CrudRepository;

import com.example.finalProject.entities.ParentEntity;
import com.example.finalProject.entities.StudentEntity;
import com.example.finalProject.entities.UserEntity;

public interface ParentRepository extends CrudRepository<ParentEntity, Long> {

	boolean existsByUser(UserEntity userEntity);
	
	ParentEntity findByUser(UserEntity userEntity);

}
