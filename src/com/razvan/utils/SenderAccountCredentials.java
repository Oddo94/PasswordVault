package com.razvan.utils;

import javax.xml.bind.annotation.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
//Class that represents the object which is used to encapsulate the credentials used to connect 
//to the email address from which the password reset confirmation code is sent to the user
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class SenderAccountCredentials {
	
	@XmlElement
	private String accountAddress;
	@XmlElement
	private String accountPassword;
	
	

}
