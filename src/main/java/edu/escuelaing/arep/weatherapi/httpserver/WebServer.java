/**
 * 
 */
package edu.escuelaing.arep.weatherapi.httpserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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

	}

}
