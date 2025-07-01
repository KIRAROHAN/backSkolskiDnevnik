package com.example.finalProject.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "ADDRESS", uniqueConstraints = @UniqueConstraint(columnNames = {"street", "city", "country"}))
@JsonIgnoreProperties({ "handler", "hibernateLazyInitializer" })
//@SQLDelete(sql = "UPDATE address SET deleted = true WHERE address_id = ? AND version = ?", check = ResultCheckStyle.COUNT) 
//@Where(clause = "deleted = false")
public class AddressEntity {

	@JsonProperty("ID")
	@Id
	//@GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "address_id", nullable = false)
	private Long id;
	
	@JsonProperty("ulica")
	@Column(name = "street", nullable = false)
	private String street;
	
	@JsonProperty("grad")
	@Column(name = "city", nullable = false)
	private String city;
	
	@JsonProperty("drzava")
	@Column(name = "country", nullable = false)
	private String country;
	
	@JsonProperty("verzija")
	@Version
	private Integer version;
	
	//@JsonIgnore
	//@Column(nullable = false)
	//private boolean deleted = Boolean.FALSE;

	@JsonIgnore
	@OneToMany(mappedBy = "address", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	private List<StudentEntity> students = new ArrayList<StudentEntity>();
	
	@JsonIgnore
	@OneToMany(mappedBy = "address", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	private List<TeacherEntity> teachers = new ArrayList<TeacherEntity>();
	
	@JsonIgnore
	@OneToMany(mappedBy = "address", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	private List<ParentEntity> parents = new ArrayList<ParentEntity>();
	
	@JsonIgnore
	@OneToMany(mappedBy = "address", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	private List<AdminEntity> admins = new ArrayList<AdminEntity>();
	

	public AddressEntity() {
		super();
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public List<StudentEntity> getStudents() {
		return students;
	}

	public void setStudents(List<StudentEntity> students) {
		this.students = students;
	}

	public List<TeacherEntity> getTeachers() {
		return teachers;
	}

	public void setTeachers(List<TeacherEntity> teachers) {
		this.teachers = teachers;
	}

	public List<ParentEntity> getParents() {
		return parents;
	}

	public void setParents(List<ParentEntity> parents) {
		this.parents = parents;
	}

	public List<AdminEntity> getAdmins() {
		return admins;
	}

	public void setAdmins(List<AdminEntity> admins) {
		this.admins = admins;
	}

	//public boolean isDeleted() {
	//	return deleted;
	//}

	//public void setDeleted(boolean deleted) {
	//	this.deleted = deleted;
	//}

	
	
	

	//public AddressEntity(String street, String city, String country) {
	//	super();
	//	this.street = street;
	//	this.city = city;
	//	this.country = country;
	//}
	
	

}
