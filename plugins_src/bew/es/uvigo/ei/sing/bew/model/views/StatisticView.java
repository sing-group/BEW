package es.uvigo.ei.sing.bew.model.views;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.border.EtchedBorder;

import es.uvigo.ei.sing.bew.constants.I18n;
import es.uvigo.ei.sing.bew.constants.ShowDialog;
import es.uvigo.ei.sing.bew.constants.ShowProgressBar;
import es.uvigo.ei.sing.bew.constants.StatisticFunctions;
import es.uvigo.ei.sing.bew.files.filters.PngSaveFilter;
import es.uvigo.ei.sing.bew.model.Statistic;
import es.uvigo.ei.sing.bew.view.components.CustomFileChooser;
import es.uvigo.ei.sing.bew.view.dialogs.StatisticPlotPopupDialog;
import es.uvigo.ei.sing.bew.view.panels.StatisticPlotPopupPanel;

/**
 * This class lets the user visualize a Statistic.
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
public class StatisticView extends JPanel {

	private static final long serialVersionUID = 1L;

	private JTextArea textArea;
	private JLabel lblInformation;

	private Statistic statistic;
	private String savePath;

	private JPanel textPanel;
	private JButton btnShowPlot;
	private StatisticPlotPopupDialog plotPopup;
	private JButton btnPng;

	/**
	 * Default constructor.
	 * 
	 * @param statistic
	 *            Statistic to view.
	 */
	public StatisticView(Statistic statistic) {
		// TODO Auto-generated constructor stub
		super();

		this.statistic = statistic;

		initialize();
	}

	/**
	 * Method to initialize the dialog.
	 */
	private void initialize() {
		setLayout(new BorderLayout(0, 0));
		{
			// Text Panel
			textPanel = new JPanel();
			add(textPanel, BorderLayout.CENTER);
			textPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null,
					null));
			GridBagLayout gblTextP = new GridBagLayout();
			gblTextP.columnWidths = new int[] { 100, 300 };
			gblTextP.rowHeights = new int[] { 50, 50, 50, 100 };
			gblTextP.columnWeights = new double[] { 1.0, 1.0, 1.0 };
			gblTextP.rowWeights = new double[] { 1.0, 1.0, 1.0, 1.0 };
			textPanel.setLayout(gblTextP);

			{
				lblInformation = new JLabel(statistic.getType() + ", "
						+ statistic.getName());
				GridBagConstraints gbcLblInfo = new GridBagConstraints();
				gbcLblInfo.gridwidth = 2;
				gbcLblInfo.insets = new Insets(0, 0, 5, 0);
				gbcLblInfo.gridx = 0;
				gbcLblInfo.gridy = 0;
				textPanel.add(lblInformation, gbcLblInfo);
			}
			{
				textArea = new JTextArea();
				textArea.setEditable(false);
				textArea.setLineWrap(true);
				textArea.setWrapStyleWord(true);

				JScrollPane scrollPane = new JScrollPane(textArea);
				GridBagConstraints gbcScrollP = new GridBagConstraints();
				gbcScrollP.gridwidth = 2;
				gbcScrollP.gridheight = 3;
				gbcScrollP.fill = GridBagConstraints.BOTH;
				gbcScrollP.insets = new Insets(0, 50, 50, 50);
				gbcScrollP.gridx = 0;
				gbcScrollP.gridy = 1;
				textPanel.add(scrollPane, gbcScrollP);
			}
			{
				GridBagConstraints gbc = new GridBagConstraints();
				gbc.gridx = 2;
				gbc.gridheight = 4;
				gbc.gridy = 0;
				gbc.fill = GridBagConstraints.BOTH;
				textPanel.add(new StatisticPlotPopupPanel(statistic,
						StatisticFunctions.showLegend(statistic.getName()),
						false), gbc);
			}
		}

		{
			JPanel bottomPane = new JPanel();
			add(bottomPane, BorderLayout.SOUTH);

			{
				JButton btnSavePlot = new JButton(I18n.get("saveTestHTML"));
				btnSavePlot.setIcon(new ImageIcon(StatisticView.class
						.getResource("/img/reporting.png")));
				bottomPane.add(btnSavePlot);

				btnSavePlot.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						savePath = "";
						try {
							// Get user destination path
							savePath = saveAsHTML();

							if (!savePath.isEmpty()) {
								// Execute Swing Worker
								new ShowProgressBar(new HeavyTask());
							}
						} catch (IOException e) {
							ShowDialog.showError(I18n.get("errorSavingTitle"),
									I18n.get("errorSaving"));
						} catch (NullPointerException e) {
							// TODO: handle exception
							StatisticFunctions
									.createTestReportInFolder(statistic);
							// Create HTML report
							StatisticFunctions.createTestReport(statistic,
									savePath);
						}

					}
				});
			}

			{
				btnPng = new JButton("Save Plot in PNG");
				btnPng.setIcon(new ImageIcon(StatisticView.class
						.getResource("/img/picture.png")));

				btnPng.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						// Get user destination path
						try {
							CustomFileChooser cfc = new CustomFileChooser();

							cfc.setDialogTitle("Save Statistic Plot in PNG");
							cfc.setFileFilter(new PngSaveFilter());
							cfc.setAcceptAllFileFilterUsed(false);

							// We open a save dialog
							int retrieval = cfc.showSaveDialog(null);

							// If selected file is xml
							if (retrieval == JFileChooser.APPROVE_OPTION) {
								String path = cfc.getSelectedFile()
										.getAbsolutePath();

								if (!path.endsWith(".png"))
									path = path.concat(".png");

								InputStream in = new ByteArrayInputStream(
										statistic.getStatisticsPlot());
								BufferedImage bImageFromConvert;
								bImageFromConvert = ImageIO.read(in);

								ImageIO.write(bImageFromConvert, "png",
										new File(path));
							}
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				});

				bottomPane.add(btnPng);
			}

			{
				btnShowPlot = new JButton("Show Plot");
				btnShowPlot.setIcon(new ImageIcon(StatisticView.class
						.getResource("/img/visualize.png")));
				bottomPane.add(btnShowPlot);

				btnShowPlot.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						if (plotPopup != null) {
							if (!plotPopup.isVisible()) {
								plotPopup.setVisible(true);
							}
						} else {
							plotPopup = new StatisticPlotPopupDialog(statistic,
									true, true);
							plotPopup.setVisible(true);
						}
					}
				});
			}
		}

		// Put Statistic Output in the text area
		writeText();
	}

	/**
	 * Method to write the textual result.
	 */
	private void writeText() {
		List<String> sText = this.statistic.getStatisticsResult();

		for (String s : sText) {
			this.textArea.append(s + "\n");
		}
	}

	/**
	 * Method to save in a file the graphic result.
	 * 
	 * @throws IOException
	 */
	private String saveAsHTML() throws IOException {
		String path = "";
		CustomFileChooser cfc = new CustomFileChooser();

		// Select directory
		cfc.setAcceptAllFileFilterUsed(false);
		cfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		// We open a save dialog
		int retrieval = cfc.showSaveDialog(this);
		if (retrieval == JFileChooser.APPROVE_OPTION) {
			path = cfc.getSelectedFile().getAbsolutePath();
		}

		return path;
	}

	/**
	 * Swing worker to do heavy operations (Generate HTML Report for a
	 * Statistic).
	 * 
	 * @author Gael Pérez Rodríguez
	 * 
	 */
	private class HeavyTask extends SwingWorker<Void, Void> {
		@Override
		public Void doInBackground() {
			// If user don't pre generate the html, the program must
			// do now
			if (statistic.getTempHTMLDir().isEmpty()) {
				StatisticFunctions.createTestReportInFolder(statistic);
				// Create HTML report
				StatisticFunctions.createTestReport(statistic, savePath);
			} else {
				// Create HTML report
				StatisticFunctions.createTestReport(statistic, savePath);
			}
			return null;
		}

		@Override
		public void done() {
			Toolkit.getDefaultToolkit().beep();
			setProgress(100);
		}
	}

}
