package blazingtwist.wswebservice.functions;

import blazingtwist.wswebservice.WebFunctionUtils;
import blazingtwist.wswebservice.WebServiceFunction;
import blazingtwist.wswebservice.WebServiceFunctionConstructor;
import com.sun.net.httpserver.HttpExchange;
import java.util.Map;

public class ProductConfigProvider extends WebServiceFunction {

	private static final String responseDocumentPath = "ProductConfig-Local.xml";

	private String responseCache = null;

	@WebServiceFunctionConstructor
	public ProductConfigProvider(String contextName) {
		super(contextName);
	}

	@Override
	public void handle(HttpExchange exchange, Map<String, String> params, Map<String, String> body) {
		if (responseCache == null) {
			responseCache = WebFunctionUtils.loadResourceAsString(responseDocumentPath);
		}

		if (responseCache == null) {
			respond(exchange, 500, INTERNAL_ERROR);
		} else {
			respond(exchange, 200, responseCache.replace("${TOKEN_PLACEHOLDER}", "ACTUAL_TOKEN"));
		}
	}
}
