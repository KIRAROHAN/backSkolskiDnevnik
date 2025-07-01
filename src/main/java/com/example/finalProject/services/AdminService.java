package com.example.finalProject.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.finalProject.dtos.AdminDTO;
import com.example.finalProject.dtos.TeacherDTO;
import com.example.finalProject.entities.AddressEntity;
import com.example.finalProject.entities.AdminEntity;
import com.example.finalProject.entities.TeacherEntity;
import com.example.finalProject.entities.UserEntity;
import com.example.finalProject.repositories.AddressRepository;
import com.example.finalProject.repositories.AdminRepository;
import com.example.finalProject.repositories.TeacherRepository;
import com.example.finalProject.repositories.UserRepository;

@Service
public class AdminService {
	@Autowired
	AdminRepository adminRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	AddressRepository addressRepository;

	public AdminEntity addAdmin(AdminDTO newAdmin) {


			try {

				AdminEntity admin = new AdminEntity();
				UserEntity user = userRepository.findByUsername(newAdmin.getEmail());
				
				admin.setName(newAdmin.getIme());
				admin.setLastName(newAdmin.getPrezime());
				admin.setPhoneNumber(newAdmin.getBrojTelefona());
				admin.setUser(user);
				
				for(AddressEntity address : addressRepository.findAll()) {
					
					if(address.getStreet().equals(newAdmin.getUlica()) && address.getCity().equals(newAdmin.getGrad())
							&& address.getCountry().equals(newAdmin.getDrzava())) { 
							//&& !address.isDeleted()) {
						
						//System.out.println("nasao istu adresu");
						admin.setAddress(address);
						adminRepository.save(admin);
						return admin;
					}
					
				}
				
				AddressEntity newAddress = new AddressEntity();
				
				newAddress.setStreet(newAdmin.getUlica());
				newAddress.setCity(newAdmin.getGrad());
				newAddress.setCountry(newAdmin.getDrzava());
				
				addressRepository.save(newAddress);
				admin.setAddress(newAddress);
				
				adminRepository.save(admin);

				return admin;
			}

			catch (Exception e) {
				e.printStackTrace();
				return null;
			}

		
	}

	public boolean changePhone(Long id, String phone) {
		try {

			AdminEntity admin = adminRepository.findById(id).get();

			admin.setPhoneNumber(phone);

			adminRepository.save(admin);

			return true;
		}

		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	

	public AdminEntity addAddressToUser(Long id, Long addressId) {
		
		try {
		
			    AdminEntity admin = adminRepository.findById(id).get();
				
				AddressEntity address = addressRepository.findById(addressId).get();
				
				admin.setAddress(address);
				
				return adminRepository.save(admin);
		}
		
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}

	public boolean changeSurname(Long id, String prezime) {
		
		try {

			AdminEntity admin = adminRepository.findById(id).get();

			admin.setLastName(prezime);

			adminRepository.save(admin);

			return true;
		}

		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean changeName(Long id, String ime) {
		
		try {

			AdminEntity admin = adminRepository.findById(id).get();

			admin.setName(ime);

			adminRepository.save(admin);

			return true;
		}

		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
