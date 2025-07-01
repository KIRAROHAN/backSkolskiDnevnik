package com.example.finalProject.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.finalProject.dtos.ParentDTO;
import com.example.finalProject.entities.AddressEntity;
import com.example.finalProject.entities.ParentEntity;
import com.example.finalProject.entities.TeacherEntity;
import com.example.finalProject.entities.UserEntity;
import com.example.finalProject.repositories.AddressRepository;
import com.example.finalProject.repositories.ParentRepository;
import com.example.finalProject.repositories.UserRepository;

@Service
public class ParentService {
	@Autowired
	ParentRepository parentRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	AddressRepository addressRepository;

	public ParentEntity addParent(ParentDTO newParent) {


			try {

				ParentEntity parent = new ParentEntity();
				UserEntity user = userRepository.findByUsername(newParent.getEmail());
				
				parent.setName(newParent.getIme());
				parent.setLastName(newParent.getPrezime());
				parent.setEmailForNotifications(newParent.getEmailZaNotifikaciju());
				parent.setPhoneNumber(newParent.getBrojTelefona());
				parent.setUser(user);
				
				for(AddressEntity address : addressRepository.findAll()) {
					
					if(address.getStreet().equals(newParent.getUlica()) && address.getCity().equals(newParent.getGrad())
							&& address.getCountry().equals(newParent.getDrzava())) { 
							//&& !address.isDeleted()) {
						
						//System.out.println("nasao istu adresu");
						parent.setAddress(address);
						parentRepository.save(parent);
						return parent;
					}
					
				}
				
				AddressEntity newAddress = new AddressEntity();
				
				newAddress.setStreet(newParent.getUlica());
				newAddress.setCity(newParent.getGrad());
				newAddress.setCountry(newParent.getDrzava());
				
				addressRepository.save(newAddress);
				parent.setAddress(newAddress);
				
				parentRepository.save(parent);

				return parent;
			}

			catch (Exception e) {
				e.printStackTrace();
				return null;
			}

		
	}

	public ParentEntity changePhone(Long id, String phone) {
		try {

			ParentEntity parent = parentRepository.findById(id).get();

			parent.setPhoneNumber(phone);

			parentRepository.save(parent);

			return parent;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public ParentEntity changeNotificationMail(Long id, String email) {
		try {

			ParentEntity parent = parentRepository.findById(id).get();

			parent.setEmailForNotifications(email);

			parentRepository.save(parent);

			return parent;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public ParentEntity addAddressToUser(Long id, Long addressId) {
		
		try {
		
			    ParentEntity parent = parentRepository.findById(id).get();
				
				AddressEntity address = addressRepository.findById(addressId).get();
				
				parent.setAddress(address);
				
				return parentRepository.save(parent);
		}
		
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}

	public ParentEntity changeSurname(Long id, String prezime) {
		
		try {

			ParentEntity parent = parentRepository.findById(id).get();

			parent.setLastName(prezime);

			parentRepository.save(parent);

			return parent;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public ParentEntity changeName(Long id, String ime) {
		
		try {

			ParentEntity parent = parentRepository.findById(id).get();

			parent.setName(ime);

			parentRepository.save(parent);

			return parent;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
