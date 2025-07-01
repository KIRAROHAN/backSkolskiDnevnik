package com.example.finalProject.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.finalProject.dtos.AddressDTO;
import com.example.finalProject.entities.AddressEntity;
import com.example.finalProject.repositories.AddressRepository;
import com.example.finalProject.services.AddressService;
import com.example.finalProject.util.AddressCustomValidator;

@Validated
@RestController
@RequestMapping(path = "api/v1/addresses")
public class AddressController {

	@Autowired
	private AddressRepository addressRepository;

	@Autowired
	private AddressCustomValidator addresssValidator;

	@Autowired
	private AddressService addressService;

	@InitBinder
	protected void initBinder(final WebDataBinder binder) {
		binder.addValidators(addresssValidator);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
		return new ResponseEntity<>("Nije validno zbog validacione greske : " + e.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Map<String, String> handlerValidationError(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();

		for (ObjectError error : ex.getBindingResult().getAllErrors()) {

			String fieldName = "";
			String errorMessage = error.getDefaultMessage();

			if (error instanceof FieldError) {
				fieldName = ((FieldError) error).getField();
			} else {
				fieldName = "object-level";

			}

			errors.put(fieldName, errorMessage);
		}

		return errors;

	}

	@Secured("ADMIN")
	@RequestMapping
	public ResponseEntity<?> getAll() {
		return new ResponseEntity<Iterable<AddressEntity>>(addressRepository.findAll(), HttpStatus.OK);
	}

	@Secured("ADMIN")
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> addAddress(@Valid @RequestBody AddressDTO newAddress) {

		AddressEntity address = addressService.addAddress(newAddress);
		
		if (!(address == null)) {

			return new ResponseEntity<>(address, HttpStatus.CREATED);
		}

		return new ResponseEntity<>("Adresa nije kreirana", HttpStatus.INTERNAL_SERVER_ERROR);

	}


	@Secured("ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, path = "/{id}")
	public ResponseEntity<?> deleteAddress(@PathVariable(name = "id") @PositiveOrZero(message = "Id ne sme biti negativan broj.") Long id) {

		AddressEntity address = addressService.deleteAddress(id);

		if (!(address == null)) {
			return new ResponseEntity<AddressEntity>(address, HttpStatus.OK);
		}
		
		return new ResponseEntity<>("Adresa nije izbrisana.", HttpStatus.INTERNAL_SERVER_ERROR);
		
	}

	@Secured("ADMIN")
	@RequestMapping(method = RequestMethod.PUT, path = "/promeniUlicu/{id}")
	public ResponseEntity<?> changeStreet(@PathVariable(name = "id") @PositiveOrZero(message = "Id ne sme biti negativan broj.") Long id,
			@RequestParam(name = "ulica") @NotBlank(message = "Ulica mora biti navedena.") String street) {
		
		AddressEntity address = addressService.changeStreet(id, street);
		
		if (!(address == null)) {
			return new ResponseEntity<AddressEntity>(address, HttpStatus.OK);
		}
		
		return new ResponseEntity<>("Ulica nije promenjena.", HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
