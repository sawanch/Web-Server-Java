package com.scu;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebServerMain {

	private static final Logger log = LoggerFactory.getLogger(WebServerMain.class);
	private static final String DOCUMENT_ROOT = "-document_root";
	private static final String PORT = "-port";

	public static void main(String[] args) {
		// taking arguments
		if (args.length != 4 || !args[0].equals(DOCUMENT_ROOT) || !args[2].equals(PORT)) {
			log.error("Kindly provide the necessary parameters in the following format\n"
					+ "-document_root {root directory} -port {port number}");
			System.exit(-1);
		}
		//initiating parameters
		String rootDirectory = args[1];
		int port = Integer.parseInt(args[3]);
		log.info("WebServer listening on port :: {}", port);

		try (ServerSocket serverSocket = new ServerSocket(port)) {//try
			while (true) {
				Socket connectionSocket = serverSocket.accept();
				Thread thread = new Thread(new HttpServerRequest(connectionSocket, rootDirectory));
				thread.start();
				log.info("WebServer thread started");
			}
		} catch (IOException e) {//catch
			log.error("Failure to listen on Port :: {}", port);
		}

	}

}
