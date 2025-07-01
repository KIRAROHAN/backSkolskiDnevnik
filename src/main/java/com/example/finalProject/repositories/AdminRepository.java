package com.example.finalProject.repositories;

import org.springframework.data.repository.CrudRepository;


import com.example.finalProject.entities.AdminEntity;
import com.example.finalProject.entities.UserEntity;

public interface AdminRepository extends CrudRepository<AdminEntity, Long> {

	boolean existsByUser(UserEntity userEntity);

	AdminEntity findByUser(UserEntity userEntity);

}
