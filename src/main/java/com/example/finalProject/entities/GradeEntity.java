package com.example.finalProject.entities;

import java.time.LocalDate;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "GRADE")
@JsonIgnoreProperties({ "handler", "hibernateLazyInitializer" })
public class GradeEntity {

	@JsonProperty("id_ocene")
	@Id
	//@GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "grade_id")
	private Long id;
	
	@JsonProperty("ocena")
	//@Pattern(regexp = "[1-5]{1}", message = "Mark is not valid.")
	@NotNull(message = "Mark must be provided.")
	@Column(name = "mark",nullable = false, length = 1)
	private Integer mark;
	
	@JsonProperty("opisna_ocena")
	@NotNull(message = "Mark description must be provided.")
	@Column(name = "description",nullable = false)
	@Pattern(regexp = "NEDOVOLJAN||DOVOLJAN||DOBAR||VRLO DOBAR||ODLICAN")
	private String description;
	
	@JsonProperty("datum_unosenja_ocene")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	@NotNull(message = "Date must be provided.")
	@Column(name = "date",nullable = false)
	//@Column(name = "date")
	private LocalDate date;
	
	@JsonProperty("zakljucna_ocena")
	@Column(name = "final_mark", nullable = false)
	//@Pattern(regexp = "^[1-5]{1}$", message = "Final mark must be int value in range from 1 to 5.")
	private Boolean finalMark;
	
	
	//@JsonProperty("nastavnik")
	@JsonIgnore
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "teacher")
	private TeacherEntity teacher;
	
	@JsonProperty("verzija")
	@Version
	private Integer version;
	
	@JsonProperty("ucenik")
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "student")
	private StudentEntity student;
	
	@JsonProperty("predmet")
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "subject")
	private SubjectEntity subject;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getMark() {
		return mark;
	}

	public void setMark(Integer mark) {
		this.mark = mark;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}


	public Boolean getFinalMark() {
		return finalMark;
	}

	public void setFinalMark(Boolean finalMark) {
		this.finalMark = finalMark;
	}

	public StudentEntity getStudent() {
		return student;
	}

	public void setStudent(StudentEntity student) {
		this.student = student;
	}

	public SubjectEntity getSubject() {
		return subject;
	}

	public void setSubject(SubjectEntity subject) {
		this.subject = subject;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
    @JsonIgnore
	public TeacherEntity getTeacher() {
		return teacher;
	}
	@JsonProperty("nastavnik")
	public void setTeacher(TeacherEntity teacher) {
		this.teacher = teacher;
	}
	
	

}
