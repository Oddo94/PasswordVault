package com.razvan.gui;

import com.razvan.installation_manager.ApplicationInstallManager;
import com.razvan.io_manager.IOFileManager;
import com.razvan.user_data_security.UserDataSecurityManager;
import com.razvan.utils.GUIInputChecker;
import com.razvan.utils.events.EditEventListener;
import com.razvan.utils.model.AccountRecord;

import javax.crypto.SecretKey;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Objects;



public class UserTableOperations extends MouseAdapter {
	private UserDashboard userDashboard;
	private JTable userDataTable;
	static int userOption;
	private static boolean hasRequestedRowEdit;
	private boolean hasCanceledChange;
	private boolean hasCopiedCellContent;
	private String oldCellValue;
	private String appDataPath = ApplicationInstallManager.APP_MAIN_FOLDER_PATH;
	private String userFileName;
	private IOFileManager fileManager = new IOFileManager();
	private UserDataSecurityManager securityManager = new UserDataSecurityManager();
	private EditEventListener editEventListener;

	public UserTableOperations(UserDashboard userDashboard, String userFileName) {
		this.userDashboard = userDashboard;
		this.userFileName = userFileName;
		userOption = -1;
	}

	public UserTableOperations() {

	}

	//The method that fills the table using the user data file if this is not empty
	public JTable fillTable(JTable table) {
		DefaultTableModel dtm =(DefaultTableModel) table.getModel();

		//File containing the encrypted userData(it will become a parameter of the fillTable method)-THE FILE CONTAINING USER DATA WILL HAVE TO BE NAMED  AFTER THE USER
		File fileToRead = new File(appDataPath + "/userData/" + userFileName);
		//Creating the string that represents the name of the user Iv storage file(by concatenating the user name and the suffix "-Iv")
		String suffix = "-Iv";
		String userIvFileName = userFileName + suffix;
		//File containing the initialization vector used for decryption(it should probably be used directly by the restoreInitializationVector method)
		File ivSource =  new File(appDataPath + "/security/iv/" + userIvFileName);

		//Encrypted data read from file
		byte[] encryptedData = fileManager.readEncryptedData(fileToRead);


		//Decrypted data returned as a string by the performAESDecryption method
		String decryptedData = securityManager.performAESDecryption(encryptedData, securityManager.retrieveSecretKey(userFileName), securityManager.restoreInitializationVector(fileManager.readDataForDecryption(ivSource)));

		//The decrypted data is split using \n as separator(should probably move the tableData array inside the if clause for a more readable code)
		String[] tableData = decryptedData.split("\n");

		//Checks to see if the decrypted String contains any values
		if(!"".equals(decryptedData)) {
			/* If it contains at least one value then each entry will be split using "," as
			  separator and the resulting array will be used to create a new table row */
			for (String entry : tableData) {
				dtm.addRow(entry.split(","));
			}
		}

		return table;
	}


	/*
	 * The method that creates the table that displays the user data.
	 * It receives the number of rows and columns as parameters.
	 * This data also used for creating the DefaultTableModel.
	 */
	public JTable createTable(int numRows, int numColumns) {
		String[] columnNames = {"Account name", "User name", "Password","Last password change date"};

		//Creates the table
		userDataTable = new JTable(numRows,numColumns);

		//Sets the table cell renderer in order to allow the highlighting of expired passwords
		setTableCellRendererForExpiredPasswordHighlight(userDataTable);

		//Creates the table model and disables cell editing (isCellEditable method will return false for any cell)
		DefaultTableModel tableModel = new DefaultTableModel(numRows,numColumns) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		tableModel.setColumnIdentifiers(columnNames);
		userDataTable.setModel(tableModel);
		//Enables the sorting option on the columns of the table
		userDataTable.setAutoCreateRowSorter(true);

		//Sets the column names of the table
		for(int i = 0; i < userDataTable.getColumnCount(); i++) {

			TableColumn currentColumn = userDataTable.getTableHeader().getColumnModel().getColumn(i);
			currentColumn.setHeaderValue(columnNames[i]);
		}

		//Creates the JPanel that contains the table
		JPanel tablePanel = new JPanel();
		JScrollPane tableScrollPane = new JScrollPane(userDataTable);
		tablePanel.add(tableScrollPane);


		//Adds the table panel to the user dashboard
		userDashboard.add(tablePanel, new BorderLayout().CENTER);

		return userDataTable;
	}

	//The method that adds the table to the user dashboard
	public void addTableToWindow(JTable table) {
		JPanel tablePanel = new JPanel();
		JScrollPane tableScrollPane = new JScrollPane(table);
		tablePanel.add(tableScrollPane);

		userDashboard.add(tablePanel);
	}


