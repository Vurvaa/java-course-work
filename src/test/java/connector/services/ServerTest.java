package connector.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import connector.concurrency.Controller;
import connector.transformer.Transformer;
import consoleUI.AppOptions;
import models.custom.Mappable;
import models.service.config.NodeAPI;
import models.service.notes.CommonModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServerTest {
    private static final String JSON_FILE = "output.json";
    private static final String CSV_FILE = "output.csv";

    @AfterEach
    void deleteFiles() {
        new File(JSON_FILE).delete();
        new File(CSV_FILE).delete();
    }


    @Test
    void testStopWithEmptyViewDisplay() {
        AppOptions options = mock(AppOptions.class);
        Connector connector = mock(Connector.class);
        DataTransformer transformer = mock(DataTransformer.class);
        DataPusher pusher = mock(DataPusher.class);
        Controller controller = mock(Controller.class);

        when(controller.isRunning()).thenReturn(true);
        when(options.viewFormat()).thenReturn("empty");

        Server server = new Server(options, connector, transformer, pusher, controller);

        server.stop();

        verify(controller).stopPoll();
    }

    @Test
    void testStopWithJsonPreview() {
        AppOptions options = mock(AppOptions.class);
        Connector connector = mock(Connector.class);
        DataTransformer transformer = mock(DataTransformer.class);
        DataPusher pusher = mock(DataPusher.class);
        Controller controller = mock(Controller.class);

        when(controller.isRunning()).thenReturn(true);
        when(options.viewFormat()).thenReturn("full");
        when(options.outputFormat()).thenReturn("JSON");

        CommonModel<String> model = new CommonModel<>(
                UUID.randomUUID(),
                "now",
                "steam",
                List.of("data")
        );

        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(new File(JSON_FILE), List.of(model));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        Server server = new Server(options, connector, transformer, pusher, controller);

        assertDoesNotThrow(server::stop);

        verify(controller).stopPoll();
    }

    @Test
    void testStopWithCsvPreview() {
        AppOptions options = mock(AppOptions.class);
        Connector connector = mock(Connector.class);
        DataTransformer transformer = mock(DataTransformer.class);
        DataPusher pusher = mock(DataPusher.class);
        Controller controller = mock(Controller.class);

        when(controller.isRunning()).thenReturn(true);
        when(options.viewFormat()).thenReturn("full");
        when(options.outputFormat()).thenReturn("CSV");

        String csv = """
            id,source,timestamp,name,score
            1,steam,now,Test Game,100
            """;

        try {
            Files.writeString(new File(CSV_FILE).toPath(), csv);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        Server server = new Server(options, connector, transformer, pusher, controller);

        assertDoesNotThrow(server::stop);

        verify(controller).stopPoll();
    }

    @Test
    void testStopWithCsvPreviewForOneApi() {
        AppOptions options = mock(AppOptions.class);
        Connector connector = mock(Connector.class);
        DataTransformer transformer = mock(DataTransformer.class);
        DataPusher pusher = mock(DataPusher.class);
        Controller controller = mock(Controller.class);

        when(controller.isRunning()).thenReturn(true);
        when(options.viewFormat()).thenReturn("api: steam");
        when(options.outputFormat()).thenReturn("CSV");

        String csv = """
            id,source,timestamp,name,score
            1,steam,now,Steam Game,100
            2,shop,now,Shop Game,200
            """;

        try {
            Files.writeString(new File(CSV_FILE).toPath(), csv);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        Server server = new Server(options, connector, transformer, pusher, controller);

        assertDoesNotThrow(server::stop);

        verify(controller).stopPoll();
    }

    @Test
    void testStopWithCsvPreviewWithoutFile() {
        AppOptions options = mock(AppOptions.class);
        Connector connector = mock(Connector.class);
        DataTransformer transformer = mock(DataTransformer.class);
        DataPusher pusher = mock(DataPusher.class);
        Controller controller = mock(Controller.class);

        when(controller.isRunning()).thenReturn(true);
        when(options.viewFormat()).thenReturn("full");
        when(options.outputFormat()).thenReturn("CSV");

        new File(CSV_FILE).delete();

        Server server = new Server(options, connector, transformer, pusher, controller);

        assertDoesNotThrow(server::stop);

        verify(controller).stopPoll();
    }

    @Test
    void testConstructorWithNullOptions() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new Server(null)
        );

        assertEquals("app options can not be null", exception.getMessage());
    }

    @Test
    void testConstructorWithNullConnector() {
        AppOptions options = mock(AppOptions.class);
        DataTransformer transformer = mock(DataTransformer.class);
        DataPusher pusher = mock(DataPusher.class);
        Controller controller = mock(Controller.class);

        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new Server(options, null, transformer, pusher, controller)
        );

        assertEquals("connector can not be null", exception.getMessage());
    }

    @Test
    void testStartWhenControllerNotRunning() {
        AppOptions options = mock(AppOptions.class);
        Connector connector = mock(Connector.class);
        DataTransformer transformer = mock(DataTransformer.class);
        DataPusher pusher = mock(DataPusher.class);
        Controller controller = mock(Controller.class);

        NodeAPI api = mock(NodeAPI.class);

        when(controller.isRunning()).thenReturn(false);
        when(options.outputFormat()).thenReturn("JSON");
        when(options.isNewFile()).thenReturn(true);
        when(options.apis()).thenReturn(List.of(api));
        when(options.poolingInterval()).thenReturn(10);

        Server server = new Server(options, connector, transformer, pusher, controller);

        server.start();

        verify(pusher).prepareFile("JSON", true);
        verify(controller).startPoll(List.of(api), 10);
    }

    @Test
    void testTestConstructorWithNullOptions() {
        Connector connector = mock(Connector.class);
        DataTransformer transformer = mock(DataTransformer.class);
        DataPusher pusher = mock(DataPusher.class);
        Controller controller = mock(Controller.class);

        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new Server(null, connector, transformer, pusher, controller)
        );

        assertEquals("app options can not be null", exception.getMessage());
    }

    @Test
    void testConstructorWithNullTransformer() {
        AppOptions options = mock(AppOptions.class);
        Connector connector = mock(Connector.class);
        DataPusher pusher = mock(DataPusher.class);
        Controller controller = mock(Controller.class);

        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new Server(options, connector, null, pusher, controller)
        );

        assertEquals("transformer can not be null", exception.getMessage());
    }

    @Test
    void testConstructorWithNullPusher() {
        AppOptions options = mock(AppOptions.class);
        Connector connector = mock(Connector.class);
        DataTransformer transformer = mock(DataTransformer.class);
        Controller controller = mock(Controller.class);

        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new Server(options, connector, transformer, null, controller)
        );

        assertEquals("pusher can not be null", exception.getMessage());
    }

    @Test
    void testStartWhenControllerAlreadyRunning() {
        AppOptions options = mock(AppOptions.class);
        Connector connector = mock(Connector.class);
        DataTransformer transformer = mock(DataTransformer.class);
        DataPusher pusher = mock(DataPusher.class);
        Controller controller = mock(Controller.class);

        when(controller.isRunning()).thenReturn(true);

        Server server = new Server(options, connector, transformer, pusher, controller);

        server.start();

        verify(pusher, never()).prepareFile(anyString(), anyBoolean());
        verify(controller, never()).startPoll(anyList(), anyInt());
    }

    @Test
    void testStopWhenControllerRunning() {
        AppOptions options = mock(AppOptions.class);
        Connector connector = mock(Connector.class);
        DataTransformer transformer = mock(DataTransformer.class);
        DataPusher pusher = mock(DataPusher.class);
        Controller controller = mock(Controller.class);

        when(controller.isRunning()).thenReturn(true);
        when(options.viewFormat()).thenReturn("empty");

        Server server = new Server(options, connector, transformer, pusher, controller);

        server.stop();

        verify(controller).stopPoll();
    }

    @Test
    void testStopWhenControllerNotRunning() {
        AppOptions options = mock(AppOptions.class);
        Connector connector = mock(Connector.class);
        DataTransformer transformer = mock(DataTransformer.class);
        DataPusher pusher = mock(DataPusher.class);
        Controller controller = mock(Controller.class);

        when(controller.isRunning()).thenReturn(false);

        Server server = new Server(options, connector, transformer, pusher, controller);

        server.stop();

        verify(controller, never()).stopPoll();
    }

    @Test
    void testShutdown() {
        AppOptions options = mock(AppOptions.class);
        Connector connector = mock(Connector.class);
        DataTransformer transformer = mock(DataTransformer.class);
        DataPusher pusher = mock(DataPusher.class);
        Controller controller = mock(Controller.class);

        Server server = new Server(options, connector, transformer, pusher, controller);

        server.shutdown();

        verify(controller).shutdown();
    }

    @Test
    void testHandleApiJson() throws Exception {
        AppOptions options = mock(AppOptions.class);
        Connector connector = mock(Connector.class);
        DataTransformer transformer = mock(DataTransformer.class);
        DataPusher pusher = mock(DataPusher.class);
        Controller controller = mock(Controller.class);
        NodeAPI api = mock(NodeAPI.class);

        byte[] body = "body".getBytes();
        CommonModel<String> commonModel = new CommonModel<>(
                UUID.randomUUID(),
                "now",
                "steam",
                List.of("data")
        );

        when(api.url()).thenReturn("https://example.com");
        when(api.name()).thenReturn("steam");
        when(connector.getResponseBody("https://example.com")).thenReturn(body);
        doReturn(commonModel)
                .when(transformer)
                .transform(body, "steam");
        when(options.outputFormat()).thenReturn("JSON");

        Server server = new Server(options, connector, transformer, pusher, controller);

        server.handleApi(api);

        verify(connector).getResponseBody("https://example.com");
        verify(transformer).transform(body, "steam");
        verify(pusher).pushJSON(commonModel);
        verify(pusher, never()).pushCSV(anyList(), anyList());
    }

    @Test
    void testHandleApiCsv() {
        AppOptions options = mock(AppOptions.class);
        Connector connector = mock(Connector.class);
        DataTransformer transformer = mock(DataTransformer.class);
        DataPusher pusher = mock(DataPusher.class);
        Controller controller = mock(Controller.class);
        NodeAPI api = mock(NodeAPI.class);
        Transformer<?> specificTransformer = mock(Transformer.class);

        Mappable item = mock(Mappable.class);

        byte[] body = "body".getBytes();

        CommonModel<Mappable> commonModel = new CommonModel<>(
                UUID.randomUUID(),
                "now",
                "steam",
                List.of(item)
        );

        when(api.url()).thenReturn("https://example.com");
        when(api.name()).thenReturn("steam");

        try {
            when(connector.getResponseBody("https://example.com")).thenReturn(body);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        when(specificTransformer.getHeadersCSV()).thenReturn(List.of("name", "score"));

        doReturn(commonModel)
                .when(transformer)
                .transform(body, "steam");
        doReturn(specificTransformer)
                .when(transformer)
                .getTransformerForName("steam");


        when(item.toCsvMaps()).thenReturn(List.of(
                Map.of(
                        "name", "game",
                        "score", "100"
                )
        ));

        when(options.outputFormat()).thenReturn("CSV");

        Server server = new Server(options, connector, transformer, pusher, controller);

        try {
            server.handleApi(api);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        verify(pusher).pushCSV(
                argThat(rows ->
                        rows.size() == 1
                                && rows.get(0).get("name").equals("game")
                                && rows.get(0).get("score").equals("100")
                                && rows.get(0).get("source").equals("steam")
                                && rows.get(0).get("timestamp").equals("now")
                                && rows.get(0).containsKey("id")
                ),
                argThat(headers ->
                        headers.equals(List.of("id", "source", "timestamp", "name", "score"))
                )
        );

        verify(pusher, never()).pushJSON(any());
    }

    @Test
    void testHandleApiBadFormat() throws Exception {
        AppOptions options = mock(AppOptions.class);
        Connector connector = mock(Connector.class);
        DataTransformer transformer = mock(DataTransformer.class);
        DataPusher pusher = mock(DataPusher.class);
        Controller controller = mock(Controller.class);
        NodeAPI api = mock(NodeAPI.class);

        byte[] body = "body".getBytes();

        CommonModel<String> commonModel = new CommonModel<>(
                UUID.randomUUID(),
                "now",
                "steam",
                List.of("data")
        );

        when(api.url()).thenReturn("https://example.com");
        when(api.name()).thenReturn("steam");

        when(connector.getResponseBody("https://example.com")).thenReturn(body);
        doReturn(commonModel)
                .when(transformer)
                .transform(body, "steam");
        when(options.outputFormat()).thenReturn("TXT");

        Server server = new Server(options, connector, transformer, pusher, controller);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> server.handleApi(api)
        );

        assertEquals("unknown working format", exception.getMessage());

        verify(pusher, never()).pushJSON(any());
        verify(pusher, never()).pushCSV(anyList(), anyList());
    }
}