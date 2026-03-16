package configReader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import connector.models.service.config.ConfigData;

import java.io.File;
import java.io.IOException;

public class ConfigReader {
    private static final String PATH = "src/main/resources/config.yaml";

    private final ObjectMapper mapper;

    public ConfigReader() {
        this.mapper = new ObjectMapper(new YAMLFactory());
    }

    public ConfigData readConfig() {
        try {
            ConfigData config = mapper.readValue(new File(PATH), ConfigData.class);
            return config;
        } catch (IOException e) {
            throw new IllegalStateException("failed to read config from: " + PATH, e);
        }
    }
}