	//The method that retrieves the table data after the user has edited it and then updates the user data file
	public void editEntry() {
		//Retrieving table data
		String tableData = getTableData();

		//Create a new IV(it changes at each encryption)
		byte[] initializationVector = securityManager.createInitializationVector();

		//Creating the string that represents the name of the user Iv storage file(by concatenating the user name and the suffix "-Iv")
		String suffix = "-Iv";
		String userIvFileName = userFileName + suffix;

		//File containing the initialization vector used for decryption(it should probably be used directly by the restoreInitializationVector method)
		File ivSource =  new File(appDataPath + "/security/iv/" + userIvFileName);

		//The file used to write the encryption data(the name should be taken from the value passed to fillTable method)
		File userDataFile = new File(appDataPath + "/userData/Razvan");

		//Secret key retrieval and encryption of the table data	
		SecretKey secretKey = securityManager.retrieveSecretKey(userFileName);
		byte[] encryptedData = securityManager.performAESEncryption(tableData, secretKey, initializationVector);

		//Null check to make sure the encryption process was successful
		if(encryptedData != null) {
			//First the data is written to the user data file
			fileManager.writeEncryptedData(userDataFile, encryptedData);

			//Then the initializaion vector is stored in the file
			securityManager.storeInitializationVector(ivSource, initializationVector);
		}

	}

	//The method that retrieves the table data after the user has inserted a new row and then updates the user data file
	public void addNewEntry(String tableData) {
		DefaultTableModel dtm = (DefaultTableModel) userDataTable.getModel();

		//String tableData = getTableData();
		String userDataFolder = "/userData/";

		File userDataFile = new File(appDataPath + userDataFolder + userFileName);
		//File ivSource = new File(appDataPath + "/security/iv_storage");

		//Creating the string that represents the name of the user Iv storage file(by concatenating the user name and the suffix "-Iv")
		String suffix = "-Iv";
		String userIvFileName = userFileName + suffix;

		//Creating the string that represents the name of the user Iv storage file(by concatenating the user name and the suffix "-Iv")
		File ivSource =  new File(appDataPath + "/security/iv/" + userIvFileName);


		SecretKey secretKey = securityManager.retrieveSecretKey(userFileName);
		byte[] initializationVector = securityManager.createInitializationVector();

		//Table data encryption
		byte[] encryptedData = securityManager.performAESEncryption(tableData, secretKey, initializationVector);

		if (encryptedData != null) {
			fileManager.writeEncryptedData(userDataFile, encryptedData);
			securityManager.storeInitializationVector(ivSource, initializationVector);
		}


		//		String fileName = "Razvan";
		//		fileManager.writeData(fileName, data);

	}

	/*
	 * The method that is used to delete a table entry. It receives a String
	 * containing the table data(except the deleted row) as parameter.The string
	 * data is overwritten in the user data file.
	 */
	public void removeEntry(String tableData) {
		DefaultTableModel dtm = (DefaultTableModel) userDataTable.getModel();

		//String tableData = getTableData();
		String userDataFolder = "/userData/";

		File userDataFile = new File(appDataPath + userDataFolder + userFileName);
		//File ivSource = new File(appDataPath + "/security/iv_storage");

		//Creating the string that represents the name of the user Iv storage file(by concatenating the user name and the suffix "-Iv")
		String suffix = "-Iv";
		String userIvFileName = userFileName + suffix;

		//File containing the initialization vector used for decryption(it should probably be used directly by the restoreInitializationVector method)
		File ivSource =  new File(appDataPath + "/security/iv/" + userIvFileName);


		SecretKey secretKey = securityManager.retrieveSecretKey(userFileName);
		byte[] initializationVector = securityManager.createInitializationVector();


		byte[] encryptedData = securityManager.performAESEncryption(tableData, secretKey, initializationVector);

		if (encryptedData != null) {
			fileManager.writeEncryptedData(userDataFile, encryptedData);
			securityManager.storeInitializationVector(ivSource, initializationVector);
		}

	}

	/*
	 * The method used for adding a MouseListener to the table so that when the user
	 * right clicks the mouse, a menu containing the options "Copy" and "Delete" will
	 * appear
	 */
	public void addMouseListenerToTable() {
		JPopupMenu popupMenu = new JPopupMenu();

		JMenuItem copyCellDataOption = new JMenuItem("Copy");
		JMenuItem editRowOption = new JMenuItem("Edit");
		JMenuItem deleteRowOption = new JMenuItem("Delete");


		popupMenu.add(copyCellDataOption);
		popupMenu.add(editRowOption);
		popupMenu.add(deleteRowOption);

		userDataTable.addMouseListener(new MouseAdapter(){

			public void mouseReleased(MouseEvent e) {
				//Checking if a table row was selected and a right click was performed on it
				if (userDataTable.getSelectedRow() >= 0 && e.getButton() == MouseEvent.BUTTON3) {
					if(e.isPopupTrigger()) {
						popupMenu.show(e.getComponent(), e.getX(), e.getY());
					}
					/*
					 * Checking if a left click was performed on a table cell.In this case the value
					 * contained in the cell is saved in a variable so that it can later be restored
					 * if the user decides to cancel the changes performed to the cell value.
					 */
				} else if (e.getButton() == MouseEvent.BUTTON1) {
					JTable table = (JTable) e.getSource();
					oldCellValue = table.getValueAt(table.getSelectedRow(), table.getSelectedColumn()).toString();
				}
			}
		});

		addMouseListenerToCopyOption(copyCellDataOption, userDataTable.getSelectedRow());
		addMouseListenerToEditOption(editRowOption, userDataTable.getSelectedRow());
		addMouseListenerToDeleteOption(deleteRowOption, userDataTable.getSelectedRow());
	}

