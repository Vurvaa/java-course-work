package configReader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import models.service.config.ConfigData;
import java.io.File;
import java.io.IOException;

public class ConfigReader {
    private static final String PATH = "src/main/resources/config.yaml";

    private final ObjectMapper mapper;
    private final String path;

    public ConfigReader() {
        this(PATH);
    }

    public ConfigReader(String path) {
        this.mapper = new ObjectMapper(new YAMLFactory());
        this.path = path;
    }

    public ConfigData readConfig() {
        try {
            return mapper.readValue(new File(path), ConfigData.class);
        } catch (IOException e) {
            throw new IllegalStateException("failed to read config from: " + PATH, e);
        }
    }
}
