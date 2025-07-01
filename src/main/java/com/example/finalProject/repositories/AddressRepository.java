package com.example.finalProject.repositories;

import org.springframework.data.repository.CrudRepository;

import com.example.finalProject.entities.AddressEntity;

public interface AddressRepository extends CrudRepository<AddressEntity, Long> {

	boolean existsByStreet(String ulica);

	boolean existsByCity(String grad);

	boolean existsByCountry(String drzava);

	boolean existsByStreetAndCityAndCountry(String ulica, String grad, String drzava);

}
