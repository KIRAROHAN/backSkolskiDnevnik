package com.example.finalProject.repositories;

import org.springframework.data.repository.CrudRepository;

import com.example.finalProject.entities.SubjectEntity;

public interface SubjectRepository extends CrudRepository<SubjectEntity, Long> {

	boolean existsByName(String predmet);

	boolean existsBySemester(String polugodiste);

	boolean existsBySchoolClass(String razred);

	boolean existsByNameAndSemesterAndSchoolClass(String predmet, String polugodiste, String razred);

	boolean existsByNameAndSemesterAndSchoolClassAndTeacherId(String predmet, String polugodiste, String razred,
			Long idNastavnika);

}
