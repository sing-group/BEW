package es.uvigo.ei.sing.bew.view.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import es.uvigo.ei.sing.bew.constants.I18n;
import es.uvigo.ei.sing.bew.view.components.TextPreviewer;

/**
 * Custom panel to do a previsualization of the selected XML in during a load
 * operation.
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
public class PreviewPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private TextPreviewer previewer;

	/**
	 * Default constructor.
	 */
	public PreviewPanel() {
		super();

		this.previewer = new TextPreviewer();

		initialize();
	}

	/**
	 * Initializes the dialog.
	 */
	public void initialize() {
		JLabel label = new JLabel(I18n.get("textPrev"), SwingConstants.CENTER);
		setPreferredSize(new Dimension(500, 0));
		setBorder(BorderFactory.createEtchedBorder());

		setLayout(new BorderLayout());

		label.setBorder(BorderFactory.createEtchedBorder());
		add(label, BorderLayout.NORTH);
		add(previewer, BorderLayout.CENTER);
	}

	/**
	 * 
	 * @return
	 */
	public TextPreviewer getPreviewer() {
		return previewer;
	}
}
