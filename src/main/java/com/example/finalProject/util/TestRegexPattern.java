package com.example.finalProject.util;

import java.util.regex.Pattern;

import org.springframework.boot.SpringApplication;

import com.example.finalProject.FinalniProjekatApplication;

import java.util.regex.Matcher;

public class TestRegexPattern {
    public static boolean useRegex(final String input) {
        // Compile regular expression
      //  final Pattern pattern = Pattern.compile("^0\\d{2}/\\d{4}\\-\\d{3}$", Pattern.CASE_INSENSITIVE);
    	final Pattern pattern = Pattern.compile("Srpski||Fiziko||Biologija||Informatika||Likovno");
        // Match regex against input
        final Matcher matcher = pattern.matcher(input);
        // Use results...
        return matcher.matches();
    }
    
    public static void main(String[] args) {
		System.out.println(useRegex("proba"));
	}
}
