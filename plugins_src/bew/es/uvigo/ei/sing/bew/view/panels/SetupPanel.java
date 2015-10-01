package es.uvigo.ei.sing.bew.view.panels;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import es.uvigo.ei.sing.bew.constants.BewConstants;
import es.uvigo.ei.sing.bew.constants.I18n;
import es.uvigo.ei.sing.bew.util.JTextFieldLimit;
import es.uvigo.ei.sing.bew.view.components.CustomTextArea;
import es.uvigo.ei.sing.bew.view.components.CustomTextField;

/**
 * Custom panel to show the Experiment setup.
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
public class SetupPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private CustomTextField fieldName;
	private CustomTextField fieldAuthors;
	private CustomTextField fieldOrganization;
	private CustomTextField fieldContact;
	private CustomTextField fieldDate;
	private CustomTextField fieldPublication;
	private CustomTextArea fieldNotes;

	private JLabel lblName;
	private JLabel lblOrganization;
	private JLabel lblContact;

	/**
	 * Default constructor.
	 */
	public SetupPanel() {
		super();

		init();
	}

	/**
	 * Initializes the dialog.
	 */
	private void init() {
		setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"),
				I18n.get("basicInfo"), TitledBorder.LEADING, TitledBorder.TOP,
				null, null));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 219, 219 };
		gridBagLayout.rowHeights = new int[] { 39, 39, 39, 39, 39, 39, 78 };
		gridBagLayout.columnWeights = new double[] { 1.0, 1.0 };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				1.0 };
		setLayout(gridBagLayout);

		JLabel labelName = new JLabel(I18n.get("experimentName"));
		labelName.setIcon(new ImageIcon(SetupPanel.class
				.getResource("/img/experiment.png")));
		GridBagConstraints gbcLabelName = new GridBagConstraints();
		gbcLabelName.fill = GridBagConstraints.BOTH;
		gbcLabelName.insets = new Insets(0, 0, 5, 5);
		gbcLabelName.gridx = 0;
		gbcLabelName.gridy = 0;
		add(labelName, gbcLabelName);

		fieldName = new CustomTextField();
		// Limit characters
		fieldName.setDocument(new JTextFieldLimit(BewConstants.NAME_LIMIT));
		fieldName.setPlaceholder("e.g. Sample Experiment 1 (max 50 characters)");
		GridBagConstraints gbcFieldName = new GridBagConstraints();
		gbcFieldName.fill = GridBagConstraints.BOTH;
		gbcFieldName.insets = new Insets(0, 0, 5, 0);
		gbcFieldName.gridx = 1;
		gbcFieldName.gridy = 0;
		add(fieldName, gbcFieldName);
		fieldName.setColumns(10);

		JLabel labelAuthors = new JLabel(I18n.get("authors"));
		labelAuthors.setIcon(new ImageIcon(SetupPanel.class
				.getResource("/img/user.png")));
		GridBagConstraints gbcLabelAuthors = new GridBagConstraints();
		gbcLabelAuthors.fill = GridBagConstraints.BOTH;
		gbcLabelAuthors.insets = new Insets(0, 0, 5, 5);
		gbcLabelAuthors.gridx = 0;
		gbcLabelAuthors.gridy = 1;
		add(labelAuthors, gbcLabelAuthors);

		fieldAuthors = new CustomTextField();
		fieldAuthors
				.setDocument(new JTextFieldLimit(BewConstants.AUTHORS_LIMIT));
		fieldAuthors.setPlaceholder("e.g. Prez, P (max 100 characters)");
		GridBagConstraints gbcFieldAuthors = new GridBagConstraints();
		gbcFieldAuthors.fill = GridBagConstraints.BOTH;
		gbcFieldAuthors.insets = new Insets(0, 0, 5, 0);
		gbcFieldAuthors.gridx = 1;
		gbcFieldAuthors.gridy = 1;
		add(fieldAuthors, gbcFieldAuthors);
		fieldAuthors.setColumns(10);

		JLabel labelOrganization = new JLabel(I18n.get("organization"));
		labelOrganization.setIcon(new ImageIcon(SetupPanel.class
				.getResource("/img/institution.png")));
		GridBagConstraints gbcLabelOrg = new GridBagConstraints();
		gbcLabelOrg.fill = GridBagConstraints.BOTH;
		gbcLabelOrg.insets = new Insets(0, 0, 5, 5);
		gbcLabelOrg.gridx = 0;
		gbcLabelOrg.gridy = 2;
		add(labelOrganization, gbcLabelOrg);

		fieldOrganization = new CustomTextField();
		fieldOrganization.setDocument(new JTextFieldLimit(
				BewConstants.ORGANIZATION_LIMIT));
		fieldOrganization.setPlaceholder("e.g. UVIGO (max 100 characters)");
		GridBagConstraints gbcFieldOrg = new GridBagConstraints();
		gbcFieldOrg.fill = GridBagConstraints.BOTH;
		gbcFieldOrg.insets = new Insets(0, 0, 5, 0);
		gbcFieldOrg.gridx = 1;
		gbcFieldOrg.gridy = 2;
		add(fieldOrganization, gbcFieldOrg);
		fieldOrganization.setColumns(10);

		JLabel labelContact = new JLabel(I18n.get("emailContact"));
		labelContact.setIcon(new ImageIcon(SetupPanel.class
				.getResource("/img/email.png")));
		GridBagConstraints gbcLabelContact = new GridBagConstraints();
		gbcLabelContact.fill = GridBagConstraints.BOTH;
		gbcLabelContact.insets = new Insets(0, 0, 5, 5);
		gbcLabelContact.gridx = 0;
		gbcLabelContact.gridy = 3;
		add(labelContact, gbcLabelContact);

		fieldContact = new CustomTextField();
		fieldContact.setPlaceholder("e.g. email@email.com");
		GridBagConstraints gbcFieldContact = new GridBagConstraints();
		gbcFieldContact.fill = GridBagConstraints.BOTH;
		gbcFieldContact.insets = new Insets(0, 0, 5, 0);
		gbcFieldContact.gridx = 1;
		gbcFieldContact.gridy = 3;
		add(fieldContact, gbcFieldContact);
		fieldContact.setColumns(10);

		JLabel labelDate = new JLabel(I18n.get("date"));
		labelDate.setIcon(new ImageIcon(SetupPanel.class
				.getResource("/img/calendar.png")));
		GridBagConstraints gbcLabelDate = new GridBagConstraints();
		gbcLabelDate.fill = GridBagConstraints.BOTH;
		gbcLabelDate.insets = new Insets(0, 0, 5, 5);
		gbcLabelDate.gridx = 0;
		gbcLabelDate.gridy = 4;
		add(labelDate, gbcLabelDate);

		fieldDate = new CustomTextField();
		fieldDate.setPlaceholder("e.g. 01/01/2014 (dd/MM/yyyy)");
		GridBagConstraints gbcFieldDate = new GridBagConstraints();
		gbcFieldDate.fill = GridBagConstraints.BOTH;
		gbcFieldDate.insets = new Insets(0, 0, 5, 0);
		gbcFieldDate.gridx = 1;
		gbcFieldDate.gridy = 4;
		add(fieldDate, gbcFieldDate);
		fieldDate.setColumns(10);

		JLabel labelPublication = new JLabel(I18n.get("publication"));
		labelPublication.setIcon(new ImageIcon(SetupPanel.class
				.getResource("/img/publication.png")));
		GridBagConstraints gbcLabelPub = new GridBagConstraints();
		gbcLabelPub.fill = GridBagConstraints.BOTH;
		gbcLabelPub.insets = new Insets(0, 0, 5, 5);
		gbcLabelPub.gridx = 0;
		gbcLabelPub.gridy = 5;
		add(labelPublication, gbcLabelPub);

		fieldPublication = new CustomTextField();
		fieldPublication.setPlaceholder("e.g. 12345678");
		GridBagConstraints gbcFieldPub = new GridBagConstraints();
		gbcFieldPub.fill = GridBagConstraints.BOTH;
		gbcFieldPub.insets = new Insets(0, 0, 5, 0);
		gbcFieldPub.gridx = 1;
		gbcFieldPub.gridy = 5;
		add(fieldPublication, gbcFieldPub);
		fieldPublication.setColumns(10);

		JLabel labelNotes = new JLabel(I18n.get("notes"));
		labelNotes.setIcon(new ImageIcon(SetupPanel.class
				.getResource("/img/description (2).png")));
		GridBagConstraints gbcLabelNotes = new GridBagConstraints();
		gbcLabelNotes.fill = GridBagConstraints.BOTH;
		gbcLabelNotes.insets = new Insets(0, 0, 0, 5);
		gbcLabelNotes.gridx = 0;
		gbcLabelNotes.gridy = 6;
		add(labelNotes, gbcLabelNotes);

		fieldNotes = new CustomTextArea();
		fieldNotes.setDocument(new JTextFieldLimit(
				BewConstants.DESCRIPTION_LIMIT));
		fieldNotes.setWrapStyleWord(true);
		fieldNotes.setFont(new Font("Tahoma", Font.PLAIN, 11));
		fieldNotes.setLineWrap(true);
		fieldNotes
				.setPlaceholder("Write any information that may be relevant to further identify or characterize the experiment, e.g. contributions of the authors to this work, funding, etc... (max 500 characters)");
		fieldNotes.setColumns(10);
		fieldNotes.setRows(20);
		JScrollPane scrollNotes = new JScrollPane(fieldNotes);
		GridBagConstraints gbcScrollNotes = new GridBagConstraints();
		gbcScrollNotes.fill = GridBagConstraints.BOTH;
		gbcScrollNotes.gridx = 1;
		gbcScrollNotes.gridy = 6;
		add(scrollNotes, gbcScrollNotes);
	}

	/**
	 * 
	 * @return
	 */
	public JTextField getFieldName() {
		return fieldName;
	}

	/**
	 * 
	 * @param fieldName
	 */
	public void setFieldName(CustomTextField fieldName) {
		this.fieldName = fieldName;
	}

	/**
	 * 
	 * @return
	 */
	public CustomTextField getFieldAuthors() {
		return fieldAuthors;
	}

	/**
	 * 
	 * @param fieldAuthors
	 */
	public void setFieldAuthors(CustomTextField fieldAuthors) {
		this.fieldAuthors = fieldAuthors;
	}

	/**
	 * 
	 * @return
	 */
	public CustomTextField getFieldOrganization() {
		return fieldOrganization;
	}

	/**
	 * 
	 * @param fieldOrganization
	 */
	public void setFieldOrganization(CustomTextField fieldOrganization) {
		this.fieldOrganization = fieldOrganization;
	}

	/**
	 * 
	 * @return
	 */
	public CustomTextField getFieldContact() {
		return fieldContact;
	}

	/**
	 * 
	 * @param fieldContact
	 */
	public void setFieldContact(CustomTextField fieldContact) {
		this.fieldContact = fieldContact;
	}

	/**
	 * 
	 * @return
	 */
	public CustomTextField getFieldDate() {
		return fieldDate;
	}

	/**
	 * 
	 * @param fieldDate
	 */
	public void setFieldDate(CustomTextField fieldDate) {
		this.fieldDate = fieldDate;
	}

	/**
	 * 
	 * @return
	 */
	public CustomTextField getFieldPublication() {
		return fieldPublication;
	}

	/**
	 * 
	 * @param fieldPublication
	 */
	public void setFieldPublication(CustomTextField fieldPublication) {
		this.fieldPublication = fieldPublication;
	}

	/**
	 * 
	 * @return
	 */
	public CustomTextArea getFieldNotes() {
		return fieldNotes;
	}

	/**
	 * 
	 * @param fieldNotes
	 */
	public void setFieldNotes(CustomTextArea fieldNotes) {
		this.fieldNotes = fieldNotes;
	}

	/**
	 * 
	 * @return
	 */
	public JLabel getLblName() {
		return lblName;
	}

	/**
	 * 
	 * @param lblName
	 */
	public void setLblName(JLabel lblName) {
		this.lblName = lblName;
	}

	/**
	 * 
	 * @return
	 */
	public JLabel getLblOrganization() {
		return lblOrganization;
	}

	/**
	 * 
	 * @param lblOrganization
	 */
	public void setLblOrganization(JLabel lblOrganization) {
		this.lblOrganization = lblOrganization;
	}

	/**
	 * 
	 * @return
	 */
	public JLabel getLblContact() {
		return lblContact;
	}

	/**
	 * 
	 * @param lblContact
	 */
	public void setLblContact(JLabel lblContact) {
		this.lblContact = lblContact;
	}

	/**
	 * 
	 * @return
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
