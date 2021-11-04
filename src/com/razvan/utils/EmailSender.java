package com.razvan.utils;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSender {
	private String from;
	private String to;
	private String subject;
	private String message;
	private String sendingAccountPassword;
	private String host;


	public EmailSender(String from, String to, String subject, String message, String sendingAccountPassword) {
		this.from = from; 
		this.to = to;
		this.subject = subject;
		this.message = message;
		this.sendingAccountPassword = sendingAccountPassword;
		this.host = "smtp.gmail.com";
	}

	public void sendEmail() throws Exception {
		Properties properties=  System.getProperties();	 
		properties.setProperty("mail.smtp.host", host);
		properties.setProperty("mail.user", from);
		properties.setProperty("mail.password", sendingAccountPassword);		
		properties.put("mail.smtp.socketFactory.port", "465");
		properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.port", "465");
		
		Authenticator authenticator = new Authenticator() {		
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(from , sendingAccountPassword);
			}
		};

		Session session = Session.getDefaultInstance(properties, authenticator);
		

		MimeMessage mimeMessage = new MimeMessage(session);

		mimeMessage.setFrom(new InternetAddress(from));

		mimeMessage.addRecipient(RecipientType.TO, new InternetAddress(to));

		mimeMessage.setSubject(subject);

		mimeMessage.setText(message);

		Transport.send(mimeMessage);

	}
}
