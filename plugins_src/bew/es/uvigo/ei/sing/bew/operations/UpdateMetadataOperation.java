package es.uvigo.ei.sing.bew.operations;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import es.uvigo.ei.aibench.core.operation.annotation.Direction;
import es.uvigo.ei.aibench.core.operation.annotation.Operation;
import es.uvigo.ei.aibench.core.operation.annotation.Port;
import es.uvigo.ei.sing.bew.constants.BewConstants;
import es.uvigo.ei.sing.bew.constants.I18n;
import es.uvigo.ei.sing.bew.constants.ShowDialog;
import es.uvigo.ei.sing.bew.constants.WebServicesConstants;

/**
 * This class is an AIBench operation for updating Metadata files from
 * BioFomics.
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
@Operation(description = "Update Metadata files from BioFomics.")
public class UpdateMetadataOperation {

	private boolean conditions;
	private boolean values;

	/**
	 * 
	 * @param method
	 */
	@Port(direction = Direction.INPUT, name = "Set Condition option", order = 1)
	public void setConditions(boolean conditions) {
		this.conditions = conditions;
	}

	/**
	 * 
	 * @param method
	 */
	@Port(direction = Direction.INPUT, name = "Set values option", order = 2)
	public void setValues(boolean values) {
		this.values = values;
	}

	/**
	 * Set the intraExperiment. Select the type of Method to create and add it.
	 * 
	 * @param exp
	 *            intraExperiment.
	 */
	@Port(direction = Direction.INPUT, name = "Set Methods option", order = 3)
	public void setMethods(boolean methods) {
		try {
			// Update Methods
			if (methods) {
				listMethods();
			} else {
				listMethodsWithoutOverwrite();
			}
			if (conditions) {
				listConditions();
			} else {
				listConditionsWithoutOverwrite();
			}
			if (values) {
				listConditionValues();
			} else {
				listConditionValuesWithoutOverwrite();
			}

			// Show OK dialog
			ShowDialog.showInfo(I18n.get("updateOkTitle"),
					I18n.get("updateOkBody"));
		} catch (UnknownHostException e) {
			// No Internet connection error
			ShowDialog.showError(I18n.get("updateErrorTitle"),
					I18n.get("webServicesConnection"));
			e.printStackTrace();
		} catch (Exception e) {
			ShowDialog.showError(I18n.get("updateErrorTitle"),
					I18n.get("unknownError"));
			e.printStackTrace();
		}
	}

	/**
	 * Web service to set the accepted Methods from the Server.
	 * 
	 * @throws IOException
	 */
	private void listMethods() throws Exception {
		// Encode the query
		String postData = "action=" + BewConstants.LISTMETHODS;

		String response;
		response = WebServicesConstants.requestWebService(postData, "", "");

		// Create JSON parser
		JsonFactory factory = new JsonFactory();
		JsonParser jp = factory.createParser(response);

		// Get Methods file
		String auxPath = UpdateMetadataOperation.class.getResource(
				"/files/" + BewConstants.METHODFILE).getPath();
		File methodFile = new File(auxPath);

		// Get actual methods file and delete it
		Path path = methodFile.toPath();
		Files.deleteIfExists(path);

		// Create new empty file and get it
		Files.createFile(path);
		File newMethods = new File(UpdateMetadataOperation.class.getResource(
				"/files/methods.txt").getPath());

		// Open writer for the file
		BufferedWriter output = new BufferedWriter(new FileWriter(newMethods,
				true));

		// Get StartObject for JSON, after first {
		jp.nextToken();
		String method = "";
		String key;
		// Compare if the first element (key) is the same than the last one
		while (jp.nextToken() != JsonToken.END_ARRAY) {

			while (jp.nextToken() != JsonToken.END_OBJECT) {
				key = jp.getText();
				if (key.equals("abbreviation")) {
					jp.nextToken();

					key = jp.getText();
					if (!key.trim().isEmpty()) {
						method += jp.getText() + "-";
					}
				} else if (key.equals("name")) {
					jp.nextToken();
					method += jp.getText() + "\t";
				} else if (key.equals("default_units")) {
					jp.nextToken();
					method += jp.getText();
				}
			}

			if (!method.isEmpty()) {
				output.append(method);
				output.newLine();
				output.flush();
			}

			method = "";

			// Move to value
			jp.nextToken();
		}
		jp.close();
		output.close();
	}

	/**
	 * 
	 * @throws Exception
	 */
	private void listMethodsWithoutOverwrite() throws Exception {
		List<String> methodLines = new ArrayList<String>();
		List<String> serverMethodLines = new ArrayList<String>();
		String currentPath = UpdateMetadataOperation.class
				.getResource("/files/" + BewConstants.METHODFILE).toString()
				.substring(5);

		// Encode the query
		String postData = "action=" + BewConstants.LISTMETHODS;

		String response;
		response = WebServicesConstants.requestWebService(postData, "", "");

		JsonFactory factory = new JsonFactory();
		JsonParser jp = factory.createParser(response);

		// Get currentConditions file
		File currentMethods = new File(currentPath);

		// Read currentConditions file
		BufferedReader br = new BufferedReader(new FileReader(currentMethods));
		String line;
		while ((line = br.readLine()) != null) {
			methodLines.add(line);
		}
		br.close();

		// Create new temporary conditions file and get it
		Path newPath = Files.createTempFile("methods", ".txt");
		File newMethods = newPath.toFile();

		// Open writer for the newConditions file
		BufferedWriter outNewMethods = new BufferedWriter(new FileWriter(
				newMethods, true));
		// Get StartObject for JSON, after first {
		jp.nextToken();
		// Compare if the first element (key) is the same than the last one
		boolean index = false;
		String method = "";
		String name = "";
		String key;
		// Compare if the first element (key) is the same than the last one
		while (jp.nextToken() != JsonToken.END_ARRAY) {

			while (jp.nextToken() != JsonToken.END_OBJECT) {
				key = jp.getText();
				if (key.equals("abbreviation")) {
					jp.nextToken();

					key = jp.getText();
					if (!key.trim().isEmpty()) {
						method += jp.getText() + "\t";
						name += jp.getText();
					}
				} else if (key.equals("name") && method.isEmpty()) {
					jp.nextToken();
					method += jp.getText() + "\t";
					name += jp.getText();
				} else if (key.equals("default_units")) {
					jp.nextToken();
					method += jp.getText();
				}
			}

			if (!method.isEmpty()) {
				if (index)
					outNewMethods.newLine();
				outNewMethods.append(method);
				outNewMethods.flush();

				// Save server conditions
				serverMethodLines.add(name);
				index = true;
			}

			method = "";
			name = "";

			// Move to value
			jp.nextToken();
		}

		// Close server stream
		jp.close();

		// Now validate the user methods in the file
		String auxS = "";

		for (String s : methodLines) {
			auxS = s.split("\t")[0];
			// Verify if user conditions (*Method*) are now accepted in the
			// server
			// (Method)
			if (auxS.startsWith("*") && auxS.endsWith("*")) {
				auxS = auxS.substring(1, auxS.length() - 1);

				if (!serverMethodLines.contains(auxS)) {
					outNewMethods.newLine();
					outNewMethods.append(s);
					outNewMethods.flush();
				}
			}
		}
		outNewMethods.close();

		try {
			// Copy temp file to condition folder
			Files.copy(newPath, Paths.get(currentPath),
					StandardCopyOption.REPLACE_EXISTING);
		} catch (InvalidPathException e) {
			// Copy temp file to condition folder
			Files.copy(newPath, Paths.get(currentPath.substring(1)),
					StandardCopyOption.REPLACE_EXISTING);
		}

		// Delete temporary file
		Files.delete(newPath);
	}

	/**
	 * Web service to set the accepted Conditions from the Server.
	 * 
	 * @throws IOException
	 */
	private void listConditions() throws Exception {
		// Encode the query
		String postData = "action=" + BewConstants.LISTCONDITIONS;

		String response;
		response = WebServicesConstants.requestWebService(postData, "", "");

		JsonFactory factory = new JsonFactory();
		JsonParser jp = factory.createParser(response);

		String auxPath = UpdateMetadataOperation.class.getResource(
				"/files/" + BewConstants.CONDITIONFILE).getPath();
		File condFile = new File(auxPath);

		// Get actual methods file and delete it
		Path path = condFile.toPath();
		Files.deleteIfExists(path);

		// Create new empty file and get it
		Files.createFile(path);
		File newConditions = new File(UpdateMetadataOperation.class
				.getResource("/files/conditions.txt").getPath());

		// Open writer for the file
		BufferedWriter output = new BufferedWriter(new FileWriter(
				newConditions, true));

		// Get StartObject for JSON, after first {
		jp.nextToken();
		boolean index = false;
		// Compare if the first element (key) is the same than the last one
		while (jp.nextToken() != JsonToken.END_OBJECT) {
			// Move to value
			jp.nextToken();
			if (index)
				output.newLine();
			output.append(jp.getText());
			output.flush();
			index = true;
		}
		jp.close();
		output.close();
	}

	/**
	 * Web service to set the accepted Conditions from the Server without
	 * overwrite user Conditions in the file.
	 * 
	 * @throws IOException
	 */
	private void listConditionsWithoutOverwrite() throws Exception {
		List<String> conditionLines = new ArrayList<String>();
		List<String> serverCondLines = new ArrayList<String>();
		String currentPath = UpdateMetadataOperation.class
				.getResource("/files/" + BewConstants.CONDITIONFILE).toString()
				.substring(5);

		// Encode the query
		String postData = "action=" + BewConstants.LISTCONDITIONS;

		String response;
		response = WebServicesConstants.requestWebService(postData, "", "");

		JsonFactory factory = new JsonFactory();
		JsonParser jp = factory.createParser(response);

		// Get currentConditions file
		File currentConditions = new File(currentPath);

		// Read currentConditions file
		BufferedReader br = new BufferedReader(
				new FileReader(currentConditions));
		String line;
		while ((line = br.readLine()) != null) {
			conditionLines.add(line);
		}
		br.close();

		// Create new temporary conditions file and get it
		Path newPath = Files.createTempFile("conditions", ".txt");
		File newConditions = newPath.toFile();

		// Open writer for the newConditions file
		BufferedWriter outNewCond = new BufferedWriter(new FileWriter(
				newConditions, true));
		// Get StartObject for JSON, after first {
		jp.nextToken();
		// Compare if the first element (key) is the same than the last one
		String text = "";
		boolean index = false;
		while (jp.nextToken() != JsonToken.END_OBJECT) {
			// Move to value
			jp.nextToken();

			// Get text
			text = jp.getText();

			// Append server conditions
			if (index)
				outNewCond.newLine();
			outNewCond.append(text);
			outNewCond.flush();
			// Save server conditions
			serverCondLines.add(text);
			index = true;
		}
		// Close server stream
		jp.close();

		// Now validate the user conditions in the file
		String auxS = "";

		for (String s : conditionLines) {
			auxS = s;
			// Verify if user conditions (*Cond*) are now accepted in the server
			// (Cond)
			if (auxS.startsWith("*") && auxS.endsWith("*")) {
				auxS = auxS.substring(1, auxS.length() - 1);

				if (!serverCondLines.contains(auxS)) {
					outNewCond.newLine();
					outNewCond.append(s);
					outNewCond.flush();
				}
			}
		}
		outNewCond.close();

		try {
			// Copy temp file to condition folder
			Files.copy(newPath, Paths.get(currentPath),
					StandardCopyOption.REPLACE_EXISTING);
		} catch (InvalidPathException e) {
			// Copy temp file to condition folder
			Files.copy(newPath, Paths.get(currentPath.substring(1)),
					StandardCopyOption.REPLACE_EXISTING);
		}

		// Delete temporary file
		Files.delete(newPath);
	}

	/**
	 * Web service to set the accepted Condition values from the Server.
	 * 
	 * @throws IOException
	 */
	private void listConditionValues() throws Exception {
		// Encode the query
		String postData = "action=" + BewConstants.LISTVALUES;

		String response;
		response = WebServicesConstants.requestWebService(postData, "", "");

//		System.out.println(response);

		JsonFactory factory = new JsonFactory();
		JsonParser jp = factory.createParser(response);

		String auxPath = UpdateMetadataOperation.class.getResource(
				"/files/" + BewConstants.VALUESFILE).getPath();
		File condFile = new File(auxPath);

		// Get actual methods file and delete it
		Path path = condFile.toPath();
		Files.deleteIfExists(path);

		// Create new empty file and get it
		Files.createFile(path);
		File newConditions = new File(UpdateMetadataOperation.class
				.getResource("/files/" + BewConstants.VALUESFILE).getPath());

		// Open writer for the file
		BufferedWriter output = new BufferedWriter(new FileWriter(
				newConditions, true));

		String key = "";
		String line = "";
		// Get StartObject for JSON, after first {
		jp.nextToken();
		// Only go over the arrays, ex: "antimicrobial":[["Val1", url],["Val2",
		// url]...]]
		while (jp.nextToken() != JsonToken.END_OBJECT) {
			jp.nextToken();
			while (jp.nextToken() != JsonToken.END_OBJECT) {
				// Start after [
				if (!jp.getText().equals("[")) {
					key = jp.getText();
				} else if (jp.getText().equals("[")) {
					while (jp.nextToken() != JsonToken.END_ARRAY) {
						line = key;
						while (jp.nextToken() != JsonToken.END_ARRAY) {
							line += "\t" + jp.getText();
						}
						output.append(line);
						output.newLine();
						output.flush();
					}
				}
			}
		}
		jp.close();
		output.close();
	}

	/**
	 * Web service to set the accepted Condition values from the Server without
	 * overwrite user Condition values in the file.
	 * 
	 * @throws IOException
	 */
	private void listConditionValuesWithoutOverwrite() throws Exception {
		List<String> condValuesLines = new ArrayList<String>();
		List<String> serverCondValuesLines = new ArrayList<String>();
		String currentPath = UpdateMetadataOperation.class
				.getResource("/files/" + BewConstants.VALUESFILE).toString()
				.substring(5);

		// Encode the query
		String postData = "action=" + BewConstants.LISTVALUES;

		String response;
		response = WebServicesConstants.requestWebService(postData, "", "");

		JsonFactory factory = new JsonFactory();
		JsonParser jp = factory.createParser(response);

		// Get currentConditions file
		File currentConditions = new File(currentPath);

		// Read currentCondition file
		BufferedReader br = new BufferedReader(
				new FileReader(currentConditions));
		String line;
		while ((line = br.readLine()) != null) {
			// do something with line.
			condValuesLines.add(line);
		}
		br.close();

		// Create new temporary conditions file and get it
		Path newPath = Files.createTempFile("condition_values", ".txt");
		File newConditions = newPath.toFile();

		// Open writer for the newConditions file
		BufferedWriter outNewCond = new BufferedWriter(new FileWriter(
				newConditions, true));

		String key = "";
		// Get StartObject for JSON, after first {
		jp.nextToken();
		boolean index = false;
		// Only go over the arrays, ex: "antimicrobial":["Val1","Val2"...]
		while (jp.nextToken() != JsonToken.END_OBJECT) {
			jp.nextToken();
			while (jp.nextToken() != JsonToken.END_OBJECT) {
				// Start after [
				if (!jp.getText().equals("[")) {
					key = jp.getText();
				} else if (jp.getText().equals("[")) {
					while (jp.nextToken() != JsonToken.END_ARRAY) {
						line = key;
						while (jp.nextToken() != JsonToken.END_ARRAY) {
							line += "\t" + jp.getText();
						}
						// Save server conditions
						serverCondValuesLines.add(line.replaceAll("\\s+", ""));
						if (index)
							outNewCond.newLine();
						outNewCond.append(line);
						outNewCond.flush();
						index = true;
					}
				}
			}
		}
		// Close server stream
		jp.close();

		// Now validate the user conditions in the file
		String auxS = "";
		String[] splitted;
		for (String s : condValuesLines) {
			splitted = s.split("\t");

			try {
				auxS = splitted[0];
				if (splitted[1].startsWith("*") && splitted[1].endsWith("*")) {
					splitted[1] = splitted[1].substring(1,
							splitted[1].length() - 1);
					auxS += "\t" + splitted[1] + splitted[2];
				} else {
					auxS = "";
				}
				// Verify if user conditions (*Cond*) are now accepted in the
				// server
				// (Cond)
				if (!serverCondValuesLines
						.contains(auxS.replaceAll("\\s+", ""))
						&& !auxS.isEmpty()) {
					outNewCond.newLine();
					outNewCond.append(s);
					outNewCond.flush();
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				e.printStackTrace();
			}

			auxS = "";
		}
		outNewCond.close();

		try {
			// Copy temp file to condition folder
			Files.copy(newPath, Paths.get(currentPath),
					StandardCopyOption.REPLACE_EXISTING);
		} catch (InvalidPathException e) {
			// Copy temp file to condition folder
			Files.copy(newPath, Paths.get(currentPath.substring(1)),
					StandardCopyOption.REPLACE_EXISTING);
		}

		// Delete temporary file
		Files.delete(newPath);
	}
}
