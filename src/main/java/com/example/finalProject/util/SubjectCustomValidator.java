package com.example.finalProject.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.example.finalProject.dtos.AddressDTO;
import com.example.finalProject.dtos.SubjectDTO;
import com.example.finalProject.dtos.SubjectDTOforFRONT;
import com.example.finalProject.dtos.UserDTO;
import com.example.finalProject.entities.UserEntity;
import com.example.finalProject.repositories.AddressRepository;
import com.example.finalProject.repositories.SubjectRepository;
import com.example.finalProject.repositories.UserRepository;

@Component
public class SubjectCustomValidator implements Validator {

	@Autowired
	SubjectRepository subjectRepository;

	@Override
	public boolean supports(Class<?> clazz) {

		// return SubjectDTO.class.equals(clazz);
		// opet menjam za front
		return SubjectDTOforFRONT.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {

		// SubjectDTO subject = (SubjectDTO) target;
		SubjectDTOforFRONT subject = (SubjectDTOforFRONT) target;

		// OVO JE U SLUCAJU DA RACUNAM I ODELJENJE KOD PREDMETA, PA MOGU RAZLICITI
		// NASTAVNICI DA PREDAJU ISTI PREDMET ISTOM RAZREDU, SAMO DA SE ODELJENJA
		// RAZLIKUJU.
		// U TOM SLUCAJU PROVERAM DA LI JE PREDMET JEDINSTVEN PO SVEMU NAZIVU ... +
		// NASTAVNIKU. I OVO JE DODATO ZA FRONT

		if (subject.getIdNastavnika() != null && subject.getIdNastavnika() != 0) {

			if (subjectRepository.existsByNameAndSemesterAndSchoolClassAndTeacherId(subject.getPredmet(),
					subject.getPolugodiste(), subject.getRazred(), subject.getIdNastavnika())) {
				errors.reject("400", "Predmet vec postoji u bazi.");
			}

		}

		else {

			if (subjectRepository.existsByNameAndSemesterAndSchoolClass(subject.getPredmet(), subject.getPolugodiste(),
					subject.getRazred())) {
				errors.reject("400", "Predmet vec postoji u bazi.");
			}

		}
	}

}
