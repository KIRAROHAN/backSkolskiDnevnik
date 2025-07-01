package com.example.finalProject.util;

import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class RequestParameterCustomValidator {
    public static boolean useRegex(String input, String newPattern) {
        // Compile regular expression
    	System.out.println(input);
    	System.out.println(newPattern);
        Pattern pattern = Pattern.compile(newPattern);
        // Match regex against input
        Matcher matcher = pattern.matcher(input);
        // Use results...
        return matcher.matches();
    }
    
   
}