	//Method for adding a TableModelListener which is responsible for managing the changes performed to the table data
	public void addTableModelListener() {
		DefaultTableModel dtm = (DefaultTableModel) userDataTable.getModel();
		TableModelListener tableListener = new TableModelListener() {

			public void tableChanged(TableModelEvent e) {

				if (e.getType() == TableModelEvent.UPDATE && !hasCopiedCellContent) {

//					if (hasCanceledChange) {
//						hasCanceledChange = false;
//						return;
//					}
//
//					DefaultTableModel dtm = (DefaultTableModel) userDataTable.getModel();
//
//
//					userOption = JOptionPane.showConfirmDialog(null,"Do you want to save the changes?","Data saving",JOptionPane.YES_NO_OPTION);
//
//					//Checks if the modified date is correct(from the value perspective) and has the right format after the user has changed it
//					String modifiedDate =(String) dtm.getValueAt(e.getFirstRow(), e.getColumn());
//					if(!GUIInputChecker.isValidDate(modifiedDate, "dd-MM-yyyy")) {
//						JOptionPane.showMessageDialog(userDashboard, "Invalid date and/or format! The date must have the format 'dd-mm-yyyy'.", "User dashboard", JOptionPane.WARNING_MESSAGE);
//						//Sets the flag to true as the change will not be saved due to the incorrect date format
//						hasCanceledChange = true;
//						//Sets the cell content to its old value since the user provided data is incorrect
//						dtm.setValueAt(oldCellValue, e.getFirstRow(), e.getColumn());
//						return;
//					}
//
//
//					if (userOption == 0) {
//						editEntry();
//					} else {
//						hasCanceledChange = true;
//						System.out.println("NO option selected");
//						dtm.setValueAt(oldCellValue, e.getFirstRow(), e.getColumn());
//						System.out.println("Changed row :" +  e.getFirstRow() +"\n" + "Changed column:" + e.getColumn());;
//					}
//
//					System.out.println("Table was updated!");
					//editEntry();

				} else if (e.getType() == TableModelEvent.INSERT) {

					if (userOption == 0) {
						//System.out.println("User option for insertion " + userOption);
						addNewEntry(getTableData());
					}

				} else if (e.getType() == TableModelEvent.DELETE) {
					removeEntry(getTableData());
				}
			}
		};

		dtm.addTableModelListener(tableListener);
	}

	//Adding MouseListener for the copy option displayed when right clicking on a table cell
	public void addMouseListenerToCopyOption(JMenuItem popupItem, int entryNumber) {
		popupItem.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				DefaultTableModel dtm = (DefaultTableModel) userDataTable.getModel();

				String dataToCopy = (String) dtm.getValueAt(userDataTable.getSelectedRow(), userDataTable.getSelectedColumn());
				//System.out.println(dataToCopy);

				//Code for copying selected cell content to clipboard
				StringSelection stringSelection = new StringSelection(dataToCopy);
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(stringSelection, null);
				hasCopiedCellContent = true;
			}
		});
	}
	public void addMouseListenerToEditOption(JMenuItem popupItem, int entryNumber) {
		popupItem.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
//				userOption = JOptionPane.showConfirmDialog(null, "Are you sure that you want to edit the selected record?", "Edit confirmation", JOptionPane.YES_NO_OPTION);
//
//				if(userOption == 0) {
//
//				}
				if (editEventListener != null) {
					AccountRecord accountRecord = getSelectedAccountRecord();
					editEventListener.onEdit(accountRecord);
				}

			}
		});
	}

	//Adding MouseListener for the delete option displayed when right clicking on a table cell
	public void addMouseListenerToDeleteOption(JMenuItem popupItem, int entryNumber) {

		popupItem.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				//removeEntry(getSelectedRowData());
				DefaultTableModel dtm = (DefaultTableModel) userDataTable.getModel();

				userOption = JOptionPane.showConfirmDialog(null, "Are you sure that you want to delete the selected row?", "Delete confirmation", JOptionPane.YES_NO_OPTION);

				if (userOption == 0) {
					dtm.removeRow(userDataTable.getSelectedRow());
				}

			}
		});
	}

	/*
	 * Method used for retrieving table data. The string stores the data in the following format:
	 * -the cell data contained in a row is separated by a ","
	 * -each row is separated by a newline ("\n") character
	 */
	public String getTableData() {
		DefaultTableModel dtm = (DefaultTableModel) userDataTable.getModel();
		StringBuilder sb = new StringBuilder();
		int numRows =  dtm.getRowCount();
		int numCols = dtm.getColumnCount();


		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < numCols; j++) {
				if (j == numCols - 1) {
					sb.append(dtm.getValueAt(i, j));
					sb.append("\n");
					break;
				}
				sb.append(dtm.getValueAt(i, j) + ",");
			}
		}

		return sb.toString();
	}

	//The method that returns the values from the selected row
