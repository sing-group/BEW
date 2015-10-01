package es.uvigo.ei.sing.bew.view.dialogs;

import java.awt.GridLayout;
import java.awt.image.BufferedImage;

import javax.swing.JDialog;
import javax.swing.WindowConstants;

import es.uvigo.ei.sing.bew.model.Statistic;
import es.uvigo.ei.sing.bew.view.components.CustomDialog;
import es.uvigo.ei.sing.bew.view.panels.StatisticPlotPopupPanel;

/**
 * Custom dialog for showing the statistic pop-up.
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
public class StatisticPlotPopupDialog extends CustomDialog {

	private static final long serialVersionUID = 1L;

	private StatisticPlotPopupPanel plotPanel;
	private JDialog parent;

	/**
	 * Default constructor with a parent.
	 * 
	 * @param parent
	 *            JDialog parent.
	 * @param statistic
	 *            Statistic to take the data.
	 * @param showLegend
	 *            Boolean to show the legend.
	 * @param isPopup
	 *            Boolean to indicate if the dialog is a popup.
	 */
	public StatisticPlotPopupDialog(JDialog parent, Statistic statistic,
			boolean showLegend, boolean isPopup) {
		super(parent);

		this.parent = parent;

		init(showLegend, statistic, isPopup);
	}

	/**
	 * Default constructor without a parent.
	 * 
	 * @param statistic
	 *            Statistic to take the data.
	 * @param showLegend
	 *            oolean to show the legend.
	 * @param isPopup
	 *            Boolean to indicate if the dialog is a popup.
	 */
	public StatisticPlotPopupDialog(Statistic statistic, boolean showLegend,
			boolean isPopup) {
		super();

		this.parent = null;

		init(showLegend, statistic, isPopup);
	}

	/**
	 * Method to initialize dialog.
	 * 
	 * @param showLegend
	 * @param statistic
	 * @param isPopup
	 */
	private void init(boolean showLegend, Statistic statistic, boolean isPopup) {
		setResizable(true);
		setModalityType(ModalityType.MODELESS);
		setAlwaysOnTop(true);
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		setTitle("Plot dialog");
		getContentPane().setLayout(new GridLayout());

		// plotPanel = new StatisticPlotPopupPanel(statistic, showLegend,
		// isPopup);
		// For now always showing the legends
		plotPanel = new StatisticPlotPopupPanel(statistic, true, isPopup);
		getContentPane().add(plotPanel);

		setSize(plotPanel.getWidth(), plotPanel.getHeight());

		setLocationRelativeTo(null);
	}

	/**
	 * Method to set a new image on the pop-up.
	 * 
	 * @param image
	 *            New BufferedImage.
	 */
	public void setNewImage(BufferedImage image) {
		plotPanel.setNewImage(image);
	}
}