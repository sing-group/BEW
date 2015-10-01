package es.uvigo.ei.sing.bew.constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.rmi.ServerException;

import javax.naming.AuthenticationException;
import javax.naming.ServiceUnavailableException;

import es.uvigo.ei.aibench.core.Base64Coder;

/**
 * Class with some methods to do operations with web services.
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
public class WebServicesConstants {

	/**
	 * Shows a specific error from the server when a web service is called.
	 * 
	 * @param status
	 *            String with the server response.
	 */
	public static void showSpecificError(String status) {
		if (status.equals("0")) {
//			System.out.println("Invalid User");
		} else if (status.equals("1")) {
//			System.out.println("Invalid Cookie");
		} else if (status.equals("2")) {
//			System.out.println("Auth Cookie Error");
		} else if (status.equals("3")) {
//			System.out.println("Experiment not found");
		} else if (status.equals("4")) {
//			System.out.println("Experiment Saved");
		} else if (status.equals("5")) {
//			System.out.println("Experiment save error");
		} else if (status.equals("6")) {
//			System.out.println("Relation save error");
		} else if (status.equals("7")) {
//			System.out.println("Need experiment Id");
		} else if (status.equals("10")) {
//			System.out.println("Unknown error");
		} else if (status.equals("11")) {
//			System.out.println("server Ok");
		} else {
//			System.out.println("Unknown error");
		}
	}

	/**
	 * Shows a specific response from the server when a web service is called.
	 * 
	 * @param status
	 *            String with the server response.
	 * @throws AuthenticationException
	 * @throws ServerException
	 * @throws ServiceUnavailableException
	 */
	public static void showServerResponse(Integer response)
			throws AuthenticationException, ServerException,
			ServiceUnavailableException {
		if (response == 401) {
			throw new AuthenticationException();
		} else if (response == 500) {
			throw new ServerException("");
		} else if (response == 200) {
			// System.out.println("200 OK");
		} else {
			throw new ServiceUnavailableException();
		}
	}

	/**
	 * Method to do a request to the server with the input web service and
	 * return it response as string.
	 * 
	 * @param webService
	 *            Web service to invoke.
	 * @return String with server response.
	 * @throws IOException
	 * @throws AuthenticationException
	 * @throws ServiceUnavailableException
	 */
	public static String requestWebService(String webService, String user,
			String pass) throws IOException, AuthenticationException,
			ServiceUnavailableException, UnknownHostException {
		// Get URL
		URL url = new URL(BewConstants.WEB);

		// Open the connection
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Length",
				String.valueOf(webService.length()));

		// Set authorization header
		String auth = Base64Coder.encodeString(user + ":" + pass);
		connection.addRequestProperty("Authorization", "Basic " + auth);

		// Send data to server
		OutputStream os = connection.getOutputStream();
		os.write(webService.getBytes());
		os.flush();
		os.close();

		showServerResponse(connection.getResponseCode());

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

		connection.disconnect();

		return responseSB.toString();
	}
}
