package es.uvigo.ei.sing.bew.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import es.uvigo.ei.aibench.core.Core;
import es.uvigo.ei.aibench.core.clipboard.ClipboardItem;
import es.uvigo.ei.aibench.core.datatypes.annotation.Datatype;
import es.uvigo.ei.aibench.core.datatypes.annotation.Structure;

/**
 * This class is one of the basic objects with which the application works in
 * memory.
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
@Datatype(structure = Structure.SIMPLE, removeMethod = "remove", namingMethod = "getNameClipboard", renameable = false)
public class Plot implements Serializable {

	private static final long serialVersionUID = 1L;

	// Clipboard name
	private String clipboardName;

	// Variable that contain the chart
	private byte[] chart;

	// Replicate information {
	private String title;
	private String xAxisTitle;
	private String yAxisTitle;
	private String type;

	private List<List<Object>> values;
	private List<Object> keys;
	private List<String> conditions;

	private Method parent;

	// }

	/**
	 * Constructor without Chart
	 * 
	 * @param title
	 *            Plot title.
	 * @param xAxisTitle
	 *            Plot XAxis title.
	 * @param yAxisTitle
	 *            Plot yAxis title.
	 * @param type
	 *            Plot type.
	 * @param values
	 *            Plot values.
	 * @param keys
	 *            Plot keys.
	 * @param method
	 *            Plot parent.
	 */
	public Plot(String title, String xAxisTitle, String yAxisTitle,
			String type, List<List<Object>> values, List<Object> keys,
			Method method, List<String> conditions) {
		this.title = title;
		this.xAxisTitle = xAxisTitle;
		this.yAxisTitle = yAxisTitle;
		this.type = type;
		this.values = values;
		this.keys = keys;
		this.parent = method;
		this.conditions = conditions;

		Calendar now = Calendar.getInstance();
		this.clipboardName = type + " - " + now.get(Calendar.HOUR_OF_DAY) + "_"
				+ now.get(Calendar.MINUTE) + "_" + now.get(Calendar.SECOND);
	}

	/**
	 * Gets the plot name in the Clipboard.
	 * 
	 * @return String with the name.
	 */
	public String getNameClipboard() {
		return clipboardName;
	}

	/**
	 * Get specific chart.
	 * 
	 * @return byte[] with the image.
	 */
	public byte[] getChart() {
		return chart;
	}

	/**
	 * Set specific chart.
	 * 
	 * @param chart
	 */
	public void setChart(byte[] chart) {
		this.chart = chart;
	}

	/**
	 * 
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * 
	 * @return
	 */
	public String getxAxisTitle() {
		return xAxisTitle;
	}

	/**
	 * 
	 * @param xAxisTitle
	 */
	public void setxAxisTitle(String xAxisTitle) {
		this.xAxisTitle = xAxisTitle;
	}

	/**
	 * 
	 * @return
	 */
	public String getyAxisTitle() {
		return yAxisTitle;
	}

	/**
	 * 
	 * @param yAxisTitle
	 */
	public void setyAxisTitle(String yAxisTitle) {
		this.yAxisTitle = yAxisTitle;
	}

	/**
	 * 
	 */
	public String getType() {
		return type;
	}

	/**
	 * 
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * 
	 * @return
	 */
	public List<List<Object>> getValues() {
		return values;
	}

	/**
	 * 
	 * @param values
	 */
	public void setValues(List<List<Object>> values) {
		this.values = values;
	}

	/**
	 * 
	 * @return
	 */
	public List<Object> getKeys() {
		return keys;
	}

	/**
	 * 
	 * @param keys
	 */
	public void setKeys(List<Object> keys) {
		this.keys = keys;
	}

	/**
	 * 
	 * @return
	 */
	public Method getParent() {
		return parent;
	}

	/**
	 * 
	 * @param parent
	 */
	public void setParent(Method parent) {
		this.parent = parent;
	}

	public List<String> getConditions() {
		return conditions;
	}

	public void setConditions(List<String> conditions) {
		this.conditions = conditions;
	}

	/**
	 * Method to remove the Plot from the system.
	 */
	public void remove() {
		List<ClipboardItem> items = Core.getInstance().getClipboard()
				.getItemsByClass(Plot.class);
		ClipboardItem torem = null;
		for (ClipboardItem item : items) {
			if (item.getUserData().equals(this)) {
				torem = item;
				break;
			}
		}
		Core.getInstance().getClipboard().removeClipboardItem(torem);
		this.parent.removePlotFromArray(this);
		System.gc();
	}
}
