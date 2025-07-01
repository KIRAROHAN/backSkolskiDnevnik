package com.example.finalProject.dtos;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
//import javax.validation.constraints.Size;

//import com.example.finalProject.entities.RoleEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserDTO {
	
	private Long id;
    
    @NotNull(message = "Email mora biti dat.")
	@Pattern(regexp = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", message = "Email nije validan.")
    private String email;
    
	@NotNull(message = "Šifra mora biti data.")
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,}$", message = "Šifra nije validna.\r\n"
			+ "    Šifra mora da sadrži makar jednu cifru [0-9].\r\n"
			+ "    Šifra mora da sadrži makar jedno latinično malo slovo [a-z].\r\n"
			+ "    Šifra mora da sadrži makar jedno latinično veliko slovo [A-Z].\r\n"
			+ "    Šifra mora da sadrži makar jedan karakter kao ! @ # & ( ).\r\n"
			+ "    Šifra mora biti dužine makar 8 karaktera.\r\n"
			+ "")
	
	//@JsonIgnore
	@JsonProperty("sifra")
    private String sifra;
	
	@NotNull(message = "Uloga mora biti navedena.")
	@Pattern(regexp = "ADMIN||TEACHER||STUDENT||PARENT", message = "Uloga ne postoji.")
	private String uloga;
    
	public UserDTO() {
		super();
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
		
	
	@JsonIgnore
	public String getSifra() {
		return sifra;
	}
	
	@JsonProperty
	public void setSifra(String sifra) {
		this.sifra = sifra;
	}

	public String getUloga() {
		return uloga;
	}

	public void setUloga(String uloga) {
		this.uloga = uloga;
	}

	





	
	
	
	
	
    
    
    
    
}
