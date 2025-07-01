package com.example.finalProject.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.example.finalProject.dtos.GradeDTO;
import com.example.finalProject.dtos.GradeDTOadmin;
import com.example.finalProject.entities.GradeEntity;
import com.example.finalProject.entities.ParentEntity;
import com.example.finalProject.entities.StudentEntity;
import com.example.finalProject.entities.SubjectEntity;
import com.example.finalProject.entities.TeacherEntity;
import com.example.finalProject.entities.UserEntity;
import com.example.finalProject.repositories.GradeRepository;
import com.example.finalProject.repositories.StudentRepository;
import com.example.finalProject.repositories.SubjectRepository;
import com.example.finalProject.repositories.TeacherRepository;
import com.example.finalProject.repositories.UserRepository;
import com.example.finalProject.services.GradeService;
import com.example.finalProject.util.RESTerror;

@Validated
@RestController
@RequestMapping(path = "api/v1/grades")
public class GradeController {

	private final Logger logger = LoggerFactory.getLogger(GradeController.class);

	@Autowired
	GradeRepository gradeRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	SubjectRepository subjectRepository;

	@Autowired
	GradeService gradeService;

	@Autowired
	StudentRepository studentRepository;

	@Autowired
	TeacherRepository teacherRepository;

	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {

		return new ResponseEntity<>("Nije validno zbog validacion greske: " + e.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Map<String, String> handlerValidationError(MethodArgumentNotValidException ex) {

		logger.info("hadlerValidationError triggered cause of validation errors: ");
		Map<String, String> errors = new HashMap<>();

		for (ObjectError error : ex.getBindingResult().getAllErrors()) {

			String fieldName = "";
			String errorMessage = error.getDefaultMessage();

			if (error instanceof FieldError) {
				fieldName = ((FieldError) error).getField();
			}

			errors.put(fieldName, errorMessage);

			logger.error(fieldName + errorMessage + " \n");
		}

		return errors;

	}

	@Secured("ADMIN")
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<?> getAllGrades() {
		logger.info("getAllGrades method invoked by " + userRepository
				.findByUsername(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString())
				.getUsername());

		return new ResponseEntity<Iterable<GradeEntity>>(gradeRepository.findAll(), HttpStatus.OK);
	}

	@Secured("ADMIN")
	@RequestMapping(method = RequestMethod.GET, path = "/{id_ocene}")
	public ResponseEntity<?> getGradeById(@PathVariable(name = "id_ocene") Long id) {

		logger.info("getGradeById method invoked by " + userRepository
				.findByUsername(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString())
				.getUsername());

		if (!gradeRepository.existsById(id)) {

			return new ResponseEntity<RESTerror>(new RESTerror(2, "Ocena sa datim ID-em nije pronadjena."),
					HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<GradeEntity>(gradeRepository.findById(id).get(), HttpStatus.OK);
	}

	@Secured("ADMIN")
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> addGrade(@Valid @RequestBody GradeDTOadmin newGrade) {
		logger.info("addGrade method invoked by " + userRepository
				.findByUsername(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString())
				.getUsername());

		if (!subjectRepository.existsById(newGrade.getIdPredmeta())) {
			return new ResponseEntity<RESTerror>(new RESTerror(2, "Predmet sa datim ID-em nije pronadjen."),
					HttpStatus.NOT_FOUND);
		}

		if (!studentRepository.existsById(newGrade.getIdUcenika())) {
			return new ResponseEntity<RESTerror>(new RESTerror(2, "Ucenik sa datim ID-em nije pronadjen."),
					HttpStatus.NOT_FOUND);
		}

		if (!teacherRepository.existsById(newGrade.getIdNastavnika())) {
			return new ResponseEntity<RESTerror>(new RESTerror(2, "Nastavnik sa datim ID-em nije pronadjen."),
					HttpStatus.NOT_FOUND);
		}

		if (!subjectRepository.findById(newGrade.getIdPredmeta()).get().getTeacher()
				.equals(teacherRepository.findById(newGrade.getIdNastavnika()).get())) {
			return new ResponseEntity<RESTerror>(
					new RESTerror(10, "Samo nastavnik koji predaje dati predmet moze uneti ocenu."),
					HttpStatus.UNAUTHORIZED);
		}

		if (!subjectRepository.findById(newGrade.getIdPredmeta()).get().getStudents()
				.contains(studentRepository.findById(newGrade.getIdUcenika()).get())) {
			return new ResponseEntity<RESTerror>(
					new RESTerror(11, "Samo nastavnik koji predaje dati predmet datom uceniku moze uneti ocenu."),
					HttpStatus.UNAUTHORIZED);
		}

		GradeEntity grade = gradeService.addGradeAdmin(newGrade);

		TeacherEntity teacher = teacherRepository.findById(newGrade.getIdNastavnika()).get();

		if (!(grade == null)) {

			if (gradeService.sendEmailtoParent(teacher, grade)) {

				System.out.println("Mail je poslat roditeljima.");
			}

			else {
				System.out.println("Mail nije poslat roditeljima.");
			}

			return new ResponseEntity<GradeEntity>(grade, HttpStatus.CREATED);
		}

		return new ResponseEntity<>("Ocena nije kreirana", HttpStatus.INTERNAL_SERVER_ERROR);

	}

	// Ne salje mail roditelju kad admin promeni ocenu iz razloga sto je verovatno
	// profesor nesto pogresio pri unosu,
	// a ne moze da se snadje pa je kontaktirao admina. Bolje da roditelji ne znaju
	// za to :)
	@Secured("ADMIN")
	@RequestMapping(method = RequestMethod.PUT, path = "/{id}")
	public ResponseEntity<?> updateGrade(@PathVariable @Positive(message = "Id mora biti pozitivan broj.") Long id,
			@RequestParam(name = "ocena") @Max(value = 5, message = "Vrednost ocene treba da bude celobrojna vrednost u opsegu od 1 do 5.") @Min(value = 1, message = "Vrednost ocene treba da bude celobrojna vrednost u opsegu od 1 do 5.") @NotNull(message = "Celobrojna vrednost ocene mora biti uneta.") Integer grade) {

		logger.info("updateGrade method invoked by " + userRepository
				.findByUsername(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString())
				.getUsername());

		if (!gradeRepository.existsById(id)) {
			return new ResponseEntity<RESTerror>(new RESTerror(2, "Ocena sa datim ID-em nije pronadjena."),
					HttpStatus.NOT_FOUND);
		}

		GradeEntity updatedGrade = gradeService.updateGrade(id, grade);

		if (!(updatedGrade == null)) {
			return new ResponseEntity<GradeEntity>(updatedGrade, HttpStatus.OK);

		}

		return new ResponseEntity<>("Ocena nije promenjena.", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Secured("ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, path = "/{id}")
	public ResponseEntity<?> deleteGrade(@PathVariable @Positive(message = "Id mora biti pozitivan broj.") Long id) {

		logger.info("deleteGrade method invoked by " + userRepository
				.findByUsername(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString())
				.getUsername());

		if (!gradeRepository.existsById(id)) {
			return new ResponseEntity<RESTerror>(new RESTerror(2, "Ocena sa datim ID-em nije pronadjena."),
					HttpStatus.NOT_FOUND);
		}

		GradeEntity deletedGrade = gradeService.deleteGrade(id);

		if (!(deletedGrade == null)) {
			return new ResponseEntity<GradeEntity>(deletedGrade, HttpStatus.OK);

		}

		return new ResponseEntity<>("Ocena nije izbrisana.", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Secured("TEACHER")
	@RequestMapping(method = RequestMethod.GET, path = "/nastavnik/pretragaOcena")
	public ResponseEntity<?> getGradesForTeacher() {

		UserEntity user = userRepository
				.findByUsername(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());

		logger.info("getGradesForTeacher method invoked by " + user.getUsername());

		TeacherEntity teacher = user.getTeacher();

		return new ResponseEntity<Iterable<GradeEntity>>(teacher.getGrades(), HttpStatus.OK);
	}

	@Secured("TEACHER")
	@RequestMapping(method = RequestMethod.GET, path = "/nastavnik/pretragaOcena/predmet/{id}")
	public ResponseEntity<?> getGradesForTeacherBySubjectId(
			@PathVariable @Positive(message = "Id mora biti pozitivan broj.") Long id) {

		UserEntity user = userRepository
				.findByUsername(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());

		logger.info("getGradesForTeacherBySubjectId method invoked by " + user.getUsername());

		TeacherEntity teacher = user.getTeacher();

		if (!subjectRepository.existsById(id)) {

			logger.info("Predmet sa ID-em " + id + " nije pronadjen.");

			return new ResponseEntity<RESTerror>(
					new RESTerror(2, "Predmet sa datim ID-em nije pronadjen medju Vasim predmetima."),
					HttpStatus.NOT_FOUND);
		}

		SubjectEntity subject = subjectRepository.findById(id).get();

		if (!teacher.getSubjects().contains(subject)) {

			logger.info(
					"Predmet sa ID-em " + id + " nije pronadjen medju predmetima koje predaje ulogovani nastavnik.");
			return new ResponseEntity<RESTerror>(
					new RESTerror(2, "Predmet sa datim ID-em nije pronadjen medju Vasim predmetima."),
					HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<Iterable<GradeEntity>>(gradeRepository.findBySubject(subject), HttpStatus.OK);
	}

	@Secured("TEACHER")
	@RequestMapping(method = RequestMethod.POST, path = "/teacher")
	public ResponseEntity<?> teacherAddingGrade(@Valid @RequestBody GradeDTO newGrade) {

		UserEntity user = userRepository
				.findByUsername(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());

		logger.info("teacherAddingGrade method invoked by " + user.getUsername());

		TeacherEntity teacher = user.getTeacher();

		if (!subjectRepository.existsById(newGrade.getIdPredmeta())) {

			logger.info("Predmet sa Id-em " + newGrade.getIdPredmeta() + " nije pronadjen.");
			return new ResponseEntity<RESTerror>(new RESTerror(2, "Predmet sa datim ID-em nije pronadjen."),
					HttpStatus.NOT_FOUND);
		}

		if (!studentRepository.existsById(newGrade.getIdUcenika())) {
			logger.info("Ucenik sa id-em " + newGrade.getIdUcenika() + " nije pronadjen.");
			return new ResponseEntity<RESTerror>(new RESTerror(2, "Ucenik sa datim ID-em nije pronadjen."),
					HttpStatus.NOT_FOUND);
		}

		if (!subjectRepository.findById(newGrade.getIdPredmeta()).get().getTeacher().equals(teacher)) {
			logger.info("Predmet sa Id-em " + newGrade.getIdPredmeta()
					+ " nije pronadjen u listi predmeta koje ulogovani nastavnik predaje.");
			return new ResponseEntity<RESTerror>(
					new RESTerror(10, "Samo nastavnik koji predaje dati predmet moze uneti ocenu."),
					HttpStatus.UNAUTHORIZED);
		}

		if (!subjectRepository.findById(newGrade.getIdPredmeta()).get().getStudents()
				.contains(studentRepository.findById(newGrade.getIdUcenika()).get())) {

			logger.info("Ucenik sa id-em " + newGrade.getIdUcenika()
					+ " nije pronadjen u listi ucenika koji slusaju predmet sa id-em " + newGrade.getIdPredmeta()
					+ " .");
			return new ResponseEntity<RESTerror>(
					new RESTerror(11, "Samo nastavnik koji predaje dati predmet datom uceniku moze uneti ocenu."),
					HttpStatus.UNAUTHORIZED);
		}

		GradeEntity grade = gradeService.addGrade(teacher, newGrade);

		if (!(grade == null)) {

			if (gradeService.sendEmailtoParent(teacher, grade)) {

				logger.info("Mail je poslat roditeljima.");
				System.out.println("Mail je poslat roditeljima.");
			}

			else {

				logger.error("Mail nije poslat roditeljima.");
				System.out.println("Mail nije poslat roditeljima.");
			}

			logger.error("Uspesno je kreirao ocenu.");
			return new ResponseEntity<GradeEntity>(grade, HttpStatus.CREATED);
		}

		logger.error("gradeService.addGrade je vratio null. Doslo je do greske.");

		return new ResponseEntity<>("Ocena nije kreirana", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Secured("TEACHER")
	@RequestMapping(method = RequestMethod.DELETE, path = "/teacher/grade/{id}")
	public ResponseEntity<?> deleteGradeForTeacher(
			@PathVariable @Positive(message = "Id mora biti pozitivan broj.") Long id) {

		UserEntity user = userRepository
				.findByUsername(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());

		logger.info("deleteGradeForTeacher method invoked by " + user.getUsername());

		TeacherEntity teacher = user.getTeacher();

		if (!gradeRepository.existsById(id)) {
			logger.info("Ocena sa id-em " + id + " nije pronadjena.");
			return new ResponseEntity<RESTerror>(new RESTerror(2, "Ocena sa datim ID-em nije pronadjena."),
					HttpStatus.NOT_FOUND);
		}

		if (!gradeRepository.findById(id).get().getTeacher().equals(teacher)) {
			logger.info("Ocena sa id-em " + id + " nije pronadjena medju ocenama koje je ulogovan nastavnik dao.");
			return new ResponseEntity<RESTerror>(
					new RESTerror(2, "Ocena sa datim ID-em nije pronadjena u ocenama koje ste Vi dali."),
					HttpStatus.NOT_FOUND);
		}

		GradeEntity deletedGrade = gradeService.deleteGrade(id);

		if (!(deletedGrade == null)) {

			if (gradeService.sendEmailtoParentWhenGradeIsDeleted(teacher, deletedGrade)) {

				logger.info("Mail je poslat roditeljima.");
				System.out.println("Mail je poslat roditeljima.");
			}

			else {

				logger.error("Mail nije poslat roditeljima.");
				System.out.println("Mail nije poslat roditeljima.");
			}
			logger.error("Uspesno je izbrisao ocenu.");
			return new ResponseEntity<GradeEntity>(deletedGrade, HttpStatus.OK);
		}

		logger.error("gradeService.deleteGrade je vratio null. Doslo je do greske.");
		return new ResponseEntity<>("Ocena nije izbrisana.", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Secured("TEACHER")
	@RequestMapping(method = RequestMethod.POST, path = "/zakljucnaOcena/izPredmeta/{idPredmeta}/zaUcenika/{idUcenika}")
	public ResponseEntity<?> addFinalGradeForTeacher(
			@PathVariable(name = "idPredmeta") @Positive(message = "Id mora biti pozitivan broj.") Long idSubject,
			@PathVariable(name = "idUcenika") @Positive(message = "Id mora biti pozitivan broj.") Long idStudent) {

		UserEntity user = userRepository
				.findByUsername(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());

		logger.info("addFinalGradeForTeacher method invoked by " + user.getUsername());

		TeacherEntity teacher = user.getTeacher();

		if (!(subjectRepository.existsById(idSubject))) {
			logger.info("Predmet sa Id-em " + idSubject + " nije pronadjen.");
			return new ResponseEntity<RESTerror>(new RESTerror(2, "Predmet sa datim ID-em nije pronadjen."),
					HttpStatus.NOT_FOUND);
		}

		if (!studentRepository.existsById(idStudent)) {
			logger.info("Ucenik sa Id-em " + idStudent + " nije pronadjen.");
			return new ResponseEntity<RESTerror>(new RESTerror(2, "Ucenik sa datim ID-em nije pronadjen."),
					HttpStatus.NOT_FOUND);
		}

		SubjectEntity subject = subjectRepository.findById(idSubject).get();

		if (!subject.getTeacher().equals(teacher)) {
			logger.info("Predmet sa Id-em " + idSubject + " ne predaje ulogovani nastavnik.");
			return new ResponseEntity<RESTerror>(new RESTerror(2, "Predmet sa datim ID-em nije pronadjen."),
					HttpStatus.NOT_FOUND);
		}

		if (!studentRepository.findById(idStudent).get().getSubjects()
				.contains(subjectRepository.findById(idSubject).get())) {

			logger.info("Predmet sa Id-em " + idSubject + " nije pronadjen u listi predmeta koje slusa ucenik sa id-em "
					+ idStudent);
			return new ResponseEntity<RESTerror>(new RESTerror(2, "Predmet sa datim ID-em nije pronadjen."),
					HttpStatus.NOT_FOUND);
		}

		StudentEntity student = studentRepository.findById(idStudent).get();

		if (student.getGrades() == null) {
			logger.info("Ucenik nema ocene. Da bi ocena mogla da se zakjuci ucenik mora imati bar jednu ocenu.");
			return new ResponseEntity<RESTerror>(
					new RESTerror(22,
							"Ucenik nema ocene. Da bi ocena mogla da se zakjuci ucenik mora imati bar jednu ocenu."),
					HttpStatus.NOT_FOUND);
		}

		GradeEntity grade = gradeService.addfinalGradeAdmin(teacher, subject, student);

		if (!(grade == null)) {

			if (gradeService.sendEmailtoParentForFinalGrade(teacher, grade)) {

				System.out.println("Mail je poslat roditeljima.");
			}

			else {
				System.out.println("Mail nije poslat roditeljima.");
			}

			logger.error("Uspesno je kreirao ocenu.");
			return new ResponseEntity<GradeEntity>(grade, HttpStatus.CREATED);
		}

		logger.error("gradeService.addfinalGradeAdmin je vratio null. Doslo je do greske.");
		return new ResponseEntity<>("Ocena nije kreirana", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Secured("TEACHER")
	@RequestMapping(method = RequestMethod.PUT, path = "/izmeniOcenu/{id}")
	public ResponseEntity<?> updateGradeForTeacher(
			@PathVariable @Positive(message = "Id mora biti pozitivan broj.") Long id,
			@RequestParam(name = "ocena") @Max(value = 5, message = "Vrednost ocene treba da bude celobrojna vrednost u opsegu od 1 do 5.") @Min(value = 1, message = "Vrednost ocene treba da bude celobrojna vrednost u opsegu od 1 do 5.") @NotNull(message = "Celobrojna vrednost ocene mora biti uneta.") Integer grade) {

		UserEntity user = userRepository
				.findByUsername(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());

		logger.info("updateGradeForTeacher method invoked by " + user.getUsername());

		TeacherEntity teacher = user.getTeacher();

		if (!gradeRepository.existsById(id)) {
			logger.info("Ocena sa datim ID-em nije pronadjena.");
			return new ResponseEntity<RESTerror>(new RESTerror(2, "Ocena sa datim ID-em nije pronadjena."),
					HttpStatus.NOT_FOUND);
		}

		if (!gradeRepository.findById(id).get().getTeacher().equals(teacher)) {
			logger.info("Ocena sa datim ID-em nije pronadjena medju ocenama koje je ulogovani nastavnik dao.");
			return new ResponseEntity<RESTerror>(
					new RESTerror(2, "Ocena sa datim ID-em nije pronadjena u ocenama koje ste Vi dali."),
					HttpStatus.NOT_FOUND);
		}

		GradeEntity updatedGrade = gradeService.updateGrade(id, grade);

		if (!(updatedGrade == null)) {

			if (gradeService.sendEmailtoParentWhenGradeIsChanged(teacher, updatedGrade)) {

				logger.info("Mail je poslat roditeljima.");
				System.out.println("Mail je poslat roditeljima.");
			}

			else {
				logger.error("Mail nije poslat roditeljima.");
				System.out.println("Mail nije poslat roditeljima.");
			}

			logger.info("Uspesno je izmenio ocenu.");
			return new ResponseEntity<GradeEntity>(updatedGrade, HttpStatus.OK);
		}
		logger.error("Ocena nije promenjena.");
		return new ResponseEntity<>("Ocena nije promenjena.", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Secured("PARENT")
	@RequestMapping(method = RequestMethod.GET, path = "/roditelj/pretragaOcena/zaUcenika/{id}")
	public ResponseEntity<?> getGradesForParent(
			@PathVariable @Positive(message = "Id mora biti pozitivan broj.") Long id) {

		UserEntity user = userRepository
				.findByUsername(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());

		logger.info("getGradesForParent method invoked by " + user.getUsername());

		ParentEntity parent = user.getParent();

		if (!studentRepository.existsById(id)) {
			logger.info("Ucenik cije ocene ulogovani roditelje pokusava da procita ne postoji u bazi.");
			return new ResponseEntity<RESTerror>(
					new RESTerror(2, "Ucenik sa datim ID-em nije pronadjen medju Vasom decom."), HttpStatus.NOT_FOUND);
		}

		StudentEntity student = studentRepository.findById(id).get();
		if (!parent.getStudents().contains(student)) {
			logger.info("Ucenik cije ocene ulogovani roditelje pokusava da procita nije medju njegovom decom.");
			return new ResponseEntity<RESTerror>(
					new RESTerror(2, "Ucenik sa datim ID-em nije pronadjen medju Vasom decom."), HttpStatus.NOT_FOUND);
		}

		logger.info("Uspesna pretraga.");
		return new ResponseEntity<Iterable<GradeEntity>>(student.getGrades(), HttpStatus.OK);
	}

	@Secured("PARENT")
	@RequestMapping(method = RequestMethod.GET, path = "/roditelj/pretragaOcena/zaUcenika/{id}/izPredmeta/{subjectId}")
	public ResponseEntity<?> getGradesForParentBySubjectId(
			@PathVariable @Positive(message = "Id mora biti pozitivan broj.") Long id,
			@PathVariable @Positive(message = "Id mora biti pozitivan broj.") Long subjectId) {

		UserEntity user = userRepository
				.findByUsername(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());

		logger.info("getGradesForParentBySubjectId method invoked by " + user.getUsername());

		ParentEntity parent = user.getParent();

		if (!studentRepository.existsById(id)) {
			logger.info("Ucenik cije ocene ulogovani roditelje pokusava da procita ne postoji u bazi.");
			return new ResponseEntity<RESTerror>(
					new RESTerror(2, "Ucenik sa datim ID-em nije pronadjen medju Vasom decom."), HttpStatus.NOT_FOUND);
		}

		if (!subjectRepository.existsById(subjectId)) {
			logger.info("Predmet nije u bazi.");
			return new ResponseEntity<RESTerror>(
					new RESTerror(2, "Predmet sa datim id-em nije u listi Vasih predmeta."), HttpStatus.NOT_FOUND);
		}

		SubjectEntity subject = subjectRepository.findById(id).get();
		StudentEntity student = studentRepository.findById(id).get();

		if (!parent.getStudents().contains(student)) {
			logger.info("Ucenik cije ocene ulogovani roditelje pokusava da procita nije medju njegovom decom.");
			return new ResponseEntity<RESTerror>(
					new RESTerror(2, "Ucenik sa datim ID-em nije pronadjen medju Vasom decom."), HttpStatus.NOT_FOUND);
		}

		if (!student.getSubjects().contains(subject)) {
			logger.info("Ucenik ne slusa predmet sa datim id-em");
			return new ResponseEntity<RESTerror>(new RESTerror(2, "Vase dete ne slusa uneti predmet."),
					HttpStatus.NOT_FOUND);
		}

		logger.info("Uspesna pretraga.");
		return new ResponseEntity<Iterable<GradeEntity>>(gradeRepository.findByStudentIdAndSubjectId(id, subjectId),
				HttpStatus.OK);
	}

	@Secured("STUDENT")
	@RequestMapping(method = RequestMethod.GET, path = "/ucenik/pretragaOcena")
	public ResponseEntity<?> getGradesForStudent() {

		UserEntity user = userRepository
				.findByUsername(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
		logger.info("getGradesForStudents method invoked by " + user.getUsername());
		StudentEntity student = user.getStudent();

		return new ResponseEntity<Iterable<GradeEntity>>(student.getGrades(), HttpStatus.OK);
	}

	@Secured("STUDENT")
	@RequestMapping(method = RequestMethod.GET, path = "/ucenik/pretragaOcena/predmet/{id}")
	public ResponseEntity<?> getGradesForStudentBySubjectId(
			@PathVariable @Positive(message = "Id mora biti pozitivan broj.") Long id) {

		UserEntity user = userRepository
				.findByUsername(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());

		logger.info("getGradesForStudentBySubjectId method invoked by " + user.getUsername());

		StudentEntity student = user.getStudent();

		if (!subjectRepository.existsById(id)) {

			logger.info("Predmet sa datim id-em ne postoji u bazi.");

			return new ResponseEntity<RESTerror>(
					new RESTerror(2, "Predmet sa datim ID-em nije pronadjen medju Vasim predmetima."),
					HttpStatus.NOT_FOUND);
		}

		SubjectEntity subject = subjectRepository.findById(id).get();

		if (!student.getSubjects().contains(subject)) {
			logger.info("Predmet sa datim id-em nije u listi predmeta koje ucenik slusa.");
			return new ResponseEntity<RESTerror>(
					new RESTerror(2, "Predmet sa datim ID-em nije pronadjen medju Vasim predmetima."),
					HttpStatus.NOT_FOUND);
		}

		Iterable<GradeEntity> grades = gradeService.getGradesForStudentBySubjectId(student.getId(), id);

		return new ResponseEntity<Iterable<GradeEntity>>(grades, HttpStatus.OK);
	}
}
