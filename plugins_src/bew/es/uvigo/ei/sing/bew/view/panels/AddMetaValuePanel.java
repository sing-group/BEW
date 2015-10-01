package es.uvigo.ei.sing.bew.view.panels;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import es.uvigo.ei.sing.bew.constants.BewConstants;
import es.uvigo.ei.sing.bew.constants.FunctionConstants;
import es.uvigo.ei.sing.bew.view.components.CustomTextField;

/**
 * Custom panel to use with CreateCondValueDialog.class
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
public class AddMetaValuePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JPanel panelCondValues;

	private List<CustomTextField> createdValues;
	private int gridY;

	/**
	 * Create the panel.
	 */
	public AddMetaValuePanel() {
		super();

		this.createdValues = new ArrayList<CustomTextField>();

		init();
	}

	/**
	 * Initializes the panel.
	 */
	private void init() {
		setLayout(new BorderLayout(0, 0));
		setBorder(new TitledBorder(null, "Introduce values",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		{
			JPanel panelButton = new JPanel();
			add(panelButton, BorderLayout.NORTH);
			panelButton.setLayout(new BoxLayout(panelButton, BoxLayout.X_AXIS));

			{
				JButton btnNewValue = new JButton("New Condition value");
				btnNewValue.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						createNewValue();

						repaint();
						revalidate();
					}
				});
				panelButton.add(btnNewValue);

				JButton btnReset = new JButton("Clear all");
				btnReset.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						panelCondValues.removeAll();
						gridY = 0;
						createdValues.clear();
					}
				});
				panelButton.add(btnReset);
			}
		}
		{
			panelCondValues = new JPanel();
			GridBagLayout gblPanelValues = new GridBagLayout();
			gblPanelValues.columnWidths = new int[] { 25, 25, 25 };
			gblPanelValues.rowHeights = new int[] {};
			gblPanelValues.columnWeights = new double[] { 0.0, 0.0, 1.0, 0.0 };
			gblPanelValues.rowWeights = new double[] { 0.0 };
			panelCondValues.setLayout(gblPanelValues);
			add(panelCondValues, BorderLayout.CENTER);
		}
	}

	/**
	 * Create the structure in the panel to introduce a new value.
	 */
	private void createNewValue() {
		final JLabel lblCondValue = new JLabel("Introduce Condition value:");
		GridBagConstraints gbcLblNewLabel = new GridBagConstraints();
		gbcLblNewLabel.insets = new Insets(0, 0, 0, 5);
		gbcLblNewLabel.gridx = 1;
		gbcLblNewLabel.gridy = gridY;
		panelCondValues.add(lblCondValue, gbcLblNewLabel);

		final CustomTextField textField = new CustomTextField("");
		textField.setPlaceholder(BewConstants.DEFAULT_VALUE);
		GridBagConstraints gbcTextField = new GridBagConstraints();
		gbcTextField.fill = GridBagConstraints.HORIZONTAL;
		gbcTextField.insets = new Insets(0, 0, 0, 5);
		gbcTextField.gridx = 2;
		gbcTextField.gridy = gridY;
		panelCondValues.add(textField, gbcTextField);
		textField.setColumns(10);
		// Add textField dynamically
		createdValues.add(textField);

		final JButton btnDeleteValue = new JButton("Delete");
		btnDeleteValue.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				panelCondValues.remove(lblCondValue);

				createdValues.remove(textField);
				panelCondValues.remove(textField);

				panelCondValues.remove(btnDeleteValue);

				gridY--;

				repaint();
				revalidate();
			}
		});

		GridBagConstraints gbcBtnNewButton = new GridBagConstraints();
		gbcBtnNewButton.insets = new Insets(0, 0, 0, 5);
		gbcBtnNewButton.gridx = 3;
		gbcBtnNewButton.gridy = gridY;
		panelCondValues.add(btnDeleteValue, gbcBtnNewButton);

		gridY++;
	}

	/**
	 * Get created values for the user and put them between *.
	 * 
	 * @return List<String> with the created values.
	 */
	public List<String> getCreatedValues() {
		List<String> toRet = new ArrayList<String>();
		String text = "";
		for (CustomTextField field : createdValues) {
			text = field.getText().trim();
			// Validate values characters
			if (FunctionConstants.simbolValidationInCond(text)
					&& !text.isEmpty()) {
				toRet.add(FunctionConstants.putAsterisks(text));
			} else {
				// If someone is invalid, clear the list
				toRet.clear();
				break;
			}
		}

		return toRet;
	}
}
