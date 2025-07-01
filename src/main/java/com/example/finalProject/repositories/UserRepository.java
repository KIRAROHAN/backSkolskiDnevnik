package com.example.finalProject.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.example.finalProject.entities.UserEntity;


public interface UserRepository extends CrudRepository<UserEntity, Long> {
	
	public UserEntity findByUsername(String username);

	public boolean existsByUsername(String username);

	public Optional<UserEntity> findById(Integer id);

	public Iterable<UserEntity> findByRole(Long roleId);



}
