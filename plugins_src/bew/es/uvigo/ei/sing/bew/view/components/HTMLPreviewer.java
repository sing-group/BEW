package es.uvigo.ei.sing.bew.view.components;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
public class HTMLPreviewer extends JComponent {
	private static final long serialVersionUID = 1L;
	private JEditorPane textPane = new JEditorPane();
	private JScrollPane scrollPane = new JScrollPane(textPane);

	/**
	 * Default constructor
	 */
	public HTMLPreviewer() {
		super();

		textPane.setEditable(false);
		textPane.setContentType("text/html");
		textPane.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					try {
						Desktop.getDesktop().browse(e.getURL().toURI());
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

		setBorder(BorderFactory.createEtchedBorder());
		setLayout(new BorderLayout());
		add(scrollPane, BorderLayout.CENTER);
	}

	/**
	 * 
	 * @param file
	 * @throws IOException
	 */
	public void configure(File file) throws IOException {
		List<String> lines = readFile(file);
		
		String[] columns;
		String toShow = "";
		String line = "";
		for (String s : lines) {
			columns = s.split("\t");

			line = "";
			for (String str : columns) {
				if (str.contains("http://") || str.contains("https://")) {
					line += "<a href=\"" + str
							+ "\">See more information</a>";
				} else {
					line += str + "\t";
				}
			}
			line += "<br>";
			
			toShow += line;
		}

		textPane.setText(toShow);
	}

	/**
	 * 
	 * @param path
	 * @return
	 * @throws IOException 
	 */
	private List<String> readFile(File toRead) throws IOException {
		List<String> lines = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(toRead));
		try {
			String line = br.readLine();

			while (line != null) {
				lines.add(line);
				line = br.readLine();
			}
		} finally {
			br.close();
		}
		
		return lines;
	}
}
