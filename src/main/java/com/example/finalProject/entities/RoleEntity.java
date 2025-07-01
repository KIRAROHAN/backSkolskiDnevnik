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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "ROLE")
@JsonIgnoreProperties({ "handler", "hibernateLazyInitializer" })
public class RoleEntity {

	@JsonProperty("ID")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "role_id")
	private Long id;

	@NotNull(message = "Naziv uloge mora biti dat.")
	@JsonProperty("naziv_uloge")
	@Column(name = "role_name")
	@Pattern(regexp = "ADMIN||TEACHER||STUDENT||PARENT", message = "Naziv uloge nije dozvoljen.")
	private String name;

	@JsonIgnore
	@OneToMany(mappedBy = "role", fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	private List<UserEntity> users = new ArrayList<>();

	public RoleEntity(Long id, String name, List<UserEntity> users) {
		super();
		this.id = id;
		this.name = name;
		this.users = users;
	}

	
	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}

	

	public List<UserEntity> getUsers() {
		return users;
	}

	
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public void setUsers(List<UserEntity> users) {
		this.users = users;
	}

	public RoleEntity() {
		super();

	}

}
