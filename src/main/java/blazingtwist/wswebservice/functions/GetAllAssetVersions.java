package blazingtwist.wswebservice.functions;

import blazingtwist.wswebservice.WebFunctionUtils;
import blazingtwist.wswebservice.WebServiceFunction;
import blazingtwist.wswebservice.WebServiceFunctionConstructor;
import com.sun.net.httpserver.HttpExchange;
import java.util.Map;

public class GetAllAssetVersions extends WebServiceFunction {

	private static final String responseDocumentPath = "AssetVersions.xml";

	private String responseCache = null;

	@WebServiceFunctionConstructor
	public GetAllAssetVersions(String contextName) {
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
			respond(exchange, 200, responseCache);
		}
	}
}
