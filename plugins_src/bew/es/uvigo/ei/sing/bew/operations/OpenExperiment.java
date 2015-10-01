package es.uvigo.ei.sing.bew.operations;

import java.io.File;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import es.uvigo.ei.aibench.core.operation.annotation.Direction;
import es.uvigo.ei.aibench.core.operation.annotation.Operation;
import es.uvigo.ei.aibench.core.operation.annotation.Port;
import es.uvigo.ei.sing.bew.constants.I18n;
import es.uvigo.ei.sing.bew.constants.ShowDialog;
import es.uvigo.ei.sing.bew.exceptions.RepeatedExpNameException;
import es.uvigo.ei.sing.bew.exceptions.RepeatedMethodNameException;
import es.uvigo.ei.sing.bew.files.FileToData;
import es.uvigo.ei.sing.bew.model.IExperiment;

/**
 * This class is an AIBench operation for importing an intraExperiment in XML.
 * 
 * @author Gael P�rez Rodr�guez.
 * 
 */
@Operation(description = "Import an Experiment.")
public class OpenExperiment {

	// Unused for now
	private String path;
	private boolean loadPlots;
	private boolean loadStatistics;
	private File tempFile;

	/**
	 * Set Experiment path.
	 * 
	 * @param path
	 */
	@Port(direction = Direction.INPUT, name = "Experiment path", order = 1)
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * Set Experiment path.
	 * 
	 * @param path
	 */
	@Port(direction = Direction.INPUT, name = "Load plots?", order = 2)
	public void setPlots(boolean loadPlots) {
		this.loadPlots = loadPlots;
	}

	/**
	 * Set Experiment path.
	 * 
	 * @param path
	 */
	@Port(direction = Direction.INPUT, name = "Load statistics?", order = 3)
	public void setStatistics(boolean loadStatistics) {
		this.loadStatistics = loadStatistics;
	}

	/**
	 * Set Experiment path.
	 * 
	 * @param path
	 */
	@Port(direction = Direction.INPUT, name = "Temporary file", order = 4)
	public void setStatistics(File tempFile) {
		this.tempFile = tempFile;
	}

	/**
	 * Import the experiment and create it.
	 * 
	 * @return IntraExperiment.
	 */
	@Port(direction = Direction.OUTPUT, order = 5)
	public IExperiment importExperiment() {
		IExperiment experiment = null;

		try {
			experiment = FileToData.xmlToData(tempFile);
		} catch (SAXException | ParserConfigurationException
				| IllegalArgumentException e) {
			ShowDialog.showError(I18n.get("errorLoadTitle"),
					I18n.get("errorLoadXml"), e.getLocalizedMessage());
		} catch (RepeatedExpNameException e) {
			ShowDialog.showError(I18n.get("duplicateNameLoadTitle"),
					I18n.get("duplicateNameLoad"));
		} catch (RepeatedMethodNameException e) {
			ShowDialog.showError(I18n.get("duplicateNameLoadTitle"),
					I18n.get("duplicateMNameLoad"));
		} catch (Exception e) {
			ShowDialog.showError(I18n.get("errorLoadTitle"),
					I18n.get("errorLoad"));
		}

		return experiment;
	}
}