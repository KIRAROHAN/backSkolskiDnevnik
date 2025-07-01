package com.example.finalProject.services;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.finalProject.dtos.AddressDTO;
import com.example.finalProject.entities.AddressEntity;
import com.example.finalProject.repositories.AddressRepository;

@Service
public class AddressService {

	@Autowired
	AddressRepository addressRepository;

	public AddressEntity addAddress(AddressDTO newAddress) {

		try {

			AddressEntity address = new AddressEntity();
			address.setStreet(newAddress.getUlica());
			address.setCity(newAddress.getGrad());
			address.setCountry(newAddress.getDrzava());

			addressRepository.save(address);

			return address;

		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}


	//ima smisla za adrese koje nisu povezane sa korisnicima, greskom unete ili zaostale
	public AddressEntity deleteAddress(Long id) {
		try {
			
			AddressEntity deletedAddress = addressRepository.findById(id).get();
			
			addressRepository.delete(deletedAddress);
			
			return deletedAddress;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	
	public AddressEntity deleteAddressSoftly(Long id) {
		try {
			
			AddressEntity deletedAddress = addressRepository.findById(id).get();
			
			addressRepository.delete(deletedAddress);
			
			//deletedAddress.setDeleted(true);
			
			return deletedAddress;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public AddressEntity changeStreet(Long id, String street) {
	
		try {
			
			AddressEntity address = addressRepository.findById(id).get();
			
			address.setStreet(street);
			
			addressRepository.save(address);
			
			return address;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
