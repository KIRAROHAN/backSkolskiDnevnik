package com.example.finalProject.controllers;

import java.util.HashMap;
import java.util.Map;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
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

import com.example.finalProject.dtos.StudentDTO;
import com.example.finalProject.entities.ParentEntity;
import com.example.finalProject.entities.StudentEntity;
import com.example.finalProject.entities.UserEntity;
import com.example.finalProject.repositories.AddressRepository;
import com.example.finalProject.repositories.ParentRepository;
import com.example.finalProject.repositories.StudentRepository;
import com.example.finalProject.repositories.UserRepository;
import com.example.finalProject.services.StudentService;
import com.example.finalProject.util.RESTerror;
import com.example.finalProject.util.RequestParameterCustomValidator;
import com.example.finalProject.util.StudentCustomValidator;

@Validated
@RestController
@RequestMapping(path = "api/v1/students")
@CrossOrigin(origins = "http://localhost:3000")
public class StudentController {
	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private ParentRepository parentRepository;

	@Autowired
	private AddressRepository addressRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private StudentCustomValidator studentValidator;

	@Autowired
	private StudentService studentService;

	@InitBinder
	protected void initBinder(final WebDataBinder binder) {
		binder.addValidators(studentValidator);
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
	public ResponseEntity<?> getAllStudents() {

		return new ResponseEntity<Iterable<StudentEntity>>(studentRepository.findAll(), HttpStatus.OK);
	}

	@Secured("ADMIN")
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> addStudent(@Valid @RequestBody StudentDTO newStudent) {

		StudentEntity student = studentService.addStudent(newStudent);

		if (!(student == null)) {

			return new ResponseEntity<>(student, HttpStatus.CREATED);
		}

		return new ResponseEntity<>("Student nije kreiran", HttpStatus.INTERNAL_SERVER_ERROR);

	}

	@Secured("ADMIN")
	@RequestMapping(method = RequestMethod.GET, path = "/uceniciPoRazredu/{razred}")
	public ResponseEntity<?> getAllStudentsBySubjectName(
			@PathVariable(name = "razred") @NotBlank(message = "Razred mora biti naveden.") @Pattern(regexp = "^[1-8]{1}.$", message = "Primer razreda: 1.") String schoolClass) {

		return new ResponseEntity<Iterable<StudentEntity>>(studentRepository.findBySchoolClass(schoolClass),
				HttpStatus.OK);
	}

	@Secured("ADMIN")
	@RequestMapping(method = RequestMethod.GET, path = "/{idStudenta}")
	public ResponseEntity<?> getStudentByID(@PathVariable(name = "idStudenta") Long id) {
		try {

			if (id.compareTo((long) 0) == -1) {
				return new ResponseEntity<RESTerror>(new RESTerror(1, "ID mora biti pozitivan broj."),
						HttpStatus.BAD_REQUEST);
			}

			if (studentRepository.existsById(id)) {
				return new ResponseEntity<StudentEntity>(studentRepository.findById(id).get(), HttpStatus.OK);
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

		if (!studentRepository.existsById(id)) {

			return new ResponseEntity<RESTerror>(new RESTerror(2, "Nastavnik sa datim ID-em nije pronadjen."),
					HttpStatus.NOT_FOUND);
		}

		if (!RequestParameterCustomValidator.useRegex(phone, "^0\\d{2}/\\d{4}\\-\\d{3}$")) {
			return new ResponseEntity<>("Uneti broj nije validan.", HttpStatus.BAD_REQUEST);

		}

		StudentEntity student = studentService.changePhone(id, phone);

		if (student != null) {

			return new ResponseEntity<>(student, HttpStatus.OK);

		}

		return new ResponseEntity<>("Broj telefona nije promenjen.", HttpStatus.INTERNAL_SERVER_ERROR);

	}

	// Nisam sigurna da ucenici pre 18- e mogu da menjaju ime i prezime, ali ajde
	// neka bude, u slucaju da je doslo do greske pri kucanju
	@Secured("ADMIN")
	@RequestMapping(method = RequestMethod.PUT, path = "/promeniPrezime/{id}")
	public ResponseEntity<?> changeSurname(@PathVariable Long id,
			@RequestParam(name = "prezime") @NotNull(message = "Prezime mora biti dato.") @Size(min = 2, max = 30, message = "Prezime mora biti izmedju {min} i {max} karaktera dugo.") String prezime) {

		if (id.compareTo((long) 0) == -1) {
			return new ResponseEntity<RESTerror>(new RESTerror(1, "ID mora biti pozitivan broj."),
					HttpStatus.BAD_REQUEST);
		}

		if (!studentRepository.existsById(id)) {

			return new ResponseEntity<RESTerror>(new RESTerror(2, "Student sa datim ID-em nije pronadjen."),
					HttpStatus.NOT_FOUND);
		}

		StudentEntity student = studentService.changeSurname(id, prezime);

		if (student != null) {

			return new ResponseEntity<>(student, HttpStatus.OK);

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

		if (!studentRepository.existsById(id)) {

			return new ResponseEntity<RESTerror>(new RESTerror(2, "Student sa datim ID-em nije pronadjen."),
					HttpStatus.NOT_FOUND);
		}

		StudentEntity student = studentService.changeName(id, ime);

		if (student != null) {

			return new ResponseEntity<>(student, HttpStatus.OK);

		}

		return new ResponseEntity<>("Ime nije promenjeno.", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Secured("ADMIN")
	@RequestMapping(method = RequestMethod.PUT, path = "/promeniRazred/{id}")
	public ResponseEntity<?> changeClass(@PathVariable Long id,
			@RequestParam(name = "razred") @NotBlank(message = "Razred mora biti naveden.") @Pattern(regexp = "^[1-8]{1}.$", message = "Primer razreda: 1.") String schoolClass) {

		if (id.compareTo((long) 0) == -1) {
			return new ResponseEntity<RESTerror>(new RESTerror(1, "ID mora biti pozitivan broj."),
					HttpStatus.BAD_REQUEST);
		}

		if (!studentRepository.existsById(id)) {

			return new ResponseEntity<RESTerror>(new RESTerror(2, "Student sa datim ID-em nije pronadjen."),
					HttpStatus.NOT_FOUND);
		}

		StudentEntity student = studentService.changeSchoolClass(id, schoolClass);

		if (student != null) {

			return new ResponseEntity<>(student, HttpStatus.OK);

		}

		return new ResponseEntity<>("Razred nije promenjen.", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Secured("ADMIN")
	@RequestMapping(path = "/{id}/address", method = RequestMethod.PUT)
	public ResponseEntity<?> addAddressToUser(@PathVariable Long id, @RequestParam(name = "idAdrese") Long addressId) {

		if (id.compareTo((long) 0) == -1 && addressId.compareTo((long) 0) == -1) {
			return new ResponseEntity<RESTerror>(new RESTerror(1, "ID mora biti pozitivan broj."),
					HttpStatus.BAD_REQUEST);
		}

		if (!studentRepository.existsById(id)) {

			return new ResponseEntity<RESTerror>(new RESTerror(2, " Student sa datim ID-em nije pronadjen."),
					HttpStatus.NOT_FOUND);
		}

		if (!addressRepository.existsById(id)) {

			return new ResponseEntity<RESTerror>(new RESTerror(2, "Adresa sa datim ID-em nije pronadjena."),
					HttpStatus.NOT_FOUND);
		}

		StudentEntity student = studentService.addAddressToUser(id, addressId);

		if (!(student == null)) {

			return new ResponseEntity<StudentEntity>(student, HttpStatus.OK);

		}

		return new ResponseEntity<>("Adresa nije dodata.", HttpStatus.INTERNAL_SERVER_ERROR);

	}

	@Secured("ADMIN")
	@RequestMapping(method = RequestMethod.PUT, path = "/dodajRoditelja/{idRoditelja}/uceniku/{idUcenika}")
	public ResponseEntity<?> addParentToStudent(
			@PathVariable(name = "idRoditelja") @PositiveOrZero(message = "Id roditelja ne sme biti negativan broj.") Long idParent,
			@PathVariable(name = "idUcenika") @PositiveOrZero(message = "Id ucenika ne sme biti negativan broj.") Long idStudent) {

		if (!parentRepository.existsById(idParent)) {
			return new ResponseEntity<>("Roditelj nije pronadjen.", HttpStatus.NOT_FOUND);
		}

		if (!studentRepository.existsById(idStudent)) {
			return new ResponseEntity<>("Ucenik nije pronadjen.", HttpStatus.NOT_FOUND);
		}

		if (studentRepository.findById(idStudent).get().getParents()
				.contains(parentRepository.findById(idParent).get())) {

			return new ResponseEntity<>("Roditelj je vec unet.", HttpStatus.BAD_REQUEST);
		}
		if (studentRepository.findById(idStudent).get().getParents().size() > 1) {
			return new ResponseEntity<>("Ucenik ne moze imati vise od 2 roditelja.", HttpStatus.BAD_REQUEST);
		}

		StudentEntity student = studentService.addParentToStudent(idParent, idStudent);

		if (!(student == null)) {

			return new ResponseEntity<>(student, HttpStatus.OK);
		}

		return new ResponseEntity<>("Uceniku nije dodat roditelj.", HttpStatus.INTERNAL_SERVER_ERROR);

	}

	@Secured("ADMIN")
	@RequestMapping(method = RequestMethod.PUT, path = "/izbrisiIzListeRoditelja/{idRoditelja}/ucenika/{idUcenika}")
	public ResponseEntity<?> removeParentFromStudent(
			@PathVariable(name = "idRoditelja") @PositiveOrZero(message = "Id roditelja ne sme biti negativan broj.") Long idParent,
			@PathVariable(name = "idUcenika") @PositiveOrZero(message = "Id ucenika ne sme biti negativan broj.") Long idStudent) {

		if (!parentRepository.existsById(idParent)) {
			return new ResponseEntity<>("Roditelj nije pronadjen.", HttpStatus.NOT_FOUND);
		}

		if (!studentRepository.existsById(idStudent)) {
			return new ResponseEntity<>("Ucenik nije pronadjen.", HttpStatus.NOT_FOUND);
		}

		if (!studentRepository.findById(idStudent).get().getParents()
				.contains(parentRepository.findById(idParent).get())) {
			return new ResponseEntity<>("Roditelj nije pronadjen.", HttpStatus.NOT_FOUND);
		}

		StudentEntity student = studentService.removeParentFromStudent(idParent, idStudent);

		if (!(student == null)) {

			return new ResponseEntity<>(student, HttpStatus.OK);
		}

		return new ResponseEntity<>("Roditelj nije izbrisan.", HttpStatus.INTERNAL_SERVER_ERROR);

	}

	// za front
	@Secured("ADMIN")
	@RequestMapping(method = RequestMethod.GET, path = "/user/{id}")
	public ResponseEntity<?> getStudentByUserID(
			@PathVariable(name = "id") @Positive(message = "Id mora da bude pozitivan broj.") Long id) {
		try {

			if (id.compareTo((long) 0) == -1) {
				return new ResponseEntity<RESTerror>(new RESTerror(1, "ID mora biti pozitivan broj."),
						HttpStatus.BAD_REQUEST);
			}

			if (userRepository.existsById(id)) {
				UserEntity user = userRepository.findById(id).get();
				if (studentRepository.existsByUser(user)) {
					return new ResponseEntity<StudentEntity>(studentRepository.findByUser(user), HttpStatus.OK);
				} else {
					return new ResponseEntity<RESTerror>(new RESTerror(2, "Ucenik nije pronadjen."),
							HttpStatus.NOT_FOUND);
				}
			}

			return new ResponseEntity<RESTerror>(new RESTerror(2, "Korisnik nije pronadjen."), HttpStatus.NOT_FOUND);

		} catch (

		Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
