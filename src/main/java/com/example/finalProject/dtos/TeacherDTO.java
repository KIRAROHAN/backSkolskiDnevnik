package com.example.finalProject.dtos;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

public class TeacherDTO {

	@NotNull(message = "Ime mora biti dato.")
	@Size(min = 2, max = 30, message = "Ime mora biti izmedju {min} i {max} karaktera dugo.")
	private String ime;

	@NotNull(message = "Prezime mora biti dato.")
	@Size(min = 2, max = 30, message = "Prezime mora biti izmedju {min} i {max} karaktera dugo.")
	private String prezime;
	
	@NotNull(message = "Nedeljni fond mora bit naveden.")
	//@PositiveOrZero(message = "Nedeljni fond ne sme biti negativan broj.")
	@Max(value = 48, message = "Nedeljni fond casova ne sme biti veci od 48.")
	@Min(value = 0, message = "Nedeljni fond casova ne sme biti manji od 0.")
	private Integer nedeljniFondCasova;
	
	@Pattern(regexp = "^0\\d{2}/\\d{4}\\-\\d{3}$", message = "Broj telefona nije validan. Šablon za broj telefona : 0xx/xxxx-xxx")
	private String brojTelefona;
	
	@NotNull(message = "Ulica mora biti uneta.")
	private String ulica;
	
	@NotNull(message = "Grad mora biti unet.")
	private String grad;
	
	@NotNull(message = "Drzava mora biti uneta.")
	private String drzava;
	
	// Ovo polje je ovde jer ne zelim da neko unosi vise puta istog nastavnika zato ne stavljam @UniqueConstraint na table, 
	// a zelim da bude dozvoljen isti unos za polja iznad u slucaju da je nastavnik i administrator
    @NotNull(message = "Email mora biti dat.")
	@Pattern(regexp = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", message = "Email nije validan.")
	private String email;

	public String getIme() {
		return ime;
	}

	public void setIme(String ime) {
		this.ime = ime;
	}

	public String getPrezime() {
		return prezime;
	}

	public void setPrezime(String prezime) {
		this.prezime = prezime;
	}

	public Integer getNedeljniFondCasova() {
		return nedeljniFondCasova;
	}

	public void setNedeljniFondCasova(Integer nedeljniFondCasova) {
		this.nedeljniFondCasova = nedeljniFondCasova;
	}

	public String getBrojTelefona() {
		return brojTelefona;
	}

	public void setBrojTelefona(String brojTelefona) {
		this.brojTelefona = brojTelefona;
	}

	public String getUlica() {
		return ulica;
	}

	public void setUlica(String ulica) {
		this.ulica = ulica;
	}

	public String getGrad() {
		return grad;
	}

	public void setGrad(String grad) {
		this.grad = grad;
	}

	public String getDrzava() {
		return drzava;
	}

	public void setDrzava(String drzava) {
		this.drzava = drzava;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public TeacherDTO() {
		super();
	}
    
    
	
	
}
