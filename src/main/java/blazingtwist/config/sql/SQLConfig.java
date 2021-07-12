package blazingtwist.config.sql;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SQLConfig {
	@JsonProperty("path")
	private String path;

	@JsonCreator
	private SQLConfig() {
	}

	public String getPath() {
		return path;
	}
}
