package blazingtwist.config.web;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;

public class WebConfig {
	@JsonProperty("address")
	private String address;

	@JsonProperty("port")
	private int port;

	@JsonProperty("threadCount")
	private int threadCount;

	@JsonProperty("functions")
	private HashMap<String, WebFunctionConfig> webServiceFunctions;

	@JsonCreator
	private WebConfig() {
	}

	public String getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}

	public int getThreadCount() {
		return threadCount;
	}

	public HashMap<String, WebFunctionConfig> getWebServiceFunctions() {
		return webServiceFunctions;
	}
}
