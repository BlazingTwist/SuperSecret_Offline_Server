package blazingtwist.wswebservice;

import blazingtwist.logback.LogbackLoggerProvider;
import blazingtwist.crypto.TripleDes;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import javax.xml.bind.JAXBException;
import org.slf4j.Logger;

public abstract class WebServiceFunction implements HttpHandler {
	protected static final Logger logger = LogbackLoggerProvider.getLogger(WebServiceFunction.class);

	public static final String CONTENT_LENGTH = "Content-Length";
	public static final String CONTENT_TYPE = "Content-Type";
	public static final String WWW_FORM_URL_ENCODED = "application/x-www-form-urlencoded";

	public static final String INVALID_PARAMS = "Received invalid Parameters";
	public static final String INVALID_BODY = "Received invalid Body";
	public static final String INTERNAL_ERROR = "Internal Server Error";
	public static final String ERROR_INVALID_SIGNATURE = "Received invalid signature";

	protected final String contextName;

	public WebServiceFunction(String contextName) {
		this.contextName = contextName;
	}

	public void register(HttpServer server) {
		server.createContext("/" + contextName, this);
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		logger.trace("Called WebService Function {}", contextName);

		String requestURI = exchange.getRequestURI().toString();
		String[] uriSplit = requestURI.split("\\?");
		Map<String, String> params = null;
		if (uriSplit.length == 2) {
			params = WebFunctionUtils.readUrlMap(uriSplit[1].replace('\r', ' '));

			for (Map.Entry<String, String> paramEntry : params.entrySet()) {
				logger.trace("\turlArg: {} = {}", paramEntry.getKey(), paramEntry.getValue());
			}
		}

		Map<String, String> body = null;
		String contentLengthString = exchange.getRequestHeaders().getFirst(CONTENT_LENGTH);
		int contentLength = contentLengthString != null ? Integer.parseInt(contentLengthString) : 0;
		if (contentLength > 0) {
			if (WWW_FORM_URL_ENCODED.equalsIgnoreCase(exchange.getRequestHeaders().getFirst(CONTENT_TYPE))) {
				byte[] dataBytes = exchange.getRequestBody().readAllBytes();
				String urlEncodedData = new String(dataBytes, StandardCharsets.UTF_8);
				String dataString = URLDecoder.decode(urlEncodedData, StandardCharsets.UTF_8);
				dataString = dataString.replace('\r', ' ');
				body = WebFunctionUtils.readUrlMap(dataString);

				for (Map.Entry<String, String> bodyEntry : body.entrySet()) {
					logger.trace("\tbodyArg: {} = {}", bodyEntry.getKey(), bodyEntry.getValue());
				}
			}
		}

		try {
			this.handle(exchange, params, body);
		} catch (Exception e) {
			logger.error("unhandled exception!", e);
			respond(exchange, 500, INTERNAL_ERROR);
		}
	}

	public abstract void handle(HttpExchange exchange, Map<String, String> params, Map<String, String> body);

	public void respond(HttpExchange exchange, int responseCode, String response) {
		try {
			logger.trace("responding with code {}", responseCode);

			exchange.getResponseHeaders().add(CONTENT_TYPE, "text/xml; charset=utf-8");
			exchange.sendResponseHeaders(responseCode, response.length());
			OutputStream outputStream = exchange.getResponseBody();
			outputStream.write(response.getBytes());
			outputStream.flush();
			outputStream.close();
		} catch (IOException e) {
			logger.error("Unable to send response! context: {}", contextName, e);
		}
	}

	public void respond(HttpExchange exchange, int responseCode, byte[] response, String contentType) {
		try {
			logger.trace("responding with code {}", responseCode);

			exchange.getResponseHeaders().add(CONTENT_TYPE, contentType);
			exchange.sendResponseHeaders(responseCode, response.length);
			OutputStream outputStream = exchange.getResponseBody();
			outputStream.write(response);
			outputStream.flush();
			outputStream.close();
		} catch (IOException e) {
			logger.error("Unable to send response! context: {}", contextName, e);
		}
	}

	public <T> void respondXml(HttpExchange exchange, int responseCode, T response, boolean encrypt) {
		try {
			@SuppressWarnings("unchecked")
			String resultString = WebFunctionUtils.marshalXml(response, (Class<T>) response.getClass(), encrypt);
			if (encrypt) {
				resultString = WebFunctionUtils.marshalXml(TripleDes.encrypt(resultString), "string", String.class, false);
			}
			respond(exchange, responseCode, resultString);
		} catch (JAXBException e) {
			logger.error("Error during respondXml", e);
			respond(exchange, 500, INTERNAL_ERROR);
		}
	}

	public <T> void respondXml(HttpExchange exchange, int responseCode, T response, String rootName, boolean encrypt) {
		try {
			@SuppressWarnings("unchecked")
			String resultString = WebFunctionUtils.marshalXml(response, rootName, (Class<T>) response.getClass(), encrypt);
			if (encrypt) {
				resultString = WebFunctionUtils.marshalXml(TripleDes.encrypt(resultString), "string", String.class, false);
			}
			respond(exchange, responseCode, resultString);
		} catch (JAXBException e) {
			logger.error("Error during respondXml", e);
			respond(exchange, 500, INTERNAL_ERROR);
		}
	}

	public void respondEncryptedString(HttpExchange exchange, int responseCode, String response) {
		try {
			String resultString = WebFunctionUtils.marshalXml(TripleDes.encrypt(response), "string", String.class, false);
			respond(exchange, responseCode, resultString);
		} catch (JAXBException e) {
			logger.error("Error during respondEncryptedString", e);
			respond(exchange, 500, INTERNAL_ERROR);
		}
	}
}
