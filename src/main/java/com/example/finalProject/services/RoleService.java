package com.example.finalProject.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.finalProject.entities.RoleEntity;
import com.example.finalProject.repositories.RoleRepository;

@Service
public class RoleService {

	@Autowired
	RoleRepository roleRepository;

	public RoleEntity addARole(RoleEntity newRole) {

		try {
			
			roleRepository.save(newRole);
			return newRole;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
