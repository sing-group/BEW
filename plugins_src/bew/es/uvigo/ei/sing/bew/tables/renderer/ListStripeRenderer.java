package es.uvigo.ei.sing.bew.tables.renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 * Custom JList renderer to do a stripe for each element.
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
public class ListStripeRenderer extends DefaultListCellRenderer {

	private static final long serialVersionUID = 1L;

	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		JLabel label = (JLabel) super.getListCellRendererComponent(list, value,
				index, isSelected, cellHasFocus);

		if (index % 2 == 0) {
			if (!list.isSelectedIndex(index)) {
				label.setBackground(new Color(240, 234, 228));
				label.setForeground(Color.BLACK);
			} else {
				label.setBackground(new Color(204, 255, 255));
				label.setForeground(Color.BLACK);
			}
		}

		return label;
	}
}