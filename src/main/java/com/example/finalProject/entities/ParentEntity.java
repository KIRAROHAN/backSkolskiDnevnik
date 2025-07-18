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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "PARENT")
@JsonIgnoreProperties({ "handler", "hibernateLazyInitializer" })
public class ParentEntity {
	
	@JsonProperty("ID")
	@Id
	//@GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "parent_id")
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
	
	@JsonIgnore
	@OneToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "user")  
	private UserEntity user;
	
	@NotNull(message = "Email must be provided.")
	@Pattern(regexp = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", message = "Email is not valid.")	
	@Column(name = "email_for_notifications", nullable = false)
	private String emailForNotifications;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	@JoinTable(name = "Student_Parent", joinColumns = {
			@JoinColumn(name = "Parent_id", nullable = false, updatable = false) }, inverseJoinColumns = {
					@JoinColumn(name = "Student_id", nullable = false, updatable = false) })
	private List<StudentEntity> students;
	//private HashMap<ParentEntity,StudentEntity> students;
	
	@JsonProperty("adresa")
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "address")
	private AddressEntity address;
	
	//^\d{3}/\d{4}\-\d{3}$
	@JsonProperty("broj_telefona")
	@Pattern(regexp = "^0\\d{2}/\\d{4}\\-\\d{3}$", message = "Phone number is not valid. Pattern for phone number : 0xx/xxxx-xxx")	
	@Column(name = "phone")
	private String phoneNumber; 

	public List<StudentEntity> getStudents() {
		return students;
	}

	public void setStudents(List<StudentEntity> students) {
		this.students = students;
	}
	
	public ParentEntity() {
		super();

	}
	

	public String getEmailForNotifications() {
		return emailForNotifications;
	}

	//public HashMap<ParentEntity, StudentEntity> getStudents() {
	//	return students;
	//}

	//public void setStudents(HashMap<ParentEntity, StudentEntity> students) {
	//	this.students = students;
	//}

	public void setEmailForNotifications(String emailForNotifications) {
		this.emailForNotifications = emailForNotifications;
	}


	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
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
	
	
	
	

}
