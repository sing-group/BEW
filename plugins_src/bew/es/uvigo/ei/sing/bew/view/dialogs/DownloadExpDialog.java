package es.uvigo.ei.sing.bew.view.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.AuthenticationException;
import javax.naming.ServiceUnavailableException;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import org.jsoup.Jsoup;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import es.uvigo.ei.sing.bew.constants.BewConstants;
import es.uvigo.ei.sing.bew.constants.I18n;
import es.uvigo.ei.sing.bew.constants.ShowDialog;
import es.uvigo.ei.sing.bew.constants.WebServicesConstants;
import es.uvigo.ei.sing.bew.tables.renderer.ListStripeRenderer;
import es.uvigo.ei.sing.bew.view.components.CustomDialog;
import es.uvigo.ei.sing.bew.view.components.CustomTextField;
import es.uvigo.ei.sing.bew.view.panels.ButtonsPanel;
import es.uvigo.ei.sing.bew.view.panels.LoginPanel;

/**
 * Custom dialog to list and download an Experiment from BioFomics.
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
public class DownloadExpDialog extends CustomDialog {

	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();
	private ButtonsPanel buttonsPanel;
	private LoginPanel loginPanel;

	private JLabel lblSearch;
	private CustomTextField textSearch;
	private JButton btnList;
	private JButton btnVisualize;
	private JList<String> experiments;
	private VisualizeBioExperiment visualizeDialog;

	private List<String> expIDs;

	private String downloadFile;
	private boolean canExit;

	/**
	 * Create the dialog.
	 */
	public DownloadExpDialog() {
		super();

		setModalityType(ModalityType.APPLICATION_MODAL);
		this.expIDs = new ArrayList<String>();

		this.visualizeDialog = new VisualizeBioExperiment(this);

		init();
		initButtons("downloadExp");

		setLocationRelativeTo(null);
	}

	/**
	 * Initializes the dialog.
	 */
	private void init() {
		setSize(new Dimension(550, 450));
		setTitle(I18n.get("DownloadExpDialogTitle"));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		getContentPane().setLayout(new BorderLayout());
		{
			// ContentPane North
			loginPanel = new LoginPanel(I18n.get("downloadLoginMessage"));
			getContentPane().add(loginPanel, BorderLayout.NORTH);
		}
		{
			// ContentPane Center
			contentPanel.setBorder(new TitledBorder(null, I18n
					.get("DownloadExpDialogExpList"), TitledBorder.LEADING,
					TitledBorder.TOP, null, null));
			getContentPane().add(contentPanel, BorderLayout.CENTER);
			GridBagLayout gblContent = new GridBagLayout();
			gblContent.columnWidths = new int[] { 25, 30, 25 };
			gblContent.rowHeights = new int[] { 25, 25, 25, 25 };
			gblContent.columnWeights = new double[] { 1.0, 1.0, 0.0 };
			gblContent.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
			contentPanel.setLayout(gblContent);
			{
				lblSearch = new JLabel(I18n.get("DownloadExpDialogSearch")
						+ ":");
				GridBagConstraints gbcLblSearch = new GridBagConstraints();
				gbcLblSearch.insets = new Insets(0, 0, 0, 5);
				gbcLblSearch.gridx = 0;
				gbcLblSearch.gridy = 0;
				contentPanel.add(lblSearch, gbcLblSearch);
			}
			{
				textSearch = new CustomTextField("");
				textSearch.setPlaceholder("Experiment gene");
				textSearch
						.setToolTipText("Introduce a key word to filter experiments list.");
				GridBagConstraints gbcTextSearch = new GridBagConstraints();
				gbcTextSearch.insets = new Insets(0, 0, 0, 5);
				gbcTextSearch.fill = GridBagConstraints.HORIZONTAL;
				gbcTextSearch.gridx = 1;
				gbcTextSearch.gridy = 0;
				contentPanel.add(textSearch, gbcTextSearch);
				textSearch.setColumns(10);
			}
			{
				btnList = new JButton(I18n.get("DownloadExpDialogList"));
				btnList.setToolTipText("List experiments for the specified user with the specified key word in search field.");
				GridBagConstraints gbcBtnList = new GridBagConstraints();
				gbcBtnList.insets = new Insets(0, 0, 0, 5);
				gbcBtnList.gridx = 2;
				gbcBtnList.gridy = 0;
				contentPanel.add(btnList, gbcBtnList);

				btnList.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						// Purge variables
						DefaultListModel<String> model = (DefaultListModel<String>) experiments
								.getModel();
						model.clear();
						expIDs.clear();

						// Call webService
						listExperiments();
						// new ShowProgressBar(new Task());
					}
				});
			}
			{
				experiments = new JList<String>(new DefaultListModel<String>());
				experiments.setCellRenderer(new ListStripeRenderer());
				// ScrollPane for JList
				JScrollPane listScroll = new JScrollPane(experiments);
				GridBagConstraints gbcListExp = new GridBagConstraints();
				gbcListExp.gridheight = 3;
				gbcListExp.gridwidth = 2;
				gbcListExp.insets = new Insets(5, 0, 0, 5);
				gbcListExp.fill = GridBagConstraints.BOTH;
				gbcListExp.gridx = 0;
				gbcListExp.gridy = 1;
				contentPanel.add(listScroll, gbcListExp);
			}
			{
				btnVisualize = new JButton(
						I18n.get("DownloadExpDialogVisualize"));
				btnVisualize
						.setToolTipText("Visualize the information of the selected experiment in the list.");
				btnVisualize.setIcon(new ImageIcon(DownloadExpDialog.class
						.getResource("/img/visualize.png")));
				GridBagConstraints gbcBtnVisual = new GridBagConstraints();
				gbcBtnVisual.gridheight = 3;
				gbcBtnVisual.gridx = 2;
				gbcBtnVisual.gridy = 1;
				contentPanel.add(btnVisualize, gbcBtnVisual);

				btnVisualize.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (!experiments.isSelectionEmpty()) {
							String selectedID = expIDs.get(experiments
									.getSelectedIndex());

							// Purge dialog
							visualizeDialog.clearAll();

							// Get experiment info and put them in the dialog
							getExperiment(selectedID);

							// Show dialog
							visualizeDialog.setVisible(true);
						}
					}
				});
			}
		}
	}

	/**
	 * Initializes buttons.
	 */
	private void initButtons(String help) {
		String[] buttonNames = { I18n.get("DownloadExpDialogDownload"),
				I18n.get("cancel") };
		URL[] iconPaths = { getClass().getResource("/img/accept.png"),
				getClass().getResource("/img/cancel.png") };

		ActionListener[] listeners = { new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Accept button
				if (finish()) {
					BewConstants.USER = loginPanel.getTextUser();
					BewConstants.PASS = loginPanel.getTextPassword();

					canExit = true;
					visualizeDialog.dispose();
					dispose();
				} else {
					canExit = false;
				}
			}
		}, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Cancel button
				canExit = false;
				visualizeDialog.dispose();
				dispose();
			}
		} };

		this.buttonsPanel = new ButtonsPanel(buttonNames, iconPaths, listeners,
				help, this);

		// ContentPane South
		getContentPane().add(this.buttonsPanel, BorderLayout.SOUTH);
	}

	/**
	 * Call web service to list Experiments and put them in the dialog.
	 */
	private void listExperiments() {
		BewConstants.USER = loginPanel.getTextUser();
		BewConstants.PASS = loginPanel.getTextPassword();

		// Query for request an experiment list
		String webService = "search=" + textSearch.getText().trim()
				+ "&action=listExperiments";

		String response = "";
		try {
			response = WebServicesConstants.requestWebService(webService,
					BewConstants.USER, BewConstants.PASS);

			int count = 0;
			if (!response.startsWith("[") && !response.endsWith("]")
					&& response.length() > 2) {
				JsonFactory factory = new JsonFactory();
				JsonParser jparser = factory.createParser(response);

				String key = "";
				String value = "";
				DefaultListModel<String> listModel = (DefaultListModel<String>) experiments
						.getModel();
				// Get StartObject for JSON, after first {
				jparser.nextToken();
				// Compare if the first element (key) is the same than the last
				// one
				while (jparser.nextToken() != JsonToken.END_OBJECT) {
					// Get key
					key = jparser.getText();
					// Add ID to the list (JList must have the same order)
					expIDs.add(key);

					// Move to value
					jparser.nextToken();
					value = jparser.getText();
					listModel.addElement(value);

					count++;
				}
				jparser.close();
			}
			// Show OK dialog
			ShowDialog
					.showInfo(
							I18n.get("listsOkTitle"),
							I18n.get("listsOkBody1") + count
									+ I18n.get("listsOkBody2"));
		} catch (AuthenticationException e) {
			// Invalid user or password
			ShowDialog.showError(I18n.get("listsErrorTitle"),
					I18n.get("authenticationError"));
			e.printStackTrace();
		} catch (ServiceUnavailableException e) {
			// Server error
			ShowDialog.showError(I18n.get("listsErrorTitle"),
					I18n.get("serverError"));
			e.printStackTrace();
		} catch (Exception e) {
			// Unknown error
			ShowDialog.showError(I18n.get("listsErrorTitle"),
					I18n.get("unknownError"));
			e.printStackTrace();
		}
	}

	/**
	 * Call web service to get an Experiment and put the information in the
	 * dialog.
	 */
	private void getExperiment(String id) {
		// Query for requesting an experiment
		String webService = "id=" + id + "&action=getExperiment";

		String response = "";
		try {
			response = WebServicesConstants.requestWebService(webService,
					BewConstants.USER, BewConstants.PASS);

			JsonFactory factory = new JsonFactory();
			JsonParser jparser = factory.createParser(response);

			String key = "";
			// Get StartObject for JSON, after first {
			jparser.nextToken();
			while (jparser.nextToken() != JsonToken.END_OBJECT) {
				key = jparser.getText();

				if (key.equals("status")) {
					WebServicesConstants.showSpecificError(jparser
							.nextTextValue());
				} else if (key.equals("Experiment")) {
					getExperimentInfo(jparser);
				} else if (key.equals("User")) {
					getUserInfo(jparser);
				} else if (key.equals("Publication")) {
					getPublicationInfo(jparser);
				} else if (key.equals("listConditions")) {
					getConditionsInfo(jparser);
				} else if (key.equals("listMethods")) {
					getMethodInfo(jparser);
				} else {
					jparser.skipChildren();
				}
			}

			jparser.close();

		} catch (AuthenticationException e) {
			// Invalid user or password
			ShowDialog.showError(I18n.get("listsErrorTitle"),
					I18n.get("authenticationError"));
			e.printStackTrace();
		} catch (ServiceUnavailableException e) {
			// Server error
			ShowDialog.showError(I18n.get("listsErrorTitle"),
					I18n.get("serverError"));
			e.printStackTrace();
		} catch (NullPointerException e) {
			// Get error for one Experiment
			ShowDialog.showError(I18n.get("listsErrorTitle"),
					I18n.get("getExpError"));
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// No Internet connection error
			ShowDialog.showError(I18n.get("listsErrorTitle"),
					I18n.get("webServicesConnection"));
			e.printStackTrace();
		} catch (Exception e) {
			// Unknown error
			ShowDialog.showError(I18n.get("listsErrorTitle"),
					I18n.get("unknownError"));
			e.printStackTrace();
		}
	}

	/**
	 * Go over Experiment node in the JSON and get the information to the
	 * visualize panel.
	 * 
	 * @param currentToken
	 * @throws JsonParseException
	 * @throws IOException
	 */
	private void getExperimentInfo(JsonParser currentToken)
			throws JsonParseException, IOException {
		String key = "";

		JsonToken token = currentToken.nextToken();
		while (token != JsonToken.END_OBJECT) {
			// Get key
			key = currentToken.getText();

			if (key.equals("name")) {
				currentToken.nextToken();
				visualizeDialog.setName(currentToken.getText());
				// experimentInfo[0] = currentToken.getText();
			} else if (key.equals("is_interexperiment")) {
				currentToken.nextToken();
				visualizeDialog.setInter(currentToken.getText());
				// experimentInfo[1] = currentToken.getText();
			} else if (key.equals("experiment_date")) {
				currentToken.nextToken();
				visualizeDialog.setDate(currentToken.getText());
				// experimentInfo[2] = currentToken.getText();
			} else if (key.equals("authors")) {
				currentToken.nextToken();
				visualizeDialog.setAuthors(currentToken.getText());
				// experimentInfo[3] = currentToken.getText();
			} else if (key.equals("note")) {
				currentToken.nextToken();
				visualizeDialog.setNotes(Jsoup.parse(currentToken.getText())
						.text());
				// experimentInfo[4] = currentToken.getText();
			} else if (key.equals("file")) {
				currentToken.nextToken();
				this.downloadFile = currentToken.getText();
			}

			token = currentToken.nextToken();
		}

		// for (String s : experimentInfo) {
		// System.out.println("Experiment info: " + s);
		// }
	}

	/**
	 * Go over User node in the JSON and get the information to the visualize
	 * panel.
	 * 
	 * @param currentToken
	 * @throws JsonParseException
	 * @throws IOException
	 */
	private void getUserInfo(JsonParser currentToken)
			throws JsonParseException, IOException {
		String key = "";

		JsonToken token = currentToken.nextToken();
		while (token != JsonToken.END_OBJECT) {
			// Get key
			key = currentToken.getText();

			if (key.equals("username")) {
				currentToken.nextToken();
				visualizeDialog.setUser(currentToken.getText());
				// userInfo[0] = currentToken.getText();
			} else if (key.equals("email")) {
				currentToken.nextToken();
				visualizeDialog.setMail(currentToken.getText());
				// userInfo[1] = currentToken.getText();
			} else if (key.equals("organization")) {
				currentToken.nextToken();
				visualizeDialog.setOrganization(currentToken.getText());
				// userInfo[2] = currentToken.getText();
			}

			token = currentToken.nextToken();
		}

		// for (String s : userInfo) {
		// System.out.println("userInfo: " + s);
		// }
	}

	/**
	 * Go over Publication node in the JSON and get the information to the
	 * visualize panel.
	 * 
	 * @param currentToken
	 * @throws JsonParseException
	 * @throws IOException
	 */
	private void getPublicationInfo(JsonParser currentToken)
			throws JsonParseException, IOException {
		String key = "";

		while (currentToken.nextToken() != JsonToken.END_ARRAY) {
			// Get key
			key = currentToken.getText();

			// Move to value
			if (key.equals("url")) {
				currentToken.nextToken();
				visualizeDialog.addPublication(currentToken.getText());
				// publicationInfo.add(currentToken.getText());
			}
		}

		// for (String s : publicationInfo) {
		// System.out.println("publicationInfo: " + s);
		// }
	}

	/**
	 * Go over listConditions node in the JSON and get the information to the
	 * visualize panel.
	 * 
	 * @param currentToken
	 * @throws JsonParseException
	 * @throws IOException
	 */
	private void getConditionsInfo(JsonParser currentToken)
			throws JsonParseException, IOException {
		// Start after {
		currentToken.nextToken();

		JsonToken token = currentToken.nextToken();
		while (token != JsonToken.END_OBJECT && token != JsonToken.END_ARRAY) {
			// Add value
			// conditions.add(currentToken.nextTextValue());
			visualizeDialog.addCondition(currentToken.nextTextValue());
			token = currentToken.nextToken();
		}

		// for (String s : conditions) {
		// System.out.println("conditions: " + s);
		// }
	}

	/**
	 * Go over listMethods node in the JSON and get the information to the
	 * visualize panel.
	 * 
	 * @param currentToken
	 * @throws JsonParseException
	 * @throws IOException
	 */
	private void getMethodInfo(JsonParser currentToken)
			throws JsonParseException, IOException {
		// Start after {
		currentToken.nextToken();

		JsonToken token = currentToken.nextToken();
		while (token != JsonToken.END_OBJECT && token != JsonToken.END_ARRAY) {
			// Add value
			// methods.add(currentToken.nextTextValue());
			visualizeDialog.addMethod(currentToken.nextTextValue());
			token = currentToken.nextToken();
		}

		// for (String s : methods) {
		// System.out.println("methods: " + s);
		// }
	}

	/**
	 * 
	 * @return
	 */
	private boolean finish() {
		boolean toRet = false;
		// Get selected experiment
		int id = experiments.getSelectedIndex();

		if (id != -1) {
			String selectedID = expIDs.get(id);

			// Get information
			getExperiment(selectedID);

			toRet = true;
		} else {
			toRet = false;
		}

		return toRet;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isExit() {
		return this.canExit;
	}

	/**
	 * 
	 * @return
	 */
	public String getDownloadFile() {
		return downloadFile;
	}

	/**
	 * Swing worker to do heavy task in background. Realizes the statistics test
	 * logic.
	 * 
	 * @author Gael Pérez Rodríguez
	 * 
	 */
	// private class Task extends SwingWorker<Void, Void> {
	// @Override
	// public Void doInBackground() {
	// // List experiments from BiofOmics
	// listExperiments();
	//
	// return null;
	// }
	//
	// @Override
	// public void done() {
	// Toolkit.getDefaultToolkit().beep();
	// setProgress(100);
	// setCursor(null); // turn off the wait cursor
	// }
	// }
}
