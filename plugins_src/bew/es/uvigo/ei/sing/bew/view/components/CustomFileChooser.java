package es.uvigo.ei.sing.bew.view.components;

import java.awt.Component;
import java.awt.HeadlessException;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;

public class CustomFileChooser extends JFileChooser {

	private static final long serialVersionUID = 1L;

	@Override
	protected JDialog createDialog(Component parent) throws HeadlessException {
		JDialog dialog = super.createDialog(parent);
		ImageIcon image = new ImageIcon(
				CustomFileChooser.class.getResource("/img/favicon.png"));
		dialog.setIconImage(image.getImage());

		return dialog;
	}
}
