package com.example.finalProject.entities;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
//import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "TEACHER")
@JsonIgnoreProperties({ "handler", "hibernateLazyInitializer" })
//@SQLDelete(sql = "UPDATE teacher SET deleted = true WHERE teacher_id = ? AND version = ?", check = ResultCheckStyle.COUNT) 
//@Where(clause = "deleted = false")
public class TeacherEntity {

	@JsonProperty("ID")
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	//@GeneratedValue(strategy = GenerationType.IDENTITY) 
	@Column(name = "teacher_id")
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
	
	@JsonProperty("verzija")
	@Version
	private Integer version;

	@JsonProperty("nedeljni_fond_casova")
	@Column(name = "weekly_fond")
	@Max(value = 48, message = "Nedeljni fond casova ne sme biti veci od 48.")
	@Min(value = 0, message = "Nedeljni fond casova ne sme biti manji od 0.")
	private Integer weeklyFond;
	
	@JsonIgnore
	@OneToMany(mappedBy = "teacher", fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	private List<GradeEntity> grades;
	
	//@JsonIgnore
	//@Column(nullable=false) 
	//private boolean deleted = Boolean.FALSE;


	//@NotFound(action = NotFoundAction.IGNORE) 
	@JsonIgnore
	@OneToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "user")
	private UserEntity user;

	@JsonIgnore
	@OneToMany(mappedBy = "teacher", fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	private List<SubjectEntity> subjects;

	@JsonProperty("adresa")
	//@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "address")
	private AddressEntity address;

	@JsonProperty("broj_telefona")
	@Pattern(regexp = "^0\\d{2}/\\d{4}\\-\\d{3}$", message = "Phone number is not valid. Pattern for phone number : 0xx/xxxx-xxx")
	// @Column(name = "phone", unique = true)
	// Posto moze vise korisnika koristiti isti broj
	@Column(name = "phone")
	private String phoneNumber;

	public TeacherEntity() {
		super();

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public List<GradeEntity> getGrades() {
		return grades;
	}

	public void setGrades(List<GradeEntity> grades) {
		this.grades = grades;
	}

	//public boolean isDeleted() {
	//	return deleted;
	//}

	//public void setDeleted(boolean deleted) {
	//	this.deleted = deleted;
	//}
	public Integer getWeeklyFond() {
		return weeklyFond;
	}

	public void setWeeklyFond(Integer weeklyFond) {
		this.weeklyFond = weeklyFond;
	}

	
}
