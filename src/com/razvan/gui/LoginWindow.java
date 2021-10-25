package com.razvan.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.*;
import javax.swing.border.EmptyBorder;


public class LoginWindow extends JFrame {
	//Labels for all the fields
	private JLabel titleLabel = new JLabel("Password Vault");
	private JLabel userNameLabel = new JLabel("Username");
	private JLabel passwordLabel = new JLabel("Password");
	private JLabel newPasswordLabel = new JLabel("New Password");
	private JLabel newConfirmPasswordLabel = new JLabel("Confirm Password");
	private JLabel blankLabel = new JLabel();
	private EmptyBorder emptyBorder = new EmptyBorder(10, 20, 20 ,20);
	

	//Username text field
	private JTextField userNameField = new JTextField(15);

	//Password fields(for login and reset forms)
	private JPasswordField passwordField = new JPasswordField(15);
	private JPasswordField newPasswordField = new JPasswordField(15);
	private JPasswordField confirmPasswordField = new JPasswordField(15);

	//Checkbox for toggling the password reset form on/off
	private JCheckBox changePasswordCheckbox =  new JCheckBox("Change password");

	//Window buttons
	private JButton loginButton = new JButton("Login");
	private JButton resetButton = new JButton("Reset");
	private JButton registerButton = new JButton("Register");
	private JButton resetPasswordButton = new JButton("Reset password");

	//The panel containing the window title
	private JPanel titlePanel = new JPanel();
	//The panel containing both the login and reset forms
	private JPanel containerInputPanel = new JPanel(new BorderLayout());
	//Panel for the user login form
	private JPanel credentialsInputPanel = new JPanel(new GridLayout(3,2,0,5));
	//Panel for the reset password form
	private JPanel resetPasswordPanel = new JPanel(new GridLayout(2,2,0,5));
	//Panel for the window buttons
	private JPanel buttonPanel = new JPanel();

	//Panel containing the whole login form panel(used for easier positioning inside the main panel) 
	private JPanel passwordInputPanel = new JPanel();
	//JPanel checkBoxPanel = new JPanel();
	//Panel containing the whole reset form panel(used for easier positioning inside the main panel) 
	private JPanel changePasswordInputPanel = new JPanel();

	//The main window panel containing all the components
	JPanel authenticationPanel = new JPanel(new BorderLayout());

	public LoginWindow() {
		
		initLoginWindow();
		setSize(375, 300);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Login");
		add(authenticationPanel);
		setVisible(true);
		
		
	}
	
	private void initLoginWindow() {
		//Creating the arrays containing the password dialog components
		ArrayList<Component> loginFormComponents = new ArrayList<Component>(Arrays.asList(userNameLabel, userNameField, passwordLabel, passwordField, blankLabel, changePasswordCheckbox));
		ArrayList<Component> resetFormComponents = new ArrayList<Component>(Arrays.asList(newPasswordLabel, newPasswordField, newConfirmPasswordLabel, confirmPasswordField));
		ArrayList<Component> windowButtons = new ArrayList<Component>(Arrays.asList(newPasswordLabel, newPasswordField, newConfirmPasswordLabel, confirmPasswordField));

		//Adding components to panels
		credentialsInputPanel.setBorder(new EmptyBorder(20,20,20,20));//Setting a border at the top of the panel for spacing
		this.addComponentsToPanel(loginFormComponents, credentialsInputPanel);

		this.addComponentsToPanel(resetFormComponents, resetPasswordPanel);
		resetPasswordPanel.setVisible(false);//Hiding the reset password panel

		this.addComponentsToButtonPanel();
		resetPasswordButton.setVisible(false);//Hiding the reset password button

		//Adding form panels to sub-panels
		this.setupSubPanels();

		//Adding sub-panels to main panel
		this.setupMainPanel();

		//Adding main panel to window
		this.add(authenticationPanel);

		//Adding action listeners to buttons
		//this.addActionListenersToFormButtons(this);
		addActionListenerToCheckBox();
		addActionListenerToButtons();

	}
	
	
	private void addComponentsToButtonPanel() {
		buttonPanel.add(resetPasswordButton, FlowLayout.LEFT);
		buttonPanel.add(loginButton, FlowLayout.LEFT);
		buttonPanel.add(resetButton, FlowLayout.CENTER);
		buttonPanel.add(registerButton, FlowLayout.RIGHT);
	}

	private void setupSubPanels() {
		titlePanel.add(titleLabel);

		passwordInputPanel.add(credentialsInputPanel);
		changePasswordInputPanel.add(resetPasswordPanel);

		containerInputPanel.add(passwordInputPanel, BorderLayout.NORTH);
		containerInputPanel.add(changePasswordInputPanel, BorderLayout.CENTER);
	}

	private void setupMainPanel() {
		authenticationPanel.add(titlePanel, BorderLayout.NORTH);
		authenticationPanel.add(containerInputPanel, BorderLayout.CENTER);
		authenticationPanel.add(buttonPanel, BorderLayout.SOUTH);	
	}

	public void addComponentsToPanel(ArrayList<Component> components, JPanel panel) {
		for (Component currentComponent : components) {
			panel.add(currentComponent);
		}
	}
	
	
	private void addActionListenerToCheckBox() {
		changePasswordCheckbox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (changePasswordCheckbox.isSelected()) {
					resetPasswordPanel.setVisible(true);
					loginButton.setVisible(false);
					resetPasswordButton.setVisible(true);
				} else {
					resetPasswordPanel.setVisible(false);
					loginButton.setVisible(true);
					resetPasswordButton.setVisible(false);
				}
			}
			
			
		});
	}
	
	private void addActionListenerToButtons() {
	resetPasswordButton.addActionListener(actionEvent -> {
		
		int userOption = JOptionPane.showConfirmDialog(this, "Are you sure that you want to reset your password?", "Password reset", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		
		System.out.printf("Selected user option %d\n", userOption);
		
		if(userOption == 0) {
			JOptionPane.showMessageDialog(this , "An email containing the password reset instructions was sent to your email address.", "Password reset", JOptionPane.INFORMATION_MESSAGE);
		
			String userInput = JOptionPane.showInputDialog(this, "Please enter the confirmation code received on your email address: ", "Password reset", JOptionPane.INFORMATION_MESSAGE);
			
			System.out.println("You entered the value " + userInput + "\n");
			
		}
		
	});
	}




}
