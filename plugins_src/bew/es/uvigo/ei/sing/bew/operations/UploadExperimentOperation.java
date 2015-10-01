package es.uvigo.ei.sing.bew.operations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.naming.AuthenticationException;
import javax.naming.ServiceUnavailableException;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import es.uvigo.ei.aibench.core.Base64Coder;
import es.uvigo.ei.aibench.core.operation.annotation.Direction;
import es.uvigo.ei.aibench.core.operation.annotation.Operation;
import es.uvigo.ei.aibench.core.operation.annotation.Port;
import es.uvigo.ei.sing.bew.constants.BewConstants;
import es.uvigo.ei.sing.bew.constants.I18n;
import es.uvigo.ei.sing.bew.constants.ShowDialog;
import es.uvigo.ei.sing.bew.files.DataToFile;
import es.uvigo.ei.sing.bew.model.IExperiment;

/**
 * This class is an AIBench operation for uploading an Experiment to BioFomics.
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
@Operation(description = "Upload an Experiment zip to BioFomics.")
public class UploadExperimentOperation {

	private IExperiment selExp;
	private boolean isInter;

	@Port(direction = Direction.INPUT, name = "Set Experiment to upload", order = 1)
	public void setExperiment(IExperiment selExp) {
		this.selExp = selExp;

		if (this.selExp.getMapIntraExpsColors() != null) {
			isInter = true;
		} else {
			isInter = false;
		}

		try {
			// First step, send Experiment data
			String json = generateJSON();

			// Second step, receive Experiment bio_ID from BiofOmics
			String bioID = getBioID(json);

			if (!bioID.isEmpty()) {
				// Third step, set bio_ID in the Experiment
				boolean success = setBioID(bioID);
				if (success) {
					// Four step, create Experiment files and send them to
					// BiofOmics
					String zipPath = saveExpInTempFolder();
					json = generateJSON(zipPath);

					// Call webService to upload the file
					int serverCode = uploadExperiment(json);
					if (serverCode < 400) {
						ShowDialog.showInfo(
								I18n.get("updloadOkTitle"),
								I18n.get("uploadOk1") + selExp.getName()
										+ I18n.get("uploadOk2")
										+ BewConstants.USER + ".");
					} else {
						if (serverCode == 401) {
							ShowDialog.showError(I18n.get("updloadErrorTitle"),
									I18n.get("uploadBadLogin"));
						} else if (serverCode == 500) {
							ShowDialog.showError(I18n.get("updloadErrorTitle"),
									I18n.get("uploadServerError"));
						}
					}
				} else {
					ShowDialog.showError(I18n.get("updloadErrorTitle"),
							I18n.get("uploadIDError"));
				}
			} else {
				ShowDialog.showError(I18n.get("updloadErrorTitle"),
						I18n.get("uploadIDError"));
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ShowDialog.showError(I18n.get("updloadErrorTitle"),
					I18n.get("webServicesConnection"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ShowDialog.showError(I18n.get("updloadErrorTitle"),
					I18n.get("updloadError"));
		}
	}

	/**
	 * Method to generate the JSON to pass to BioFomics. This JSON includes the
	 * files for the Experiment.
	 * 
	 * @param zipPath
	 *            Path of the temporary zip files.
	 * @return String with the JSON.
	 * @throws IOException
	 */
	private String generateJSON(String zipPath) throws IOException {
		// Initialize JSON variable
		String json = "{\"Experiment\":{";

		json = getJSONExperiment(json, zipPath);

		json += "}";

		return json;
	}

	/**
	 * Method to generate the JSON to pass to BioFomics. This JSON doesn't
	 * include the files for the Experiment.
	 * 
	 * @return String with the JSON.
	 * @throws IOException
	 */
	private String generateJSON() throws IOException {
		// Initialize JSON variable
		String json = "{\"Experiment\":{";

		json = getJSONExperiment(json);

		json += "}";

		return json;
	}

	/**
	 * Method to fill the JSON with the experiment information. This JSON
	 * includes the files field.
	 * 
	 * @param json
	 *            Input JSON to fill with data.
	 * @param zipPath
	 *            Path of the temporary zip files for the Experiment.
	 * @return String with the new JSON.
	 * @throws IOException
	 */
	private String getJSONExperiment(String json, String zipPath)
			throws IOException {
		// Get bioID
		json += "\"id\":\"" + selExp.getBioID() + "\"";
		// Set zip file
		Path path = FileSystems.getDefault().getPath(zipPath);
		json += ",\"file\":\""
				+ new String(Base64Coder.encode(Files.readAllBytes(path)))
				+ "\"}";

		return json;
	}

	/**
	 * Method to fill the JSON with the experiment information. This JSON
	 * doesn't include the files field.
	 * 
	 * @param json
	 *            Input JSON to fill with data.
	 * @return String with the new JSON.
	 * @throws IOException
	 */
	private String getJSONExperiment(String json) throws IOException {
		String[] expSetup = this.selExp.getExpSetup();

		// Get name
		json += "\"name\":\"" + expSetup[0] + "\"";
		// Get note
		json += ",\"note\":\"" + expSetup[6] + "\"";
		// Get date
		json += ",\"experiment_date\":\"" + expSetup[4] + "\"";
		// Get authors
		json += ",\"authors\":\"" + expSetup[1] + "\"";
		// Get interExperiment
		if (isInter) {
			// 1 == true
			json += ",\"interexperiment\":\"1\"";
		} else {
			// 0 == false
			json += ",\"interexperiment\":\"0\"";
		}
		// Get bioID
		json += ",\"bio_id\":\"" + expSetup[7] + "\"}";

		if (!expSetup[5].isEmpty()) {
			// Get publications
			json += ", \"Publications\":[{\"url\":\"" + expSetup[5] + "\"}]";
		}

		return json;
	}

	/**
	 * Method to save the selected Experiment, in xml and xls (if possible), in
	 * a temporary folder. The files will be contained inside a zip.
	 * 
	 * @return String with the path of the zip file.
	 * @throws IOException
	 */
	private String saveExpInTempFolder() throws IOException {
		String toRet = "";

		// Create temporary File
		String filePath = Files.createTempDirectory("").toString() + "/";
		filePath = filePath.replace("\\", "/");

		// Get bioID
		String bioID = selExp.getBioID();
		if (bioID.isEmpty()) {
			bioID = "bio_";
		}

		DataToFile.saveXMLData(selExp, filePath + "xml");
		// Only save in XLS if the Experiment is an IntraExperiment
		if (!isInter) {
			DataToFile.saveXLSData(selExp, filePath + "xls");
		}

		// Generate ZIP file with Java 7
		Map<String, String> zipProperties = new HashMap<>();
		zipProperties.put("create", "true");
		zipProperties.put("encoding", "UTF-8");

		// Create zip file
		URI zipDisk;
		toRet = filePath + bioID + ".zip";
		if (toRet.startsWith("/")) {
			zipDisk = URI.create("jar:file:" + toRet);
		} else {
			zipDisk = URI.create("jar:file:/" + toRet);
		}

		// Adding files to zip
		try (FileSystem zipfs = FileSystems.newFileSystem(zipDisk,
				zipProperties)) {
			// Create file inside zip
			Path zipFilePath = zipfs.getPath(bioID + ".xml");
			// Path where the file to be added resides
			Path addNewFile = Paths.get(filePath + "xml.xml");
			// Append file to ZIP File
			Files.copy(addNewFile, zipFilePath);

			if (!isInter) {
				// Now go for the xls file
				zipFilePath = zipfs.getPath(bioID + ".xls");
				addNewFile = Paths.get(filePath + "xls.xls");
				Files.copy(addNewFile, zipFilePath);
			}
		}

		// Delete temp files
		Files.deleteIfExists(Paths.get(filePath + "xml.xml"));
		Files.deleteIfExists(Paths.get(filePath + "xls.xls"));

		return toRet;
	}

	/**
	 * Method to open the connection to send the JSON to BiofOmics server.
	 * 
	 * @return A HttpURLConnection with the server.
	 * @throws IOException
	 */
	private HttpURLConnection openConnection() throws IOException {
		// Get URL
		URL url;

		url = new URL(BewConstants.WEB);

		// Open the connection
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		// Set authorization header
		String auth = Base64Coder.encodeString(BewConstants.USER + ":"
				+ BewConstants.PASS);
		connection.addRequestProperty("Authorization", "Basic " + auth);
		connection.setDoOutput(true);
		connection.setInstanceFollowRedirects(true);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		connection.setRequestProperty("charset", "UTF-8");
		connection.setUseCaches(false);

		return connection;
	}

	/**
	 * Method to upload the Experiment JSON in BiofOmics.
	 * 
	 * @param json
	 *            JSON with the Experiment data.
	 * @throws IOException
	 * @throws ServiceUnavailableException
	 * @throws AuthenticationException
	 * @return An integer with the server code.
	 */
	private int uploadExperiment(String json) throws IOException {
		int toRet = 0;

		// Get connection
		HttpURLConnection connection = openConnection();

		// Set data and webservice
		String data = URLEncoder.encode("action", "UTF-8") + "="
				+ URLEncoder.encode("saveExperiment", "UTF-8");
		data += "&" + URLEncoder.encode("saveData", "UTF-8") + "="
				+ URLEncoder.encode(json, "UTF-8");

		connection.setRequestProperty("Content-Length",
				Integer.toString(data.length()));

		try (OutputStreamWriter out = new OutputStreamWriter(
				connection.getOutputStream())) {
			out.write(data);
		} catch (ConnectException e) {
			// TimeOut exception
			return 500;
		}

		try {
			// Read response
			StringBuilder responseSB = new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			// Get response from Server
			String line;
			while ((line = br.readLine()) != null)
				responseSB.append(line);

			// Close streams
			br.close();

			// System.out.println("Server response: " + responseSB.toString());
		} catch (IOException e) {
			InputStream error = connection.getErrorStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(error));
			// Get response from Server
			String line;
			StringBuilder responseSB = new StringBuilder();
			while ((line = br.readLine()) != null)
				responseSB.append(line);

			String errorS = responseSB.toString();
			// errorS = errorS.substring(errorS.indexOf("APIError"));

			// System.out.println("Error: " + responseSB.toString());
		}

		toRet = connection.getResponseCode();

		connection.disconnect();

		return toRet;
	}

	/**
	 * Method to send the JSON with the server data but without the file field
	 * to BiofOmics and receive the bioID from the server.
	 * 
	 * @param json
	 * @throws IOException
	 * @throws ServiceUnavailableException
	 * @throws AuthenticationException
	 * @return A String with the JSON contained the bioID.
	 */
	private String getBioID(String json) throws IOException {
		String toRet = "";

		// Get connection
		HttpURLConnection connection = openConnection();

		// Set data and webservice
		String data = URLEncoder.encode("action", "UTF-8") + "="
				+ URLEncoder.encode("saveExperiment", "UTF-8");
		data += "&" + URLEncoder.encode("saveData", "UTF-8") + "="
				+ URLEncoder.encode(json, "UTF-8");

		connection.setRequestProperty("Content-Length",
				Integer.toString(data.length()));

		try (OutputStreamWriter out = new OutputStreamWriter(
				connection.getOutputStream())) {
			out.write(data);
		} catch (ConnectException e) {
			// TimeOut exception
			return toRet;
		}

		try {
			// Read response
			StringBuilder responseSB = new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			// Get response from Server
			String line;
			while ((line = br.readLine()) != null)
				responseSB.append(line);

			// Close streams
			br.close();

			// System.out.println("Server response: " + responseSB.toString());
			toRet = responseSB.toString();
		} catch (IOException e) {
			InputStream error = connection.getErrorStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(error));
			// Get response from Server
			String line;
			StringBuilder responseSB = new StringBuilder();
			while ((line = br.readLine()) != null)
				responseSB.append(line);

			toRet = responseSB.toString();
			// errorS = errorS.substring(errorS.indexOf("APIError"));

			// System.out.println("Error: " + responseSB.toString());
		}

		connection.disconnect();

		return toRet;
	}

	/**
	 * 
	 * @param bioID
	 */
	private boolean setBioID(String bioID) {
		boolean toRet = false;
		try {
			JsonFactory factory = new JsonFactory();
			JsonParser jparser = factory.createParser(bioID);

			String key = "";
			// Get StartObject for JSON, after first {
			jparser.nextToken();
			while (jparser.nextToken() != JsonToken.END_OBJECT) {
				key = jparser.getText();

				if (key.equals("id")) {
					key = jparser.nextTextValue();

					// Set bioID in Experiment
					selExp.setBioID(key);

					toRet = true;
				} else {
					jparser.skipChildren();
				}
			}
			jparser.close();

		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return toRet;
	}
}
