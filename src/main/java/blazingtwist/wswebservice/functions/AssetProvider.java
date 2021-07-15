package blazingtwist.wswebservice.functions;

import blazingtwist.wswebservice.WebFunctionUtils;
import blazingtwist.wswebservice.WebServiceFunction;
import blazingtwist.wswebservice.WebServiceFunctionConstructor;
import com.sun.net.httpserver.HttpExchange;
import java.util.Map;

public class AssetProvider extends WebServiceFunction {

	@WebServiceFunctionConstructor
	public AssetProvider(String contextName) {
		super(contextName);
	}

	@Override
	public void handle(HttpExchange exchange, Map<String, String> params, Map<String, String> body) {
		String targetFile = exchange.getRequestURI().getPath()
				.replace(contextName, "")
				.replace("/", "");
		logger.warn("received request for file: {}", targetFile);
		byte[] file = WebFunctionUtils.loadResource("Assets/" + targetFile);
		if (file == null) {
			respond(exchange, 404, INTERNAL_ERROR);
		} else {
			respond(exchange, 200, file, "application/octet-stream");
			logger.info("Provided file '{}'", targetFile);
		}
	}
}

