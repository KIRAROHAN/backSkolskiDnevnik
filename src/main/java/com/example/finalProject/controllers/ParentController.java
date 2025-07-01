package com.example.finalProject.controllers;

import java.util.HashMap;
import java.util.Map;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

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

import com.example.finalProject.dtos.ParentDTO;
import com.example.finalProject.entities.AdminEntity;
import com.example.finalProject.entities.ParentEntity;
import com.example.finalProject.entities.TeacherEntity;
import com.example.finalProject.entities.UserEntity;
import com.example.finalProject.repositories.AddressRepository;
import com.example.finalProject.repositories.ParentRepository;
import com.example.finalProject.repositories.UserRepository;
import com.example.finalProject.services.ParentService;
import com.example.finalProject.util.ParentCustomValidator;
import com.example.finalProject.util.RESTerror;
import com.example.finalProject.util.RequestParameterCustomValidator;

@Validated
@RestController
@RequestMapping(path = "api/v1/parents")
@CrossOrigin(origins = "http://localhost:3000")
public class ParentController {

	@Autowired
	private ParentRepository parentRepository;

	@Autowired
	private AddressRepository addressRepository;
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ParentCustomValidator parentValidator;

	@Autowired
	private ParentService parentService;

	@InitBinder
	protected void initBinder(final WebDataBinder binder) {
		binder.addValidators(parentValidator);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
		return new ResponseEntity<>("Nije validno zbog validacione greske: " + e.getMessage(), HttpStatus.BAD_REQUEST);
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
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<?> getAllParents() {

		return new ResponseEntity<Iterable<ParentEntity>>(parentRepository.findAll(), HttpStatus.OK);
	}

	@Secured("ADMIN")
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> addParent(@Valid @RequestBody ParentDTO newParent) {

		ParentEntity parent = parentService.addParent(newParent);
		if (!(parent == null)) {

			return new ResponseEntity<>(parent, HttpStatus.CREATED);
		}

		return new ResponseEntity<>("Roditelj nije kreiran", HttpStatus.INTERNAL_SERVER_ERROR);

	}

	@Secured("ADMIN")
	@RequestMapping(method = RequestMethod.GET, path = "/{idRoditelja}")
	public ResponseEntity<?> getParentByID(@PathVariable(name = "idRoditelja") Long id) {
		try {

			if (id.compareTo((long) 0) == -1) {
				return new ResponseEntity<RESTerror>(new RESTerror(1, "ID mora biti pozitivan broj."),
						HttpStatus.BAD_REQUEST);
			}

			if (parentRepository.existsById(id)) {
				return new ResponseEntity<ParentEntity>(parentRepository.findById(id).get(), HttpStatus.OK);
			}

			return new ResponseEntity<RESTerror>(new RESTerror(2, "Roditelj sa datim ID-em nije pronadjen."),
					HttpStatus.NOT_FOUND);

		} catch (

		Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Secured("ADMIN")
	@RequestMapping(method = RequestMethod.PUT, path = "/promeniBrojTelefona/{id}")
	public ResponseEntity<?> changePhoneNumber(@PathVariable Long id,
			@RequestParam(name = "brojTelefona") String phone) {

		if (id.compareTo((long) 0) == -1) {
			return new ResponseEntity<RESTerror>(new RESTerror(1, "ID mora biti pozitivan broj."),
					HttpStatus.BAD_REQUEST);
		}

		if (!parentRepository.existsById(id)) {

			return new ResponseEntity<RESTerror>(new RESTerror(2, "Roditelj sa datim ID-em nije pronadjen."),
					HttpStatus.NOT_FOUND);
		}

		if (!RequestParameterCustomValidator.useRegex(phone, "^0\\d{2}/\\d{4}\\-\\d{3}$")) {
			return new ResponseEntity<>("Uneti broj nije validan.", HttpStatus.BAD_REQUEST);

		}

		ParentEntity parent = parentService.changePhone(id, phone);
		if (parent != null) {

			return new ResponseEntity<>(parent, HttpStatus.OK);

		}

		return new ResponseEntity<>("Broj telefona nije promenjen.", HttpStatus.INTERNAL_SERVER_ERROR);

	}

	@Secured("ADMIN")
	@RequestMapping(method = RequestMethod.PUT, path = "/promeniNotifikacioniMail/{id}")
	public ResponseEntity<?> changeNotificationMail(@PathVariable Long id,
			@RequestParam(name = "notifikacioniMail") @Pattern(regexp = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
					+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", message = "Data email adresa nije validna. Primer validnog email-a ime@mail.com") String email) {

		if (id.compareTo((long) 0) == -1) {
			return new ResponseEntity<RESTerror>(new RESTerror(1, "ID mora biti pozitivan broj."),
					HttpStatus.BAD_REQUEST);
		}

		if (!parentRepository.existsById(id)) {

			return new ResponseEntity<RESTerror>(new RESTerror(2, "Roditelj sa datim ID-em nije pronadjen."),
					HttpStatus.NOT_FOUND);
		}

		ParentEntity parent = parentService.changeNotificationMail(id, email);
		if (parent != null) {

			return new ResponseEntity<>(parent, HttpStatus.OK);

		}

		return new ResponseEntity<>("Email nije promenjen.", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Secured("ADMIN")
	@RequestMapping(method = RequestMethod.PUT, path = "/promeniPrezime/{id}")
	public ResponseEntity<?> changeSurname(@PathVariable Long id,
			@RequestParam(name = "prezime") @NotNull(message = "Prezime mora biti dato.") @Size(min = 2, max = 30, message = "Prezime mora biti izmedju {min} i {max} karaktera dugo.") String prezime) {

		if (id.compareTo((long) 0) == -1) {
			return new ResponseEntity<RESTerror>(new RESTerror(1, "ID mora biti pozitivan broj."),
					HttpStatus.BAD_REQUEST);
		}

		if (!parentRepository.existsById(id)) {

			return new ResponseEntity<RESTerror>(new RESTerror(2, "Roditelj sa datim ID-em nije pronadjen."),
					HttpStatus.NOT_FOUND);
		}

		ParentEntity parent = parentService.changeSurname(id, prezime);
		if (parent != null) {

			return new ResponseEntity<>(parent, HttpStatus.OK);

		}
		return new ResponseEntity<>("Prezime nije promenjeno.", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Secured("ADMIN")
	@RequestMapping(method = RequestMethod.PUT, path = "/promeniIme/{id}")
	public ResponseEntity<?> changeName(@PathVariable Long id,
			@RequestParam(name = "ime") @NotNull(message = "Ime mora biti dato.") @Size(min = 2, max = 30, message = "Ime mora biti izmedju {min} i {max} karaktera dugo.") String ime) {

		if (id.compareTo((long) 0) == -1) {
			return new ResponseEntity<RESTerror>(new RESTerror(1, "ID mora biti pozitivan broj."),
					HttpStatus.BAD_REQUEST);
		}

		if (!parentRepository.existsById(id)) {

			return new ResponseEntity<RESTerror>(new RESTerror(2, "Roditelj sa datim ID-em nije pronadjen."),
					HttpStatus.NOT_FOUND);
		}

		ParentEntity parent = parentService.changeName(id, ime);
		if (parent != null) {

			return new ResponseEntity<>(parent, HttpStatus.OK);

		}

		return new ResponseEntity<>("Ime nije promenjeno.", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Secured("ADMIN")
	@RequestMapping(path = "/{id}/address", method = RequestMethod.PUT)
	public ResponseEntity<?> addAddressToUser(@PathVariable Long id, @RequestParam(name = "idAdrese") Long addressId) {

		if (id.compareTo((long) 0) == -1 && addressId.compareTo((long) 0) == -1) {
			return new ResponseEntity<RESTerror>(new RESTerror(1, "ID mora biti pozitivan broj."),
					HttpStatus.BAD_REQUEST);
		}

		if (!parentRepository.existsById(id)) {

			return new ResponseEntity<RESTerror>(new RESTerror(2, "Roditelj sa datim ID-em nije pronadjen."),
					HttpStatus.NOT_FOUND);
		}

		if (!addressRepository.existsById(id)) {

			return new ResponseEntity<RESTerror>(new RESTerror(2, "Adresa sa datim ID-em nije pronadjena."),
					HttpStatus.NOT_FOUND);
		}

		ParentEntity parent = parentService.addAddressToUser(id, addressId);

		if (!(parent == null)) {

			return new ResponseEntity<ParentEntity>(parent, HttpStatus.OK);

		}

		return new ResponseEntity<>("Adresa nije dodata.", HttpStatus.INTERNAL_SERVER_ERROR);

	}

	
	//za front
		@Secured("ADMIN")
		@RequestMapping(method = RequestMethod.GET, path = "/user/{id}")
		public ResponseEntity<?> getParentByUserID(@PathVariable(name = "id") @Positive(message = "Id mora da bude pozitivan broj.") Long id) {
			try {

				if (id.compareTo((long) 0) == -1) {
					return new ResponseEntity<RESTerror>(new RESTerror(1, "ID mora biti pozitivan broj."),
							HttpStatus.BAD_REQUEST);
				}

				if (userRepository.existsById(id)) {
					UserEntity user = userRepository.findById(id).get();
					return new ResponseEntity<ParentEntity>(parentRepository.findByUser(user), HttpStatus.OK);
				}

				return new ResponseEntity<RESTerror>(new RESTerror(2, "Roditelj nije pronadjen."),
						HttpStatus.NOT_FOUND);

			} catch (

			Exception e) {
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

}
