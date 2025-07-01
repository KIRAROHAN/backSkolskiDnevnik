package com.example.finalProject.dtos;

import javax.validation.constraints.NotNull;

public class AddressDTO {
	
	@NotNull(message = "ulica mora biti navedena.")
	private String ulica;
	
	@NotNull(message = "grad mora biti naveden.")
	private String grad;
	
	@NotNull(message = "drzava mora biti navedena.")
	private String drzava;

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

	public AddressDTO() {
		super();
	}
	
	
	

}
