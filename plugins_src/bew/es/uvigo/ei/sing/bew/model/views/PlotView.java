package es.uvigo.ei.sing.bew.model.views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.jfree.chart.ChartPanel;

import es.uvigo.ei.sing.bew.constants.I18n;
import es.uvigo.ei.sing.bew.constants.PlotFunctions;
import es.uvigo.ei.sing.bew.constants.ShowDialog;
import es.uvigo.ei.sing.bew.exceptions.NoTimeException;
import es.uvigo.ei.sing.bew.model.Plot;

/**
 * This class lets the user visualize a Plot.
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
public class PlotView extends JPanel {

	private static final long serialVersionUID = 1L;

	private Plot plot;

	/**
	 * Default Constructor
	 * 
	 * @param plot
	 *            Plot to view
	 */
	public PlotView(Plot plot) {
		// TODO Auto-generated constructor stub
		super();

		this.plot = plot;

		try {
			initialize();
		} catch (NoTimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Method to initialize the dialog
	 * 
	 * @throws NoTimeException
	 */
	public void initialize() throws NoTimeException {
		setLayout(new BorderLayout(0, 0));

		// Do the plot again in order to set the JFreeChart in the panel
		final ChartPanel chartPanel = new ChartPanel(
				PlotFunctions.createJFreeChart(plot.getValues(),
						plot.getKeys(), plot.getParent(), plot.getType(),
						plot.getTitle(), plot.getyAxisTitle(),
						plot.getConditions()));
		chartPanel.setBorder(new TitledBorder(null, "Plot",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		chartPanel.setMouseWheelEnabled(true);

		this.add(chartPanel, BorderLayout.CENTER);

		JPanel buttonPane = new JPanel();
		buttonPane
				.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		FlowLayout flButtonPane = (FlowLayout) buttonPane.getLayout();
		flButtonPane.setHgap(20);
		add(buttonPane, BorderLayout.SOUTH);

		JButton btnSaveAs = new JButton(I18n.get("saveAs"));
		btnSaveAs.setIcon(new ImageIcon(PlotView.class
				.getResource("/img/picture.png")));
		btnSaveAs.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					chartPanel.doSaveAs();
				} catch (IOException e) {
					ShowDialog.showError(I18n.get("errorSavingTitle"),
							I18n.get("errorSaving"));
				}
			}
		});
		buttonPane.add(btnSaveAs);

		JButton btnProperties = new JButton(I18n.get("properties"));
		btnProperties.setIcon(new ImageIcon(PlotView.class
				.getResource("/img/properties.png")));
		btnProperties.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chartPanel.doEditChartProperties();
			}
		});
		buttonPane.add(btnProperties);

		JButton btnPrint = new JButton(I18n.get("print"));
		btnPrint.setIcon(new ImageIcon(PlotView.class
				.getResource("/img/print.png")));
		btnPrint.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chartPanel.createChartPrintJob();
			}
		});
		buttonPane.add(btnPrint);
	}
}
