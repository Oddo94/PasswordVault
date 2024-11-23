package com.razvan.gui;

import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ErrorDisplayManager {
	
	//The valid list of message types
	public enum messageType {
		EXCEPTION, ERROR;
	}
	
	//The method that displays the error in a warning message window
	public static void displayError(JFrame currentComponent, String stackTrace, Enum messageType ) {
		String stringEnum = null;
		if (messageType == ErrorDisplayManager.messageType.EXCEPTION) {
			stringEnum = "Exception";
		} else if (messageType == ErrorDisplayManager.messageType.ERROR) {
			stringEnum = "Error";
		}
		
		JPanel errorPanel = new JPanel(new BorderLayout());
		JLabel messageLabel = new JLabel("The following " + stringEnum.toLowerCase() + " has occured:");
		JTextArea textArea = new JTextArea(10,10);
		JScrollPane scrollPane = new JScrollPane(textArea);
		
		scrollPane.setPreferredSize(new Dimension(500,200));
		textArea.setText(stackTrace);
		errorPanel.add(messageLabel, BorderLayout.NORTH);
		errorPanel.add(scrollPane, BorderLayout.CENTER);
		
		JOptionPane.showMessageDialog(currentComponent,errorPanel, stringEnum, JOptionPane.ERROR_MESSAGE);
	}

	//Method for retrieving the error message
	public static String getStackTraceMessage(Throwable ex) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		
		ex.printStackTrace(pw);
		
		return sw.getBuffer().toString();
		
	}
	
}
