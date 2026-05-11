package connector.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.service.notes.CommonModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DataPusherTest {
    private static final String JSON_FILE = "output.json";
    private static final String CSV_FILE = "output.csv";

    @AfterEach
    void deleteFiles() {
        deleteFileOrDir(new File(JSON_FILE));
        deleteFileOrDir(new File(CSV_FILE));
    }


    @Test
    void testPrepareJsonFile() {
        DataPusher pusher = new DataPusher();

        pusher.prepareFile("JSON", true);

        File file = new File(JSON_FILE);

        assertAll(
                () -> assertTrue(file.exists()),
                () -> assertTrue(file.length() > 0),
                () -> assertEquals("[ ]", Files.readString(file.toPath()).trim())
        );
    }

    @Test
    void testPrepareCsvFile() {
        DataPusher pusher = new DataPusher();

        pusher.prepareFile("CSV", true);

        File file = new File(CSV_FILE);

        assertAll(
                () -> assertTrue(file.exists()),
                () -> assertEquals(0, file.length())
        );
    }

    @Test
    void testPushCsvWithReadError() {
        File file = new File(CSV_FILE);
        file.mkdir();

        DataPusher pusher = new DataPusher();

        assertDoesNotThrow(() -> pusher.pushCSV(
                List.of(Map.of(
                        "id", "1",
                        "source", "api1",
                        "timestamp", "now",
                        "name", "player1"
                )),
                List.of("name")
        ));

        assertAll(
                () -> assertTrue(file.exists()),
                () -> assertTrue(file.isDirectory())
        );
    }

    @Test
    void testPushCsvWhenFileDoesNotExist() {
        File file = new File(CSV_FILE);
        file.delete();

        DataPusher pusher = new DataPusher();

        pusher.pushCSV(
                List.of(Map.of(
                        "id", "1",
                        "source", "api1",
                        "timestamp", "now",
                        "name", "player1"
                )),
                List.of("name")
        );

        try {
            String csv = Files.readString(file.toPath());

            assertAll(
                    () -> assertTrue(file.exists()),
                    () -> assertTrue(csv.contains("id,source,timestamp,name")),
                    () -> assertTrue(csv.contains("1,api1,now,player1"))
            );
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void testPrepareJsonFileWithError() {
        File file = new File(JSON_FILE);
        file.mkdir();

        DataPusher pusher = new DataPusher();

        assertDoesNotThrow(() -> pusher.prepareFile("JSON", true));

        assertAll(
                () -> assertTrue(file.exists()),
                () -> assertTrue(file.isDirectory())
        );
    }

    @Test
    void testPrepareCsvFileWithError() {
        File file = new File(CSV_FILE);
        file.mkdir();

        DataPusher pusher = new DataPusher();

        assertDoesNotThrow(() -> pusher.prepareFile("CSV", true));

        assertAll(
                () -> assertTrue(file.exists()),
                () -> assertTrue(file.isDirectory())
        );
    }

    @Test
    void testPrepareFileWhenNotNewFile() {
        DataPusher pusher = new DataPusher();

        pusher.prepareFile("JSON", false);

        File jsonFile = new File(JSON_FILE);
        File csvFile = new File(CSV_FILE);

        assertAll(
                () -> assertFalse(jsonFile.exists()),
                () -> assertFalse(csvFile.exists())
        );
    }

    @Test
    void testPushJsonNewFile() {
        DataPusher pusher = new DataPusher();

        CommonModel<String> model = new CommonModel<>(
                UUID.randomUUID(),
                "now",
                "test-api",
                List.of("one", "two")
        );

        pusher.pushJSON(model);

        File file = new File(JSON_FILE);
        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode root = mapper.readTree(file);
            assertAll(
                    () -> assertTrue(file.exists()),
                    () -> assertTrue(root.isArray()),
                    () -> assertEquals(1, root.size()),
                    () -> assertEquals("test-api", root.get(0).get("source").asText()),
                    () -> assertEquals("now", root.get(0).get("timeStamp").asText()),
                    () -> assertEquals("one", root.get(0).get("data").get(0).asText()),
                    () -> assertEquals("two", root.get(0).get("data").get(1).asText())
            );
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void testPushJsonAppendToFile() {
        DataPusher pusher = new DataPusher();

        CommonModel<String> first = new CommonModel<>(
                UUID.randomUUID(),
                "now",
                "api1",
                List.of("first")
        );

        CommonModel<String> second = new CommonModel<>(
                UUID.randomUUID(),
                "now",
                "api2",
                List.of("second")
        );

        pusher.pushJSON(first);
        pusher.pushJSON(second);

        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(new File(JSON_FILE));

            assertAll(
                    () -> assertEquals(2, root.size()),
                    () -> assertEquals("api1", root.get(0).get("source").asText()),
                    () -> assertEquals("api2", root.get(1).get("source").asText())
            );
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void testPushCsvNewFile() {
        DataPusher pusher = new DataPusher();

        List<String> headers = List.of("name", "score");
        List<Map<String, String>> rows = List.of(
                Map.of(
                        "id", "1",
                        "source", "test-api",
                        "timestamp", "now",
                        "name", "player1",
                        "score", "100"
                )
        );

        pusher.pushCSV(rows, headers);

        try {
            String csv = Files.readString(new File(CSV_FILE).toPath());

            assertAll(
                    () -> assertTrue(csv.contains("id,source,timestamp,name,score")),
                    () -> assertTrue(csv.contains("1,test-api,now,player1,100"))
            );
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void testPushCsvAppendRows() {
        DataPusher pusher = new DataPusher();

        pusher.pushCSV(
                List.of(Map.of(
                        "id", "1",
                        "source", "api1",
                        "timestamp", "now",
                        "name", "player1"
                )),
                List.of("name")
        );

        pusher.pushCSV(
                List.of(Map.of(
                        "id", "2",
                        "source", "api2",
                        "timestamp", "now",
                        "score", "200"
                )),
                List.of("score")
        );

        try {
            String csv = Files.readString(new File(CSV_FILE).toPath());

            assertAll(
                    () -> assertTrue(csv.contains("id,source,timestamp,name,score")),
                    () -> assertTrue(csv.contains("1,api1,now,player1,")),
                    () -> assertTrue(csv.contains("2,api2,now,,200"))
            );
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void testPushJsonWithWriteError() {
        File file = new File(JSON_FILE);
        file.mkdir();

        DataPusher pusher = new DataPusher();

        CommonModel<String> model = new CommonModel<>(
                UUID.randomUUID(),
                "now",
                "test-api",
                List.of("one")
        );

        assertDoesNotThrow(() -> pusher.pushJSON(model));

        assertTrue(file.exists());
        assertTrue(file.isDirectory());
    }

    @Test
    void testPushCsvWithEmptyRows() {
        DataPusher pusher = new DataPusher();

        pusher.pushCSV(List.of(), List.of("name", "score"));

        try {
            String csv = Files.readString(new File(CSV_FILE).toPath());

            assertTrue(csv.contains("id,source,timestamp,name,score"));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void deleteFileOrDir(File file) {
        if (!file.exists()) {
            return;
        }

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File child : files) {
                    deleteFileOrDir(child);
                }
            }
        }

        file.delete();
    }

    @Test
    void testHandleIOException() throws Exception {
        Files.createDirectory(Path.of(CSV_FILE));

        DataPusher dataPusher = new DataPusher();

        assertDoesNotThrow(() -> dataPusher.pushCSV(
                List.of(Map.of(
                        "id", "1",
                        "source", "test",
                        "timestamp", "123"
                )),
                List.of("id", "source", "timestamp")
        ));
    }
}