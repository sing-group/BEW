package es.uvigo.ei.sing.bew.tables;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

/**
 * Auxiliary table to use with another JTable to show its rows.
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
public class RowNumberTable extends JTable implements ChangeListener,
		PropertyChangeListener {

	private static final long serialVersionUID = 1L;

	private JTable main;

	/**
	 * Default constructor.
	 * 
	 * @param table
	 *            Main table.
	 */
	public RowNumberTable(JTable table) {
		super();

		main = table;
		main.addPropertyChangeListener(this);

		init();
	}

	/**
	 * 
	 */
	private void init() {
		getTableHeader().setReorderingAllowed(false);
		setFocusable(false);
		setAutoCreateColumnsFromModel(false);
		setModel(main.getModel());
		setSelectionModel(main.getSelectionModel());

		TableColumn column = new TableColumn();

		column.setCellRenderer(new RowNumberRenderer());
		column.setHeaderRenderer(new HeaderRenderer(main));

		addColumn(column);

		getTableHeader().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
				// Select all cells in main table
				main.selectAll();
			};
		});

		getColumnModel().getColumn(0).setPreferredWidth(50);
		setPreferredScrollableViewportSize(getPreferredSize());
	}

	@Override
	public void addNotify() {
		super.addNotify();

		Component comp = getParent();

		// Keep scrolling of the row table in sync with the main table.

		if (comp instanceof JViewport) {
			JViewport viewport = (JViewport) comp;
			viewport.addChangeListener(this);
		}
	}

	/*
	 * Delegate method to main table
	 */
	@Override
	public int getRowCount() {
		return main.getRowCount();
	}

	@Override
	public int getRowHeight(int row) {
		return main.getRowHeight(row);
	}

	/*
	 * This table does not use any data from the main TableModel, so just return
	 * a value based on the row parameter.
	 */
	@Override
	public Object getValueAt(int row, int column) {
		return Integer.toString(row + 1);
	}

	/*
	 * Don't edit data in the main TableModel by mistake
	 */
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

	/**
	 * 
	 */
	public void stateChanged(ChangeEvent e) {
		// Keep the scrolling of the row table in sync with main table
		JViewport viewport = (JViewport) e.getSource();
		JScrollPane scrollPane = (JScrollPane) viewport.getParent();
		scrollPane.getVerticalScrollBar()
				.setValue(viewport.getViewPosition().y);
	}

	/**
	 * 
	 */
	public void propertyChange(PropertyChangeEvent e) {
		// Keep the row table in sync with the main table

		if ("selectionModel".equals(e.getPropertyName())) {
			setSelectionModel(main.getSelectionModel());
		}

		if ("model".equals(e.getPropertyName())) {
			setModel(main.getModel());
		}
	}

	/**
	 * Private class. Specific render for RowNumberTable.class.
	 * 
	 * @author Gael Pérez Rodríguez
	 * 
	 */
	private static class RowNumberRenderer extends DefaultTableCellRenderer {
		public RowNumberRenderer() {
			setHorizontalAlignment(JLabel.CENTER);
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			if (table != null) {
				JTableHeader header = table.getTableHeader();

				if (header != null) {
					setForeground(header.getForeground());
					setBackground(header.getBackground());
					setFont(header.getFont());
				}
			}

			if (isSelected) {
				setFont(getFont().deriveFont(Font.BOLD));
			}

			setText((value == null) ? "" : value.toString());
			setBorder(UIManager.getBorder("TableHeader.cellBorder"));

			return this;
		}
	}

	/**
	 * Private class. Specific render for RowNumberTable.class.
	 * 
	 * @author Gael Pérez Rodríguez
	 * 
	 */
	private static class HeaderRenderer extends DefaultTableCellRenderer {
		private JTable main;

		public HeaderRenderer(JTable main) {
			this.main = main;

			setHorizontalAlignment(JLabel.CENTER);
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			if (table != null) {
				JTableHeader header = main.getTableHeader();

				if (header != null) {
					setForeground(header.getForeground());
					setBackground(header.getBackground());
					setFont(header.getFont());
				}
			}

			setBorder(UIManager.getBorder("TableHeader.cellBorder"));
			setIcon(new ImageIcon(
					RowNumberRenderer.class.getResource("/img/selectAll.png")));
			setText("All");

			return this;
		}

	}
}