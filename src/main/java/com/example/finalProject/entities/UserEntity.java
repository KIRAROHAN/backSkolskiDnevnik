package com.example.finalProject.entities;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

//import org.hibernate.annotations.ResultCheckStyle;
//import org.hibernate.annotations.SQLDelete;
//import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "user")
@JsonIgnoreProperties({ "handler", "hibernateLazyInitializer" })
//@SQLDelete(sql = "UPDATE user SET deleted = true WHERE user_id = ? AND version = ?", check = ResultCheckStyle.COUNT)
//@Where(clause = "deleted = false")
public class UserEntity {

	@JsonProperty("ID")
	@Id
	//@GeneratedValue(strategy = GenerationType.AUTO) 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long id;

	// zamisljeno kao email adresa korisnika
	// roditelj ce imati 2 email adrese, u slucaju da zeli da mu obavestenja stizu
	// na drugaciju email adresu od one koju koristi za logovanje
	@JsonProperty("email")
	@NotNull(message = "Username must be provided.")
	@Pattern(regexp = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", message = "Username is not valid.")
	@Column(name = "username", nullable = false, unique = true)
	private String username;

	@NotNull(message = "Password must be provided.")
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,}$", message = "Password is not valid.\r\n"
			+ "    Password must contain at least one digit [0-9].\r\n"
			+ "    Password must contain at least one lowercase Latin character [a-z].\r\n"
			+ "    Password must contain at least one uppercase Latin character [A-Z].\r\n"
			+ "    Password must contain at least one special character like ! @ # & ( ).\r\n"
			+ "    Password must contain a length of at least 8 characters.\r\n" + "")
	//@JsonIgnore
	@JsonProperty("sifra")
	@Column(name = "password", nullable = false)
	private String password;

	@NotNull(message = "User must have a role.")
	@JsonProperty("id_uloge")
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "role")
	private RoleEntity role;

	@JsonIgnore
	@Version
	private Integer version;

	//@JsonIgnore
	//@Column
	//private boolean deleted = Boolean.FALSE;

	//public boolean isDeleted() {
	//	return deleted;
	//}

	//public void setDeleted(boolean deleted) {
	//	this.deleted = deleted;
	//}

	// @JsonProperty("ucenik")
	@JsonIgnore
	@OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	private StudentEntity student;

	// @JsonProperty("nastavnik")
	@JsonIgnore
	@OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	private TeacherEntity teacher;

	// @JsonProperty("roditelj")
	@JsonIgnore
	@OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	private ParentEntity parent;

	// @JsonProperty("administrator")
	@JsonIgnore
	@OneToOne(mappedBy = "user", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	private AdminEntity admin;

	public UserEntity() {
		super();

	}

	public RoleEntity getRole() {
		return role;
	}

	public void setRole(RoleEntity role) {
		this.role = role;
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@JsonIgnore
	public String getPassword() {
		return password;
	}

	@JsonProperty("sifra")
	public void setPassword(String password) {
		this.password = password;
	}

	public StudentEntity getStudent() {
		return student;
	}

	public void setStudent(StudentEntity student) {
		this.student = student;
	}

	public TeacherEntity getTeacher() {
		return teacher;
	}

	public void setTeacher(TeacherEntity teacher) {
		this.teacher = teacher;
	}

	public ParentEntity getParent() {
		return parent;
	}

	public void setParent(ParentEntity parent) {
		this.parent = parent;
	}

	public AdminEntity getAdmin() {
		return admin;
	}

	public void setAdmin(AdminEntity admin) {
		this.admin = admin;
	}

}
