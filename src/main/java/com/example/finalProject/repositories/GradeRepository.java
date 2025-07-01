package com.example.finalProject.repositories;


import javax.validation.constraints.Positive;

import org.springframework.data.repository.CrudRepository;

import com.example.finalProject.entities.GradeEntity;
import com.example.finalProject.entities.StudentEntity;
import com.example.finalProject.entities.SubjectEntity;

public interface GradeRepository extends CrudRepository<GradeEntity, Long>{

	Iterable<GradeEntity> findBySubject(SubjectEntity subject);

	boolean existsByFinalMark(boolean b);

	Iterable<GradeEntity> findByStudentAndSubject(StudentEntity student, SubjectEntity subject);

	Iterable<GradeEntity> findByStudentIdAndSubjectId( Long id, Long subjectId);
	

}
