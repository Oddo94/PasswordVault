package com.razvan.application_runner;

import com.razvan.gui.LoginWindow;
import com.razvan.installation_manager.ApplicationInstallManager;

import javax.swing.*;

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
