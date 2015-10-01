package es.uvigo.ei.sing.bew.view.dialogs;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import es.uvigo.ei.sing.bew.constants.I18n;
import es.uvigo.ei.sing.bew.constants.PlotFunctions;
import es.uvigo.ei.sing.bew.constants.ShowDialog;
import es.uvigo.ei.sing.bew.constants.StatisticFunctions;
import es.uvigo.ei.sing.bew.model.Method;
import es.uvigo.ei.sing.bew.view.components.CustomDialog;
import es.uvigo.ei.sing.bew.view.panels.ButtonsPanel;
import es.uvigo.ei.sing.bew.view.panels.SelectExpAndMethodPanel;

/**
 * Custom dialog for selecting a statistic test or a plot and a method.
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
public class SelectTreeDialog extends CustomDialog {

	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();
	private JTextArea textDescription;
	private javax.swing.JTree treeFunctions;

	// Leaf of the tree
	private Object selectedFunction;
	// Parent of the selected leaf
	private Object selectedType;

	private boolean canExit;
	private boolean isPlot;

	private SelectExpAndMethodPanel methodPane;

	/**
	 * Default constructor.
	 * 
	 * @param isPlot
	 *            True if the user want a plot, false if the user want a test
	 */
	public SelectTreeDialog(boolean isPlot) {
		super();

		methodPane = new SelectExpAndMethodPanel(false, false);
		this.isPlot = isPlot;

		initialize();
		if (isPlot)
			initButtons("createPlot");
		else
			initButtons("createTest");
	}

	/**
	 * Method to initialize dialog.
	 */
	public void initialize() {
		if (isPlot)
			setTitle(I18n.get("selectPlot"));
		else
			setTitle(I18n.get("selectTest"));

		setModal(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(520, 360);
		setLocationRelativeTo(null);

		getContentPane().setLayout(new BorderLayout());
		{
			getContentPane().add(methodPane, BorderLayout.NORTH);
		}
		contentPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null,
				null));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new GridLayout(0, 1, 0, 0));
		{
			JSplitPane splitPane = new JSplitPane();
			// Resizes width for the two components
			splitPane.setResizeWeight(0.2);
			splitPane.setOneTouchExpandable(true);
			contentPanel.add(splitPane);
			{
				treeFunctions = new javax.swing.JTree();

				treeFunctions
						.addTreeSelectionListener(new TreeSelectionListener() {

							@Override
							public void valueChanged(TreeSelectionEvent e) {
								DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeFunctions
										.getLastSelectedPathComponent();

								// If nothing is selected
								if (node == null)
									return;

								// User only must select leafs, not roots
								if (!node.isLeaf()) {
									textDescription.removeAll();
									try {
										textDescription.setText(I18n.get(node
												.getUserObject().toString()));
									} catch (Exception e1) {
									}

									return;
								} else {
									// Retrieve the node that was selected
									selectedFunction = node.getUserObject();
									selectedType = node.getParent();

									textDescription.removeAll();

									String text = I18n.get(selectedFunction
											.toString());
									if (text != null)
										textDescription.setText(text);
									else
										textDescription.setText(I18n
												.get(selectedType.toString()));
								}
							}
						});

				// Fill tree with values
				if (isPlot) {
					treeFunctions.setModel(new DefaultTreeModel(
							new DefaultMutableTreeNode("Plot") {
								private static final long serialVersionUID = 1L;
								{
									DefaultMutableTreeNode node1;
									node1 = new DefaultMutableTreeNode("Bars");
									node1.add(new DefaultMutableTreeNode(
											PlotFunctions.STATISTICAL));
									add(node1);
									node1 = new DefaultMutableTreeNode(
											"Points");
									node1.add(new DefaultMutableTreeNode(
											PlotFunctions.TIME));
									add(node1);
								}
							}));
				} else {
					treeFunctions.setModel(new DefaultTreeModel(
							new DefaultMutableTreeNode("Statistic") {
								private static final long serialVersionUID = 1L;
								{
									DefaultMutableTreeNode node1;
									node1 = new DefaultMutableTreeNode(
											"Outliers");
									node1.add(new DefaultMutableTreeNode(
											StatisticFunctions.GRUBBS));
									// node_1.add(new DefaultMutableTreeNode(
									// "Bonferroni"));
									add(node1);
									node1 = new DefaultMutableTreeNode(
											"Normality");
									node1.add(new DefaultMutableTreeNode(
											StatisticFunctions.SAPHIRO));
									node1.add(new DefaultMutableTreeNode(
											StatisticFunctions.KOLMOGOROV));
									node1.add(new DefaultMutableTreeNode(
											StatisticFunctions.LILLIE));
									add(node1);
									node1 = new DefaultMutableTreeNode(
											"Homogeneity");
									node1.add(new DefaultMutableTreeNode(
											StatisticFunctions.LEVENE));
									node1.add(new DefaultMutableTreeNode(
											StatisticFunctions.BARTLETT));
									// node_1.add(new DefaultMutableTreeNode(
									// "Brown-Forsythe"));
									add(node1);
									node1 = new DefaultMutableTreeNode(
											"Variance");
									node1.add(new DefaultMutableTreeNode(
											StatisticFunctions.ANOVA));
									node1.add(new DefaultMutableTreeNode(
											StatisticFunctions.TTEST));
									node1.add(new DefaultMutableTreeNode(
											StatisticFunctions.KRUSKAL));
									add(node1);
								}
							}));
				}
				// End fill tree

				splitPane.setLeftComponent(treeFunctions);
			}
			{
				textDescription = new JTextArea();
				textDescription.setEditable(false);
				textDescription.setLineWrap(true);
				textDescription.setBackground(SystemColor.control);
				textDescription.setWrapStyleWord(true);

				JScrollPane descriptionScroll = new JScrollPane(textDescription);
				splitPane.setRightComponent(descriptionScroll);
			}
		}
	}

	/**
	 * Initializes buttons.
	 */
	private void initButtons(String help) {
		String[] buttonNames = { "Ok", I18n.get("cancel") };
		URL[] buttonIcons = { getClass().getResource("/img/accept.png"),
				getClass().getResource("/img/cancel.png") };
		ActionListener[] buttonListeners = { new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				finish();
			}
		}, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				canExit = false;
				dispose();
			}
		} };

		getContentPane().add(
				new ButtonsPanel(buttonNames, buttonIcons, buttonListeners,
						help, this), BorderLayout.SOUTH);
	}

	/**
	 * Method to finish the dialog. This method validates if the user has
	 * selected a method and a test.
	 */
	public void finish() {
		// If the user select one function in the tree
		if (this.selectedFunction != null && methodPane.finish()) {
			this.canExit = true;
			dispose();
		} else {
			ShowDialog.showError(I18n.get("selectMethodTestTitle"),
					I18n.get("selectMethodTest"));
			this.canExit = false;
		}
	}

	/**
	 * Get selected function.
	 * 
	 * @return
	 */
	public Object getSelectedFunction() {
		return selectedFunction;
	}

	/**
	 * Get selected type of the function.
	 * 
	 * @return
	 */
	public Object getSelectedType() {
		return selectedType;
	}

	/**
	 * Get selected Method.
	 * 
	 * @return
	 */
	public Method getSelectedMethod() {
		return methodPane.getSelectedMethod();
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
