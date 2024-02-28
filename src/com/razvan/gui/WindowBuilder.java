package com.razvan.gui;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WindowBuilder extends MouseAdapter {
	private UserDashboard userDashboard;
	private UserTableOperations handler;
	private JPanel parentPanel = new JPanel(new BorderLayout());
	//Creating the entry form panel
	private JPanel newEntryForm = new JPanel();

	//Field labels
	private JLabel accountNameLabel = new JLabel("Account name");
	private JLabel userNameLabel = new JLabel("User name");
	private JLabel passwordLabel = new JLabel("Password");
	private JLabel lastChangeDateLabel = new JLabel("Last change date");
	private JLabel emptyLabel = new JLabel("Empty label");

	//Form input fields
	private JTextField accountNameField = new JTextField();
	private JTextField userNameField = new JTextField();
	private JTextField passwordField = new JTextField(15);

	//Form buttons
	private JButton addNewEntryButton = new JButton("Add entry");
	private JButton resetFormButton = new JButton("Reset");
	private JButton saveChangesButton = new JButton("Save changes");

	private JDateChooser dateChooser = new JDateChooser();




	public WindowBuilder(UserDashboard userDashboard, UserTableOperations handler) {
		this.userDashboard = userDashboard;
		this.handler = handler;

		//Sets the default date of the JDateChooser
		dateChooser.setDate(new Date());
		//Sets the JDateChooser date format
		dateChooser.setDateFormatString("dd-MM-yyyy");
		//Sets the JDateChooser size
		dateChooser.setMaximumSize(new Dimension(150, 25));

		dateChooser.getDateEditor().setEnabled(false);

		//Sets size for the rest of the form input fields
		accountNameField.setMaximumSize(new Dimension(300, 25));
		userNameField.setMaximumSize(new Dimension(300, 25));
		passwordField.setMaximumSize(new Dimension(300, 25));
	}

	public void createNewEntryForm() {
		//Creating field and field name arrays for further processing
		JComponent[] fieldsArray = {accountNameField, userNameField, passwordField, dateChooser};
		String[] fieldNames = {"Account field", "User field", "Password field"};

		//Setting field names for further checks regarding the state of each field(empty or not)
		setNamesToInputFields(fieldsArray, fieldNames);

		//Setting the form layout
		setFormComponentsLayout(newEntryForm);

		/*Adding the entry form panel to the parent panel
	    and the parent panel to the main window */
		parentPanel.add(newEntryForm,BorderLayout.CENTER);
		userDashboard.add(parentPanel, BorderLayout.SOUTH);

		//Adding action listeners
		addActionListenersToFormButtons(fieldsArray);
	}

	private void setFormComponentsLayout(JPanel newEntryForm) {
		GroupLayout layout = new GroupLayout(newEntryForm);
		newEntryForm.setLayout(layout);

		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		//Adding the components horizontally, one after the other into two groups
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		hGroup.addGroup(layout.createParallelGroup().addComponent(accountNameLabel).addComponent(userNameLabel).addComponent(passwordLabel).addComponent(lastChangeDateLabel).addComponent(resetFormButton));
		hGroup.addGroup(layout.createParallelGroup().addComponent(accountNameField).addComponent(userNameField).addComponent(passwordField).addComponent(dateChooser).addComponent(addNewEntryButton));
		hGroup.addGroup(layout.createParallelGroup().addComponent(emptyLabel).addComponent(emptyLabel).addComponent(emptyLabel).addComponent(emptyLabel).addComponent(emptyLabel).addComponent(saveChangesButton));
		layout.setHorizontalGroup(hGroup);

//		layout.setHorizontalGroup(
//				layout.createParallelGroup()
//								.addComponent(accountNameLabel)
//								.addComponent(accountNameField)
//								.addComponent(userNameLabel)
//								.addComponent(userNameField)
//								.addComponent(passwordLabel)
//								.addComponent(passwordField)
//								.addComponent(lastChangeDateLabel)
//								.addComponent(dateChooser)
//						.addGroup(layout.createSequentialGroup()
//								.addComponent(resetFormButton)
//								.addComponent(addNewEntryButton)
//								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
//								.addComponent(saveChangesButton)));

		//Setting vertical alignment of the form components
//		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
//		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).
//				addComponent(accountNameLabel).addComponent(accountNameField));
//		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).
//				addComponent(userNameLabel).addComponent(userNameField));
//		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).
//				addComponent(passwordLabel).addComponent(passwordField));
//		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).
//				addComponent(lastChangeDateLabel).addComponent(dateChooser));
//		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(resetFormButton).addComponent(addNewEntryButton));
//		layout.setVerticalGroup(vGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(Alignment.BASELINE)
						.addComponent(accountNameLabel)
						.addComponent(accountNameField)
						.addComponent(emptyLabel))
				.addGroup(layout.createParallelGroup(Alignment.BASELINE)
						.addComponent(userNameLabel)
						.addComponent(userNameField)
						.addComponent(emptyLabel))
				.addGroup(layout.createParallelGroup(Alignment.BASELINE)
						.addComponent(passwordLabel)
						.addComponent(passwordField)
						.addComponent(emptyLabel))
				.addGroup(layout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lastChangeDateLabel)
						.addComponent(dateChooser)
						.addComponent(emptyLabel))
				.addGroup(layout.createParallelGroup()
						.addComponent(resetFormButton)
						.addComponent(addNewEntryButton)
						//.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, 100)
						.addComponent(saveChangesButton));

