package com.razvan.gui;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.toedter.calendar.JDateChooser;

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

	//Form input fields
	private JTextField accountNameField = new JTextField();
	private JTextField userNameField = new JTextField();
	private JTextField passwordField = new JTextField(15);

	//Form buttons
	private JButton addNewEntryButton = new JButton("Add entry");
	private JButton resetFormButton = new JButton("Reset");

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
		layout.setHorizontalGroup(hGroup);

		//Setting vertical alignment of the form components
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).
				addComponent(accountNameLabel).addComponent(accountNameField));
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).
				addComponent(userNameLabel).addComponent(userNameField));
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).
				addComponent(passwordLabel).addComponent(passwordField));
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).
				addComponent(lastChangeDateLabel).addComponent(dateChooser));
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(resetFormButton).addComponent(addNewEntryButton));
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
			//CHANGE-removes all the rows from the user data table when the user logs out of his account
			DefaultTableModel userDataTableModel = handler.getUserDataTableModel();
			LoginWindow loginWindow = new LoginWindow();

			userDashboard.setVisible(false);
			//CHANGE-removes the user dashboard window after logout
			userDashboard.dispose();
			loginWindow.setVisible(true);

		});

		exitOption.addActionListener(actionEvent -> {
			System.exit(0);
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
	}
}
