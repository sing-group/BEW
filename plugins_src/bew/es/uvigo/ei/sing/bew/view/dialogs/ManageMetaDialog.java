package es.uvigo.ei.sing.bew.view.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import es.uvigo.ei.sing.bew.constants.BewConstants;
import es.uvigo.ei.sing.bew.constants.I18n;
import es.uvigo.ei.sing.bew.view.components.CustomDialog;
import es.uvigo.ei.sing.bew.view.components.IManageMeta;
import es.uvigo.ei.sing.bew.view.components.TextPreviewer;
import es.uvigo.ei.sing.bew.view.panels.ButtonsPanel;

/**
 * Custom dialog to create Condition values.
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
public class ManageMetaDialog extends CustomDialog {

	private static final long serialVersionUID = 1L;

	private JTabbedPane tabbedPaneValues;
	private TextPreviewer textPreviewer;
	private IManageMeta manageComponent;

	private boolean canExit;
	private String metadata;

	/**
	 * Create the panel.
	 */
	public ManageMetaDialog(String metadata, IManageMeta manageComponent) {
		super();

		this.metadata = metadata;
		this.manageComponent = manageComponent;

		try {
			commonInit();
			initButtons();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @throws Exception
	 * 
	 */
	private void commonInit() throws Exception {
		setMinimumSize(new Dimension(800, 600));
		getContentPane().setLayout(new BorderLayout(0, 0));
		this.setModal(true);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setAlwaysOnTop(false);

		{
			tabbedPaneValues = new JTabbedPane(JTabbedPane.TOP);
			getContentPane().add(tabbedPaneValues, BorderLayout.CENTER);
		}

		// Call private init
		if (BewConstants.CONDITIONFILE.equals(metadata)) {
			init("Manage Conditions",
					"Visualize Conditions",
					"Add Conditions",
					ManageMetaDialog.class.getResource(
							"/files/" + BewConstants.CONDITIONFILE).toString());
		} else if (BewConstants.VALUESFILE.equals(metadata)) {
			init("Manage Condition values",
					"Visualize values",
					"Add values",
					ManageMetaDialog.class.getResource(
							"/files/" + BewConstants.VALUESFILE).toString());
		} else if (BewConstants.METHODFILE.equals(metadata)) {
			init("Manage Methods",
					"Visualize Methods",
					"Add Methods",
					ManageMetaDialog.class.getResource(
							"/files/" + BewConstants.METHODFILE).toString());
		}
	}

	/**
	 * Initializes the dialog.
	 * 
	 * @throws Exception
	 */
	private void init(String title, String visualize, String addition,
			String file) throws Exception {
		setTitle("Add a value to a nominal Condition");

		{
			this.textPreviewer = new TextPreviewer();
			textPreviewer.configure(new File(file.substring(5)));
			tabbedPaneValues.addTab(visualize, textPreviewer);
		}
		{
			tabbedPaneValues.addTab(addition, (JPanel) manageComponent);
		}
	}

	/**
	 * Initializes buttons.
	 */
	private void initButtons() {
		String[] buttonNames = { I18n.get("finalize"), I18n.get("cancel") };
		URL[] buttonIcons = { getClass().getResource("/img/accept.png"),
				getClass().getResource("/img/cancel.png") };
		ActionListener[] buttonListeners = { new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (finish()) {
					canExit = true;
					dispose();
				}
			}
		}, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				canExit = false;
				dispose();
			}
		} };

		if (metadata.equals(BewConstants.METHODFILE)) {
			getContentPane().add(
					new ButtonsPanel(buttonNames, buttonIcons, buttonListeners,
							"manageMethods", this), BorderLayout.SOUTH);
		} else if (metadata.equals(BewConstants.VALUESFILE)) {
			getContentPane().add(
					new ButtonsPanel(buttonNames, buttonIcons, buttonListeners,
							"addCondValue", this), BorderLayout.SOUTH);
		} else if (metadata.equals(BewConstants.CONDITIONFILE)) {
			getContentPane().add(
					new ButtonsPanel(buttonNames, buttonIcons, buttonListeners,
							"addCond", this), BorderLayout.SOUTH);
		}
	}

	/**
	 * 
	 * @return
	 */
	private boolean finish() {
		return manageComponent.finish();
	}

	/**
	 * 
	 * @return
	 */
	public boolean isExit() {
		return canExit;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isNominal() {
		return manageComponent.isNominal();
	}

	/**
	 * 
	 * @return
	 */
	public String getName() {
		return manageComponent.getName();
	}

	/**
	 * 
	 * @return
	 */
	public String getUnits() {
		return manageComponent.getUnits();
	}

	/**
	 * 
	 * @return
	 */
	public Map<String, List<String>> getCondAndValues() {
		return manageComponent.getCondAndValues();
	}

	/**
	 * 
	 * @return
	 */
	public List<String> getCondValues() {
		return manageComponent.getCondValues();
	}
}
