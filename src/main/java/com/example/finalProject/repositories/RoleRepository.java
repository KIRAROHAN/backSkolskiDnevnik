package com.example.finalProject.repositories;

import org.springframework.data.repository.CrudRepository;

import com.example.finalProject.entities.RoleEntity;

public interface RoleRepository extends CrudRepository<RoleEntity, Long> {

	RoleEntity findByName(String uloga);

	boolean existsByName(String name);

}
