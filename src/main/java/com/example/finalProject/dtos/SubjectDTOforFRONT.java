package com.example.finalProject.dtos;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

// za front napravljeno
public class SubjectDTOforFRONT {
	
	
	@NotBlank(message="Naziv predmeta mora biti naveden.")
	private String predmet;
		
	@NotBlank(message = "Polugodiste u kojem se odrzava predmet moze biti ili PRVO, ili DRUGO.")
	@Pattern(regexp = "PRVO||DRUGO", message = "Polugodiste moze bite ili PRVO, ili DRUGO.")
	private String polugodiste;
	
	@NotBlank(message = "Razred mora biti naveden.")
	@Pattern(regexp = "^[1-8]{1}[.]$", message =  "Primer razreda: 1.")
	private String razred;
	
	//@Positive(message = "id nastavnika mora biti pozitivan broj")
	private Long idNastavnika;


	public Long getIdNastavnika() {
		return idNastavnika;
	}

	public void setIdNastavnika(Long idNastavnika) {
		this.idNastavnika = idNastavnika;
	}

	public String getPredmet() {
		return predmet;
	}

	public void setPredmet(String predmet) {
		this.predmet = predmet;
	}

	public String getPolugodiste() {
		return polugodiste;
	}

	public void setPolugodiste(String polugodiste) {
		this.polugodiste = polugodiste;
	}

	public String getRazred() {
		return razred;
	}

	public void setRazred(String razred) {
		this.razred = razred;
	}
	

}
