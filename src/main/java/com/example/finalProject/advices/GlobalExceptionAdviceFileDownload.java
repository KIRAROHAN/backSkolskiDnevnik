package com.example.finalProject.advices;

import java.util.HashMap;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionAdviceFileDownload {
	
	@ExceptionHandler(MultipartException.class)
	public HashMap<String, String> handleMultipartExeception(MultipartException e) {
		
		//FlashMap
		
		HashMap<String,String> map = new HashMap<String,String>();
		
		map.put("message", e.getCause().getMessage());
		//FlashMap map = new FlashMap();
		//map.put("message", e.getCause().getMessage());
		return map;
}
}
