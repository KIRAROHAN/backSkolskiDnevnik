package com.example.finalProject.advices;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


import com.example.finalProject.util.RESTerror;


@ControllerAdvice
public class GlobalExceptionAdvice {
	
		@ExceptionHandler(NumberFormatException.class)
		@ResponseBody
		public ResponseEntity<RESTerror> handleNumberFormatException(NumberFormatException e) {
		    return new ResponseEntity<RESTerror>(new RESTerror(3, "Id mora biti ceo broj."),
					HttpStatus.BAD_REQUEST);

		}
		
	
		
}

