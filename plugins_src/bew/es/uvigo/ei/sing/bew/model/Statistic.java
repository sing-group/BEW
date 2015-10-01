package es.uvigo.ei.sing.bew.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import es.uvigo.ei.aibench.core.Core;
import es.uvigo.ei.aibench.core.clipboard.ClipboardItem;
import es.uvigo.ei.aibench.core.datatypes.annotation.Datatype;
import es.uvigo.ei.aibench.core.datatypes.annotation.Structure;

/**
 * This class is one of the basic objects with which the application works in
 * memory.
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
@Datatype(structure = Structure.SIMPLE, removeMethod = "remove", namingMethod = "getNameClipboard", renameable = false)
public class Statistic implements Serializable {

	private static final long serialVersionUID = 1L;

	// Clipboard name
	private String clipboardName;

	private String name;
	private String type;
	private List<String> statisticsResult;
	private byte[] statisticsPlot;
	private Map<Object, List<Object>> condMeasur;
	private String[] cond;
	private String result;
	private String tempHTMLDir = "";

	private Method parent;

	/**
	 * Basic constructor.
	 * 
	 * @param parent
	 *            Method parent.
	 * @param name
	 *            Statistic name.
	 * @param type
	 *            Statistic type.
	 */
	public Statistic(Method parent, String name, String type) {
		this.parent = parent;
		this.name = name;
		this.type = type;
		this.statisticsResult = new ArrayList<String>();
		this.condMeasur = new LinkedHashMap<Object, List<Object>>();

		Calendar now = Calendar.getInstance();
		this.clipboardName = type + " - " + now.get(Calendar.HOUR_OF_DAY) + "_"
				+ now.get(Calendar.MINUTE) + "_" + now.get(Calendar.SECOND);
	}

	/**
	 * Complete constructor.
	 * 
	 * @param parent
	 *            Method parent.
	 * @param name
	 *            Statistic name.
	 * @param type
	 *            Statistic type.
	 * @param statisticsResult
	 *            Textual result.
	 * @param statisticsPlot
	 *            Graphic result in byte[].
	 */
	public Statistic(Method parent, String name, String type,
			List<String> statisticsResult, byte[] statisticsPlot) {
		this.parent = parent;
		this.name = name;
		this.type = type;
		this.statisticsResult = statisticsResult;
		this.statisticsPlot = statisticsPlot;
		this.condMeasur = new LinkedHashMap<Object, List<Object>>();

		// Set Clipboard name
		Calendar now = Calendar.getInstance();
		this.clipboardName = type + " - " + now.get(Calendar.HOUR_OF_DAY) + "_"
				+ now.get(Calendar.MINUTE) + "_" + now.get(Calendar.SECOND);
	}

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            Method parent.
	 * @param name
	 *            Statistic name.
	 * @param type
	 *            Statistic type,
	 * @param statisticsResult
	 *            Textual result.
	 */
	public Statistic(Method parent, String name, String type,
			List<String> statisticsResult) {
		this.parent = parent;
		this.name = name;
		this.type = type;
		this.statisticsResult = statisticsResult;
		this.condMeasur = new LinkedHashMap<Object, List<Object>>();

		// Set Clipboard name
		Calendar now = Calendar.getInstance();
		this.clipboardName = type + " - " + now.get(Calendar.HOUR_OF_DAY) + "_"
				+ now.get(Calendar.MINUTE) + "_" + now.get(Calendar.SECOND);
	}

	/**
	 * 
	 * @return
	 */
	public String getNameClipboard() {
		return clipboardName;
	}

	/**
	 * Get name.
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set name.
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get type.
	 * 
	 * @return
	 */
	public String getType() {
		return type;
	}

	/**
	 * Set type.
	 * 
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Get textual result.
	 * 
	 * @return
	 */
	public List<String> getStatisticsResult() {
		return statisticsResult;
	}

	/**
	 * Set textual result.
	 * 
	 * @param statisticsResult
	 */
	public void setStatisticsResult(List<String> statisticsResult) {
		this.statisticsResult = statisticsResult;
	}

	/**
	 * Get graphic result.
	 * 
	 * @return
	 */
	public byte[] getStatisticsPlot() {
		return statisticsPlot;
	}

	/**
	 * Set graphic result.
	 * 
	 * @param statisticsPlot
	 */
	public void setStatisticsPlot(byte[] statisticsPlot) {
		this.statisticsPlot = statisticsPlot;
	}

	/**
	 * Get conditions and measurements.
	 * 
	 * @return
	 */
	public Map<Object, List<Object>> getConditionMeasurements() {
		return condMeasur;
	}

	/**
	 * Set conditions and measurements.
	 * 
	 * @param condMeasur
	 */
	public void setConditionMeasurements(Map<Object, List<Object>> condMeasur) {
		this.condMeasur = condMeasur;
	}

	/**
	 * Get conditions.
	 * 
	 * @return
	 */
	public String[] getCond() {
		return cond;
	}

	/**
	 * Set conditions.
	 * 
	 * @param cond
	 */
	public void setCond(String cond) {
		this.cond = cond.split(",");
	}

	/**
	 * 
	 * @return
	 */
	public String getResult() {
		return result;
	}

	/**
	 * 
	 * @param result
	 */
	public void setResult(String result) {
		this.result = result;
	}

	/**
	 * 
	 * @return
	 */
	public String getTempHTMLDir() {
		return tempHTMLDir;
	}

	/**
	 * 
	 * @param tempHTMLDir
	 */
	public void setTempHTMLDir(String tempHTMLDir) {
		this.tempHTMLDir = tempHTMLDir;
	}

	/**
	 * Method to remove the statistic from the system.
	 */
	public void remove() {
		List<ClipboardItem> items = Core.getInstance().getClipboard()
				.getItemsByClass(Statistic.class);
		ClipboardItem torem = null;
		for (ClipboardItem item : items) {
			if (item.getUserData().equals(this)) {
				torem = item;
				break;
			}
		}
		Core.getInstance().getClipboard().removeClipboardItem(torem);
		this.parent.removeStatisticFromArray(this);
		System.gc();
	}
}
