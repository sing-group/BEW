package es.uvigo.ei.sing.bew.view.dialogs;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import es.uvigo.ei.sing.bew.constants.I18n;
import es.uvigo.ei.sing.bew.view.components.CustomDialog;
import es.uvigo.ei.sing.bew.view.panels.ButtonsPanel;

/**
 * Custom dialog to show the about.
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
public class AboutDialog extends CustomDialog {

	private static final long serialVersionUID = 1L;

	// private final URL LOGO_ESEI =
	// getClass().getResource("/img/esei_logo.png");
	// private final URL LOGO_MINHO = getClass()
	// .getResource("/img/minho_logo.png");
	// private final URL LOGO_FEUP =
	// getClass().getResource("/img/feup_logo.png");

	private JTextPane textPane;

	/**
	 * Default constructor.
	 * 
	 * @param parent
	 *            JFrame parent.
	 */
	public AboutDialog() {
		super();

		initialize();
		initButtons();

		setLocationRelativeTo(null);
		setVisible(true);
	}

	/**
	 * Method to initialize dialog.
	 */
	public void initialize() {
		setTitle(I18n.get("aboutDialog"));
		setSize(486, 266);
		setModal(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		JPanel bodyPane = new JPanel();
		bodyPane.setLayout(new GridLayout(0, 1, 0, 0));

		textPane = new JTextPane();
		textPane.setEditable(false);
		textPane.setContentType("text/html");
		textPane.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					URL url = e.getURL();

					try {
						if (url.getProtocol().equals("mailto")) {
							Desktop.getDesktop().mail(e.getURL().toURI());
						} else {
							Desktop.getDesktop().browse(e.getURL().toURI());
						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (URISyntaxException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});

		bodyPane.add(textPane);

		configureEditorPane();

		getContentPane().add(bodyPane, BorderLayout.CENTER);
	}

	/**
	 * Initializes buttons.
	 */
	private void initButtons() {
		String[] buttonNames = { "Ok" };
		URL[] buttonIcons = { getClass().getResource("/img/accept.png") };
		ActionListener[] buttonListeners = { new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		} };

		getContentPane().add(
				new ButtonsPanel(buttonNames, buttonIcons, buttonListeners),
				BorderLayout.SOUTH);
	}

	/**
	 * Method to fill the panel with information.
	 */
	private void configureEditorPane() {
		String imgPath = AboutDialog.class.getResource("/img").toString();

		textPane.setText("<div>\r\n\t<ul>\r\n\t\t<li>\r\n\t\t\tProject name: Biofilms Experiment Workbench (BEW).\r\n\t\t</li>\r\n\t\t<li>\r\n\t\t\tProject version: 2.0.\r\n\t\t</li>\r\n\t\t<li>\r\n\t\t\tMain developer: Gael P\u00E9rez Rodr\u00EDguez <a href=\"mailto:analia@uvigo.es?cc=gprodriguez2@esei.uvigo.es&subject=BEW\">gprodriguez2@esei.uvigo.es</a>.\r\n\t\t</li>\r\n\t\t<li>\r\n\t\t\tProject leader: An\u00E1lia Maria Garc\u00EDa Louren\u00E7o <a href=\"mailto:analia@uvigo.es?cc=gprodriguez2@esei.uvigo.es&subject=BEW\">analia@uvigo.es</a>.\r\n\t\t</li>\r\n\t\t<li>\r\n\t\t\tThis software results from the joint collaboration of the groups: <a href=\"http://sing.ei.uvigo.es\">SING</a>, <a href=\"http://www.ceb.uminho.pt/biofilm/Default.aspx\">Biofilms</a> and <a href=\"http://paginas.fe.up.pt/~nazevedo/\">Lepabae</a>.\r\n\t\t</li>\r\n\t</ul>\r\n\t<span> \r\n\t\t<img src=\""
				+ imgPath
				+ "/esei_logo.png\" Hspace=\"10\">\r\n\t\t<img src=\""
				+ imgPath
				+ "/minho_logo.png\" Hspace=\"10\">\r\n\t\t<img src=\""
				+ imgPath
				+ "/feup_logo.png\" Hspace=\"10\">\r\n\t</span>\r\n</div>\r\n\r\n\r\n");
	}
}
