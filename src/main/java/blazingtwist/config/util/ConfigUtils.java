package blazingtwist.config.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;

public class ConfigUtils {
	public static ObjectMapper createDefaultMapper(){
		return new ObjectMapper()
				.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_VALUES,
						MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS,
						MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
				.disable(MapperFeature.AUTO_DETECT_CREATORS,
						MapperFeature.AUTO_DETECT_FIELDS,
						MapperFeature.AUTO_DETECT_GETTERS,
						MapperFeature.AUTO_DETECT_IS_GETTERS);
	}

	public static <T> T loadSegmentedConfig(Class<T> clazz, String... resourcePaths) {
		if (resourcePaths.length == 0) {
			return null;
		}
		Config config = null;
		for (String resourcePath : resourcePaths) {
			if (config == null) {
				config = ConfigFactory.parseResources(resourcePath);
			} else {
				config = config.withFallback(ConfigFactory.parseResources(resourcePath));
			}
		}
		config = config.resolve();
		return loadConfigToClass(clazz, config);
	}

	public static <T> T loadConfig(Class<T> clazz, String resourcePath) {
		Config config = ConfigFactory.parseResources(resourcePath);
		config = config.resolve();
		return loadConfigToClass(clazz, config);
	}

	private static <T> T loadConfigToClass(Class<T> clazz, Config config) {
		String configJson = config.root().render(ConfigRenderOptions.concise());
		System.out.println(configJson);
		ObjectMapper mapper = createDefaultMapper();

		try {
			return mapper.readValue(configJson, clazz);
		} catch (JsonProcessingException e) {
			System.err.println("Failed to parse configFile!");
			e.printStackTrace();
			return null;
		}
	}
}
