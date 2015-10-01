package es.uvigo.ei.sing.bew.view.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import es.uvigo.ei.aibench.core.Core;
import es.uvigo.ei.aibench.core.clipboard.ClipboardItem;
import es.uvigo.ei.sing.bew.constants.I18n;
import es.uvigo.ei.sing.bew.model.Experiment;
import es.uvigo.ei.sing.bew.model.IExperiment;
import es.uvigo.ei.sing.bew.model.Method;

/**
 * Custom panel for selecting a method from an experiment.
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
public class SelectExpAndMethodPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private final static String EMPTY_METHOD = "New empty Method";

	private JComboBox<String> comboMethod;
	private JComboBox<ClipboardItem> comboExp;

	private Method selectedMethod;
	private String selMethodName;
	private IExperiment selectedExp;

	private boolean onlyIntraExp;
	private boolean isNewMethod;

	/**
	 * Default constructor.
	 */
	public SelectExpAndMethodPanel(boolean onlyIntraExp, boolean isNewMethod) {
		super();

		this.onlyIntraExp = onlyIntraExp;
		this.isNewMethod = isNewMethod;

		initialize();
	}

	/**
	 * Method to initialize dialog.
	 */
	public void initialize() {
		setSize(300, 50);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 150, 150, 0 };
		gridBagLayout.rowHeights = new int[] { 25, 25, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);
		{

			JLabel lblSelectExp = new JLabel(I18n.get("selectExperiment"));
			lblSelectExp.setHorizontalAlignment(SwingConstants.CENTER);
			GridBagConstraints gbcLblSelectExp = new GridBagConstraints();
			gbcLblSelectExp.insets = new Insets(0, 0, 5, 5);
			gbcLblSelectExp.gridx = 0;
			gbcLblSelectExp.gridy = 0;
			add(lblSelectExp, gbcLblSelectExp);

		}
		comboExp = new JComboBox<ClipboardItem>();
		comboExp.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {
				// Get selected Experiment
				ClipboardItem item = (ClipboardItem) comboExp.getSelectedItem();
				selectedExp = (IExperiment) item.getUserData();

				// Set in comboMethod the methods to the selected
				// experiment
				loadMethods();
			}
		});

		GridBagConstraints gbcComboExp = new GridBagConstraints();
		gbcComboExp.fill = GridBagConstraints.HORIZONTAL;
		gbcComboExp.insets = new Insets(0, 0, 5, 0);
		gbcComboExp.gridx = 1;
		gbcComboExp.gridy = 0;
		add(comboExp, gbcComboExp);
		{

			JLabel lblSelectMethod = new JLabel(I18n.get("selectMethod"));
			lblSelectMethod.setHorizontalAlignment(SwingConstants.CENTER);
			GridBagConstraints gbcLblMethod = new GridBagConstraints();
			gbcLblMethod.insets = new Insets(0, 0, 0, 5);
			gbcLblMethod.gridx = 0;
			gbcLblMethod.gridy = 1;
			add(lblSelectMethod, gbcLblMethod);

			comboMethod = new JComboBox<String>();
			comboMethod.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent arg0) {
					if (arg0.getStateChange() == ItemEvent.SELECTED)
						selMethodName = (String) comboMethod.getSelectedItem();
				}
			});

			GridBagConstraints gbcComboMethod = new GridBagConstraints();
			gbcComboMethod.fill = GridBagConstraints.HORIZONTAL;
			gbcComboMethod.gridx = 1;
			gbcComboMethod.gridy = 1;
			add(comboMethod, gbcComboMethod);

			// Filling JCombos
			fillComboExp();
		}
	}

	/**
	 * Fills the combo with the cliboard Experiments.
	 */
	private void fillComboExp() {
		List<ClipboardItem> items;
		if (onlyIntraExp) {
			// Obtain all methods of intraExperiments
			items = Core.getInstance().getClipboard()
					.getItemsByClass(Experiment.class);
		} else {
			// Obtain all methods of all Experiments
			items = Core.getInstance().getClipboard()
					.getItemsByClass(IExperiment.class);
		}

		if (!items.isEmpty()) {
			// Add all the methods to the ComboBox
			for (ClipboardItem item : items) {
				// We save the first experiment (selected by default)
				if (selectedExp == null)
					selectedExp = (IExperiment) item.getUserData();

				this.comboExp.addItem(item);
			}

			// We load the methods for selected experiment
			loadMethods();
		}
	}

	/**
	 * Puts in the combo the Experiment methods.
	 */
	private void loadMethods() {
		// Purge ComboMethods
		comboMethod.removeAllItems();

		// Load the methods for the selected experiment
		List<Method> list = selectedExp.getMethods().getMetodos();

		for (Method method : list) {
			// We save the first selected method (selected by default)
			if (selMethodName == null)
				selMethodName = method.getName();

			this.comboMethod.addItem(method.getName());
		}

		if (onlyIntraExp && isNewMethod) {
			// If we are loading only intraExps (from addMethod operation)
			this.comboMethod.addItem(EMPTY_METHOD);
		}
	}

	/**
	 * Method to finish the dialog. This method validates if is something
	 * selected.
	 * 
	 * @return True if something is selected, false otherwise.
	 */
	public boolean finish() {
		if (this.selMethodName != null) {
			if (this.selMethodName.equals(EMPTY_METHOD)) {
				selectedMethod = null;
				return true;
			} else {
				List<Method> methods = selectedExp.getMethods().getMetodos();

				for (Method m : methods) {
					if (m.getName().equals(selMethodName)) {
						selectedMethod = m;
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Get selected method.
	 * 
	 * @return
	 */
	public Method getSelectedMethod() {
		return selectedMethod;
	}

	/**
	 * Get selected experiment.
	 * 
	 * @return
	 */
	public IExperiment getSelectedExp() {
		return selectedExp;
	}

	/**
	 * Get selected method name.
	 * 
	 * @return
	 */
	public String getSelectedMethodName() {
		return selMethodName;
	}

	/**
	 * 
	 * @param methodName
	 */
	public void addMethodToCombo(String methodName) {
		this.comboMethod.addItem(methodName);
	}
}
