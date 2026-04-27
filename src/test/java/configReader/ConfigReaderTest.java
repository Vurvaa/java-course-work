package configReader;

import models.service.config.ConfigData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ConfigReaderTest {

    @TempDir
    Path tempDir;

    @Test
    void testReadConfigGood() throws Exception {
        Path configFile = tempDir.resolve("config.yaml");

        String yaml = """
                apis:
                  - name: api1
                    url: http://localhost:8000
                outputFormat: JSON
                maxTaskNum: 5
                pollingInterval: 10
                """;

        Files.writeString(configFile, yaml);

        ConfigReader reader = new ConfigReader(configFile.toString());

        ConfigData config = reader.readConfig();

        assertAll(
                () -> assertNotNull(config),
                () -> assertEquals("JSON", config.outputFormat()),
                () -> assertEquals(5, config.maxTaskNum()),
                () -> assertEquals(10, config.pollingInterval()),
                () -> assertEquals(1, config.apis().size()),
                () -> assertEquals("api1", config.apis().get(0).name())
        );
    }

    @Test
    void testReadConfigBadPath() {
        ConfigReader reader = new ConfigReader("bad/path/config.yaml");

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                reader::readConfig
        );

        assertAll(
                () -> assertTrue(exception.getMessage().contains("failed to read config from")),
                () -> assertNotNull(exception.getCause())
        );
    }
}