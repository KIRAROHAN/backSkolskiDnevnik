package com.example.finalProject.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(path = "/api/v1/download")
public class DownloadController {
	
	private final Logger logger = LoggerFactory.getLogger(DownloadController.class);

	@Secured("ADMIN")
	@RequestMapping(path = "/logs", method = RequestMethod.GET)
	public ResponseEntity<ByteArrayResource> downloadLogFile() throws IOException {
		
		logger.info("downloadLogFile method invoked ");

		Path path = Paths
				.get("placeholder for path");
		ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

		return ResponseEntity.ok().header("Content-Disposition", "attachment; filename=spring-boot-logging.log")
				.contentType(MediaType.MULTIPART_FORM_DATA).body(resource);
	}

	
}
