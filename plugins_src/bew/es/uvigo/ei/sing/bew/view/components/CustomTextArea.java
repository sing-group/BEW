package es.uvigo.ei.sing.bew.view.components;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JTextArea;
import javax.swing.text.Document;

import es.uvigo.ei.sing.bew.util.ContextMenuMouseListener;

/**
 * Custom text area with a placeholder.
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
@SuppressWarnings("serial")
public class CustomTextArea extends JTextArea {
	private String placeholder;

	/**
	 * 
	 */
	public CustomTextArea() {
		super();
		addMouseListener(new ContextMenuMouseListener());
	}

	/**
	 * 
	 * @param pDoc
	 * @param pText
	 * @param pRows
	 * @param pColumns
	 */
	public CustomTextArea(final Document pDoc, final String pText,
			final int pRows, final int pColumns) {
		super(pDoc, pText, pRows, pColumns);
		addMouseListener(new ContextMenuMouseListener());
	}

	/**
	 * 
	 * @param pRows
	 * @param pColumns
	 */
	public CustomTextArea(final int pRows, final int pColumns) {
		super(pRows, pColumns);
		addMouseListener(new ContextMenuMouseListener());
	}

	/**
	 * 
	 * @param pText
	 */
	public CustomTextArea(final String pText) {
		super(pText);
		addMouseListener(new ContextMenuMouseListener());
	}

	/**
	 * 
	 * @param pText
	 * @param pRows
	 * @param pColumns
	 */
	public CustomTextArea(final String pText, final int pRows,
			final int pColumns) {
		super(pText, pRows, pColumns);
		addMouseListener(new ContextMenuMouseListener());
	}

	/**
	 * 
	 * @return
	 */
	public String getPlaceholder() {
		return placeholder;
	}

	@Override
	protected void paintComponent(final Graphics pGraph) {
		super.paintComponent(pGraph);

		if (placeholder.length() == 0 || getText().length() > 0) {
			return;
		}

		final Graphics2D graph = (Graphics2D) pGraph;
		graph.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		graph.setColor(getDisabledTextColor());
		graph.drawString(placeholder, getInsets().left, pGraph.getFontMetrics()
				.getMaxAscent() + getInsets().top);
	}

	/**
	 * 
	 * @param str
	 */
	public void setPlaceholder(final String str) {
		placeholder = str;
	}
}
