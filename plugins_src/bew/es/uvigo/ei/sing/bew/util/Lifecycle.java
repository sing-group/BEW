package es.uvigo.ei.sing.bew.util;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.HelpSetException;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import javax.swing.SwingWorker;

import org.platonos.pluginengine.PluginLifecycle;

import es.uvigo.ei.aibench.core.Core;
import es.uvigo.ei.aibench.workbench.Workbench;
import es.uvigo.ei.sing.bew.constants.BewConstants;
import es.uvigo.ei.sing.bew.constants.I18n;
import es.uvigo.ei.sing.bew.constants.ShowProgressBar;
import es.uvigo.ei.sing.bew.constants.StatisticFunctions;
import es.uvigo.ei.sing.bew.model.Experiment;
import es.uvigo.ei.sing.bew.model.IExperiment;
import es.uvigo.ei.sing.bew.model.Method;
import es.uvigo.ei.sing.bew.view.dialogs.AboutDialog;

/**
 * This class lets do some configurations on BEW during boot.
 * 
 * @author Gael P�rez Rodr�guez.
 * 
 */
public class Lifecycle extends PluginLifecycle {

	private JMenuBar aiBenchMenu;
	private JToolBar aiBenchTool;

	private JMenu othersMenu;
	private JMenu fileMenu;
	private JMenu utilitiesMenu;

	@Override
	protected void start() {
		// TODO Auto-generated method stub
		super.start();

		// Take AIBench MenuBar
		this.aiBenchMenu = Workbench.getInstance().getMenuBar();
		// Take AIBench ToolBar
		this.aiBenchTool = Workbench.getInstance().getToolBar();

		this.aiBenchMenu.add(new JMenu(I18n.get("help")));
		// OthersMenu is the last, FileMenu is the first
		this.othersMenu = aiBenchMenu.getMenu(aiBenchMenu.getMenuCount() - 1);
		this.fileMenu = aiBenchMenu.getMenu(0);
		this.utilitiesMenu = aiBenchMenu.getMenu(2);

		addAboutToAIBench();
		addHelpToMenu();
		addCloseToMenu();
		addUpdatePackagesToMenu();

		// Delete files in temporary OS folder
		deleteTempFiles();

		// Adding listener to Clipboard to enable and disable operation
		// dynamically
		ClipboardBasedOperationActivator listener = new ClipboardBasedOperationActivator();
		// Without Experiment you can't do...
		listener.addRequirement("bew.saveExperimentOperation",
				IExperiment.class);
		listener.addRequirement("bew.exportExperimentOperation",
				Experiment.class);
		listener.addRequirement("bew.addMethodToExperiment", Experiment.class);
		listener.addRequirement("bew.editExperiment", IExperiment.class);
		listener.addRequirement("bew.createGraphic", IExperiment.class);
		listener.addRequirement("bew.createStatistic",
				IExperiment.class);
		listener.addRequirement("bew.compareExperiments", Experiment.class);
		listener.addRequirement("bew.reportingOperation",
				IExperiment.class);
		listener.addRequirement("bew.uploadExperimentOperation",
				IExperiment.class);
		// Without Method you can't do...
		listener.addRequirement("bew.editMethod", Method.class);
		listener.addRequirement("bew.editMethod", Experiment.class);

		Core.getInstance().getClipboard().addClipboardListener(listener);

		// Set iconImage
		Workbench
				.getInstance()
				.getMainFrame()
				.setIconImage(
						Toolkit.getDefaultToolkit()
								.getImage(
										Lifecycle.class
												.getResource("/img/favicon.png")));

		// Get user OS
		BewConstants.OS = System.getProperty("os.name").toLowerCase();
	}

	/**
	 * Deletes temporal files created by the program.
	 */
	private void deleteTempFiles() {
		String tempPath = System.getProperty("java.io.tmpdir");
		File[] tempFiles = new File(tempPath).listFiles();

		for (File f : tempFiles) {
			if (f.getName().startsWith(BewConstants.TEMPFILENAME))
				f.delete();
		}
	}

