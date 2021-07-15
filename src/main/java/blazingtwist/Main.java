package blazingtwist;

import blazingtwist.config.ConfigUtils;
import blazingtwist.config.sql.SQLConfig;
import blazingtwist.config.web.WebConfig;
import blazingtwist.config.web.WebFunctionConfig;
import blazingtwist.database.MainDBAccessor;
import blazingtwist.logback.LogbackLoggerProvider;
import blazingtwist.wswebservice.WebServiceFunction;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import org.reflections.Reflections;
import org.slf4j.Logger;

public class Main {
	private static final Logger logger = LogbackLoggerProvider.getLogger(Main.class);
	private static WebHttpHandler webHandler;

	public static WebHttpHandler getWebHandler() {
		return webHandler;
	}

	private static Set<Class<? extends WebServiceFunction>> findWebFunctions() {
		Reflections reflections = new Reflections("blazingtwist.wswebservice.functions");
		return reflections.getSubTypesOf(WebServiceFunction.class);
	}

	public static void main(String[] args) {
		SQLConfig sqlConfig = ConfigUtils.loadConfig(SQLConfig.class, "SQLConfig.conf");
		MainDBAccessor.initialize(sqlConfig);
		Runtime.getRuntime().addShutdownHook(new Thread(Main::onShutdown));

		WebConfig webConfig = ConfigUtils.loadConfig(WebConfig.class, "WebConfig.conf");
		HashMap<String, WebFunctionConfig> webFunctionsConfig = webConfig.getWebServiceFunctions();
		ArrayList<WebServiceFunction> webFunctions = new ArrayList<>();

		Set<Class<? extends WebServiceFunction>> webFunctionTypes = findWebFunctions();
		for (Class<? extends WebServiceFunction> webFunctionType : webFunctionTypes) {

			String functionName = webFunctionType.getSimpleName();
			if (!webFunctionsConfig.containsKey(functionName)) {
				continue;
			}

			WebFunctionConfig functionConfig = webFunctionsConfig.get(functionName);
			if (!functionConfig.isEnabled()) {
				continue;
			}

			try {
				webFunctions.add(webFunctionType.getDeclaredConstructor(String.class).newInstance(functionConfig.getContextName()));
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				logger.error("Unable to load WebFunction {}", webFunctionType.getSimpleName(), e);
			}
		}

		try {
			webHandler = new WebHttpHandler(webConfig, webFunctions);
		} catch (IOException e) {
			logger.error("Unable to start Server", e);
			System.exit(-1);
		}
	}

	private static void onShutdown() {
		MainDBAccessor.disconnect();
	}
}
