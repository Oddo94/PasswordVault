package com.razvan.gui;

import java.awt.*;
import java.awt.Dialog.ModalExclusionType;
import java.awt.event.*;
import java.io.File;

import javax.swing.*;
import java.util.*;
import javax.swing.text.JTextComponent;

import com.razvan.gui.ErrorDisplayManager.messageType;
import com.razvan.installation_manager.ApplicationInstallManager;
import com.razvan.utils.GUIInputChecker;
import com.razvan.utils.XMLFileReader;

public class SenderAccountCredentialsWindow extends JDialog {
	//Labels
	private JLabel titleLabel;
	private JLabel accountAddressLabel;
	private JLabel accountPasswordLabel;

	//Text input fields
	private JTextField accountAddressField;
	private JPasswordField accountPasswordField;

	//Buttons
	private JButton saveButton;
	private JButton resetButton;
	private JButton cancelButton;

	//Panels
	private JPanel buttonPanel;
	private	JPanel inputLabelPanel;
	private	JPanel inputFieldPanel;
	private	JPanel containerInputPanel;
	private	JPanel titlePanel;
	private	JPanel mainPanel;

	//The application main folder path 
	//private String appDataPath = System.getProperty("user.home") + "/AppData/Roaming/PasswordVault";
	private String appDataPath = ApplicationInstallManager.APP_MAIN_FOLDER_PATH;

	//Array containing the text fields present in the window 
	private ArrayList<JTextComponent> fieldList;


	public SenderAccountCredentialsWindow() {

		initializeWindowComponents();
		setupSubPanels();
		setupMainPanel();
		setupWindow();

		this.getRootPane().setDefaultButton(saveButton);

		//Sets the window as modal which means that no other windows can be accessed until it is closed
		//The modality type must be set before the dialog is made visible otherwise it won't work
		this.setModalityType(ModalityType.APPLICATION_MODAL); 
		this.setVisible(true);

	}

	//Method that initializes the window components
	private void initializeWindowComponents() {
		//Panels
		buttonPanel = new JPanel();
		inputLabelPanel = new JPanel(new GridLayout(2,1,10,10));
		inputFieldPanel = new JPanel(new GridLayout(2,1,10,10));
		containerInputPanel = new JPanel();
		titlePanel = new JPanel();
		mainPanel = new JPanel(new BorderLayout());

		//Labels
		titleLabel = new JLabel("Sender account credentials form");
		accountAddressLabel = new JLabel("Account address");
		accountPasswordLabel = new JLabel("Account password");

		//Fields
		accountAddressField = new JTextField(15);
		accountPasswordField = new JPasswordField(15);

		//Buttons
		saveButton = new JButton("Save");
		resetButton = new JButton("Reset");
		cancelButton = new JButton("Cancel");

		fieldList = new ArrayList<>(Arrays.asList(accountAddressField, accountPasswordField));;


	}

	private void setupSubPanels() {
		//Adds label to title panel
		titlePanel.add(titleLabel);

		//Adds labels to label container
		inputLabelPanel.add(accountAddressLabel);
		inputLabelPanel.add(accountPasswordLabel);

		//Adds fields to field container
		inputFieldPanel.add(accountAddressField);
		inputFieldPanel.add(accountPasswordField);

		//Adds the label and field panels to the container input panel(the one containing the labels and fields)
		containerInputPanel.add(inputLabelPanel);
		containerInputPanel.add(inputFieldPanel);


		//Adds components to the button panel
		buttonPanel.add(saveButton, FlowLayout.LEFT);
		buttonPanel.add(resetButton, FlowLayout.CENTER);
		buttonPanel.add(cancelButton, FlowLayout.RIGHT);

	}

	private void setupMainPanel() {
		//Adds all the sub-panels(title panel, container input panel, button panel) to the main panel 
		mainPanel.add(titlePanel, BorderLayout.NORTH);
		mainPanel.add(containerInputPanel, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);
	}

	private void setupWindow() {

		setSize(375,300);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);
		setTitle("Sender account credentials form");
		add(mainPanel);
		addActionListenersToButtons();
		addWindowListener();
	}


	public void addWindowListener() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);

			}
		});

	}

	private void addActionListenersToButtons() {

		resetButton.addActionListener(actionEvent -> {			
			GUIInputChecker.resetFields(fieldList);

		});

		cancelButton.addActionListener(actionEvent -> {

			setVisible(false);		

		});

		saveButton.addActionListener(actionEvent -> {
			String accountAddress = accountAddressField.getText();
			char[] accountPassword = accountPasswordField.getPassword();

			//Checks if the fields contain data
			if (!GUIInputChecker.hasDataOnRequiredFields(fieldList)) {
				JOptionPane.showMessageDialog(this,  "Please fill in the account address and/or the account password!", "Sender account credentials form", JOptionPane.INFORMATION_MESSAGE);
				return;
			}


			//Checks if the entered email address is valid
			if (!GUIInputChecker.isValidEmail(accountAddress)) {
				JOptionPane.showMessageDialog(this,  "Invalid email! Please try again.", "Sender account credentials form", JOptionPane.INFORMATION_MESSAGE);
				return;
			}

			//Saves data to the file and shows the appropriate message to the user according to the method execution result(0-success, -1-failure)
			if (saveData(accountAddress, accountPassword) == 0) {
				JOptionPane.showMessageDialog(this,  "Data successfully saved!", "Sender account credentials form", JOptionPane.INFORMATION_MESSAGE);			
			} else {
				JOptionPane.showMessageDialog(this, "Unable to save the provided data", "Sender account credentials form", JOptionPane.ERROR_MESSAGE);
			}	

		});


	}

	//Method for saving the user input data to the sender_account_credentials.xml file
	private int saveData(String accountAddress, char[] accountPassword) {
		File senderAccountCredentialsFile = new File(ApplicationInstallManager.APP_MAIN_FOLDER_PATH + "/passwordReset/sender_account_credentials.xml");
		XMLFileReader xmlReader = new XMLFileReader(senderAccountCredentialsFile);

		try {
			xmlReader.createCustomXMLCredentialsFile(accountAddress, accountPassword);
			return 0;

		} catch (Exception ex) {
			String stackTrace = ErrorDisplayManager.getStackTraceMessage(ex);
			ErrorDisplayManager.displayError(null, stackTrace, messageType.EXCEPTION);

			return -1;
		}

	}
}


