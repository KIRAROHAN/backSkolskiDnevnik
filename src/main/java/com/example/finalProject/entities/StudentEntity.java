package com.example.finalProject.entities;

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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "STUDENT")
@JsonIgnoreProperties({ "handler", "hibernateLazyInitializer" })
public class StudentEntity {
	
	@JsonProperty("id_ucenika")
	@Id
	//@GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "student_id")
	private Long id;
	
	@JsonProperty("ime")
	@NotNull(message = "First name must be specified.")
	@Size(min = 2, max = 30, message = "First name must be between {min} and {max} characters long.")
	@Column(name = "name", nullable = false)
	private String name;

	@JsonProperty("prezime")
	@NotNull(message = "Last name must be specified.")
	@Size(min = 2, max = 30, message = "Last name must be between {min} and {max} characters long.")
	@Column(name = "last_name", nullable = false)
	private String lastName;
	
	@JsonProperty("razred")
	@NotNull(message = "Class must be specified.")
	@Pattern(regexp = "^[1-8]{1}.$", message =  "Example of class: 1.")
	private String schoolClass;

	@JsonProperty("verzija")
	@Version
	private Integer version;
	
	@JsonProperty("id_korisnika")
	@OneToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "user") 
	private UserEntity user;
	
	@JsonProperty("Roditelji")
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	@JoinTable(name = "Student_Parent", joinColumns = {
			@JoinColumn(name = "Student_id", nullable = false, updatable = false) }, inverseJoinColumns = {
					@JoinColumn(name = "Parent_id", nullable = false, updatable = false) })
    private List<ParentEntity> parents;

	@JsonProperty("Predmeti")
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	@JoinTable(name = "Student_Subject", joinColumns = {
			@JoinColumn(name = "Student_id", nullable = false, updatable = false) }, inverseJoinColumns = {
					@JoinColumn(name = "Subject_id", nullable = false, updatable = false) })
	private List<SubjectEntity> subjects;
	
	//@OnDelete(action = OnDeleteAction.CASCADE)
	@JsonIgnore
	@OneToMany(mappedBy = "student", fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	private List<GradeEntity> grades;
	
	@JsonProperty("adresa")
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "address")
	private AddressEntity address;
	
	@JsonProperty("broj_telefona")
	@Pattern(regexp = "^0\\d{2}/\\d{4}\\-\\d{3}$", message = "Phone number is not valid. Pattern for phone number : 0xx/xxxx-xxx")	
	@Column(name = "phone")
	private String phoneNumber;
	
	public StudentEntity() {
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


	public String getLastName() {
		return lastName;
	}


	public void setLastName(String lastName) {
		this.lastName = lastName;
	}


	public List<GradeEntity> getGrades() {
		return grades;
	}



	public void setGrades(List<GradeEntity> grades) {
		this.grades = grades;
	}



	public List<ParentEntity> getParents() {
		return parents;
	}

	public void setParents(List<ParentEntity> parents) {
		this.parents = parents;
	}

	public List<SubjectEntity> getSubjects() {
		return subjects;
	}

	public void setSubjects(List<SubjectEntity> subjects) {
		this.subjects = subjects;
	}
	

	public UserEntity getUser() {
		return user;
    }

	public void setUser(UserEntity user) {
		this.user = user;
	}


	public AddressEntity getAddress() {
		return address;
	}


	public void setAddress(AddressEntity address) {
		this.address = address;
	}


	public String getPhoneNumber() {
		return phoneNumber;
	}


	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}


	public Integer getVersion() {
		return version;
	}


	public void setVersion(Integer version) {
		this.version = version;
	}
	
	public String getSchoolClass() {
		return schoolClass;
	}


	public void setSchoolClass(String schoolClass) {
		this.schoolClass = schoolClass;
	}
	

	
	
}
