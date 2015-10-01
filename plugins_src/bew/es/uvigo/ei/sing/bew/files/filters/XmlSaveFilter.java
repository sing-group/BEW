package es.uvigo.ei.sing.bew.files.filters;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * This class provides an extension for saving in xml format.
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
public class XmlSaveFilter extends FileFilter {
	@Override
	public boolean accept(final File file) {
		if (file.isDirectory()) {
			return true;
		}

		String extension = FileExtensions.getExtension(file);
		if (extension != null) {
			return extension.equals(FileExtensions.XML);
		}

		return false;
	}

	// The description of this filter
	@Override
	public String getDescription() {
		return ".XML Format";
	}
}
