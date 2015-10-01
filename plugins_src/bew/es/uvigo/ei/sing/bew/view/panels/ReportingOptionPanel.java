package es.uvigo.ei.sing.bew.view.panels;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import es.uvigo.ei.sing.bew.model.Method;
import es.uvigo.ei.sing.bew.model.Plot;
import es.uvigo.ei.sing.bew.model.Statistic;

/**
 * Custom panel to show some options in the reporting dialog.
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
public class ReportingOptionPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private ListsPanel panelStat;

	private Method parent;
	// private Map<Method, List<Plot>> mapMethodPlots;
	private Map<String, Plot> mapPlotName;
	// private Map<Method, List<Statistic>> mapMethodStatistics;
	private Map<String, Statistic> mapStatisticName;

	private ListsPanel listPanelPlots;

	/**
	 * Default constructor.
	 * 
	 * @param parent
	 *            Method parent.
	 */
	public ReportingOptionPanel(Method parent) {
		super();

		this.parent = parent;
		// this.mapMethodPlots = new HashMap<Method, List<Plot>>();
		this.mapPlotName = new HashMap<String, Plot>();
		// this.mapMethodStatistics = new HashMap<Method, List<Statistic>>();
		this.mapStatisticName = new HashMap<String, Statistic>();

		init();
	}

	/**
	 * Initializes the dialog.
	 */
	private void init() {
		setLayout(new GridLayout(0, 1, 0, 0));

		{
			JPanel panelPlots = new JPanel();
			panelPlots.setBorder(new TitledBorder(UIManager
					.getBorder("TitledBorder.border"), "Plots reporting",
					TitledBorder.LEADING, TitledBorder.TOP, null, null));
			add(panelPlots);
			panelPlots.setLayout(new GridLayout(0, 1, 0, 0));
			{
				listPanelPlots = new ListsPanel("Available Plots",
						"Selected Plots", false);
				listPanelPlots.getSelectedList().setToolTipText("Remove plots from the report.");
				listPanelPlots.getAvailableList().setToolTipText("Select the plots to include in the report.");
				panelPlots.add(listPanelPlots);

				// Add plots
				addPlotsToList();
			}
		}

		{
			JPanel panelStatistics = new JPanel();
			panelStatistics.setBorder(new TitledBorder(UIManager
					.getBorder("TitledBorder.border"), "Statistics reporting",
					TitledBorder.LEADING, TitledBorder.TOP, null, null));
			add(panelStatistics);
			panelStatistics.setLayout(new GridLayout(1, 0, 0, 0));
			{
				panelStat = new ListsPanel("Available Statistics",
						"Selected Statistics", false);
				panelStat.getSelectedList().setToolTipText("Remove statistics from the report.");
				panelStat.getAvailableList().setToolTipText("Select the statistics to include in the report.");
				panelStatistics.add(panelStat);

				// Add Statistics
				addStatisticsToList();
			}
		}
	}

	/**
	 * Add method plots to the list.
	 */
	private void addPlotsToList() {
		List<String> plotNames = new ArrayList<String>();

		List<Plot> plots = parent.getArrayPlots().getElements();
		for (Plot p : plots) {
			plotNames.add(p.getNameClipboard());
			mapPlotName.put(p.getNameClipboard(), p);
		}

		if (!plotNames.isEmpty())
			listPanelPlots.addStringsToList(plotNames);
	}

	/**
	 * Add method statistics to the list.
	 */
	private void addStatisticsToList() {
		List<String> statisticsNames = new ArrayList<String>();

		List<Statistic> statistics = parent.getArrayStatistics().getElements();
		for (Statistic s : statistics) {
			statisticsNames.add(s.getNameClipboard());
			mapStatisticName.put(s.getNameClipboard(), s);
		}

		if (!statisticsNames.isEmpty())
			panelStat.addStringsToList(statisticsNames);
	}

	/**
	 * 
	 * @return
	 */
	public List<Plot> getSelectedPlots() {
		// List with <Plot>
		List<Plot> toRet = new ArrayList<Plot>();
		List<Object> selectedPlots = listPanelPlots.getAllSelectedValues();

		String name;
		// Go over selected Plot names
		for (Object value : selectedPlots) {
			name = value.toString();
			toRet.add(mapPlotName.get(name));
		}

		return toRet;
	}

	/**
	 * 
	 * @return
	 */
	public List<Statistic> getSelectedStatistics() {
		// List with <Statistic>
		List<Statistic> toRet = new ArrayList<Statistic>();
		List<Object> selStat = panelStat.getAllSelectedValues();

		String name;
		// Go over selected Statistic names
		for (Object value : selStat) {
			name = value.toString();
			toRet.add(mapStatisticName.get(name));
		}

		return toRet;
	}
}
