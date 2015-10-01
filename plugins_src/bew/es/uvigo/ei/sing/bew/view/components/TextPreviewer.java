package es.uvigo.ei.sing.bew.view.components;

import java.awt.BorderLayout;
import java.awt.Point;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

/**
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
public class TextPreviewer extends JComponent {
	private static final long serialVersionUID = 1L;
	private CustomTextArea textArea = new CustomTextArea("");
	private JScrollPane scrollPane = new JScrollPane(textArea);
	private Map<String, byte[]> content;

	/**
	 * Default constructor
	 */
	public TextPreviewer() {
		super();

		textArea.setEditable(false);
		textArea.setPlaceholder("");
		content = new HashMap<String, byte[]>();

		setBorder(BorderFactory.createEtchedBorder());
		setLayout(new BorderLayout());
		add(scrollPane, BorderLayout.CENTER);
	}

	/**
	 * 
	 * @param file
	 */
	public void configure(File file) {
		textArea.setText(contentsOfFile(file));
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JViewport vp = scrollPane.getViewport();

				vp.setViewPosition(new Point(0, 0));
			}
		});
	}

	/**
	 * 
	 * @param originalPath
	 * @param file
	 */
	public void configure(String originalPath, byte[] file) {
		this.content.clear();

		// Add decrypted content to the list
		if (!content.containsKey(originalPath)) {
			this.content.put(originalPath, file);
		}

		String content = new String(file);

		textArea.setText(content);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JViewport vp = scrollPane.getViewport();

				vp.setViewPosition(new Point(0, 0));
			}
		});
	}

	/**
	 * 
	 * @param file
	 * @return
	 */
	public String contentsOfFile(File file) {
		String s = new String();
		char[] buff = new char[50000];
		InputStream is;
		InputStreamReader reader;
		URL url;

		try {
			reader = new FileReader(file);

			int nch;

			while ((nch = reader.read(buff, 0, buff.length)) != -1) {
				s = s + new String(buff, 0, nch);
			}
			reader.close();
		} catch (java.io.IOException ex) {
			s = "Could not load file";
		}

		return s;
	}

	/**
	 * 
	 */
	public void clearText() {
		this.textArea.setText("This file is incorrect!");
	}

	/**
	 * 
	 */
	public void clearContent() {
		content.clear();
	}

	/**
	 * 
	 * @return
	 */
	public Map<String, byte[]> getContent() {
		return content;
	}
}
