package com.razvan.application_runner;
import java.util.*;

import javax.swing.SwingUtilities;

import com.razvan.gui.LoginWindow;
import com.razvan.gui.UserDashboard;
import com.razvan.installation_manager.ApplicationInstallManager;

public class PasswordVault {

	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (!ApplicationInstallManager.hasMainAppFolder()) {

					ApplicationInstallManager.createAppFoldersAndFiles();
					ApplicationInstallManager.createKeyStoreFile();
				}

				new LoginWindow();

			}

		});
	}
}
