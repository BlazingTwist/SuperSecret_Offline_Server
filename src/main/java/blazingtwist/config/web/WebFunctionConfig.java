package blazingtwist.config.web;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class WebFunctionConfig {
	@JsonProperty("enabled")
	private boolean enabled;

	@JsonProperty("contextName")
	private String contextName;

	@JsonCreator
	private WebFunctionConfig() {
	}

	public boolean isEnabled() {
		return enabled;
	}

	public String getContextName() {
		return contextName;
	}
}
