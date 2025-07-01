package com.example.finalProject.controllers;

import java.util.HashMap;
import javax.validation.constraints.PositiveOrZero;
import java.util.Map;
import java.util.Optional;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
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

import com.example.finalProject.dtos.UserDTO;
import com.example.finalProject.dtos.UserDTOforFRONT;
import com.example.finalProject.entities.RoleEntity;
import com.example.finalProject.entities.UserEntity;
import com.example.finalProject.repositories.UserRepository;
import com.example.finalProject.services.UserService;
import com.example.finalProject.util.Encryption;
import com.example.finalProject.util.RESTerror;
import com.example.finalProject.util.RequestParameterCustomValidator;
//import com.example.finalProject.util.UserCustomValidator;
import com.example.finalProject.util.UserFrontCustomValidator;

@Validated
@RestController
@RequestMapping(path = "api/v1/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	//@Autowired
	//private UserCustomValidator userValidator;
	
	@Autowired
	private UserFrontCustomValidator userFrontValidator;

	@InitBinder
	protected void initBinder(final WebDataBinder binder) {
		//binder.addValidators(userValidator);
		binder.addValidators(userFrontValidator);
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
	public ResponseEntity<?> getAllUsers() {

		return new ResponseEntity<Iterable<UserEntity>>(userRepository.findAll(), HttpStatus.OK);
	}

	
	@Secured("ADMIN")
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> addUser(@Valid @RequestBody UserDTOforFRONT newUser) {

		UserEntity user = userService.addUser(newUser);

		if (!(user == null)) {

			return new ResponseEntity<>(user, HttpStatus.CREATED);

		}

		return new ResponseEntity<>("Korisnik nije kreiran", HttpStatus.INTERNAL_SERVER_ERROR);

	}
	
	// ZA FRONT PUT
	
	@Secured("ADMIN")
	@RequestMapping(method = RequestMethod.PUT, path = "/update/{id}")
	public ResponseEntity<?> changeUser(@PathVariable @PositiveOrZero(message = "Id ne sme biti negativan broj.") Long id, @Valid @RequestBody UserDTOforFRONT newUser) {

		if (!userRepository.existsById(id)) {
			return new ResponseEntity<>("Korisnik nije pronadjen.", HttpStatus.NOT_FOUND);
		}
		UserEntity user = userService.changeUser(id, newUser);

		if (!(user == null)) {

			return new ResponseEntity<>(user, HttpStatus.OK);

		}

		return new ResponseEntity<>("Korisnik nije kreiran", HttpStatus.INTERNAL_SERVER_ERROR);

	}

	@Secured("ADMIN")
	@RequestMapping(method = RequestMethod.PUT, path = "/promeniSifru/{id}")
	public ResponseEntity<?> changePassword(@PathVariable Long id,
			@RequestParam(name = "sifra") @NotBlank(message = "Sifra ne sme bit prazan string.") String novaSifra) {

		if (id.compareTo((long) 0) == -1) {
			return new ResponseEntity<RESTerror>(new RESTerror(1, "ID mora biti pozitivan broj."),
					HttpStatus.BAD_REQUEST);
		}

		if (!userRepository.existsById(id)) {

			return new ResponseEntity<RESTerror>(new RESTerror(2, "Korisnik sa datim ID-em nije pronadjen."),
					HttpStatus.NOT_FOUND);
		}

		if (!RequestParameterCustomValidator.useRegex(novaSifra,
				"^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,}$")) {
			return new ResponseEntity<>("Šifra mora da sadrži makar jednu cifru [0-9].\r\n"
					+ "    Šifra mora da sadrži makar jedno latinično malo slovo [a-z].\r\n"
					+ "    Šifra mora da sadrži makar jedno latinično veliko slovo [A-Z].\r\n"
					+ "    Šifra mora da sadrži makar jedan karakter kao ! @ # & ( ).\r\n"
					+ "    Šifra mora biti dužine makar 8 karaktera.\r\n", HttpStatus.BAD_REQUEST);

		}

		UserEntity user = userService.changePassword(id, novaSifra);

		if (user != null) {

			return new ResponseEntity<UserEntity>(user, HttpStatus.OK);

		}

		return new ResponseEntity<>("Sifra nije promenjena.", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Secured("ADMIN")
	@RequestMapping(method = RequestMethod.PUT, path = "/promeniEmail/{id}")
	public ResponseEntity<?> changeUsername(@PathVariable Long id, @RequestParam String email) {

		if (id.compareTo((long) 0) == -1) {
			return new ResponseEntity<RESTerror>(new RESTerror(1, "ID mora biti pozitivan broj."),
					HttpStatus.BAD_REQUEST);
		}

		if (!userRepository.existsById(id)) {

			return new ResponseEntity<RESTerror>(new RESTerror(2, "Korisnik sa datim ID-em nije pronadjen."),
					HttpStatus.NOT_FOUND);
		}

		if (!RequestParameterCustomValidator.useRegex(email,
				"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")) {
			return new ResponseEntity<>("Data email adresa nije validna. Primer validnog email-a ime@mail.com",
					HttpStatus.BAD_REQUEST);

		}

		UserEntity user = userService.changeUsername(id, email);

		if (user != null) {

			return new ResponseEntity<UserEntity>(user, HttpStatus.OK);

		}

		return new ResponseEntity<>("Email adresa nije promenjena.", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Secured("ADMIN")
	@RequestMapping(method = RequestMethod.PUT, path = "/promeniUlogu/{id}")
	public ResponseEntity<?> changeRole(@PathVariable Long id,
			@RequestParam(name = "uloga") @NotBlank(message = "Uloga ne moze biti prazan string.") String newRole) {

		if (id.compareTo((long) 0) == -1) {
			return new ResponseEntity<RESTerror>(new RESTerror(1, "ID mora biti pozitivan broj."),
					HttpStatus.BAD_REQUEST);
		}

		if (!userRepository.existsById(id)) {

			return new ResponseEntity<RESTerror>(new RESTerror(2, "Korisnik sa datim ID-em nije pronadjen."),
					HttpStatus.NOT_FOUND);
		}

		if (!RequestParameterCustomValidator.useRegex(newRole, "ADMIN||TEACHER||STUDENT||PARENT")) {
			return new ResponseEntity<>("Uneta uloga ne postoji.", HttpStatus.BAD_REQUEST);

		}

		UserEntity user = userService.changeRole(id, newRole);

		if (user != null) {

			return new ResponseEntity<UserEntity>(user, HttpStatus.OK);

		}

		return new ResponseEntity<>("Uloga korisnika nije promenjena.", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Secured("ADMIN")
	@RequestMapping(method = RequestMethod.GET, path = "/{korisnickiId}")
	public ResponseEntity<?> getUserByID(@PathVariable(name = "korisnickiId") Long id) {
		try {

			if (id.compareTo((long) 0) == -1) {
				return new ResponseEntity<RESTerror>(new RESTerror(1, "ID mora biti pozitivan broj."),
						HttpStatus.BAD_REQUEST);
			}

			if (userRepository.existsById(id)) {
						
				return new ResponseEntity<UserEntity>(userRepository.findById(id).get(), HttpStatus.OK);
			}

			return new ResponseEntity<RESTerror>(new RESTerror(2, "Korisnik sa datim ID-em nije pronadjen."),
					HttpStatus.NOT_FOUND);

		} catch (

		Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	// dodala za projekat na front- endu
	@Secured({"ADMIN", "PARENT", "STUDENT", "TEACHER"})
	@RequestMapping(method = RequestMethod.GET, path = "/searchByEmail/{email}")
	public ResponseEntity<?> getUserRoleByUsername(@PathVariable(name = "email") String email) {
		try {
			
			UserEntity user = userRepository.findByUsername(email);
			
			String loggedUser = userRepository
					.findByUsername(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString())
					.getUsername();
			

			if (user == null) {
				

			return new ResponseEntity<RESTerror>(new RESTerror(2, "Korisnik sa datim korisnickim imenom nije pronadjen."),
					HttpStatus.NOT_FOUND);
			
			}
			
			if(user.getUsername() != loggedUser) {
				
				return new ResponseEntity<RESTerror>(new RESTerror(111, "Zabranjen pristup. Nije tvoje korisnicko ime."),
						HttpStatus.FORBIDDEN);
			}
			
			RoleEntity role = user.getRole();
			
			return new ResponseEntity<>(role, HttpStatus.OK);
			
			

		} catch (

		Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	@Secured("ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, path = "/{korisnickiId}")
	public ResponseEntity<?> deleteUserByID(@PathVariable(name = "korisnickiId") Long id) {
		try {

			if (id.compareTo((long) 0) == -1) {
				return new ResponseEntity<RESTerror>(new RESTerror(1, "ID mora biti pozitivan broj."),
						HttpStatus.BAD_REQUEST);
			}

			if (userRepository.existsById(id)) {

				UserEntity deletedUser = userService.deleteUser(id);

				if (!(deletedUser == null)) {

					return new ResponseEntity<>(deletedUser, HttpStatus.OK);
				} else {
					return new ResponseEntity<>("Korisnik nije izbrisan.", HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}

			return new ResponseEntity<RESTerror>(new RESTerror(2, "Korisnik sa datim ID-em nije pronadjen."),
					HttpStatus.NOT_FOUND);

		} catch (

		Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Secured("ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, path = "/softDelete/{korisnickiId}")
	public ResponseEntity<?> deleteSoftlyUserByID(@PathVariable(name = "korisnickiId") Long id) {
		try {

			if (id.compareTo((long) 0) == -1) {
				return new ResponseEntity<RESTerror>(new RESTerror(1, "ID mora biti pozitivan broj."),
						HttpStatus.BAD_REQUEST);
			}

			if (userRepository.existsById(id)) {

				UserEntity deletedUser = userRepository.findById(id).get();

				userRepository.delete(deletedUser);

				// deletedUser.setDeleted(true);

				return new ResponseEntity<>(deletedUser, HttpStatus.OK);
			}

			else {

				return new ResponseEntity<RESTerror>(new RESTerror(2, "Korisnik sa datim ID-em nije pronadjen."),
						HttpStatus.NOT_FOUND);

			}

		} catch (

		Exception e) {
			return new ResponseEntity<>("Korisnik nije izbrisan.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
