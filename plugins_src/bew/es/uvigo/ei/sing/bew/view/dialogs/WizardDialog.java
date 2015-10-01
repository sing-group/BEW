package es.uvigo.ei.sing.bew.view.dialogs;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import es.uvigo.ei.sing.bew.constants.FunctionConstants;
import es.uvigo.ei.sing.bew.constants.I18n;
import es.uvigo.ei.sing.bew.constants.ShowDialog;
import es.uvigo.ei.sing.bew.files.WizardToFile;
import es.uvigo.ei.sing.bew.sheets.IWizardStep;
import es.uvigo.ei.sing.bew.sheets.SheetFactory;
import es.uvigo.ei.sing.bew.view.components.CustomDialog;
import es.uvigo.ei.sing.bew.view.panels.ButtonsPanel;

/**
 * Wizard for creating IntraExperiments.
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
public class WizardDialog extends CustomDialog {

	private static final long serialVersionUID = 1L;

	private List<IWizardStep> steps;

	final private SheetFactory factory;
	final private WizardToFile wizardToFile;

	// Pointer to actualSheet
	private int position;

	// Flag to indicate type of exit
	private boolean canExit;
	// Flag to check if exist a constantCondition in the Wizard
	private boolean isConstCond;

	private final JPanel content = new JPanel();
	private ButtonsPanel buttonsPanel;
	private final CardLayout cardLay = new CardLayout();
	private JButton next;
	// Labels to indicate the number of the pages
	private final JLabel currentPage = new JLabel("Page 1 of");
	private final JLabel totalPage = new JLabel("1");
	// Introduction label
	private final JLabel texto = new JLabel();
	private final String nextPage = I18n.get("next");
	private final String finalize = I18n.get("finalize");
	// Actual image
	private Icon setupImage;
	private Icon methodImage;
	private JLabel labelWizard;

	/**
	 * Default constructor when you create an empty Wizard.
	 */
	public WizardDialog() {
		super();

		setTitle(I18n.get("bewWizard"));

		this.steps = new ArrayList<IWizardStep>();
		this.position = 1;
		this.canExit = false;

		// Set introduction text
		this.texto.setText(I18n.get("createWizard"));

		factory = new SheetFactory();
		wizardToFile = new WizardToFile(this);

		initialize();
		initButtons("createIntra");

		List<JButton> buttonList = this.buttonsPanel.getPanelButtons();
		for (JButton button : buttonList) {
			if (button.getText().equals(nextPage)) {
				this.next = button;
				break;
			}
		}

		// This will be the default index page (a ConstantCondition Sheet)
		addSheet("cond");
		cardLay.show(content, I18n.get("conditionSheetName"));
	}

	/**
	 * Constructor used when you create a Wizard from a File.
	 * 
	 * @param steps
	 *            Sheets that the Wizard will load.
	 */
	public WizardDialog(List<IWizardStep> steps) {
		super();

		this.steps = new ArrayList<IWizardStep>();
		this.steps = steps;
		this.position = 1;
		this.canExit = false;

		this.texto.setText(I18n.get("importWizard"));

		factory = new SheetFactory();
		wizardToFile = new WizardToFile(this);

		initialize();
		insertStepsInLayout();
		initButtons("importExp");

		List<JButton> buttonList = this.buttonsPanel.getPanelButtons();
		for (JButton button : buttonList) {
			if (button.getText().equals(nextPage)) {
				this.next = button;
				if (steps.size() == 1)
					this.next.setText(finalize);
				break;
			}
		}

		// If there aren't ConstantConditions in the File, we must create one
		if (!checkConstant()) {
			addSheet("cond");
			// The first page must be the Setup so we need to decrease one value
			// (when the program add a page always increase 1, so it will show
			// page 2).
			decreasePage();
		}
		cardLay.show(content, I18n.get("conditionSheetName"));
	}

	/**
	 * Method to initialize dialog.
	 */
	public void initialize() {
		this.setModal(true);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		// JDialog dimensions
		this.setMinimumSize(new Dimension(960, 720));
		this.setMaximumSize(new Dimension(960, 720));
		this.setPreferredSize(new Dimension(960, 720));
		this.setAlwaysOnTop(false);
		this.setLocationRelativeTo(null);

		Container contentPane = this.getContentPane();
		contentPane.setLayout(new BorderLayout());

		// Header layout
		JPanel header = new JPanel();
		header.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		header.setBackground(Color.white);
		header.setLayout(new GridLayout(0, 3, 0, 0));

		// Header items
		// Header image
		labelWizard = new JLabel();
		labelWizard.setHorizontalAlignment(SwingConstants.CENTER);
		{
			ImageIcon setupIcon = new ImageIcon(getClass().getResource(
					"/img/wizardSetup.png"));
			ImageIcon methodIcon = new ImageIcon(getClass().getResource(
					"/img/wizardMethod.png"));
			setupImage = new ImageIcon(setupIcon.getImage().getScaledInstance(
					130, 100, Image.SCALE_DEFAULT));
			methodImage = new ImageIcon(methodIcon.getImage()
					.getScaledInstance(130, 100, Image.SCALE_DEFAULT));
			// Default image
			labelWizard.setIcon(setupImage);
		}
		header.add(labelWizard);

		// Header text
		texto.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 11));
		texto.setHorizontalAlignment(SwingConstants.CENTER);
		header.add(texto);

		// Pages panel
		JPanel pagesPanel = new JPanel();
		pagesPanel.setBackground(Color.WHITE);
		// Layout for Panel
		GridBagLayout gblPagesPanel = new GridBagLayout();
		{
			gblPagesPanel.columnWidths = new int[] { 50 };
			gblPagesPanel.rowHeights = new int[] { 50, 50 };
			gblPagesPanel.columnWeights = new double[] { 1.0, 1.0 };
			gblPagesPanel.rowWeights = new double[] { 0.0, 1.0 };
			pagesPanel.setLayout(gblPagesPanel);
		}
		// Layout for actual page
		GridBagConstraints gbcCurrentPage = new GridBagConstraints();
		{
			gbcCurrentPage.anchor = GridBagConstraints.EAST;
			gbcCurrentPage.insets = new Insets(0, 0, 0, 5);
			gbcCurrentPage.fill = GridBagConstraints.VERTICAL;
			gbcCurrentPage.gridx = 0;
			gbcCurrentPage.gridy = 1;
			pagesPanel.add(currentPage, gbcCurrentPage);
			currentPage.setHorizontalTextPosition(SwingConstants.RIGHT);
		}
		// Layout for total page
		GridBagConstraints gbcTotalPage = new GridBagConstraints();
		{
			gbcTotalPage.anchor = GridBagConstraints.WEST;
			gbcTotalPage.insets = new Insets(0, 0, 0, 5);
			gbcTotalPage.gridx = 1;
			gbcTotalPage.gridy = 1;
			pagesPanel.add(totalPage, gbcTotalPage);
			totalPage.setHorizontalTextPosition(SwingConstants.RIGHT);
		}
		header.add(pagesPanel);

		// Add CardLayout
		content.setLayout(cardLay);
		// Insert the layouts in the ContentPane
		contentPane.add(header, BorderLayout.NORTH);
		contentPane.add(content, BorderLayout.CENTER);
	}

	/**
	 * Initializes buttons.
	 */
	private void initButtons(String help) {
		String[] buttonNames = { I18n.get("previous"), I18n.get("next"),
				I18n.get("addMethod"), I18n.get("validate"),
				I18n.get("deletePage"), I18n.get("saveAs"), I18n.get("exit") };
		URL[] iconPaths = { getClass().getResource("/img/back.png"),
				getClass().getResource("/img/next.png"),
				getClass().getResource("/img/add.png"),
				getClass().getResource("/img/glass.png"),
				getClass().getResource("/img/delete.png"),
				getClass().getResource("/img/save.png"),
				getClass().getResource("/img/exit.png") };

		ActionListener[] listeners = { new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Previous button
				previousSheet();
			}
		}, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Next button
				nextSheet();
			}
		}, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Add method button
				addSheet("data");
			}
		}, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Validate button
				if (validateSheet())
					ShowDialog.showInfo(I18n.get("correctStructureTitle"),
							I18n.get("correctStructure"));
			}
		}, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Delete button
				deleteSheet();
			}
		}, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Save button
				if (validateSheet())
					wizardToFile.saveXMLData();
			}
		}, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Exit button
				exit();
			}
		} };

		this.buttonsPanel = new ButtonsPanel(buttonNames, iconPaths, listeners,
				help, this);
		// Add button pane
		getContentPane().add(this.buttonsPanel, BorderLayout.SOUTH);
	}

	/**
	 * Method to insert the Steps in Wizard inside the CardLayaout Panel.
	 */
	public void insertStepsInLayout() {
		// Insert in the content
		for (IWizardStep step : steps) {
			content.add(step.getPanel(), step.getSheetName());
		}

		totalPage.setText(String.valueOf(steps.size()));
		currentPage.setText("Page " + String.valueOf(position) + " of");

		repaint();
		revalidate();
	}

	/**
	 * Method to close the Wizard and exit. It ask the user for saving data.
	 */
	public void exit() {
		// Create a dialog to ask the user for saving data
		Object[] options = { I18n.get("yes"), "No", I18n.get("cancel") };
		int res = JOptionPane.showOptionDialog(this,
				I18n.get("saveBeforeQuit"), I18n.get("sure"),
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,
				null, options, options[0]);

		// n == YES
		if (res == 0) {
			// Save Experiment in a file (XLS/XML)
			this.wizardToFile.saveXMLData();
		}
		// n == NO
		else if (res == 1) {
			this.dispose();
		}
	}

	/**
	 * Method to select the next Sheet for the Wizard inside the CardLayout.
	 */
	private void nextSheet() {
		// If there are sheets...
		if (!steps.isEmpty() && validateSheet()) {
			// If we are in the last Sheet (Finalize)
			if (position == steps.size()) {
				// Flag becomes true and exit the Dialog
				this.canExit = true;
				this.dispose();
			} else {
				// If the current Sheet is not the last one...
				if (position < steps.size())
					increasePage();

				cardLay.next(content);
			}
		}
	}

	/**
	 * Method to validate the actual Sheet. If the sheet is an experimentSetup
	 * we must validate the other experiments in the Clipboard, because the name
	 * must be unique. If the sheet is a methodSheet we must validate if there
	 * are any other method with the same name for this experiment.
	 * 
	 * @return True if the Sheet is valid, else otherwise.
	 */
	private boolean validateSheet() {
		if (!steps.isEmpty()) {
			// Take the actual Sheet
			IWizardStep wStep = (IWizardStep) getCurrentComponent();
			// Validate ExperimentShet
			if (wStep.getSheetName().equals(I18n.get("conditionSheetName"))) {
				// Validate if Sheet is valid and it has a unique name from the
				// others experiment in the clipboard
				if (FunctionConstants.validateExperimentNames(wStep
						.getExpSetup()[0])) {
					if (wStep.next())
						return true;
				} else {
					ShowDialog.showError(I18n.get("duplicateNameTitle"),
							I18n.get("duplicateName"));
				}

			}
			// Validate MethodSheet
			else {
				// Call the interface next() method used to validate the Sheet
				if (wStep.next() && validateSheetName(wStep))
					return true;
			}
		} else if (steps.isEmpty())
			return true;

		return false;
	}

	/**
	 * 
	 * @param wStep
	 * @return
	 */
	private boolean validateSheetName(IWizardStep wStep) {
		String name = wStep.getSheetName();
		int index = 1;
		boolean repetition = true;
		List<String> names = new ArrayList<String>();

		for (IWizardStep step : steps) {
			if (!step.equals(wStep)) {
				names.add(step.getSheetName());
			}
		}

		while (repetition) {
			if (!names.contains(name)) {
				repetition = false;
			} else {
				name = wStep.getSheetName() + "_" + index;
				index++;
			}
		}

		wStep.setSheetName(name);
		return true;
	}

	/**
	 * Method to go to the previous Sheet in the Wizard.
	 */
	private void previousSheet() {
		if (!steps.isEmpty() && position > 1) {
			decreasePage();

			// if (next.getText().equals(this.finalizar))
			// next.setText(this.siguiente);
			//
			// paginaActual.setText("Page " + String.valueOf(contenidoPosicion)
			// + " of");

			cardLay.previous(content);
		}

	}

	/**
	 * Method to add a new Sheet to the Wizard. The current sheet must be valid.
	 * 
	 * @param type
	 *            Type of the Sheet (Data or other).
	 */
	private void addSheet(String type) {
		IWizardStep wStep;

		// Introducing a dataSheet and validate if the actual Sheet is valid
		if (type.equals("data") && validateSheet()) {
			// If String == data, we introduce a data sheet
			wStep = factory.newDataSheetConf();

			putWizardStep(wStep);

			cardLay.next(content);

		}
		// Else if String == condition, we introduce a condition sheet. We
		// must save its id and change our constant flag to true
		else {
			if (!this.isConstCond) {
				wStep = factory.newCondSheetConf();
				this.isConstCond = true;
				putWizardStep(wStep);
			}
		}
	}

	/**
	 * Private Method to add a new step to the StepsMap.
	 * 
	 * @param wStep
	 *            The IWizardStep to introduce.
	 * @param id
	 *            The Unique ID of the WS.
	 */
	private void putWizardStep(IWizardStep wStep) {
		this.steps.add(wStep);

		// Introduce in JPanel, name + component
		content.add(wStep.getSheetName(), wStep.getPanel());

		// Modify the total pages of the Wizard
		totalPage.setText(String.valueOf(steps.size()));

		increasePage();

		repaint();
		revalidate();
	}

	/**
	 * Method to delete the actual DataSheet.
	 */
	private void deleteSheet() {
		try {
			// If there is something to remove...
			if (!steps.isEmpty()) {
				// Take the actual Sheet
				Component currentSheet = getCurrentComponent();
				IWizardStep wStep = (IWizardStep) currentSheet;

				// The actual Sheet mustn't be the ConstantCondition Sheet (We
				// can't delete it)
				if (!isConstantSheet(wStep)) {
					// Delete it from the StepsMap
					steps.remove(wStep);

					// Show the previous sheet before remove
					decreasePage();

					cardLay.previous(content);
					// Remove it from the JPanel
					content.remove(currentSheet);

					repaint();
					revalidate();
				}

			}
		} catch (ArrayIndexOutOfBoundsException e1) {

		}
	}

	/**
	 * Method to obtain the current sheet in the Wizard.
	 * 
	 * @return Component, the actual visible sheet. Null, otherwise.
	 */
	public Component getCurrentComponent() {
		Component[] components = content.getComponents();

		for (Component c : components) {
			if (c.isVisible()) {
				return c;
			}
		}
		return null;
	}

	/**
	 * Method to check is the entry Sheet is a ConstantSheet.
	 * 
	 * @param wStep
	 *            The IWizardStep to check.
	 * @return True if the sheet is constant, else otherwise.
	 */
	private boolean isConstantSheet(IWizardStep wStep) {
		if (wStep.getSheetName().equals(I18n.get("conditionSheetName")))
			return true;

		return false;
	}

	/**
	 * Set steps.
	 * 
	 * @param steps
	 */
	public void setSteps(List<IWizardStep> steps) {
		this.steps.clear();
		this.steps.addAll(steps);
	}

	/**
	 * Get steps.
	 * 
	 * @return
	 */
	public List<IWizardStep> getSteps() {
		return this.steps;
	}

	/**
	 * Get exit.
	 * 
	 * @return
	 */
	public boolean isExit() {
		return this.canExit;
	}

	/**
	 * Check constant.
	 * 
	 * @return
	 */
	public boolean isConstant() {
		return isConstCond;
	}

	/**
	 * Method to check if the Wizard has a ConstantCondition Sheet already
	 * created.
	 */
	private boolean checkConstant() {
		for (IWizardStep ws : this.steps) {
			if (ws.isConstant()) {
				this.isConstCond = true;
				return true;
			}
		}
		return false;
	}

	/**
	 * Set constant.
	 * 
	 * @param isConstant
	 */
	public void setConstant(boolean isConstant) {
		this.isConstCond = isConstant;
	}

	/**
	 * Pass to the next page if possible.
	 */
	public void increasePage() {
		if (position + 1 <= steps.size())
			this.position++;

		// Modify buttons
		if (this.position == steps.size())
			next.setText(this.finalize);
		else
			next.setText(this.nextPage);

		// Modify pages
		currentPage.setText("Page " + String.valueOf(position) + " of");

		// Modify image
		if (position == 1)
			labelWizard.setIcon(setupImage);
		else
			labelWizard.setIcon(methodImage);
	}

	/**
	 * Pass to the previous page if possible.
	 */
	public void decreasePage() {
		this.position--;

		// Modify buttons
		if ((position < 0 || position > steps.size()) && !steps.isEmpty())
			position = steps.size();

		if (position == steps.size())
			next.setText(this.finalize);
		else
			next.setText(this.nextPage);

		// Modify pages
		totalPage.setText(String.valueOf(steps.size()));

		currentPage.setText("Page " + String.valueOf(position) + " of");

		// Modify image
		if (position == 1)
			labelWizard.setIcon(setupImage);
		else
			labelWizard.setIcon(methodImage);
	}
}
