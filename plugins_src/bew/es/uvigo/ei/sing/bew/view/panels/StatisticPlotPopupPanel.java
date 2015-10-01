package es.uvigo.ei.sing.bew.view.panels;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import es.uvigo.ei.sing.bew.constants.FunctionConstants;
import es.uvigo.ei.sing.bew.constants.I18n;
import es.uvigo.ei.sing.bew.model.Statistic;
import es.uvigo.ei.sing.bew.tables.RowNumberTable;
import es.uvigo.ei.sing.bew.tables.renderer.StripeTableCellRender;

/**
 * Custom dialog for showing the statistic pop-up.
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
public class StatisticPlotPopupPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private Statistic statistic;

	private JLabel lblImage;
	private JTable condTable;

	private BufferedImage image;

	/**
	 * Default constructor without a parent.
	 * 
	 * @param statistic
	 *            Statistic to take the data.
	 * @param showLegend
	 *            Boolean to show the legend.
	 * @param isPopup
	 *            Boolean to indicate if the dialog is a popup.
	 */
	public StatisticPlotPopupPanel(Statistic statistic, boolean showLegend,
			boolean isPopup) {
		super();

		this.statistic = statistic;

		try {
			init(showLegend, isPopup);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Method to initialize dialog.
	 * 
	 * @param showLegend
	 *            Boolean, if true the method shows the legend.
	 * @param isPopup
	 *            Boolean to indicate if the dialog is a popup.
	 * @throws IOException
	 */
	private void init(boolean showLegend, boolean isPopup) throws IOException {
		setLayout(new GridLayout());

		this.image = FunctionConstants.byteToBufferedImg(statistic
				.getStatisticsPlot());

		// If the image is for non popup dialog we resize it
		if (!isPopup) {
			int heigth = (int) (this.image.getHeight() * 0.75);
			int width = (int) (this.image.getWidth() * 0.75);
			this.image = FunctionConstants.imageResize(image, width, heigth,
					RenderingHints.VALUE_INTERPOLATION_BICUBIC, true);
		}

		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.0);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);

		add(splitPane);

		lblImage = new JLabel(new ImageIcon(this.image));
		splitPane.setLeftComponent(lblImage);

		if (showLegend) {
			condTable = new JTable(new DefaultTableModel(new Object[][] {},
					new String[] { I18n.get("plotConditions"),
							I18n.get("realConditions") }));
			StripeTableCellRender renderer = new StripeTableCellRender();
			condTable.setDefaultRenderer(Object.class, renderer);

			//
			fillCondTable();

			JScrollPane scrollTable = new JScrollPane(condTable);
			// Rows header
			RowNumberTable rowTable = new RowNumberTable(condTable);

			scrollTable.setRowHeaderView(rowTable);
			scrollTable.setCorner(JScrollPane.UPPER_LEFT_CORNER,
					rowTable.getTableHeader());
			splitPane.setRightComponent(scrollTable);

			setSize(new Dimension(this.image.getWidth(),
					this.image.getHeight() + 200));
		} else {
			JPanel emptyPanel = new JPanel();
			splitPane.setRightComponent(emptyPanel);

			splitPane.setDividerLocation(1.0);

			setSize(new Dimension(this.image.getWidth(), this.image.getHeight()));
		}
	}

	/**
	 * Method to set a new image on the pop-up.
	 * 
	 * @param image
	 *            New BufferedImage.
	 */
	public void setNewImage(BufferedImage image) {
		lblImage.setIcon(new ImageIcon(image));
		repaint();
		revalidate();
	}

	/**
	 * 
	 */
	private void fillCondTable() {
		Set<Object> keys = this.statistic.getConditionMeasurements().keySet();
		String[] cond = this.statistic.getCond();
		Iterator<Object> ite = keys.iterator();

		DefaultTableModel mtm = (DefaultTableModel) condTable.getModel();

		Object[] row = new Object[condTable.getColumnCount()];
		int index = 0;
		while (ite.hasNext()) {
			row[0] = cond[index];
			row[1] = ite.next();

			mtm.addRow(row);

			index++;
		}
	}
}