package com.example.finalProject.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Encryption {

	public static String getPassEncoded(String pass) {
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		//System.out.println(bCryptPasswordEncoder.encode(pass));
		return bCryptPasswordEncoder.encode(pass);
	}

	public static boolean validatePassword(String pass, String encodedPass) {
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		//ukoliko postoji {bcrypt} u password-u skine ga
		String replacedEncodedPassword = encodedPass.replace("{bcrypt}", "");
		return bCryptPasswordEncoder.matches(pass,replacedEncodedPassword);
		//return bCryptPasswordEncoder.matches(getPassEncoded(pass),encodedPass);
	}
	
	public static void main(String[] args) {
		System.out.println(getPassEncoded("password"));
		;
	}


}
