package com.example.finalProject.dtos;

public class EmailDTO {
	private String to;
	private String subject;
	private String text;

	public EmailDTO() {
		super();

	}

	public EmailDTO(String to, String subject, String text) {
		super();
		this.to = to;
		this.subject = subject;
		this.text = text;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	//@Override
	//public String toString() {
	//	return "EmailDTO [to=" + to + ", subject=" + subject + ", text=" + text + "]";
	//}

}
