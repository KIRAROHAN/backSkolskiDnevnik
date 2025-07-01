package com.example.finalProject.dtos;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;


public class GradeDTOadmin {
	
	//@Pattern(regexp = "[1-5]{1}", message = "Ocena nije validna. Vrednost ocene treba da bude celobrojna vrednost u opsegu od 1 do 5.")
	@NotNull(message = "Celobrojna vrednost ocene mora biti uneta.")
	@Max(value = 5, message = "Vrednost ocene treba da bude celobrojna vrednost u opsegu od 1 do 5.")
	@Min(value = 1, message = "Vrednost ocene treba da bude celobrojna vrednost u opsegu od 1 do 5.")
	private Integer ocena;
	
	//@NotBlank(message = "Opisna ocena mora biti uneta.")
	//@Pattern(regexp = "NEDOVOLJAN||DOVOLJAN||DOBAR||VRLO DOBAR||ODLICAN", message = "Opisna ocena nije validna.")
	//private String opisnaOcena;
	
	@NotNull(message = "Id ucenika mora dat.")
	@Positive(message = "Id ucenika mora biti pozitivan broj.")
	private Long idUcenika;
	
	@NotNull(message = "Id predmeta mora dat.")
	@Positive(message = "Id predmeta mora biti pozitivan broj.")
	private Long idPredmeta;
	
	@NotNull(message = "Id nastavnika mora dat.")
	@Positive(message = "Id nastavnika mora biti pozitivan broj.")
	private Long idNastavnika;
	
	public GradeDTOadmin() {
		super();
		
	}

	public Integer getOcena() {
		return ocena;
	}

	public void setOcena(Integer ocena) {
		this.ocena = ocena;
	}

	public Long getIdUcenika() {
		return idUcenika;
	}

	public void setIdUcenika(Long idUcenika) {
		this.idUcenika = idUcenika;
	}

	public Long getIdPredmeta() {
		return idPredmeta;
	}

	public void setIdPredmeta(Long idPredmeta) {
		this.idPredmeta = idPredmeta;
	}

	public Long getIdNastavnika() {
		return idNastavnika;
	}

	public void setIdNastavnika(Long idNastavnika) {
		this.idNastavnika = idNastavnika;
	}
	
	
	
	

}
