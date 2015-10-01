package es.uvigo.ei.sing.bew.files.filters;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * This class provides an extension for saving in png format.
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
public class PngSaveFilter extends FileFilter {
	@Override
	public boolean accept(final File file) {
		if (file.isDirectory()) {
			return true;
		}

		String extension = FileExtensions.getExtension(file);
		if (extension != null) {
			return extension.equals(FileExtensions.PNG);
		}

		return false;
	}

	// The description of this filter
	@Override
	public String getDescription() {
		return ".PNG Format";
	}
}
