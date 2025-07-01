package com.example.finalProject.repositories;

import org.springframework.data.repository.CrudRepository;


import com.example.finalProject.entities.TeacherEntity;
import com.example.finalProject.entities.UserEntity;

public interface TeacherRepository extends CrudRepository<TeacherEntity, Long> {

	boolean existsByUser(UserEntity userEntity);

	TeacherEntity findByUser(UserEntity userEntity);

}
