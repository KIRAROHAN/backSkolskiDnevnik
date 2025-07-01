package com.example.finalProject.services;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.Valid;
import javax.validation.constraints.Positive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.finalProject.controllers.DownloadController;
import com.example.finalProject.dtos.EmailDTO;
import com.example.finalProject.dtos.GradeDTO;
import com.example.finalProject.dtos.GradeDTOadmin;
import com.example.finalProject.entities.GradeEntity;
import com.example.finalProject.entities.ParentEntity;
import com.example.finalProject.entities.StudentEntity;
import com.example.finalProject.entities.SubjectEntity;
import com.example.finalProject.entities.TeacherEntity;
import com.example.finalProject.repositories.GradeRepository;
import com.example.finalProject.repositories.StudentRepository;
import com.example.finalProject.repositories.SubjectRepository;
import com.example.finalProject.repositories.TeacherRepository;

@Service
public class GradeService {
	
	private final Logger logger = LoggerFactory.getLogger(GradeService.class);

	@Autowired
	StudentRepository studentRepository;

	@Autowired
	TeacherRepository teacherRepository;

	@Autowired
	SubjectRepository subjectRepository;

	@Autowired
	GradeRepository gradeRepository;

	@Autowired
	EmailService emailService;

	@PersistenceContext
	private EntityManager em;