//		layout.setVerticalGroup(
//			layout.createParallelGroup()
//								.addComponent(accountNameLabel)
//								.addComponent(accountNameField)
//								.addComponent(userNameLabel)
//								.addComponent(userNameField)
//								.addComponent(passwordLabel)
//								.addComponent(passwordField)
//								.addComponent(lastChangeDateLabel)
//								.addComponent(dateChooser)
//						.addGroup(layout.createSequentialGroup()
//								.addComponent(resetFormButton)
//								.addComponent(addNewEntryButton)
//								.addComponent(saveChangesButton))
//		);

		layout.setVerticalGroup(vGroup);
	}

	//Top menu creation
	public void setUpMenu() {
		JMenuBar menuBar = new JMenuBar();
		JMenu optionMenu = new JMenu("Options");

		JMenuItem logoutOption = new JMenuItem("Logout");
		JMenuItem exitOption = new JMenuItem("Exit");

		optionMenu.add(logoutOption);
		optionMenu.add(exitOption);

		menuBar.add(optionMenu);

		addActionListenersToMenuOptions(logoutOption, exitOption);
		userDashboard.setJMenuBar(menuBar);

	}


	public void addActionListenersToMenuOptions(JMenuItem logoutOption, JMenuItem exitOption) {
		logoutOption.addActionListener(actionEvent ->{
			checkIfUserConfirmsLogout();
		});

		exitOption.addActionListener(actionEvent -> {
			checkIfUserConfirmsExit();
		});
	}

	//Method for setting the names of input fields
	private void setNamesToInputFields(JComponent[] inputFields, String[] fieldNames) {
		if (inputFields.length != fieldNames.length) {
			return;
		}

		int i = 0;
		for (JComponent currentField : inputFields) {
			currentField.setName(fieldNames[i++]);
		}

	}

	//Method for collecting the data entered by the user 
	private String collectNewEntryData(JComponent[] fieldsArray ) {
		StringBuilder rowData = new StringBuilder();

		//The objects are casted because the fieldsArray is of type JComponent and its components need to be treated differently according to their specific type
		for (int i = 0; i < fieldsArray.length; i++) {
			String fieldContent = "";
			if (fieldsArray[i].getClass() == JTextField.class) {
				fieldContent = ((JTextField) fieldsArray[i]).getText();

			} else if (fieldsArray[i].getClass() == JDateChooser.class) {
				//The following format code is necessary in order to display the date retrieved from the JDateChooser in the correct format when it is inserted in the JTable(e.g. 08-12-2021)
				//Gets the selected date
				Date selectedDate = dateChooser.getDate();
				//Creates the format object
				SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
				//Formats the date from the JDateChooser and creates a new String object having this format
				String formattedDate = sdf.format(selectedDate);

				//The value is assigned to the fieldContent variable
				fieldContent = formattedDate.toString();
			}

			//The separator "," is added only if the processing has not reached the last object of the array
			if (i != fieldsArray.length - 1) {
				if ("".equals(fieldContent)) {
					rowData.append("null" + ",");
				} else {
					rowData.append(fieldContent + ",");

				}
			} else {
				if ("".equals(fieldContent)) {
					rowData.append("null");
				} else {
					rowData.append(fieldContent);

				}
			}


		}

		return rowData.toString();
	}



	public void addActionListenersToFormButtons(JComponent[] fieldsArray) {

		//Adding action listener to the reset button of the entry form
		resetFormButton.addActionListener(actionEvent -> {
			//The casting is necessary for the reasons provided in the collectNewEntryData method
			for (JComponent currentField : fieldsArray) {
				if (currentField.getClass() == JTextField.class) {
					((JTextField) currentField).setText("");

				} else if (currentField.getClass() == JDateChooser.class) {
					//Sets the date of the JDateChooser as the current date when the control is reset
					Date currentDate = new Date();
					((JDateChooser) currentField).setDate(currentDate);
				}
			}
		});

		//Adding action listener to the add new entry button of the entry form
		addNewEntryButton.addActionListener(actionEvent -> {

			for (JComponent currentField : fieldsArray) {
				if (currentField.getClass() == JTextField.class) {
					if (((JTextField) currentField).getText().isEmpty()) {
						JOptionPane.showMessageDialog(userDashboard, "Please fill in the required fields!");
						return;
					}
				}

			}

			String rowData = collectNewEntryData(fieldsArray);

			handler.addNewEntry(rowData.toString());
			UserTableOperations.userOption = JOptionPane.showConfirmDialog(null,"Do you want to insert a new row?","Data saving",JOptionPane.YES_NO_CANCEL_OPTION);
			if( UserTableOperations.userOption == 0) {
				handler.getUserDataTableModel().addRow(rowData.toString().split(","));

			}
		});

		userDashboard.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				checkIfUserConfirmsExit();
			}
		});
	}

	public void checkIfUserConfirmsLogout() {
		String message = "Are you sure that you want to log out?";
		int confirmationResult = displayConfirmationPopup(userDashboard, message);

		if(confirmationResult == -1) {
			return;
		} else {
			LoginWindow loginWindow = new LoginWindow();

			userDashboard.setVisible(false);
			//Removes the user dashboard window after logout
			userDashboard.dispose();

			//Displays the login window
			loginWindow.setVisible(true);
		}
	}

	public void checkIfUserConfirmsExit() {
		String message = "Are you sure that you want to exit?";
		int confirmationResult = displayConfirmationPopup(userDashboard, message);

		if(confirmationResult == -1) {
			userDashboard.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		} else {
			//Instructs the programs to exit when the users presses the 'X' button of the window (no other command is needed)
			userDashboard.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			//If the window close command is sent from another control (e.g. JMenuItem) System.exit(0) needs to be called explicitly to force the program termination
			System.exit(0);
		}
	}

	//Generic method for displaying confirmation pop-ups in the user dashboard window
	public int displayConfirmationPopup(JFrame frame, String message) {
		int userOption = JOptionPane.showConfirmDialog(frame, message, "User dashboard", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,null);

		//When the user selects the 'No' option or closes the dialog from the 'X' button
		if(userOption == 1 || userOption == -1) {
			return -1;
		}

		return 0;
	}
}
