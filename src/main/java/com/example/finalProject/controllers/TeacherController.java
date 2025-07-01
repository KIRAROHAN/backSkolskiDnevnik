package com.example.finalProject.controllers;

import java.util.HashMap;
import java.util.Map;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
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

import com.example.finalProject.dtos.TeacherDTO;
import com.example.finalProject.entities.StudentEntity;
import com.example.finalProject.entities.TeacherEntity;
import com.example.finalProject.entities.UserEntity;
import com.example.finalProject.repositories.AddressRepository;
import com.example.finalProject.repositories.TeacherRepository;
import com.example.finalProject.repositories.UserRepository;
import com.example.finalProject.services.TeacherService;
import com.example.finalProject.util.RESTerror;
import com.example.finalProject.util.RequestParameterCustomValidator;
import com.example.finalProject.util.TeacherCustomValidator;

@Validated
@RestController
@RequestMapping(path = "api/v1/teachers")
@CrossOrigin(origins = "http://localhost:3000")
public class TeacherController {

	@Autowired
	private TeacherRepository teacherRepository;

	@Autowired
	private AddressRepository addressRepository;
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TeacherCustomValidator teacherValidator;

	@Autowired
	private TeacherService teacherService;

	@InitBinder
	protected void initBinder(final WebDataBinder binder) {
		binder.addValidators(teacherValidator);
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
	public ResponseEntity<?> getAllTeachers() {

		return new ResponseEntity<Iterable<TeacherEntity>>(teacherRepository.findAll(), HttpStatus.OK);
	}

	@Secured("ADMIN")
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> addTeacher(@Valid @RequestBody TeacherDTO newTeacher) {

		TeacherEntity teacher = teacherService.addTeacher(newTeacher);
		if (!(teacher == null)) {

			return new ResponseEntity<>(teacher, HttpStatus.CREATED);
		}

		return new ResponseEntity<>("Nastavnik nije kreiran", HttpStatus.INTERNAL_SERVER_ERROR);

	}

	@Secured("ADMIN")
	@RequestMapping(method = RequestMethod.GET, path = "/{idNastavnika}")
	public ResponseEntity<?> getTeacherByID(@PathVariable(name = "idNastavnika") Long id) {
		try {

			if (id.compareTo((long) 0) == -1) {
				return new ResponseEntity<RESTerror>(new RESTerror(1, "ID mora biti pozitivan broj."),
						HttpStatus.BAD_REQUEST);
			}

			if (teacherRepository.existsById(id)) {
				return new ResponseEntity<TeacherEntity>(teacherRepository.findById(id).get(), HttpStatus.OK);
			}

			return new ResponseEntity<RESTerror>(new RESTerror(2, "Nastavnik sa datim ID-em nije pronadjen."),
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

		if (!teacherRepository.existsById(id)) {

			return new ResponseEntity<RESTerror>(new RESTerror(2, "Nastavnik sa datim ID-em nije pronadjen."),
					HttpStatus.NOT_FOUND);
		}

		if (!RequestParameterCustomValidator.useRegex(phone, "^0\\d{2}/\\d{4}\\-\\d{3}$")) {
			return new ResponseEntity<>("Uneti broj nije validan.", HttpStatus.BAD_REQUEST);

		}

		TeacherEntity teacher = teacherService.changePhone(id, phone);

		if (teacher != null) {

			return new ResponseEntity<>(teacher, HttpStatus.OK);

		}

		return new ResponseEntity<>("Broj telefona nije promenjen.", HttpStatus.INTERNAL_SERVER_ERROR);

	}

	@Secured("ADMIN")
	@RequestMapping(method = RequestMethod.PUT, path = "/promeniNedeljniFondCasova/{id}")
	public ResponseEntity<?> changeWeeklyFond(@PathVariable Long id,
			@RequestParam(name = "nedeljniFond") @Min(value = 0, message = "Nedeljni fond casova ne moze biti manji od 0.") @Max(value = 48, message = "Nedeljni fond casova ne moze biti veci od 48.") Integer fond) {

		if (id.compareTo((long) 0) == -1) {
			return new ResponseEntity<RESTerror>(new RESTerror(1, "ID mora biti pozitivan broj."),
					HttpStatus.BAD_REQUEST);
		}

		if (!teacherRepository.existsById(id)) {

			return new ResponseEntity<RESTerror>(new RESTerror(2, "Nastavnik sa datim ID-em nije pronadjen."),
					HttpStatus.NOT_FOUND);
		}

		TeacherEntity teacher = teacherService.changeWeeklyFond(id, fond);

		if (teacher != null) {

			return new ResponseEntity<>(teacher, HttpStatus.OK);

		}

		return new ResponseEntity<>("Nedeljni fond nije promenjen.", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Secured("ADMIN")
	@RequestMapping(method = RequestMethod.PUT, path = "/promeniPrezime/{id}")
	public ResponseEntity<?> changeSurname(@PathVariable Long id,
			@RequestParam(name = "prezime") @NotNull(message = "Prezime mora biti dato.") @Size(min = 2, max = 30, message = "Prezime mora biti izmedju {min} i {max} karaktera dugo.") String prezime) {

		if (id.compareTo((long) 0) == -1) {
			return new ResponseEntity<RESTerror>(new RESTerror(1, "ID mora biti pozitivan broj."),
					HttpStatus.BAD_REQUEST);
		}

		if (!teacherRepository.existsById(id)) {

			return new ResponseEntity<RESTerror>(new RESTerror(2, "Nastavnik sa datim ID-em nije pronadjen."),
					HttpStatus.NOT_FOUND);
		}

		TeacherEntity teacher = teacherService.changeSurname(id, prezime);

		if (teacher != null) {

			return new ResponseEntity<>(teacher, HttpStatus.OK);

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

		if (!teacherRepository.existsById(id)) {

			return new ResponseEntity<RESTerror>(new RESTerror(2, "Nastavnik sa datim ID-em nije pronadjen."),
					HttpStatus.NOT_FOUND);
		}

		TeacherEntity teacher = teacherService.changeName(id, ime);

		if (teacher != null) {

			return new ResponseEntity<>(teacher, HttpStatus.OK);

		} 

		return new ResponseEntity<>("Ime nije promenjeno.", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@RequestMapping(path = "/{id}/address", method = RequestMethod.PUT)
	public ResponseEntity<?> addAddressToUser(@PathVariable Long id, @RequestParam(name = "idAdrese") Long addressId) {

		if (id.compareTo((long) 0) == -1 && addressId.compareTo((long) 0) == -1) {
			return new ResponseEntity<RESTerror>(new RESTerror(1, "ID mora biti pozitivan broj."),
					HttpStatus.BAD_REQUEST);
		}

		if (!teacherRepository.existsById(id)) {

			return new ResponseEntity<RESTerror>(new RESTerror(2, "Nastavnik sa datim ID-em nije pronadjen."),
					HttpStatus.NOT_FOUND);
		}

		if (!addressRepository.existsById(id)) {

			return new ResponseEntity<RESTerror>(new RESTerror(2, "Adresa sa datim ID-em nije pronadjena."),
					HttpStatus.NOT_FOUND);
		}

		TeacherEntity teacher = teacherService.addAddressToUser(id, addressId);

		if (!(teacher == null)) {

			return new ResponseEntity<TeacherEntity>(teacher, HttpStatus.OK);

		}

		return new ResponseEntity<>("Adresa nije dodata.", HttpStatus.INTERNAL_SERVER_ERROR);

	}

	@Secured("ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, path = "/softDelete/{id}")
	public ResponseEntity<?> deleteSoftlyUserByID(@PathVariable(name = "id") Long id) {
		try {

			if (id.compareTo((long) 0) == -1) {
				return new ResponseEntity<RESTerror>(new RESTerror(1, "ID mora biti pozitivan broj."),
						HttpStatus.BAD_REQUEST);
			}

			if (teacherRepository.existsById(id)) {

				TeacherEntity deletedTeacher = teacherRepository.findById(id).get();

				teacherRepository.delete(deletedTeacher);

				// deletedTeacher.setDeleted(true);

				return new ResponseEntity<>(deletedTeacher, HttpStatus.OK);
			}

			else {

				return new ResponseEntity<RESTerror>(new RESTerror(2, "Nastavnik sa datim ID-em nije pronadjen."),
						HttpStatus.NOT_FOUND);

			}

		} catch (

		Exception e) {
			return new ResponseEntity<>("Nastavnik nije izbrisan.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	//za front
	@Secured("ADMIN")
	@RequestMapping(method = RequestMethod.GET, path = "/user/{id}")
	public ResponseEntity<?> getTeachertudentByUserID(@PathVariable(name = "id") @Positive(message = "Id mora da bude pozitivan broj.") Long id) {
		try {

			if (id.compareTo((long) 0) == -1) {
				return new ResponseEntity<RESTerror>(new RESTerror(1, "ID mora biti pozitivan broj."),
						HttpStatus.BAD_REQUEST);
			}

			if (userRepository.existsById(id)) {
				UserEntity user = userRepository.findById(id).get();
				return new ResponseEntity<TeacherEntity>(teacherRepository.findByUser(user), HttpStatus.OK);
			}

			return new ResponseEntity<RESTerror>(new RESTerror(2, "Nastavnik nije pronadjen."),
					HttpStatus.NOT_FOUND);

		} catch (

		Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


}
