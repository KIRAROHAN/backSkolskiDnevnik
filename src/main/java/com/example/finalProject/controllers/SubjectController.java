package com.example.finalProject.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
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

import com.example.finalProject.dtos.StudentDTOforFront;
import com.example.finalProject.dtos.SubjectDTO;
import com.example.finalProject.dtos.SubjectDTOforFRONT;
import com.example.finalProject.entities.AddressEntity;
import com.example.finalProject.entities.StudentEntity;
import com.example.finalProject.entities.SubjectEntity;
import com.example.finalProject.repositories.StudentRepository;
import com.example.finalProject.repositories.SubjectRepository;
import com.example.finalProject.repositories.TeacherRepository;
import com.example.finalProject.services.SubjectService;
import com.example.finalProject.util.RESTerror;
import com.example.finalProject.util.SubjectCustomValidator;

@Validated
@RestController
@RequestMapping(path = "api/v1/subjects")
@CrossOrigin(origins = "http://localhost:3000")
public class SubjectController {

	@Autowired
	private SubjectRepository subjectRepository;

	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private TeacherRepository teacherRepository;

	@Autowired
	private SubjectService subjectService;

	@Autowired
	private SubjectCustomValidator subjectValidator;

	@InitBinder
	protected void initBinder(final WebDataBinder binder) {
		binder.addValidators(subjectValidator);
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

	@Secured({"ADMIN", "PARENT", "STUDENT", "TEACHER"})
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<?> getAll() {
		return new ResponseEntity<Iterable<SubjectEntity>>(subjectRepository.findAll(), HttpStatus.OK);
	}

	@Secured({"ADMIN", "TEACHER"})
	@RequestMapping(method = RequestMethod.GET, path = "/{id}")
	public ResponseEntity<?> getSubjectById(
			@PathVariable @PositiveOrZero(message = "Id ne sme biti negativan broj.") Long id) {

		try {
			if (!subjectRepository.existsById(id)) {
				return new ResponseEntity<>("Predmet nije pronadjen.", HttpStatus.NOT_FOUND);
			}

			return new ResponseEntity<SubjectEntity>(subjectRepository.findById(id).get(), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Secured("ADMIN")
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> addSubject(@Valid @RequestBody SubjectDTO newSubject) {

		SubjectEntity subject = subjectService.addSubject(newSubject);
		if (!(subject == null)) {

			return new ResponseEntity<>(subject, HttpStatus.CREATED);
		}

		return new ResponseEntity<>("Predmet nije kreiran.", HttpStatus.INTERNAL_SERVER_ERROR);

	}
	
	@Secured("ADMIN")
	@RequestMapping(method = RequestMethod.POST, path="/postForFront")
	public ResponseEntity<?> addSubjectForFRONT(@Valid @RequestBody SubjectDTOforFRONT newSubject) {

		SubjectEntity subject = subjectService.addSubjectForFront(newSubject);
		if (!(subject == null)) {

			return new ResponseEntity<>(subject, HttpStatus.CREATED);
		}

		return new ResponseEntity<>("Predmet nije kreiran.", HttpStatus.INTERNAL_SERVER_ERROR);

	}
	
	
	// za front napravljeno
	@Secured("ADMIN")
	@RequestMapping(method = RequestMethod.PUT, path = "/update/{id}")
	public ResponseEntity<?> updateSubject(@PathVariable @PositiveOrZero(message = "Id ne sme biti negativan broj.") Long id, @Valid @RequestBody SubjectDTOforFRONT updatedSubject) {

		
		if (!subjectRepository.existsById(id)) {
			return new ResponseEntity<>("Predmet nije pronadjen.", HttpStatus.NOT_FOUND);
		}
		
		if(!teacherRepository.existsById(updatedSubject.getIdNastavnika())) {
			
			return new ResponseEntity<>("Nastavnik nije pronadjen.", HttpStatus.NOT_FOUND);
		}
		
		
		SubjectEntity subject = subjectService.updateSubject(id, updatedSubject);
		
		if( subject != null) {
			
			return new ResponseEntity<>(subject, HttpStatus.OK);
		}
		
		

		return new ResponseEntity<>("Predmet nije update-ovan.", HttpStatus.INTERNAL_SERVER_ERROR);

	}


	@Secured("ADMIN")
	@RequestMapping(method = RequestMethod.PUT, path = "/promeniNaziv/{id}")
	public ResponseEntity<?> changeNameOfSubject(
			@PathVariable @PositiveOrZero(message = "Id ne sme biti negativan broj.") Long id,
			@RequestParam(name = "naziv") @NotNull(message = "Naziv predmeta mora biti naveden.") String name) {

		if (!subjectRepository.existsById(id)) {
			return new ResponseEntity<>("Predmet nije pronadjen.", HttpStatus.NOT_FOUND);
		}
		if (subjectService.changeNameOfSubject(id, name)) {

			return new ResponseEntity<>("Naziv predmeta je uspesno promenjen.", HttpStatus.OK);
		}

		return new ResponseEntity<>("Naziv predmeta nije promenjen.", HttpStatus.INTERNAL_SERVER_ERROR);

	}

	@Secured("ADMIN")
	@RequestMapping(method = RequestMethod.PUT, path = "/promeniSemestar/{id}")
	public ResponseEntity<?> changeNameOfSemester(
			@PathVariable @PositiveOrZero(message = "Id ne sme biti negativan broj.") Long id,
			@RequestParam(name = "polugodiste")@NotBlank(message = "Polugodiste u kojem se odrzava predmet moze biti ili PRVO, ili DRUGO.") @Pattern(regexp = "PRVO||DRUGO", message = "Polugodiste u kojem se odrzava predmet moze biti ili PRVO, ili DRUGO.") String name) {

		if (!subjectRepository.existsById(id)) {
			return new ResponseEntity<>("Predmet nije pronadjen.", HttpStatus.NOT_FOUND);
		}

		if (subjectService.changeNameOfSemester(id, name)) {

			return new ResponseEntity<>("Polugodiste uspesno promenjeno.", HttpStatus.OK);
		}

		return new ResponseEntity<>("Polugodiste nije promenjeno.", HttpStatus.INTERNAL_SERVER_ERROR);

	}
	
	@Secured("ADMIN")
	@RequestMapping(method = RequestMethod.PUT, path = "/promeniRazred/{id}")
	public ResponseEntity<?> changeClass(@PathVariable Long id,
			@RequestParam(name = "razred") @NotBlank(message = "Razred mora biti naveden.") @Pattern(regexp = "^[1-8]{1}[.]$", message =  "Primer razreda: 1.") String schoolClass) {

		if (id.compareTo((long) 0) == -1) {
			return new ResponseEntity<RESTerror>(new RESTerror(1, "ID mora biti pozitivan broj."),
					HttpStatus.BAD_REQUEST);
		}

		if (!subjectRepository.existsById(id)) {

			return new ResponseEntity<RESTerror>(new RESTerror(2, "Predmet sa datim ID-em nije pronadjen."),
					HttpStatus.NOT_FOUND);
		}

		if (subjectService.changeSchoolClass(id, schoolClass)) {

			return new ResponseEntity<>("Razred je uspesno promenjen.", HttpStatus.OK);

		}

		return new ResponseEntity<>("Razred nije promenjen.", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	
	//brisanja kada predmet nije referenciran
	@Secured("ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, path = "/{id}")
	public ResponseEntity<?> deleteSubject(
			@PathVariable(name = "id") @PositiveOrZero(message = "Id ne sme biti negativan broj.") Long id) {

		if (!subjectRepository.existsById(id)) {
			return new ResponseEntity<>("Predmet nije pronadjen.", HttpStatus.NOT_FOUND);
		}
		SubjectEntity subject = subjectService.deleteSubject(id);

		if (!(subject == null)) {
			return new ResponseEntity<SubjectEntity>(subject, HttpStatus.OK);
		}

		return new ResponseEntity<>("Predmet nije izbrisan.", HttpStatus.INTERNAL_SERVER_ERROR);

	}
	
	@Secured("ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, path = "/subject/{id}")
	public ResponseEntity<?> deleteSubjectWithTrick(
			@PathVariable(name = "id") @PositiveOrZero(message = "Id ne sme biti negativan broj.") Long id) {

		if (!subjectRepository.existsById(id)) {
			return new ResponseEntity<>("Predmet nije pronadjen.", HttpStatus.NOT_FOUND);
		}
		SubjectEntity subject = subjectService.deleteSubjectWithTrick(id);

		if (!(subject == null)) {
			return new ResponseEntity<SubjectEntity>(subject, HttpStatus.OK);
		}

		return new ResponseEntity<>("Predmet nije izbrisan.", HttpStatus.INTERNAL_SERVER_ERROR);

	}
	

	@Secured("ADMIN")
	@RequestMapping(method = RequestMethod.PUT, path = "/dodajNastavnika/{idNastavnika}/predmetu/{idPredmeta}")
	public ResponseEntity<?> addTeacherToSubject(
			@PathVariable(name = "idNastavnika") @PositiveOrZero(message = "Id nastavnika ne sme biti negativan broj.") Long idTeacher,
			@PathVariable(name = "idPredmeta") @PositiveOrZero(message = "Id predmeta ne sme biti negativan broj.") Long idSubject) {

		if (!teacherRepository.existsById(idTeacher)) {
			return new ResponseEntity<>("Nastavnik nije pronadjen.", HttpStatus.NOT_FOUND);
		}

		if (!subjectRepository.existsById(idSubject)) {
			return new ResponseEntity<>("Predmet nije pronadjen.", HttpStatus.NOT_FOUND);
		}

		if (subjectService.addTeacherToSubject(idTeacher, idSubject)) {

			return new ResponseEntity<>("Predmetu je uspesno dodat nastavnik.", HttpStatus.OK);
		}

		return new ResponseEntity<>("Predmetu nije dodat nastavnik.", HttpStatus.INTERNAL_SERVER_ERROR);

	}

	@Secured("ADMIN")
	@RequestMapping(method = RequestMethod.PUT, path = "/dodajUcenika/{idUcenika}/predmetu/{idPredmeta}")
	public ResponseEntity<?> addStudentToSubject(
			@PathVariable(name = "idUcenika") @PositiveOrZero(message = "Id ucenika ne sme biti negativan broj.") Long idStudent,
			@PathVariable(name = "idPredmeta") @PositiveOrZero(message = "Id predmeta ne sme biti negativan broj.") Long idSubject) {

		if (!studentRepository.existsById(idStudent)) {
			return new ResponseEntity<>("Ucenik nije pronadjen.", HttpStatus.NOT_FOUND);
		}

		if (!subjectRepository.existsById(idSubject)) {
			return new ResponseEntity<>("Predmet nije pronadjen.", HttpStatus.NOT_FOUND);
		}

		if(!subjectRepository.findById(idSubject).get().getSchoolClass().equals(studentRepository.findById(idStudent).get().getSchoolClass())) {
			return new ResponseEntity<>("Razred ucenika i razred predmeta nisu isti.", HttpStatus.BAD_REQUEST);
		}
		
		if(subjectRepository.findById(idSubject).get().getStudents().contains(studentRepository.findById(idStudent).get())) {
			return new ResponseEntity<>("Ucenik je vec unet.", HttpStatus.BAD_REQUEST);
		}
		SubjectEntity subject = subjectService.addStudentToSubject(idStudent, idSubject);

		if (!(subject == null)) {

			return new ResponseEntity<>(subject, HttpStatus.OK);
		}

		return new ResponseEntity<>("Predmetu nije dodat ucenik.", HttpStatus.INTERNAL_SERVER_ERROR);

	}
	
	@Secured("ADMIN")
	@RequestMapping(method = RequestMethod.PUT, path = "/izbrisiIzListeUcenika/{idUcenika}/predmet/{idPredmeta}")
	public ResponseEntity<?> removeSubjectFromStudent(@PathVariable(name = "idUcenika") @PositiveOrZero(message = "Id ucenika ne sme biti negativan broj.") Long idStudent, @PathVariable(name = "idPredmeta") @PositiveOrZero(message = "Id predmeta ne sme biti negativan broj.") Long idSubject) {

		if(!subjectRepository.existsById(idSubject)) {
			return new ResponseEntity<>("Predmet nije pronadjen.", HttpStatus.NOT_FOUND);
			}
		
		if(!studentRepository.existsById(idStudent)) {
			return new ResponseEntity<>("Ucenik nije pronadjen.", HttpStatus.NOT_FOUND);
			}
		
		
		if(!subjectRepository.findById(idSubject).get().getStudents().contains(studentRepository.findById(idStudent).get()) ) {
			return new ResponseEntity<>("Ucenik nije pronadjen.", HttpStatus.NOT_FOUND);
		}
		
		SubjectEntity subject = subjectService.removeStudentFromSubject(idStudent, idSubject);
		
		if (!(subject == null)) {

			return new ResponseEntity<>(subject, HttpStatus.OK);
		}

		return new ResponseEntity<>("Ucenik  nije izbrisan.", HttpStatus.INTERNAL_SERVER_ERROR);

	}
	
	// za front
	@Secured({"ADMIN" , "TEACHER"})
	@RequestMapping(method = RequestMethod.GET, path = "/ListaUcenika/predmeta/{idPredmeta}")
	public ResponseEntity<?> getStudentsFromSubject( @PathVariable(name = "idPredmeta") @PositiveOrZero(message = "Id predmeta ne sme biti negativan broj.") Long idSubject) {

		if(!subjectRepository.existsById(idSubject)) {
			return new ResponseEntity<>("Predmet nije pronadjen.", HttpStatus.NOT_FOUND);
			}
		
		List<StudentDTOforFront> students = subjectService.subjectStudents(idSubject);
		
		if (students != null) {

			return new ResponseEntity<>(students, HttpStatus.OK);
		}

		return new ResponseEntity<>("Doslo je do interne greske.", HttpStatus.INTERNAL_SERVER_ERROR);

	}


}