	public GradeEntity addGrade(TeacherEntity teacher, GradeDTO newGrade) {
		try {

			GradeEntity grade = new GradeEntity();
			StudentEntity student = studentRepository.findById(newGrade.getIdUcenika()).get();
			SubjectEntity subject = subjectRepository.findById(newGrade.getIdPredmeta()).get();

			grade.setDate(LocalDate.now());
			switch (newGrade.getOcena()) {
			case 0:
				grade.setDescription("NEDOVOLJAN");
				break;
			case 1:
				grade.setDescription("NEDOVOLJAN");
				break;
			case 2:
				grade.setDescription("DOVOLJAN");
				break;
			case 3:
				grade.setDescription("DOBAR");
				break;
			case 4:
				grade.setDescription("VRLO DOBAR");
				break;
			case 5:
				grade.setDescription("ODLICAN");
			}
			grade.setMark(newGrade.getOcena());
			grade.setFinalMark(false);
			grade.setStudent(student);
			grade.setSubject(subject);
			grade.setTeacher(teacher);

			gradeRepository.save(grade);

			return grade;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public GradeEntity addGradeAdmin(GradeDTOadmin newGrade) {
		try {

			GradeEntity grade = new GradeEntity();
			StudentEntity student = studentRepository.findById(newGrade.getIdUcenika()).get();
			SubjectEntity subject = subjectRepository.findById(newGrade.getIdPredmeta()).get();
			TeacherEntity teacher = teacherRepository.findById(newGrade.getIdNastavnika()).get();

			grade.setDate(LocalDate.now());
			
			switch (newGrade.getOcena()) {
			case 0:
				grade.setDescription("NEDOVOLJAN");
				break;
			case 1:
				grade.setDescription("NEDOVOLJAN");
				break;
			case 2:
				grade.setDescription("DOVOLJAN");
				break;
			case 3:
				grade.setDescription("DOBAR");
				break;
			case 4:
				grade.setDescription("VRLO DOBAR");
				break;
			case 5:
				grade.setDescription("ODLICAN");
			}
			grade.setMark(newGrade.getOcena());
			grade.setFinalMark(false);
			grade.setStudent(student);
			grade.setSubject(subject);
			grade.setTeacher(teacher);
			
			gradeRepository.save(grade);

			return grade;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Boolean sendEmailtoParent(TeacherEntity teacher, GradeEntity grade) {
		try {

			List<ParentEntity> parents = grade.getStudent().getParents();

			for (ParentEntity parent : parents) {
            
				EmailDTO emailDTO = new EmailDTO(parent.getEmailForNotifications(), "Obavestenje o oceni.",
						"Postovani/a " + parent.getName() + "," + "\n\n" + "Obavestavamo Vas da je vase dete "
								+ grade.getStudent().getName() + " " + grade.getStudent().getLastName()
								+ " dobilo ocenu " + grade.getMark() +  " iz predmeta " + grade.getSubject().getName()
								+ ".\n\n" + "Ocenu je dao nastavnik " + teacher.getName() + " " + teacher.getLastName()
								+ ".\n\n" + "Srdacan pozdrav");
				
				emailService.sendSimplMessage(emailDTO);

			}

			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}
	
	public Boolean sendEmailtoParentWhenGradeIsDeleted(TeacherEntity teacher, GradeEntity grade) {
		try {

			List<ParentEntity> parents = grade.getStudent().getParents();

			for (ParentEntity parent : parents) {
            
				EmailDTO emailDTO = new EmailDTO(parent.getEmailForNotifications(), "Obavestenje o oceni.",
						"Postovani/a " + parent.getName() + "," + "\n\n" + "Obavestavamo Vas da je Vasem detetu "
								+ grade.getStudent().getName() + " " + grade.getStudent().getLastName()
								+ " izbrisana ocena " + grade.getMark() + " dobijena "+ grade.getDate() + " iz predmeta " + grade.getSubject().getName()
								+ ".\n\n" + "Ocenu je izbrisao nastavnik " + teacher.getName() + " " + teacher.getLastName()
								+ ".\n\n" + "Srdacan pozdrav");
				
				emailService.sendSimplMessage(emailDTO);

			}

			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}
	
	public Boolean sendEmailtoParentWhenGradeIsChanged(TeacherEntity teacher, GradeEntity grade) {
		try {

			List<ParentEntity> parents = grade.getStudent().getParents();

			for (ParentEntity parent : parents) {
             
				EmailDTO emailDTO = new EmailDTO(parent.getEmailForNotifications(), "Obavestenje o izmenjenoj oceni.",
						"Postovani/a " + parent.getName() + "," + "\n\n" + "Obavestavamo Vas da je vasem dete "
								+ grade.getStudent().getName() + " " + grade.getStudent().getLastName()
								+ " izmenjena ocena " + grade.getMark() + " iz predmeta " + grade.getSubject().getName()
								+ ".\n\n" + "Ocenu je izmenio nastavnik " + teacher.getName() + " " + teacher.getLastName()
								+ ".\n\n" + "Srdacan pozdrav");
				
				emailService.sendSimplMessage(emailDTO);

			}

			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}
	
	public Boolean sendEmailtoParentForFinalGrade(TeacherEntity teacher, GradeEntity grade) {
		try {

			List<ParentEntity> parents = grade.getStudent().getParents();

			for (ParentEntity parent : parents) {
             //umesto parent.getEmailForNotification() stavila sam kirarohan09@gmail.com da ne bih slucajni poslala nekom
				EmailDTO emailDTO = new EmailDTO(parent.getEmailForNotifications(), "Obavestenje o zavrsnoj oceni.",
						"Postovani/a " + parent.getName() + "," + "\n\n" + "Obavestavamo Vas da je vasem dete "
								+ grade.getStudent().getName() + " " + grade.getStudent().getLastName()
								+ " zakljucena ocena " + grade.getMark() + " iz predmeta " + grade.getSubject().getName()
								+ ".\n\n" + "Ocenu je zakljucio nastavnik " + teacher.getName() + " " + teacher.getLastName()
								+ ".\n\n" + "Srdacan pozdrav");
				
				emailService.sendSimplMessage(emailDTO);

			}

			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	public List<GradeEntity> getGradesForStudentBySubjectId(Long id, Long subjectId) {
	

		Query query = em.createNativeQuery("SELECT g.grade_id,g.date,g.description,g.final_mark,sub.subject_name\r\n"
				+ "FROM grade g, student s, subject sub\r\n"
				+ "where g.student = s.student_id AND g.subject = sub.subject_id\r\n"
				+ "and s.student_id = :id and sub.subject_id = :subjectId");
		

		query.setParameter("id", id);
		query.setParameter("subjectId", subjectId);

		return query.getResultList();

	}
	

	public GradeEntity deleteGrade(Long id) {
		try {

			GradeEntity grade = gradeRepository.findById(id).get();

			gradeRepository.delete(grade);

			return grade;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public GradeEntity updateGrade(Long id, Integer newMark) {
		try {

			GradeEntity grade = gradeRepository.findById(id).get();

			grade.setMark(newMark);
			
			switch (newMark) {
			case 0:
				grade.setDescription("NEDOVOLJAN");
				break;
			case 1:
				grade.setDescription("NEDOVOLJAN");
				break;
			case 2:
				grade.setDescription("DOVOLJAN");
				break;
			case 3:
				grade.setDescription("DOBAR");
				break;
			case 4:
				grade.setDescription("VRLO DOBAR");
				break;
			case 5:
				grade.setDescription("ODLICAN");
			}
			grade.setDate(LocalDate.now());

			gradeRepository.save(grade);

			return grade;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
 
	
	public GradeEntity addfinalGradeAdmin(TeacherEntity teacher, SubjectEntity subject, StudentEntity student) {
		try {

			GradeEntity grade = new GradeEntity();
			
			Query query = em.createNativeQuery("SELECT g.mark\r\n"
					+ "FROM grade g, student s, subject sub\r\n"
					+ "where g.student = s.student_id AND g.subject = sub.subject_id\r\n"
					+ "and s.student_id = :id and sub.subject_id = :subjectId");

			query.setParameter("id", student.getId());
			query.setParameter("subjectId", subject.getId());
			

			List<Integer> grades = query.getResultList();
			
            Integer sumOfMarks = 0;
			

			for (Integer g : grades) {

				sumOfMarks += g;

			}

			Integer avarageMark = Math.round(sumOfMarks / grades.size());

			grade.setMark(avarageMark);
			System.out.println(avarageMark);

			switch (avarageMark) {
			case 0:
				grade.setDescription("NEDOVOLJAN");
				break;
			case 1:
				grade.setDescription("NEDOVOLJAN");
				break;
			case 2:
				grade.setDescription("DOVOLJAN");
				break;
			case 3:
				grade.setDescription("DOBAR");
				break;
			case 4:
				grade.setDescription("VRLO DOBAR");
				break;
			case 5:
				grade.setDescription("ODLICAN");
			}


			grade.setDate(LocalDate.now());
			grade.setFinalMark(true);
			grade.setStudent(student);
			grade.setTeacher(teacher);
			grade.setSubject(subject);

			gradeRepository.save(grade);

			return grade;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
