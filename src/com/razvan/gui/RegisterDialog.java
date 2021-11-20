package com.razvan.gui;


import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.text.JTextComponent;

import com.razvan.installation_manager.ApplicationInstallManager;
import com.razvan.user_authentication.PasswordEncryptionManager;
import com.razvan.user_data_security.UserDataSecurityManager;
import java.io.*;


public class RegisterDialog extends JDialog{
	JFrame parentWindow;
	JDialog loginDialog;
	JLabel userNameLabel = new JLabel("Username");
	JLabel passwordLabel = new JLabel("Password");

	JTextField userNameField = new JTextField(15);
	JPasswordField passwordField = new JPasswordField(15);

	JButton registerButton = new JButton("Register");
	JButton resetButton = new JButton("Reset");
	JButton redirectButton = new JButton("Login");
	
	
	//The application main folder path 
	//private String appDataPath = System.getProperty("user.home") + "/AppData/Roaming/PasswordVault";
	private String appDataPath = ApplicationInstallManager.APP_MAIN_FOLDER_PATH;
	private PasswordEncryptionManager pem = new PasswordEncryptionManager();
	private UserDataSecurityManager securityManager = new UserDataSecurityManager();


	public RegisterDialog(JFrame parentWindow,JDialog loginDialog, boolean modal) {
		super(parentWindow, modal);
        this.parentWindow = parentWindow;
        this.loginDialog = loginDialog;
        this.getRootPane().setDefaultButton(registerButton);
		
		JPanel buttonPanel = new JPanel();
		JPanel inputLabelPanel = new JPanel(new GridLayout(2,1,5,5));
		JPanel inputFieldPanel = new JPanel(new GridLayout(2,1,5,5));
		JPanel containerInputPanel = new JPanel();
		JPanel titlePanel = new JPanel();
		JPanel authenticationPanel = new JPanel(new BorderLayout());
		
		


		JLabel titleLabel = new JLabel("Register form");
		titlePanel.add(titleLabel);


		inputLabelPanel.add(userNameLabel);
		inputLabelPanel.add(passwordLabel);
		inputFieldPanel.add(userNameField);
		inputFieldPanel.add(passwordField);
		containerInputPanel.add(inputLabelPanel);
		containerInputPanel.add(inputFieldPanel);



		buttonPanel.add(registerButton, FlowLayout.LEFT);
		buttonPanel.add(resetButton, FlowLayout.CENTER);
		buttonPanel.add(redirectButton, FlowLayout.RIGHT);


		authenticationPanel.add(titlePanel, BorderLayout.NORTH);
		authenticationPanel.add(containerInputPanel, BorderLayout.CENTER);
		authenticationPanel.add(buttonPanel, BorderLayout.SOUTH);

		setSize(300,300);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);
		setTitle("Register");
		add(authenticationPanel);
		addActionListenersToButtons();
		addWindowListener();
		setVisible(true);
		
	}

	
	private void addActionListenersToButtons() {
		//UserDashboard userDashboard = new UserDashboard("RegisterWindow");
		resetButton.addActionListener(actionEvent ->{

			resetAllFields();

		});

		redirectButton.addActionListener(actionEvent -> {

			setVisible(false);
			loginDialog.setVisible(true);

		});

		registerButton.addActionListener(actionEvent -> {
			boolean isFilled = true;

			//Various data checks
			if (userNameField.getText().isEmpty() && passwordField.getPassword().length == 0) {

				JOptionPane.showMessageDialog(this, "Please fill in the username and password fields!");
				isFilled = false;
		

			} else if(userNameField.getText().isEmpty()) {

				JOptionPane.showMessageDialog(this, "Please fill in the username field!");
				isFilled = false;
				

			} else if(passwordField.getPassword().length == 0) {

				JOptionPane.showMessageDialog(this, "Please fill in the password field!");
				isFilled = false;
				
			}

			if (isFilled && !checkUsername(userNameField.getText())) {
				JOptionPane.showMessageDialog(this, "The chosen username doesn't respect the rules! Username should: \n 1. be at least 3 characters long \n 2. should contain only lowercase and uppercase letters(a-zA-Z) or digits(0-9) \n 3. it cannot start with an underscore");
				return;
			}
			
			if (isFilled && !checkPasswordStrength(String.valueOf(passwordField.getPassword()))) {
				JOptionPane.showMessageDialog(this, "The chosen password is not strong enough! Your password should: \n 1. be at least 10 characters long \n 2. contain lowercase and uppercase letters(a-zA-Z), special characters(!@#*/) and digits(0-9)");
				return;
			}

			
			//New user creation logic
			String userName = userNameField.getText();
			
			if (pem.userExists(userName)) {
				JOptionPane.showMessageDialog(this, "The entered user already exists!");
			} else {
				//**Create a different method for writing the new user data to the authentication_data file-it should append the new data instead of overwriting
				pem.writeAuthenticationDataToFile(pem.prepareAuthenticationData(userName, passwordField.getPassword()));
				createUserDataFile(userName);
				createUserIvFile(userName);
				setSecretKeyForUser(userName);
				JOptionPane.showMessageDialog(this, "Your user was successfully created!");
				resetAllFields();
			}
			
		});
	}
	
	
	public void addWindowListener() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);

			}
		});
	}
	
	//Method for checking the if the password contains all the required characters(lowercase/uppercase letters, digits,special characters)
	public static boolean checkPasswordStrength(String password) {
		//Modify the regex so that it checks if all the required characters are prsent in the password at the same time!!!
		Pattern pattern = Pattern.compile("[\\w,.\\/<>?;'\\\\:\\|\\[\\]\\{\\}`~!@#\\$%\\^&\\*\\(\\)\\+=]{10,20}");
		Matcher matcher = pattern.matcher(password);
		
		//Password length check
		return matcher.matches() && password.length() >= 10 ? true : false;
	}
	
	//Username check method(the username cannot start with _ , it can contain(a-z, A-Z, 0-9, _) and must have between 3 and 10 characters)
	private boolean checkUsername(String userName) {
		Pattern pattern = Pattern.compile("[_]{0}[\\w]{3,20}");
		Matcher matcher = pattern.matcher(userName);
		
		return matcher.matches() ? true : false;
	}
	

	//The method for creating the new user data file
	private void createUserDataFile(String userName) {
		
		try {
			File file = new File(appDataPath + "/userData/" + userName);
			file.createNewFile();

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	//The method for creating the new user initialization vector file
	private void createUserIvFile(String userName) {
		String suffix = "-Iv";
		String userIvFileName = userName + suffix;
		
		try {
			
			File ivSource =  new File(appDataPath + "/security/iv/" + userIvFileName);
			ivSource.createNewFile();

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	//The method for creating a secret key for the new user
	public void setSecretKeyForUser(String userName) {
		
		securityManager.storeSecretKey(securityManager.createAESKey(), userName);
	}
	
	//Field reset method
	private void resetAllFields() {
		
		userNameField.setText("");
		passwordField.setText("");
	}
}
