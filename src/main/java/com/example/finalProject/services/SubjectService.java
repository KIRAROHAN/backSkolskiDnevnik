package com.example.finalProject.services;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.finalProject.dtos.StudentDTO;
import com.example.finalProject.dtos.StudentDTOforFront;
import com.example.finalProject.dtos.SubjectDTO;
import com.example.finalProject.dtos.SubjectDTOforFRONT;
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
public class SubjectService {

	@Autowired
	private SubjectRepository subjectRepository;
	
	@Autowired
	private TeacherRepository teacherRepository;

	@Autowired
	private GradeRepository gradeRepository;
	
	@Autowired
	private StudentRepository studentRepository;
	
	
	public SubjectEntity addSubjectForFront(SubjectDTOforFRONT newSubject) {

		try {
			SubjectEntity subject = new SubjectEntity();

			subject.setName(newSubject.getPredmet());
			subject.setSchoolClass(newSubject.getRazred());
			subject.setSemester(newSubject.getPolugodiste());
			
			//System.out.println(newSubject.getIdNastavnika());
			
			TeacherEntity teacher = null;
			if(newSubject.getIdNastavnika()!=null && newSubject.getIdNastavnika()!=0) {
			   teacher = teacherRepository.findById(newSubject.getIdNastavnika()).get();
			}
			subject.setTeacher(teacher);

			subjectRepository.save(subject);

			return subject;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	
	public SubjectEntity addSubject(SubjectDTO newSubject) {

		try {
			SubjectEntity subject = new SubjectEntity();

			subject.setName(newSubject.getPredmet());
			subject.setSchoolClass(newSubject.getRazred());
			subject.setSemester(newSubject.getPolugodiste());

			subjectRepository.save(subject);

			return subject;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public SubjectEntity deleteSubject(Long id) {
		try {

			SubjectEntity subject = subjectRepository.findById(id).get();

			subjectRepository.delete(subject);

			return subject;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public SubjectEntity deleteSubjectWithTrick(Long id) {
		try {
			
			SubjectEntity subject = subjectRepository.findById(id).get();
			
			subject.setTeacher(null);
			
			for (StudentEntity s : subject.getStudents()) {
				s.getSubjects().remove(subject);
				studentRepository.save(s);
			}
		
			
			subjectRepository.save(subject);
			
			gradeRepository.deleteAll(subject.getGrades());
			
			subjectRepository.delete(subject);

			return subject;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean changeNameOfSubject(Long id, String name) {
		try {

			SubjectEntity subject = subjectRepository.findById(id).get();
			
			subject.setName(name);

			subjectRepository.save(subject);

			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean changeNameOfSemester(Long id, String semester) {
		try {

			SubjectEntity subject = subjectRepository.findById(id).get();
			
			subject.setSemester(semester);

			subjectRepository.save(subject);

			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean addTeacherToSubject(Long idTeacher,Long idSubject) {
		
		try {

			SubjectEntity subject = subjectRepository.findById(idSubject).get();
			TeacherEntity teacher = teacherRepository.findById(idTeacher).get();
			
			subject.setTeacher(teacher);

			subjectRepository.save(subject);

			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public SubjectEntity addStudentToSubject(Long idStudent, Long idSubject) {
		try {

			SubjectEntity subject = subjectRepository.findById(idSubject).get();
			StudentEntity student = studentRepository.findById(idStudent).get();
			
			List<StudentEntity> students = subject.getStudents();

			if (students == null) {

				students = new ArrayList<StudentEntity>();

			}

			students.add(student);

			subject.setStudents(students);

			subjectRepository.save(subject);

			return subject;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public SubjectEntity removeStudentFromSubject(Long idStudent, Long idSubject) {

		try {

			StudentEntity student = studentRepository.findById(idStudent).get();

			SubjectEntity subject = subjectRepository.findById(idSubject).get();
			
			subject.getStudents().remove(student);
			
			subjectRepository.save(subject);

			return subject;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	public boolean changeSchoolClass(Long id, String schoolClass) {

		try {

			SubjectEntity subject = subjectRepository.findById(id).get();

			subject.setSchoolClass(schoolClass);

			subjectRepository.save(subject);

			return true;
		}

		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	// za front napravljeno
	public SubjectEntity updateSubject(Long id, SubjectDTOforFRONT updatedSubject) {

		try {

			SubjectEntity subject = subjectRepository.findById(id).get();

			subject.setName(updatedSubject.getPredmet());
			subject.setSchoolClass(updatedSubject.getRazred());
			subject.setSemester(updatedSubject.getPolugodiste());
			
			TeacherEntity teacher = null;
			if(updatedSubject.getIdNastavnika()!=null && updatedSubject.getIdNastavnika()!=0) {
			   teacher = teacherRepository.findById(updatedSubject.getIdNastavnika()).get();
			}
			subject.setTeacher(teacher);
			
			subjectRepository.save(subject);
			

			return subject;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	// i ovo je za front
	public List<StudentDTOforFront> subjectStudents(Long id) {

		try {
			
			List<StudentDTOforFront> listaStudenta = new ArrayList<>();

			SubjectEntity subject = subjectRepository.findById(id).get();

			List<StudentEntity> students = subject.getStudents();
			
			
			for(int i=0; i<students.size(); i++ ) {
				
				StudentDTOforFront studentDTO = new StudentDTOforFront();
				StudentEntity student = students.get(i);
				
				studentDTO.setIme(student.getName());
				studentDTO.setPrezime(student.getLastName());
				studentDTO.setID(student.getId());
				
				listaStudenta.add(studentDTO);
				
			
			}
			

			return listaStudenta;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
