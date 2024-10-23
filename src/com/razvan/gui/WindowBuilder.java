package com.razvan.gui;

import com.razvan.utils.SimpleDocumentListener;
import com.razvan.utils.events.EditEventListener;
import com.razvan.utils.model.AccountRecord;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class WindowBuilder extends MouseAdapter implements EditEventListener {
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
    private JLabel emptyLabel = new JLabel();

    //Form input fields
    private JTextField accountNameField = new JTextField();
    private JTextField userNameField = new JTextField();
    private JTextField passwordField = new JTextField(15);

    //Form buttons
    private JButton addNewEntryButton = new JButton("Add entry");
    private JButton resetFormButton = new JButton("Reset");
    private JButton saveChangesButton = new JButton("Save changes");

    private JDateChooser dateChooser = new JDateChooser();

    private List<JComponent> fieldList;
    private AccountRecord originalAccountRecord;


    public WindowBuilder(UserDashboard userDashboard, UserTableOperations handler) {
        this.userDashboard = userDashboard;
        this.handler = handler;
        this.fieldList = new ArrayList<>(Arrays.asList(accountNameField, userNameField, passwordField, dateChooser));

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

        //Sets the edit record event listener
        handler.setEditEventListener(this);

        //Disables the 'Add entry' and 'Save changes' buttons
        addNewEntryButton.setEnabled(false);
        saveChangesButton.setEnabled(false);
    }

    public void createNewEntryForm() {
        //Creating field and field name arrays for further processing
        //JComponent[] fieldsArray = {accountNameField, userNameField, passwordField, dateChooser};
        String[] fieldNames = {"Account field", "User field", "Password field"};

        //Setting field names for further checks regarding the state of each field(empty or not)
        setNamesToInputFields(fieldList, fieldNames);

        //Setting the form layout
        setFormComponentsLayout(newEntryForm);

		/*Adding the entry form panel to the parent panel
	    and the parent panel to the main window */
        parentPanel.add(newEntryForm, BorderLayout.CENTER);
        userDashboard.add(parentPanel, BorderLayout.SOUTH);

        //Adding action listeners
        addActionListenersToTextFields(fieldList);
        addActionListenersToFormButtons(fieldList);
    }

    private void setFormComponentsLayout(JPanel newEntryForm) {
        GroupLayout layout = new GroupLayout(newEntryForm);
        newEntryForm.setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        //Adding the components horizontally, one after the other into three groups
        GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup()
                        .addComponent(accountNameLabel)
                        .addComponent(userNameLabel)
                        .addComponent(passwordLabel)
                        .addComponent(lastChangeDateLabel)
                        .addComponent(resetFormButton))
                .addGroup(layout.createParallelGroup()
                        .addComponent(accountNameField)
                        .addComponent(userNameField)
                        .addComponent(passwordField)
                        .addComponent(dateChooser)
                        .addComponent(addNewEntryButton))
                .addGroup(layout.createParallelGroup()
                        .addComponent(emptyLabel)
                        .addComponent(emptyLabel)
                        .addComponent(emptyLabel)
                        .addComponent(emptyLabel)
                        .addComponent(emptyLabel)
                        .addComponent(saveChangesButton));

        layout.setHorizontalGroup(hGroup);

        //Setting the vertical alignment of the form components contained in those three groups
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
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(resetFormButton)
                        .addComponent(addNewEntryButton)
                        .addComponent(saveChangesButton));

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
        logoutOption.addActionListener(actionEvent -> {
            checkIfUserConfirmsLogout();
        });

        exitOption.addActionListener(actionEvent -> {
            checkIfUserConfirmsExit();
        });
    }

    //Method for setting the names of input fields
    private void setNamesToInputFields(List<JComponent> inputFields, String[] fieldNames) {
        if (inputFields.size() != fieldNames.length) {
            return;
        }

        int i = 0;
        for (JComponent currentField : inputFields) {
            currentField.setName(fieldNames[i++]);
        }

    }

    //Method for collecting the data entered by the user
    private String collectNewEntryData(List<JComponent> fieldList) {
        StringBuilder rowData = new StringBuilder();

        //The objects are cast because the fieldsArray is of type JComponent and its components need to be treated differently according to their specific type
        for (int i = 0; i < fieldList.size(); i++) {
            String fieldContent = "";
            if (fieldList.get(i).getClass() == JTextField.class) {
                fieldContent = ((JTextField) fieldList.get(i)).getText();

            } else if (fieldList.get(i).getClass() == JDateChooser.class) {
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
            if (i != fieldList.size() - 1) {
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


    public void addActionListenersToFormButtons(List<JComponent> fieldList) {

        //Adding action listener to the reset button of the entry form
        resetFormButton.addActionListener(actionEvent -> {
            resetFormFields(fieldList);

            //Disables the 'Save changes' button if the reset button was pressed while the form contains data that is about to be edited
            if (saveChangesButton.isEnabled()) {
                saveChangesButton.setEnabled(false);
            }

            //Sets record editing flag to false
            if (handler.getHasRequestedRowEdit()) {
                handler.setHasRequestedRowEdit(false);
            }
        });

        //Adding action listener to the add new entry button of the entry form
        addNewEntryButton.addActionListener(actionEvent -> {
            String rowData = collectNewEntryData(fieldList);

            handler.addNewEntry(rowData);
            UserTableOperations.userOption = JOptionPane.showConfirmDialog(null, "Do you want to insert a new row?", "Data saving", JOptionPane.YES_NO_CANCEL_OPTION);
            if (UserTableOperations.userOption == 0) {
                handler.getUserDataTableModel().addRow(rowData.split(","));
                //Resets the form fields after the new record insertion
                resetFormFields(fieldList);
            }
        });

        saveChangesButton.addActionListener(actionEvent -> {
            int userEditOption = displayConfirmationPopup(userDashboard, "Are you sure that you want to save the changes made to the selected record?");

            if (userEditOption == -1) {
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

            String accountName = accountNameField.getText();
            String userName = userNameField.getText();
            String password = passwordField.getText();
            String lastChangeDate = sdf.format(dateChooser.getDate());

            AccountRecord accountRecord = new AccountRecord(accountName, userName, password, lastChangeDate);
            int rowIndex = handler.getSelectedRowIndexBasedOnRecord(originalAccountRecord);

            if (rowIndex == -1) {
                JOptionPane.showMessageDialog(userDashboard, "An error occurred while retrieving the selected row index!", "Data edit", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int updateResult = handler.updateSelectedRow(accountRecord, rowIndex);

            if (updateResult == -1) {
                JOptionPane.showMessageDialog(userDashboard, "An error occurred while updating the selected record!", "Data edit", JOptionPane.ERROR_MESSAGE);
                return;
            }

            //Saves the data to the file
            handler.editEntry();

            JOptionPane.showMessageDialog(userDashboard, "The selected record was successfully updated!", "Data edit", JOptionPane.INFORMATION_MESSAGE);

            //Clears data from form
            resetFormFields(fieldList);

            //Sets record editing flag to false
            handler.setHasRequestedRowEdit(false);

            //Disables the 'Save changes' button after a successful edit
            saveChangesButton.setEnabled(false);

            //Resets the original account record object
            originalAccountRecord = null;
        });

        userDashboard.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                checkIfUserConfirmsExit();
            }
        });
    }

    public void addActionListenersToTextFields(List<JComponent> fieldList) {
        accountNameField.getDocument().addDocumentListener(new SimpleDocumentListener() {
            public void update(DocumentEvent e) {
                //The 'Add entry' button is enabled only if all the required fields contain data and if the user is not currently editing a record
                if (hasDataOnRequiredFields(fieldList) && !handler.getHasRequestedRowEdit()) {
                    setButtonState(addNewEntryButton, true);
                } else {
                    setButtonState(addNewEntryButton, false);
                }
            }
        });

        userNameField.getDocument().addDocumentListener(new SimpleDocumentListener() {
            public void update(DocumentEvent e) {
                if (hasDataOnRequiredFields(fieldList) && !handler.getHasRequestedRowEdit()) {
                    setButtonState(addNewEntryButton, true);
                } else {
                    setButtonState(addNewEntryButton, false);
                }
            }
        });

        passwordField.getDocument().addDocumentListener(new SimpleDocumentListener() {
            public void update(DocumentEvent e) {
                if (hasDataOnRequiredFields(fieldList) && !handler.getHasRequestedRowEdit()) {
                    setButtonState(addNewEntryButton, true);
                } else {
                    setButtonState(addNewEntryButton, false);
                }
            }
        });
    }

    public void checkIfUserConfirmsLogout() {
        String message = "Are you sure that you want to log out?";
        int confirmationResult = displayConfirmationPopup(userDashboard, message);

        if (confirmationResult == -1) {
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

        if (confirmationResult == -1) {
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
        int userOption = JOptionPane.showConfirmDialog(frame, message, "User dashboard", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null);

        //When the user selects the 'No' option or closes the dialog from the 'X' button
        if (userOption == 1 || userOption == -1) {
            return -1;
        }

        return 0;
    }

    //This method is executed when the user selects the record editing option
    @Override
    public void onEdit(AccountRecord accountRecord) {
        if (accountRecord == null) {
            return;
        }

        String extractedAccountName = accountRecord.getAccountName();
        String extractedUserName = accountRecord.getUsername();
        String extractedPassword = accountRecord.getPassword();
        String extractedLastChangeDate = accountRecord.getLastChangeDate();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date lastChangeDate;

        try {
            lastChangeDate = sdf.parse(extractedLastChangeDate);
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(userDashboard, "Unable to parse the date of the record being selected for edit!", "Data edit", JOptionPane.ERROR_MESSAGE);
            return;
        }

        accountNameField.setText(extractedAccountName);
        userNameField.setText(extractedUserName);
        passwordField.setText(extractedPassword);
        dateChooser.setDate(lastChangeDate);
        saveChangesButton.setEnabled(true);

        //Disables the 'Add entry" button in order to prevent the insertion of a duplicate record containing the data that is about to be edited
        addNewEntryButton.setEnabled(false);

        //Sets flag which indicates that the user is editing a row
        handler.setHasRequestedRowEdit(true);

        //The account record object before being modified
        originalAccountRecord = accountRecord;
    }

    //Method for checking if all the required fields are populated with data
    public boolean hasDataOnRequiredFields(List<JComponent> fieldList) {
        if (fieldList == null) {
            return false;
        }

        for (JComponent component : fieldList) {
            //Only the text fields are checked because the date chooser is always populated with a value
            if (component instanceof JTextField) {
                JTextField textField = (JTextField) component;
                if ("".equals(textField.getText())) {
                    return false;
                }
            }
        }

        return true;
    }

    //Generic method for setting the state(enabled/disabled) of a specified button
    public void setButtonState(JButton targetButton, boolean state) {
        if (targetButton == null) {
            return;
        }

        targetButton.setEnabled(state);
    }

    //Generic method for clearing data from the specified fields
    public void resetFormFields(List<JComponent> fieldList) {
        if (fieldList == null) {
            return;
        }

        for (JComponent currentField : fieldList) {
            if (currentField.getClass() == JTextField.class) {
                ((JTextField) currentField).setText("");

            } else if (currentField.getClass() == JDateChooser.class) {
                //Sets the date of the JDateChooser as the current date when the control is reset
                Date currentDate = new Date();
                ((JDateChooser) currentField).setDate(currentDate);
            }
        }
    }
}
