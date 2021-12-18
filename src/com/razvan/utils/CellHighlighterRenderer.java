package com.razvan.utils;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public class CellHighlighterRenderer extends DefaultTableCellRenderer {
	private String passwordDate;
	private String passwordDateFormat;

	public CellHighlighterRenderer(String passwordDate, String passwordDateFormat) {
		this.passwordDate = passwordDate;
		this.passwordDateFormat = passwordDateFormat;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		int daysSinceCreationLimit = 180;

		boolean passwordAgeCheckResult = false;
		boolean wasChecked = false;

		try {
			passwordAgeCheckResult = GUIInputChecker.isExpiredPassword(passwordDate, passwordDateFormat, daysSinceCreationLimit);
			wasChecked = true;

		} catch (Exception ex) {
			ex.printStackTrace();
		}


		if (!passwordAgeCheckResult && wasChecked) {
			cell.setBackground(Color.RED);
			setOpaque(true);
			repaint();
		}
		
		
	
		return cell;

	}
	

}