	/**
	 * Adds about operation to AIBench.
	 */
	private void addAboutToAIBench() {
		// Create the icon for the operation
		Icon aboutIcon = new ImageIcon(getClass().getResource("/img/about.png"));
		// Create the MenuItem for the AboutView
		JMenuItem aboutMenu = new JMenuItem(I18n.get("about"));
		// Create the button for the tool
		JButton aboutButton = new JButton(aboutIcon);
		aboutButton
				.setSize(aboutIcon.getIconWidth(), aboutIcon.getIconHeight());
		aboutButton.setToolTipText("Show the about dialog.");

		// Create the About listener for the button and the menu
		ActionListener aboutListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new AboutDialog();
			}
		};

		// Add icon and listener to menu
		aboutMenu.setIcon(aboutIcon);
		aboutMenu.addActionListener(aboutListener);
		// Add icon and listener to tool
		aboutButton.setIcon(aboutIcon);
		aboutButton.addActionListener(aboutListener);

		// Add components to AIBench
		this.othersMenu.add(aboutMenu);
		this.aiBenchTool.add(aboutButton);
	}

	/**
	 * Adds Help operation to AIBench.
	 */
	private void addHelpToMenu() {
		// We define the application help
		HelpSet helpset;
		try {
			// Create the helpSet and HelpBroker for JavaHelp
			helpset = new HelpSet(getClass().getClassLoader(), getClass()
					.getResource("/help/help_set.hs"));
			final HelpBroker helpB = helpset.createHelpBroker("Help Window");

			// Create the icon for the operation
			Icon helpIcon = new ImageIcon(getClass().getResource(
					"/img/help.png"));
			// Create the MenuItem for the HelpView
			JMenuItem helpMenu = new JMenuItem(I18n.get("helpContents"));
			// Create the button for the tool
			JButton helpButton = new JButton(helpIcon);
			helpButton.setToolTipText("Show the help dialog.");

			// Create the Help listener for the button and the menu
			ActionListener helpListener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					helpB.setDisplayed(true);
				}
			};

			helpB.enableHelpKey(Workbench.getInstance().getMainFrame()
					.getContentPane(), "index", helpset);

			// Add icon and listener to menu
			helpMenu.setIcon(helpIcon);
			helpMenu.addActionListener(helpListener);
			// Add icon and listener to tool
			helpButton.setIcon(helpIcon);
			helpButton.addActionListener(helpListener);

			// Add components to AIBench
			this.othersMenu.add(helpMenu);
			this.aiBenchTool.add(helpButton);
		} catch (HelpSetException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} // End JavaHelp
	}

	/**
	 * Adds close operation to AIBench.
	 */
	private void addCloseToMenu() {
		// Create the MenuItem for Close AIBench
		JMenuItem close = new JMenuItem(I18n.get("close"));
		// Create the icon for the operation
		Icon exitIcon = new ImageIcon(getClass().getResource("/img/exit.png"));

		close.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				System.exit(0);
			}
		});

		close.setIcon(exitIcon);

		this.fileMenu.add(close);
	}

	/**
	 * Adds update operation to AIBench.
	 */
	private void addUpdatePackagesToMenu() {
		// Create the MenuItem for Close AIBench
		JMenuItem update = new JMenuItem("Update R packages");
		// Create the icon for the operation
		Icon refreshIcon = new ImageIcon(getClass().getResource(
				"/img/refresh.png"));

		update.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Execute Swing Worker
				new ShowProgressBar(new HeavyTask());
			}
		});

		update.setIcon(refreshIcon);

		this.utilitiesMenu.add(update);
	}

	/**
	 * Swing worker to do heavy operations in background. Update R packages.
	 * 
	 * @author Gael P�rez Rodr�guez
	 * 
	 */
	private class HeavyTask extends SwingWorker<Void, Void> {
		@Override
		public Void doInBackground() {
			StatisticFunctions.installAllPackages();

			return null;
		}

		@Override
		public void done() {
			Toolkit.getDefaultToolkit().beep();
			setProgress(100);
		}
	}
}
