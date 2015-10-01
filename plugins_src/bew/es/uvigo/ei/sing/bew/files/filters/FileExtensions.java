package es.uvigo.ei.sing.bew.files.filters;

import java.io.File;

/**
 * This class provides extensions for files.
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
public class FileExtensions {
	public final static String XLS = "xls";
	public final static String XML = "xml";
	public final static String PNG = "png";
	public final static String TXT = "txt";

	/**
	 * Get the extension of a file.
	 * 
	 * @param file
	 *            File to get the extension.
	 * @return String with the extension.
	 */
	public static String getExtension(final File file) {
		String ext = null;
		String name = file.getName();
		final int i = name.lastIndexOf('.');

		if (i > 0 && i < name.length() - 1) {
			ext = name.substring(i + 1).toLowerCase();
		}
		return ext;
	}

}
