package es.uvigo.ei.sing.bew.view.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import es.uvigo.ei.sing.bew.constants.I18n;
import es.uvigo.ei.sing.bew.tables.RowNumberTable;
import es.uvigo.ei.sing.bew.tables.renderer.ListStripeRenderer;
import es.uvigo.ei.sing.bew.tables.renderer.StripeTableCellRender;
import es.uvigo.ei.sing.bew.view.components.CustomDialog;
import es.uvigo.ei.sing.bew.view.panels.ButtonsPanel;

/**
 * Custom dialog to visualize an Experiment from BioFomics.
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
public class VisualizeBioExperiment extends CustomDialog {

	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();
	private ButtonsPanel buttonsPanel;

	private JTextField textFieldName;
	private JTextField textFieldDate;
	private JTextField textFieldAuthor;
	private JTextField textFieldUser;
	private JTextField textFieldMail;
	private JTextField textFieldOrg;
	private JTextArea textAreaNote;

	private JList<String> listPublication;
	private DefaultListModel<String> listModel;

	private JTable tableConditions;
	private DefaultTableModel dtmConditions;

	private JTable tableMethods;
	private DefaultTableModel dtmMethods;

	private JCheckBox chckInter;

	/**
	 * Create the dialog.
	 */
	public VisualizeBioExperiment(JDialog parent) {
		super();
		
		init();
		initButtons();

		setLocationRelativeTo(null);
	}

	/**
	 * Initializes the dialog.
	 */
	private void init() {
		setTitle(I18n.get("VisualizeBioExperimentTitle"));
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		setSize(new Dimension(880, 625));

		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JPanel panelInfo = new JPanel();
			panelInfo.setBorder(new TitledBorder(null, I18n.get("info"),
					TitledBorder.LEADING, TitledBorder.TOP, null, null));
			contentPanel.add(panelInfo, BorderLayout.NORTH);
			GridBagLayout gblPanelInfo = new GridBagLayout();
			gblPanelInfo.columnWidths = new int[] { 25, 25, 25, 25 };
			gblPanelInfo.rowHeights = new int[] { 50, 50, 50, 50, 50, 50 };
			gblPanelInfo.columnWeights = new double[] { 1.0, 1.0, 1.0, 1.0 };
			gblPanelInfo.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0,
					Double.MIN_VALUE };
			panelInfo.setLayout(gblPanelInfo);
			{
				JLabel lblName = new JLabel(
						I18n.get("VisualizeBioExperimentName"));
				GridBagConstraints gbcLblName = new GridBagConstraints();
				gbcLblName.insets = new Insets(0, 0, 5, 5);
				gbcLblName.gridx = 0;
				gbcLblName.gridy = 0;
				panelInfo.add(lblName, gbcLblName);
			}
			{
				textFieldName = new JTextField();
				textFieldName.setEditable(false);
				GridBagConstraints gbcFieldName = new GridBagConstraints();
				gbcFieldName.insets = new Insets(0, 0, 5, 5);
				gbcFieldName.fill = GridBagConstraints.HORIZONTAL;
				gbcFieldName.gridx = 1;
				gbcFieldName.gridy = 0;
				panelInfo.add(textFieldName, gbcFieldName);
				textFieldName.setColumns(10);
			}
			{
				JLabel lblUser = new JLabel(I18n.get("LoginPanelUser"));
				GridBagConstraints gbcLblUser = new GridBagConstraints();
				gbcLblUser.insets = new Insets(0, 0, 5, 5);
				gbcLblUser.gridx = 2;
				gbcLblUser.gridy = 0;
				panelInfo.add(lblUser, gbcLblUser);
			}
			{
				textFieldUser = new JTextField();
				textFieldUser.setEditable(false);
				GridBagConstraints gbcFieldUser = new GridBagConstraints();
				gbcFieldUser.insets = new Insets(0, 0, 5, 0);
				gbcFieldUser.fill = GridBagConstraints.HORIZONTAL;
				gbcFieldUser.gridx = 3;
				gbcFieldUser.gridy = 0;
				panelInfo.add(textFieldUser, gbcFieldUser);
				textFieldUser.setColumns(10);
			}
			{
				JLabel lblInter = new JLabel(
						I18n.get("VisualizeBioExperimentInter"));
				GridBagConstraints gbcLblInter = new GridBagConstraints();
				gbcLblInter.insets = new Insets(0, 0, 5, 5);
				gbcLblInter.gridx = 0;
				gbcLblInter.gridy = 1;
				panelInfo.add(lblInter, gbcLblInter);
			}
			{
				JLabel lblMail = new JLabel(
						I18n.get("VisualizeBioExperimentMail"));
				GridBagConstraints gbcLblMail = new GridBagConstraints();
				gbcLblMail.insets = new Insets(0, 0, 5, 5);
				gbcLblMail.gridx = 2;
				gbcLblMail.gridy = 1;
				panelInfo.add(lblMail, gbcLblMail);
			}
			{
				textFieldMail = new JTextField();
				textFieldMail.setEditable(false);
				GridBagConstraints gbcFieldMail = new GridBagConstraints();
				gbcFieldMail.insets = new Insets(0, 0, 5, 0);
				gbcFieldMail.fill = GridBagConstraints.HORIZONTAL;
				gbcFieldMail.gridx = 3;
				gbcFieldMail.gridy = 1;
				panelInfo.add(textFieldMail, gbcFieldMail);
				textFieldMail.setColumns(10);
			}
			{
				textFieldDate = new JTextField();
				textFieldDate.setEditable(false);
				GridBagConstraints gbcFieldDate = new GridBagConstraints();
				gbcFieldDate.insets = new Insets(0, 0, 5, 5);
				gbcFieldDate.fill = GridBagConstraints.HORIZONTAL;
				gbcFieldDate.gridx = 1;
				gbcFieldDate.gridy = 2;
				panelInfo.add(textFieldDate, gbcFieldDate);
				textFieldDate.setColumns(10);
			}
			{
				JLabel lblDate = new JLabel(
						I18n.get("VisualizeBioExperimentDate"));
				GridBagConstraints gbcLblDate = new GridBagConstraints();
				gbcLblDate.insets = new Insets(0, 0, 5, 5);
				gbcLblDate.gridx = 0;
				gbcLblDate.gridy = 2;
				panelInfo.add(lblDate, gbcLblDate);
			}
			{
				JLabel lblOrganization = new JLabel(
						I18n.get("VisualizeBioExperimentOrg"));
				GridBagConstraints gbcLblOrg = new GridBagConstraints();
				gbcLblOrg.insets = new Insets(0, 0, 5, 5);
				gbcLblOrg.gridx = 2;
				gbcLblOrg.gridy = 2;
				panelInfo.add(lblOrganization, gbcLblOrg);
			}
			{
				textFieldOrg = new JTextField();
				textFieldOrg.setEditable(false);
				GridBagConstraints gbcFieldOrg = new GridBagConstraints();
				gbcFieldOrg.insets = new Insets(0, 0, 5, 0);
				gbcFieldOrg.fill = GridBagConstraints.HORIZONTAL;
				gbcFieldOrg.gridx = 3;
				gbcFieldOrg.gridy = 2;
				panelInfo.add(textFieldOrg, gbcFieldOrg);
				textFieldOrg.setColumns(10);
			}
			{
				textFieldAuthor = new JTextField();
				textFieldAuthor.setEditable(false);
				GridBagConstraints gbcTextFieldAuthor = new GridBagConstraints();
				gbcTextFieldAuthor.insets = new Insets(0, 0, 5, 5);
				gbcTextFieldAuthor.fill = GridBagConstraints.HORIZONTAL;
				gbcTextFieldAuthor.gridx = 1;
				gbcTextFieldAuthor.gridy = 3;
				panelInfo.add(textFieldAuthor, gbcTextFieldAuthor);
				textFieldAuthor.setColumns(10);
			}
			{
				JLabel lblAuthors = new JLabel(
						I18n.get("VisualizeBioExperimentAuthors"));
				GridBagConstraints gbcLblAuthors = new GridBagConstraints();
				gbcLblAuthors.insets = new Insets(0, 0, 5, 5);
				gbcLblAuthors.gridx = 0;
				gbcLblAuthors.gridy = 3;
				panelInfo.add(lblAuthors, gbcLblAuthors);
			}
			{
				chckInter = new JCheckBox("");
				chckInter.setEnabled(false);
				GridBagConstraints gbcChckInter = new GridBagConstraints();
				gbcChckInter.insets = new Insets(0, 0, 5, 5);
				gbcChckInter.gridx = 1;
				gbcChckInter.gridy = 1;
				panelInfo.add(chckInter, gbcChckInter);
			}
			{
				JLabel lblPublications = new JLabel(
						I18n.get("VisualizeBioExperimentPubli"));
				GridBagConstraints gbcLblPub = new GridBagConstraints();
				gbcLblPub.insets = new Insets(0, 0, 5, 5);
				gbcLblPub.gridx = 2;
				gbcLblPub.gridy = 4;
				panelInfo.add(lblPublications, gbcLblPub);
			}
			{
				listModel = new DefaultListModel<String>();
				listPublication = new JList<String>(listModel);
				listPublication.setCellRenderer(new ListStripeRenderer());
				JScrollPane panelPubli = new JScrollPane(listPublication);

				GridBagConstraints gbcTextAreaPub = new GridBagConstraints();
				gbcTextAreaPub.gridheight = 2;
				gbcTextAreaPub.insets = new Insets(0, 0, 5, 0);
				gbcTextAreaPub.fill = GridBagConstraints.BOTH;
				gbcTextAreaPub.gridx = 3;
				gbcTextAreaPub.gridy = 4;
				panelInfo.add(panelPubli, gbcTextAreaPub);
			}
			{
				JLabel lblNotes = new JLabel(
						I18n.get("VisualizeBioExperimentNotes"));
				GridBagConstraints gbcLblNotes = new GridBagConstraints();
				gbcLblNotes.insets = new Insets(0, 0, 0, 5);
				gbcLblNotes.gridx = 0;
				gbcLblNotes.gridy = 4;
				panelInfo.add(lblNotes, gbcLblNotes);
			}
			{
				textAreaNote = new JTextArea();
				textAreaNote.setColumns(25);
				textAreaNote.setRows(5);
				textAreaNote.setEditable(false);
				textAreaNote.setWrapStyleWord(true);
				textAreaNote.setLineWrap(true);
				JScrollPane panelNote = new JScrollPane(textAreaNote);
				GridBagConstraints gbcTextNote = new GridBagConstraints();
				gbcTextNote.gridheight = 2;
				gbcTextNote.insets = new Insets(0, 0, 0, 5);
				gbcTextNote.fill = GridBagConstraints.BOTH;
				gbcTextNote.gridx = 1;
				gbcTextNote.gridy = 4;
				panelInfo.add(panelNote, gbcTextNote);
			}
		}
		{
			JPanel panelTables = new JPanel();
			panelTables.setBorder(new TitledBorder(null,
					"Conditions and Methods", TitledBorder.LEADING,
					TitledBorder.TOP, null, null));
			contentPanel.add(panelTables, BorderLayout.CENTER);
			panelTables.setLayout(new GridLayout(0, 2, 0, 0));
			{
				dtmConditions = new DefaultTableModel();
				dtmConditions.addColumn(I18n.get("conditions"));

				tableConditions = new JTable(dtmConditions);
				tableConditions.setEnabled(false);

				// Set stripe renderer
				StripeTableCellRender renderer = new StripeTableCellRender();
				tableConditions.setDefaultRenderer(String.class, renderer);

				// ScrollPane for table
				JScrollPane scrollConditions = new JScrollPane(tableConditions,
						JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
						JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

				// Rows header for table
				RowNumberTable rowTable = new RowNumberTable(tableConditions);

				scrollConditions.setRowHeaderView(rowTable);
				scrollConditions.setCorner(JScrollPane.UPPER_LEFT_CORNER,
						rowTable.getTableHeader());

				panelTables.add(scrollConditions);
			}
			{
				dtmMethods = new DefaultTableModel();
				dtmMethods.addColumn(I18n.get("methods"));

				tableMethods = new JTable(dtmMethods);
				tableMethods.setEnabled(false);

				// Set stripe renderer
				StripeTableCellRender renderer = new StripeTableCellRender();
				tableMethods.setDefaultRenderer(String.class, renderer);

				// ScrollPane for table
				JScrollPane scrollMethods = new JScrollPane(tableMethods,
						JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
						JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

				// Rows header for table
				RowNumberTable rowTable = new RowNumberTable(tableMethods);

				scrollMethods.setRowHeaderView(rowTable);
				scrollMethods.setCorner(JScrollPane.UPPER_LEFT_CORNER,
						rowTable.getTableHeader());

				panelTables.add(scrollMethods);
			}
		}
	}

	/**
	 * Initializes buttons.
	 */
	private void initButtons() {
		String[] buttonNames = { I18n.get("VisualizeBioExperimentClose") };
		URL[] iconPaths = { getClass().getResource("/img/exit.png") };

		ActionListener[] listeners = { new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		} };

		this.buttonsPanel = new ButtonsPanel(buttonNames, iconPaths, listeners);

		// ContentPane South
		getContentPane().add(this.buttonsPanel, BorderLayout.SOUTH);
	}

	/**
	 * 
	 */
	public void setName(String name) {
		this.textFieldName.setText(name);
	}

	/**
	 * 
	 * @param date
	 */
	public void setDate(String date) {
		this.textFieldDate.setText(date);
	}

	/**
	 * 
	 * @param authors
	 */
	public void setAuthors(String authors) {
		this.textFieldAuthor.setText(authors);
	}

	/**
	 * 
	 * @param user
	 */
	public void setUser(String user) {
		this.textFieldUser.setText(user);
	}

	/**
	 * 
	 * @param mail
	 */
	public void setMail(String mail) {
		this.textFieldMail.setText(mail);
	}

	/**
	 * 
	 * @param organization
	 */
	public void setOrganization(String organization) {
		this.textFieldOrg.setText(organization);
	}

	/**
	 * 
	 * @param notes
	 */
	public void setNotes(String notes) {
		this.textAreaNote.setText(notes);
	}

	/**
	 * 
	 * @param isInter
	 */
	public void setInter(String isInter) {
		chckInter.setSelected(Boolean.parseBoolean(isInter));
	}

	/**
	 * 
	 * @param publication
	 */
	public void addPublication(String publication) {
		listModel.addElement(publication);
	}

	/**
	 * 
	 * @param condition
	 */
	public void addCondition(String condition) {
		dtmConditions.addRow(new String[] { condition });
	}

	/**
	 * 
	 * @param method
	 */
	public void addMethod(String method) {
		dtmMethods.addRow(new String[] { method });
	}

	/**
	 * Clear all the data.
	 */
	public void clearAll() {
		// Purge fields
		this.textFieldName.setText("");
		this.textFieldDate.setText("");
		this.textFieldAuthor.setText("");
		this.textFieldUser.setText("");
		this.textFieldMail.setText("");
		this.textFieldOrg.setText("");
		this.textAreaNote.setText("");

		// Purge list
		listModel.removeAllElements();
		// Purge tables rows
		int rows = dtmConditions.getRowCount();
		for (int i = rows - 1; i >= 0; i--) {
			dtmConditions.removeRow(i);
		}
		rows = dtmMethods.getRowCount();
		for (int i = rows - 1; i >= 0; i--) {
			dtmMethods.removeRow(i);
		}
	}

}
