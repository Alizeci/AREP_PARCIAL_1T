/**
 * 
 */
package edu.escuelaing.arep.weatherapi.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Clase que contiene todas las características del Webserver.
 * 
 * @author aleja
 */
public class WebServer {

	/**
	 * Atributo que define el WebServer
	 */
	public static final WebServer _instance = new WebServer();

	protected String city;

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getURL() {
		return "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=182835f08a24103febf5318c1af5167b";
	}

	public WebServer() {

	}

	/**
	 * Preparando la comunicación para el intercambio de mensajes.
	 * 
	 * @param args - peticiones del cliente
	 * @param port - puerto de comunicación
	 * @throws IOException        - Cuando no es posible establecer la comunicación
	 * @throws URISyntaxException - Cuando no es posible interpretar la URI
	 */
	public void startSocket(String[] args, int port) throws IOException, URISyntaxException {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.err.println("Could not listen on port: 35000.");
			System.exit(1);
		}

		boolean running = true;
		while (running) {
			Socket clientSocket = null;
			try {
				// System.out.println("Listo para recibir ...");
				clientSocket = serverSocket.accept();

			} catch (IOException e) {
				System.err.println("Accept failed.");
				System.exit(1);
			}
			serverConnection(clientSocket);
		}
		serverSocket.close();
	}

	/**
	 * Conectando el Cliente con el Servidor y atendiendo su petición(es).
	 * 
	 * @param clientSocket - Comunicación con el cliente
	 * @throws IOException        - Cuando no es posible establecer la comunicación.
	 * @throws URISyntaxException - Cuando no es posible interpretar la URI.
	 */
	public void serverConnection(Socket clientSocket) throws IOException, URISyntaxException {
		if (clientSocket != null) {

			PrintWriter out;
			BufferedReader in;
			OutputStream los_outputStream;
			InputStream lis_inputStream;

			los_outputStream = clientSocket.getOutputStream();
			lis_inputStream = clientSocket.getInputStream();

			if ((los_outputStream != null) && (lis_inputStream != null)) {

				String inputLine, outputLine;
				StringBuilder request;
				InputStreamReader lisr_inputStreamReader;

				request = new StringBuilder();
				lisr_inputStreamReader = new InputStreamReader(lis_inputStream);
				out = new PrintWriter(los_outputStream, true); // envío de msgs al Cliente.

				if (lisr_inputStreamReader != null) {
					in = new BufferedReader(lisr_inputStreamReader); // recibir msgs del Cliente

					if (in != null && in.ready()) {

						while ((inputLine = in.readLine()) != null) {
							System.out.println("Received: " + inputLine);
							request.append(inputLine);
							if (!in.ready()) {
								break;
							}
						}
					}
					String ls_request;
					ls_request = request.toString();

					if ((ls_request != null) && (!ls_request.isEmpty())) {

						String ls_uriStr;
						String[] las_request;

						las_request = ls_request.split(" ");

						if ((las_request != null)) {
							ls_uriStr = las_request[1];

							if ((ls_uriStr != null) && (!ls_uriStr.isEmpty())) {
								URI resourceURI = new URI(ls_uriStr);
								
								String api = resourceURI.getPath();
								String query = resourceURI.getQuery();
								
								//System.out.println("api: " + api);
								//System.out.println("query: " + query);
								
								if (api.equals("/clima")) {
									outputLine = weatherPage();
									out.println(outputLine);
									
								} else if (api.equals("/consulta") && query!= null) {
									String nameCity = query.substring(query.indexOf("=")+1);
									
									if (nameCity!=null) {
										//System.out.println(query.substring(query.indexOf("=")+1));
										setCity(nameCity);
										
										String response = getURL();
										
										if (response!=null) {
											outputLine = weatherCity(response);
											out.println(outputLine);
										}else {
											throw new IOException("ServerConnection response informació del clima desconocida!");
										}
									}else {
										throw new IOException("ServerConnection city input vacío o nulo!");
									}
								} else {
									outputLine = default404Response();
									out.println(outputLine);
								}
							}
						}
					}
					out.close();
					in.close();
				}
			} else {
				throw new IOException("ServerConnection BufferReader input vacío o nulo!");
			}
			clientSocket.close();
		} else {
			throw new IOException("ServerConnection Socket no puede ser nulo");
		}
	}

	private String weatherCity(String response) {
		String outputLine = "HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n" + "\r\n" + "<!DOCTYPE html>\n"
				+ "<html>\n" + "	<head>\n" + "		<meta charset=\"UTF-8\">\n"
				+ "<meta http-equiv=\"refresh\" content=\"1;URL="+response+"\">"
				+ "		<title>Weatherpage</title>\n" + "	</head>\n" + "	<body>\n" + "</body>\n" + "</html>\n";
		return outputLine;
	}

	/**
	 * Página del clima al intentar conectar con el API /clima
	 * 
	 * @return la página de la url por defecto
	 */
	private String weatherPage() {
		String outputLine = "HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n" + "\r\n" + "<!DOCTYPE html>\n"
				+ "<html>\n" + "	<head>\n" + "		<meta charset=\"UTF-8\">\n"
				+ "<meta http-equiv=\"refresh\" content=\"1;URL=http://openweathermap.org\">"
				+ "		<title>Weatherpage</title>\n" + "	</head>\n" + "	<body>\n" + "</body>\n" + "</html>\n";
		return outputLine;
	}

	/**
	 * Página por defecto al intentar conectar con el servidor
	 * 
	 * @return la página por defecto en html
	 */
	private String default404Response() {
		String outputLine = "HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n" + "\r\n" + "<!DOCTYPE html>\n"
				+ "<html>\n" + "	<head>\n" + "		<meta charset=\"UTF-8\">\n" + "		<title>Inicio</title>\n"
				+ "	</head>\n" + "	<body>\n" + "		<h1>NOT FOUND 404</h1>\n" + "	</body>\n" + "</html>\n";
		return outputLine;
	}

}