//	public String getSelectedRowData() {
//		StringBuilder sb = new StringBuilder();
//		int row = userDataTable.getSelectedRow();
//		int columnCount = userDataTable.getColumnCount();
//
//		for (int i = 0; i < columnCount; i++) {
//			if (i == columnCount - 1) {
//				sb.append(userDataTable.getValueAt(row, i));
//				break;
//			}
//			sb.append(userDataTable.getValueAt(row, i) + "||");
//		}
//
//		return sb.toString();
//	}

	public AccountRecord getSelectedAccountRecord() {
		int row = userDataTable.getSelectedRow();

		int accountNameColumnIndex = 0;
		int usernameColumnIndex= 1;
		int passwordColumnIndex = 2;
		int lastChangeDateColumnIndex = 3;

		AccountRecord selectedAccount = new AccountRecord();
		selectedAccount.setAccountName(userDataTable.getValueAt(row, accountNameColumnIndex).toString());
		selectedAccount.setUsername(userDataTable.getValueAt(row, usernameColumnIndex).toString());
		selectedAccount.setPassword(userDataTable.getValueAt(row, passwordColumnIndex).toString());
		selectedAccount.setLastChangeDate(userDataTable.getValueAt(row, lastChangeDateColumnIndex).toString());

		return selectedAccount;
	}


	//Sets the table cell renderer so that the expired passwords are highlighted
	private void setTableCellRendererForExpiredPasswordHighlight(JTable table) {
		Objects.requireNonNull(table, "The table object provided for setting the cell renderer cannot be null.");

		table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
			@Override
			public Component getTableCellRendererComponent(JTable table,
														   Object value, boolean isSelected, boolean hasFocus, int row, int col) {

				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

				String status = (String)table.getModel().getValueAt(row, 3);
				DefaultTableModel dtm = getUserDataTableModel();
				String passwordDateFormat = "dd-MM-yyyy";


				int passwordDateColumnIndex = 3;
				//The number of days from creation after which the password is considered expired
				int daysSinceCreationLimit = 180;


				String passwordDate = (String) dtm.getValueAt(row, passwordDateColumnIndex);
				boolean isExpiredPassword = false;
				boolean wasChecked = false;

				try {
					isExpiredPassword = GUIInputChecker.isExpiredPassword(passwordDate, passwordDateFormat, daysSinceCreationLimit);
					wasChecked = true;

				} catch (Exception ex) {
					ex.printStackTrace();
				}


				if (isExpiredPassword && wasChecked) {
					//Sets the RED/WHITE color scheme for highlighting expired passwords
					setBackground(Color.RED);
					setForeground(Color.WHITE);
				} else {
					//Sets WHITE/BLACK standard color scheme if the password is not expired
					setBackground(table.getBackground());
					setForeground(table.getForeground());
				}
				return this;
			}
		});
	}

	public int updateSelectedRow(AccountRecord accountRecord) {
		if (accountRecord == null) {
			return -1;
		}

		int selectedRowIndex = userDataTable.getSelectedRow();
		userDataTable.setValueAt(accountRecord.getAccountName(), selectedRowIndex, 0);
		userDataTable.setValueAt(accountRecord.getUsername(),selectedRowIndex, 1);
		userDataTable.setValueAt(accountRecord.getPassword(),selectedRowIndex, 2);
		userDataTable.setValueAt(accountRecord.getLastChangeDate(),selectedRowIndex, 3);

		return 0;
	}


	public DefaultTableModel getUserDataTableModel() {
		return (DefaultTableModel) userDataTable.getModel();
	}

	public JTable getUserDataTable() {
		return this.userDataTable;
	}

	public boolean getHasRequestedRowEdit() {
		return hasRequestedRowEdit;
	}

	public void setHasRequestedRowEdit(boolean state) {
		hasRequestedRowEdit = state;
	}

	public void setEditEventListener(EditEventListener editEventListener) {
		this.editEventListener = editEventListener;
	}
}

