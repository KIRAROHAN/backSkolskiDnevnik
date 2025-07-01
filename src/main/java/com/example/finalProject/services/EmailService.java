package com.example.finalProject.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.finalProject.dtos.EmailDTO;

@Service
public class EmailService{

	@Autowired
	public JavaMailSender emailSender;

	public void sendSimplMessage(EmailDTO emailDTO) {
		// Create a new message
		SimpleMailMessage message = new SimpleMailMessage();
		// Set to, subject, text of the created message
		message.setTo(emailDTO.getTo());
		message.setSubject(emailDTO.getSubject());
		message.setText(emailDTO.getText());
		// Send the message
		emailSender.send(message);

	}

	

}
