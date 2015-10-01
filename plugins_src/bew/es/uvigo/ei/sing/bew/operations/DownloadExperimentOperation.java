package es.uvigo.ei.sing.bew.operations;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Calendar;

import javax.naming.AuthenticationException;
import javax.naming.ServiceUnavailableException;

import es.uvigo.ei.aibench.core.Base64Coder;
import es.uvigo.ei.aibench.core.operation.annotation.Direction;
import es.uvigo.ei.aibench.core.operation.annotation.Operation;
import es.uvigo.ei.aibench.core.operation.annotation.Port;
import es.uvigo.ei.sing.bew.constants.BewConstants;
import es.uvigo.ei.sing.bew.constants.I18n;
import es.uvigo.ei.sing.bew.constants.ShowDialog;
import es.uvigo.ei.sing.bew.constants.WebServicesConstants;

/**
 * This class is an AIBench operation for downloading an Experiment from
 * BioFomics.
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
@Operation(description = "Download an Experiment zip from BioFomics.")
public class DownloadExperimentOperation {

	private String downloadFile;
	private String savePath;

	@Port(direction = Direction.INPUT, name = "Set download url", order = 1)
	public void setDownload(String downloadFile) {
		this.downloadFile = downloadFile;
	}

	@Port(direction = Direction.INPUT, name = "Set path for saving file", order = 2)
	public void setSavePath(String savePath) {
		this.savePath = savePath;

		// Call webService to download the file
		downloadFile();
	}

	private void downloadFile() {
		// Get URL
		URL url;
		try {
			url = new URL(downloadFile);

			// Open the connection
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			// Set authorization header
			String auth = Base64Coder.encodeString(BewConstants.USER + ":"
					+ BewConstants.PASS);
			connection.addRequestProperty("Authorization", "Basic " + auth);

			// Get connection input Stream
			ReadableByteChannel rbc = Channels.newChannel(connection
					.getInputStream());

			// Get fileName if exists
			String raw = connection.getHeaderField("Content-Disposition");
			String fileName = "";
			if (raw != null && raw.indexOf('=') != -1) {
				fileName = raw.split("=")[1].replace("\"", "");
			}
			// If not generate name
			else {
				Calendar now = Calendar.getInstance();
				fileName = "BioFomics-" + now.get(Calendar.HOUR_OF_DAY) + "_"
						+ now.get(Calendar.MINUTE) + "_"
						+ now.get(Calendar.SECOND) + ".zip";
			}

			WebServicesConstants.showServerResponse(connection
					.getResponseCode());

			FileOutputStream fos = new FileOutputStream(savePath + "/"
					+ fileName);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

			fos.close();

			connection.disconnect();

			// Show OK dialog
			ShowDialog.showInfo(
					I18n.get("downloadOkTitle"),
					I18n.get("downloadOkBody1") + fileName
							+ I18n.get("downloadOkBody2") + savePath);

		} catch (FileNotFoundException e) {
			ShowDialog.showError(I18n.get("downloadErrorTitle"),
					I18n.get("fileNotFound"));
			e.printStackTrace();
		} catch (AuthenticationException e) {
			// Invalid user or password
			ShowDialog.showError(I18n.get("listsErrorTitle"),
					I18n.get("authenticationError"));
			e.printStackTrace();
		} catch (ServiceUnavailableException e) {
			// Server error
			ShowDialog.showError(I18n.get("listsErrorTitle"),
					I18n.get("serverError"));
			e.printStackTrace();
		} catch (Exception e) {
			// Unknown error
			ShowDialog.showError(I18n.get("listsErrorTitle"),
					I18n.get("unknownError"));
			e.printStackTrace();
		}
	}
}
