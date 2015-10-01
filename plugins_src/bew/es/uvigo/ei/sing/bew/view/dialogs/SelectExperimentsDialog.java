package es.uvigo.ei.sing.bew.view.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import es.uvigo.ei.aibench.core.Core;
import es.uvigo.ei.aibench.core.clipboard.ClipboardItem;
import es.uvigo.ei.sing.bew.constants.I18n;
import es.uvigo.ei.sing.bew.constants.ShowDialog;
import es.uvigo.ei.sing.bew.model.Condition;
import es.uvigo.ei.sing.bew.model.Experiment;
import es.uvigo.ei.sing.bew.model.IExperiment;
import es.uvigo.ei.sing.bew.model.Method;
import es.uvigo.ei.sing.bew.view.components.CustomDialog;
import es.uvigo.ei.sing.bew.view.panels.ButtonsPanel;

/**
 * Custom dialog for selecting the intraExperiment in a comparison.
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
public class SelectExperimentsDialog extends CustomDialog {

	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();

	private JLabel lblNumExp;
	private JScrollPane experimentScroll;
	private JPanel experimentPane;
	private JSpinner spinnerExp;
	private JButton btnCreateFields;

	private List<ClipboardItem> experimentList;
	// List to save the dynamic methodComboBoxes.
	private List<JComboBox<ClipboardItem>> dynamicCombos;

	// Variable to save methods parents. Selected experiments must be
	// distinct.
	private List<IExperiment> selExperiments;
	private List<Method> selectedMethods;
	private Map<String, Color> experimentColors;

	private int numberExps;
	private boolean canExit;

	/**
	 * Default constructor.
	 */
	public SelectExperimentsDialog() {
		super();
		
		this.experimentList = Core.getInstance().getClipboard()
				.getItemsByClass(Experiment.class);
		this.numberExps = this.experimentList.size();
		this.selExperiments = new ArrayList<IExperiment>();
		this.selectedMethods = new ArrayList<Method>();
		this.experimentColors = new HashMap<String, Color>();
		this.dynamicCombos = new ArrayList<JComboBox<ClipboardItem>>();

		init();
		initButtons();
	}

	/**
	 * Method to initialize dialog.
	 */
	public void init() {
		setTitle("Select IntraExperiments to compare");
		setSize(640, 480);
		setModal(true);
		setLocationRelativeTo(null);

		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JPanel typePane = new JPanel();
			typePane.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null,
					null));
			contentPanel.add(typePane, BorderLayout.NORTH);
			GridBagLayout gblTypePane = new GridBagLayout();
			gblTypePane.columnWidths = new int[] { 210, 150, 60 };
			gblTypePane.rowHeights = new int[] { 20, 20 };
			gblTypePane.columnWeights = new double[] { 1.0, 1.0, 1.0 };
			gblTypePane.rowWeights = new double[] { 1.0, 1.0 };
			typePane.setLayout(gblTypePane);
			{
				lblNumExp = new JLabel(I18n.get("experimentsFound")
						+ String.valueOf(numberExps));
				lblNumExp.setFont(new Font("Tahoma", Font.ITALIC, 11));
				lblNumExp.setHorizontalAlignment(SwingConstants.RIGHT);
				GridBagConstraints gbcLblNumExp = new GridBagConstraints();
				gbcLblNumExp.gridwidth = 2;
				gbcLblNumExp.insets = new Insets(0, 0, 5, 10);
				gbcLblNumExp.fill = GridBagConstraints.BOTH;
				gbcLblNumExp.gridx = 1;
				gbcLblNumExp.gridy = 0;
				typePane.add(lblNumExp, gbcLblNumExp);
			}
			{
				JLabel lblIntroduceExp = new JLabel(
						I18n.get("introduceNumberExps"));
				lblIntroduceExp.setHorizontalAlignment(SwingConstants.CENTER);
				GridBagConstraints gbcLblIntroExp = new GridBagConstraints();
				gbcLblIntroExp.fill = GridBagConstraints.HORIZONTAL;
				gbcLblIntroExp.insets = new Insets(0, 0, 0, 5);
				gbcLblIntroExp.gridx = 0;
				gbcLblIntroExp.gridy = 1;
				typePane.add(lblIntroduceExp, gbcLblIntroExp);
			}
			{
				spinnerExp = new JSpinner();
				spinnerExp.setModel(new SpinnerNumberModel(new Integer(0),
						new Integer(0), null, new Integer(1)));
				GridBagConstraints gbcSpinnerExp = new GridBagConstraints();
				gbcSpinnerExp.fill = GridBagConstraints.BOTH;
				gbcSpinnerExp.insets = new Insets(0, 0, 0, 5);
				gbcSpinnerExp.gridx = 1;
				gbcSpinnerExp.gridy = 1;
				typePane.add(spinnerExp, gbcSpinnerExp);
			}
			{
				btnCreateFields = new JButton(I18n.get("createFields"));

				// Listener for clicking button
				btnCreateFields.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						int spinnerValue = (int) spinnerExp.getValue();

						// Remove the panel
						experimentPane.removeAll();
						experimentPane.repaint();
						experimentPane.revalidate();
						dynamicCombos.clear();

						if (spinnerValue <= numberExps)
							createFields(spinnerValue);
					}
				});

				GridBagConstraints gbcBtnButton = new GridBagConstraints();
				gbcBtnButton.fill = GridBagConstraints.BOTH;
				gbcBtnButton.gridx = 2;
				gbcBtnButton.gridy = 1;
				typePane.add(btnCreateFields, gbcBtnButton);
			}
		}
		{
			experimentPane = new JPanel();
			experimentPane.setBorder(new EtchedBorder(EtchedBorder.LOWERED,
					null, null));
			GridBagLayout gblExpPane = new GridBagLayout();
			gblExpPane.columnWidths = new int[] { 210, 210 };
			gblExpPane.rowHeights = new int[] { 0 };
			gblExpPane.columnWeights = new double[] { Double.MIN_VALUE,
					1.0 };
			gblExpPane.rowWeights = new double[] { 0.0 };
			experimentPane.setLayout(gblExpPane);
		}
		{
			experimentScroll = new JScrollPane(experimentPane);
			contentPanel.add(experimentScroll, BorderLayout.CENTER);
		}
	}

	/**
	 * Initializes buttons.
	 */
	private void initButtons() {
		String[] buttonNames = { "Ok", I18n.get("cancel") };
		URL[] buttonIcons = { getClass().getResource("/img/accept.png"),
				getClass().getResource("/img/cancel.png") };
		ActionListener[] buttonListeners = { new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Validate that the methods are the same with different
				// parents
				if (retrieveMethodsAndExperiments()) {
					// Validate that the methods have the same
					// conditions
					if (areSameConditions()) {
						if (validateSelectedColors()) {
							// Flag to exit the dialog
							canExit = true;
							dispose();
						} else
							selectedMethods.clear();
					} else
						selectedMethods.clear();
				} else
					selectedMethods.clear();
			}
		}, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				canExit = false;
				dispose();
			}
		} };

		getContentPane().add(
				new ButtonsPanel(buttonNames, buttonIcons, buttonListeners,
						"compareIntra", this), BorderLayout.SOUTH);
	}

	/**
	 * Creates different fields in the dialog according to the input value.
	 * 
	 * @param spinnerValue
	 *            Value from sppiner.
	 */
	private void createFields(int spinnerValue) {

		int index = 0;
		// Creating the pair of components (Experiment + Method)
		for (int i = 0; i < spinnerValue * 3; i = i + 3) {
			// Create label experiment
			JLabel lblNewLabel = new JLabel(I18n.get("selectExperiment")
					+ index + " :");
			lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
			GridBagConstraints gbcLblLabel = new GridBagConstraints();
			gbcLblLabel.anchor = GridBagConstraints.CENTER;
			gbcLblLabel.insets = new Insets(0, 0, 5, 5);
			gbcLblLabel.gridx = 0;
			gbcLblLabel.gridy = i;
			experimentPane.add(lblNewLabel, gbcLblLabel);
			// End create label

			// Create combo experiment
			final JComboBox comboBox = new JComboBox();
			comboBox.setPrototypeDisplayValue("xxxxxxxxxxxxxxxxxxx");
			GridBagConstraints gbcComboBox = new GridBagConstraints();
			gbcComboBox.insets = new Insets(0, 0, 5, 0);
			gbcComboBox.fill = GridBagConstraints.HORIZONTAL;
			gbcComboBox.gridx = 1;
			gbcComboBox.gridy = i;
			experimentPane.add(comboBox, gbcComboBox);
			fillComboExperiment(comboBox);
			// End create combo

			// Create label method
			JLabel lblLabel1 = new JLabel(I18n.get("selectMethod") + index
					+ " :");
			lblLabel1.setHorizontalAlignment(SwingConstants.CENTER);
			GridBagConstraints gbcLblLabel1 = new GridBagConstraints();
			gbcLblLabel1.anchor = GridBagConstraints.CENTER;
			gbcLblLabel1.insets = new Insets(0, 0, 5, 5);
			gbcLblLabel1.gridx = 0;
			gbcLblLabel1.gridy = i + 1;
			experimentPane.add(lblLabel1, gbcLblLabel1);
			// End create label

			// Create combo method
			final JComboBox comboBox2 = new JComboBox();
			comboBox2.setPrototypeDisplayValue("xxxxxxxxxxxxxxxxxxx");
			GridBagConstraints gbcComboBox2 = new GridBagConstraints();
			gbcComboBox2.insets = new Insets(0, 0, 5, 0);
			gbcComboBox2.fill = GridBagConstraints.HORIZONTAL;
			gbcComboBox2.gridx = 1;
			gbcComboBox2.gridy = i + 1;
			experimentPane.add(comboBox2, gbcComboBox2);
			fillComboMethod((ClipboardItem) comboBox.getSelectedItem(),
					comboBox2);
			// End create combo

			// Create color chooser
			JLabel lblLabel2 = new JLabel(I18n.get("selectColor"));
			lblLabel2.setHorizontalAlignment(SwingConstants.CENTER);
			GridBagConstraints gbcLblLabel2 = new GridBagConstraints();
			gbcLblLabel2.anchor = GridBagConstraints.CENTER;
			gbcLblLabel2.insets = new Insets(0, 0, 20, 5);
			gbcLblLabel2.gridx = 0;
			gbcLblLabel2.gridy = i + 2;
			experimentPane.add(lblLabel2, gbcLblLabel2);

			JButton btnColor = new JButton(I18n.get("clickMe"));
			btnColor.setName(String.valueOf(index));
			GridBagConstraints gbcBtnColor = new GridBagConstraints();
			gbcBtnColor.insets = new Insets(0, 0, 20, 0);
			gbcBtnColor.fill = GridBagConstraints.HORIZONTAL;
			gbcBtnColor.gridx = 1;
			gbcBtnColor.gridy = i + 2;
			experimentPane.add(btnColor, gbcBtnColor);
			btnColor.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					Color color = JColorChooser.showDialog(null,
							I18n.get("pickColor"), Color.WHITE);
					JButton button = (JButton) arg0.getSource();

					if (color != null) {
						button.setBackground(color);
						experimentColors.put(button.getName(), color);
					}
				}
			});
			// End color chooser

			comboBox.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent arg0) {
					ClipboardItem exp = (ClipboardItem) comboBox
							.getSelectedItem();

					fillComboMethod(exp, comboBox2);
				}
			});

			// Add methodCombo to the list
			dynamicCombos.add(comboBox2);

			index++;
		}

		// Repaint the panel
		experimentScroll.repaint();
		experimentScroll.revalidate();
	}

	/**
	 * Method to fill the experiment combo box with all the experiments created
	 * in the Clipboard.
	 * 
	 * @param combo
	 *            JComboBox, to fill with the experiments.
	 */
	private void fillComboExperiment(JComboBox<ClipboardItem> combo) {
		for (ClipboardItem item : experimentList) {
			combo.addItem(item);
		}
	}

	/**
	 * Method to fill the method combo box. This function put the methods of the
	 * input experiment.
	 * 
	 * @param exp
	 *            ClipboardItem, input experiment to get methods.
	 * @param comboMethod
	 *            JComboBox, to fill with methods.
	 */
	private void fillComboMethod(ClipboardItem exp,
			JComboBox<ClipboardItem> comboMethod) {
		// Remove comboMethod
		comboMethod.removeAllItems();

		// Get all methods in the Clipboard
		List<ClipboardItem> list = Core.getInstance().getClipboard()
				.getItemsByClass(Method.class);
		ClipboardItem parent;

		// Go over the methods
		for (ClipboardItem m : list) {
			// Get the parent class (Method list)
			parent = Core.getInstance().getClipboard().getParent(m);

			// Validate if the parent class of the list (Experiment) its equals
			// to the input
			if (Core.getInstance().getClipboard().getParent(parent).equals(exp)) {
				comboMethod.addItem(m);
			}
		}
	}

	/**
	 * Method to validate the methods in the comboBoxes. The selected methods
	 * must be the same in each comboBox. Besides, the methods must have
	 * different parents (Experiment).
	 * 
	 * @return
	 */
	private boolean retrieveMethodsAndExperiments() {
		this.selExperiments.clear();
		// We compare all the other methods with this
		Method referenceMethod;
		Method aux;

		// Minimum 2 experiments
		if (this.dynamicCombos.size() > 1) {
			// Get the referenceMethod
			ClipboardItem item = (ClipboardItem) dynamicCombos.get(0)
					.getSelectedItem();
			referenceMethod = (Method) item.getUserData();

			// Retrieve the selected methods for the comboBoxes
			for (JComboBox<ClipboardItem> combo : this.dynamicCombos) {
				// Get selected item in the comboBox
				item = (ClipboardItem) combo.getSelectedItem();
				aux = (Method) item.getUserData();

				// Validate the selected methods in the lists are the same
				if (selectedMethods.size() == 0
						|| referenceMethod.getName().equals(aux.getName())) {
					// Cast to method and save it
					selectedMethods.add(aux);

					// Validate if experiments are different
					if (!this.selExperiments
							.contains(aux.getParent())) {
						this.selExperiments.add(aux.getParent());
					} else {
						ShowDialog.showError(I18n.get("differentExpTitle"),
								I18n.get("differentExp"));
						return false;
					}
				} else {
					ShowDialog.showError(I18n.get("differentMethodsTitle"),
							I18n.get("differentMethods"));
					return false;
				}
			}
			return true;
		}
		ShowDialog.showError(I18n.get("oneExpTitle"), I18n.get("oneExp"));
		return false;
	}

	/**
	 * Method to validate if the selected methods have the same conditions and
	 * the same condition values. Besides, they must have the same number of
	 * conditions.
	 * 
	 * @return
	 */
	private boolean areSameConditions() {
		// We compare all the other methods with this
		Method referenceMethod = selectedMethods.get(0);
		List<Condition> conditions = referenceMethod.getArrayCondition()
				.getElements();

		Method auxMethod;
		List<Condition> auxConditions;
		boolean valid = true; // Flag to break the loop
		int index = 0;
		while (index < selectedMethods.size()) {
			// Go over the methods
			auxMethod = selectedMethods.get(index);

			// If the number are the same, we must compare the condition values
			if (auxMethod.getNumConditions() == referenceMethod
					.getNumConditions()) {
				auxConditions = auxMethod.getArrayCondition().getElements();

				// Here we compare the condition and values of each method. Must
				// be the same
				valid = compareConditionLists(conditions, auxConditions);
			}
			// If the methods have different number of conditions, they cannot
			// be compared
			else {
				ShowDialog.showError(I18n.get("differentCondTitle"),
						I18n.get("differentCond"));
				return false;
			}

			index++;
		}

		return valid;
	}

	/**
	 * 
	 * @return
	 */
	private boolean validateSelectedColors() {
		if (this.experimentColors.size() == this.dynamicCombos.size()) {
			return true;
		} else {
			ShowDialog.showError(I18n.get("noColorsTitle"),
					I18n.get("noColors"));
			return false;
		}
	}

	/**
	 * Method to compare the two input lists. The method see each value and
	 * validate if they are the same.
	 * 
	 * @param list1
	 *            .
	 * @param list2
	 *            .
	 * @return
	 */
	private boolean compareConditionLists(List<Condition> list1,
			List<Condition> list2) {
		Condition aux1;
		Condition aux2;
		for (int i = 0; i < list1.size(); i++) {
			aux1 = list1.get(i);
			aux2 = list2.get(i);

			if (!aux1.getName().equals(aux2.getName())) {
				ShowDialog.showError(I18n.get("differentCondTitle"),
						I18n.get("differentCond"));
				return false;
			}
		}

		return true;
	}

	/**
	 * Get selected methods.
	 * 
	 * @return Object[].
	 */
	public Object[] getSelectedMethods() {
		return selectedMethods.toArray();
	}

	/**
	 * Get selected intraExperiments.
	 * 
	 * @return
	 */
	public Object[] getSelectedIntraExperiments() {
		return selExperiments.toArray();
	}

	/**
	 * Get selected colors.
	 * 
	 * @return
	 */
	public Object[] getExperimentColors() {
		Object[] colors = this.experimentColors.values().toArray();
		Object[] toRet = new Object[colors.length];

		int index = 0;
		for (int i = colors.length - 1; i >= 0; i--) {
			toRet[index] = colors[i];
			index++;
		}

		return toRet;
	}

	/**
	 * Check exit.
	 * 
	 * @return
	 */
	public boolean isExit() {
		return canExit;
	}
}
