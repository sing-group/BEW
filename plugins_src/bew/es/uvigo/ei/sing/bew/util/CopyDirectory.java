package es.uvigo.ei.sing.bew.util;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Class to copy a whole directory.
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
public class CopyDirectory extends SimpleFileVisitor<Path> {

	private Path source;
	private Path target;

	/**
	 * Default constructor.
	 * 
	 * @param source
	 *            Source directory.
	 * @param target
	 *            Target directory.
	 */
	public CopyDirectory(Path source, Path target) {
		super();
		this.source = source;
		this.target = target;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attributes)
			throws IOException {
		Files.copy(file, target.resolve(source.relativize(file)),
				StandardCopyOption.REPLACE_EXISTING);
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult preVisitDirectory(Path directory,
			BasicFileAttributes attributes) throws IOException {
		Path targetDirectory = target.resolve(source.relativize(directory));
		try {
			Files.copy(directory, targetDirectory);
		} catch (FileAlreadyExistsException e) {
			if (!Files.isDirectory(targetDirectory)) {
				throw e;
			}
		}
		return FileVisitResult.CONTINUE;
	}
}