package com.example.finalProject.util;


public class RESTerror {
	
	//@JsonView(Views.Public.class)
	private int code;
	//@JsonView(Views.Public.class)
	private String message;
	
	public RESTerror(int code, String message) {
	this.code = code;
	this.message = message;
	}
	
	public int getCode() {
	return code;
	}
	
	public String getMessage() {
	return message;
	}

}
