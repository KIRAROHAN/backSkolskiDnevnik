package com.example.finalProject.entities;

//import java.util.HashMap;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "SUBJECT")
@JsonIgnoreProperties({ "handler", "hibernateLazyInitializer" })
public class SubjectEntity {
	
	@JsonProperty("ID")
	@Id
	//@GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "subject_id")
	private Long id;
	
	@NotNull(message = "Subject name must be provided.")
	@Column(name = "subject_name",nullable = false)
	@JsonProperty("predmet")
	private String name;
	
	@NotNull(message = "Shool class must be provided.")
	@Column(name = "school_class_name",nullable = false)
	@JsonProperty("razred")
	private String schoolClass;
	
	@NotNull(message = "Semester must be provided.")
	@Column(name = "semester",nullable = false)
	@JsonProperty("polugodiste")
	private String semester;
	
	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	@JoinTable(name = "Student_Subject", joinColumns = {
			@JoinColumn(name = "Subject_id", nullable = false, updatable = false) }, inverseJoinColumns = {
					@JoinColumn(name = "Student_id", nullable = false, updatable = false) })
	//private HashMap<SubjectEntity,StudentEntity> students;
	private List<StudentEntity> students;
	
	@JsonProperty("Nastavnik")
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "teacher")
	private TeacherEntity teacher;
	
	@JsonIgnore
	@OneToMany(mappedBy = "subject", fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	private List<GradeEntity> grades;
	//private HashMap<SubjectEntity,GradeEntity> grades;
	
	@Version
	private Integer version;
	
	public SubjectEntity() {
		super();
	
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSemester() {
		return semester;
	}

	public void setSemester(String semester) {
		this.semester = semester;
	}

	public List<StudentEntity> getStudents() {
		return students;
	}

	public void setStudents(List<StudentEntity> students) {
		this.students = students;
	}

	public TeacherEntity getTeacher() {
		return teacher;
	}

	public void setTeacher(TeacherEntity teacher) {
		this.teacher = teacher;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

/*	public HashMap<SubjectEntity, StudentEntity> getStudents() {
		return students;
	}

	public void setStudents(HashMap<SubjectEntity, StudentEntity> students) {
		this.students = students;
	}

	public HashMap<SubjectEntity, GradeEntity> getGrades() {
		return grades;
	}

	public void setGrades(HashMap<SubjectEntity, GradeEntity> grades) {
		this.grades = grades;
	}
	*/

	public List<GradeEntity> getGrades() {
		return grades;
	}

	public void setGrades(List<GradeEntity> grades) {
		this.grades = grades;
	}

	public String getSchoolClass() {
		return schoolClass;
	}

	public void setSchoolClass(String schoolClass) {
		this.schoolClass = schoolClass;
	}
	
	
	
	
	
	
	
	

}
