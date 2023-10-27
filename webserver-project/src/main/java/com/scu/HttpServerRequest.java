package com.scu;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpServerRequest implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(HttpServerRequest.class);
	private static final String INDEX_FILE_HTML = "index.html";
	private static final String NEW_LINE = "\r\n";

	private static final String STATUS_200 = "HTTP/1.1 200 OK";
	private static final String STATUS_400 = "HTTP/1.1 400 Bad Request";
	private static final String STATUS_403 = "HTTP/1.1 403 Forbidden";
	private static final String STATUS_404 = "HTTP/1.1 404 Not Found";
	private static final String STATUS_405 = "HTTP/1.1 405 Method Not Allowed";
	private static final String STATUS_500 = "HTTP/1.1 500 Internal Server Error";

	private static final String CONTENT_TYPE_TEXT_HTML = "text/html";
	private static final String CONTENT_TYPE_IMG_JPEG = "image/jpeg";
	private static final String CONTENT_TYPE_IMG_PNG = "image/png";
	private static final String CONTENT_TYPE_IMG_GIF = "image/gif";
	private static final String CONTENT_TYPE_APPLICATION = "application/octet-stream";


	
	private static final String CONTENT_404_NOT_FOUND = "<html><head><title>Not Found</title></head><body><h1 style='color: black; text-align: center;'>404 Error: Not Found</h1></body></html>";


	private Socket socket;
	private String rootDirectory;

	public HttpServerRequest(Socket socket, String rootDirectory) {
		this.socket = socket;
		this.rootDirectory = rootDirectory;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		log.info("Current thread {}", Thread.currentThread().getName());

		// try-with resources
		try (BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				DataOutputStream os = new DataOutputStream(socket.getOutputStream())) {

			int statusCode;
			String contentType;
			byte[] entityBody = null;

			String requestLine = br.readLine();

			// 400 Response
			if (requestLine == null || requestLine.isBlank()) {
				statusCode = 400;
				writeHttpResponse(os, statusCode, CONTENT_TYPE_TEXT_HTML, null);
				closeSocket();
				return;
			}

			log.info("Request body is {}", requestLine);
			String[] requestArgs = requestLine.split("\\s+");

			// 405 Response
			if (!requestArgs[0].equalsIgnoreCase("GET")) {
				statusCode = 405;
				writeHttpResponse(os, statusCode, CONTENT_TYPE_TEXT_HTML, null);
				closeSocket();
				return;
			}

			String filename = requestArgs[1];
			if (filename.equals("/")) {
				filename = INDEX_FILE_HTML;
			}
			Path filePath = Paths.get(rootDirectory, filename);

			// Construct the response message.
			if (Files.exists(filePath)) {
				if (Files.isDirectory(filePath)) {
					log.error("Requested resource {} is a directory", filePath.toString());
					statusCode = 400;
					contentType = CONTENT_TYPE_TEXT_HTML;
				} else if (!Files.isReadable(filePath)) {
					log.error("Do not have access to read the file {}", filePath.toString());
					statusCode = 403;
					contentType = CONTENT_TYPE_TEXT_HTML;
				} else {
					log.info("Requested file {}", filePath.toString());
					String fileName = filePath.getFileName().toString();
					if (fileName.endsWith(".html") || fileName.endsWith(".txt")) {
						contentType = CONTENT_TYPE_TEXT_HTML;
					} else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
						contentType = CONTENT_TYPE_IMG_JPEG;
					} else if (fileName.endsWith(".png")) {
						contentType = CONTENT_TYPE_IMG_PNG;
					} else if (fileName.endsWith(".gif")) {
						contentType = CONTENT_TYPE_IMG_GIF;
					} else {
						contentType = CONTENT_TYPE_APPLICATION;
					}
					entityBody = Files.readAllBytes(filePath);
					statusCode = 200;

				}
			} else {
				log.error("Requested file {} does not exist ", filePath.toString());
				statusCode = 404;
				contentType = CONTENT_TYPE_TEXT_HTML;
				entityBody = CONTENT_404_NOT_FOUND.getBytes();
			}

			writeHttpResponse(os, statusCode, contentType, entityBody);
			closeSocket();

		} catch (IOException e) {
			log.error("Error in processing request", e);
		}

	}

	private void writeHttpResponse(DataOutputStream outputStream, int statusCode, String contentType,
			byte[] responseBody) throws IOException {
		// Write the HTTP content
		outputStream.writeBytes(buildStatusLine(statusCode));

		String headers = buildHttpHeaders(statusCode, contentType, responseBody);
		outputStream.writeBytes(headers);
		outputStream.writeBytes(NEW_LINE);
		if (responseBody != null) {
			outputStream.write(responseBody);
		}
	}

	private String buildStatusLine(int statusCode) {
		switch (statusCode) {
		case 200:
			log.debug("Building status line for HTTP 200 OK");
			return STATUS_200 + NEW_LINE;
		case 400:
			log.debug("Building status line for HTTP 400 Bad Request");
			return STATUS_400 + NEW_LINE;
		case 403:
			log.debug("Building status line for HTTP 403 Forbidden");
			return STATUS_403 + NEW_LINE;
		case 404:
			log.debug("Building status line for HTTP 404 Not Found");
			return STATUS_404 + NEW_LINE;
		case 405:
			log.debug("Building status line for HTTP 405 Method Not Allowed");
			return STATUS_405 + NEW_LINE;
		default:
			log.debug("Building status line for HTTP 500 Internal Server Error");
			return STATUS_500 + NEW_LINE;
		}
	}

	private String buildHttpHeaders(int status, String type, byte[] body) {
		StringBuilder headerString = new StringBuilder();
		headerString.append("Date: ").append(getServerTime()).append(NEW_LINE);
		headerString.append("Content-Type: ").append(type).append(NEW_LINE);
		if (body != null) {
			headerString.append("Content-Length: ").append(body.length).append(NEW_LINE);
		}
		if (status == 405) {
			headerString.append("Allow: GET").append(NEW_LINE);
		}
		return headerString.toString();
	}

	private String getServerTime() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z", Locale.US)
				.withZone(ZoneId.of("GMT"));
		return LocalDateTime.now().format(formatter);
	}

	private void closeSocket() throws IOException {
		socket.close();
	}
}
