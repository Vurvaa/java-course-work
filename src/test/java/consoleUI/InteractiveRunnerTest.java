package consoleUI;

import models.service.config.ConfigData;
import models.service.config.NodeAPI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InteractiveRunnerTest {
    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;

    @AfterEach
    void restoreSystemStreams() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    @ParameterizedTest
    @ValueSource(strings = {"start", "stop", "\\q"})
    void testGoodCommands(String command) {
        System.setIn(new ByteArrayInputStream((command + "\n").getBytes()));

        ConfigData config = mock(ConfigData.class);
        InteractiveRunner runner = new InteractiveRunner(config);

        String result = runner.getCommand();

        assertEquals(command, result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"hello", "exit", "START", "Stop", "", "123"})
    void testBadCommands(String command) {
        System.setIn(new ByteArrayInputStream((command + "\n").getBytes()));

        ConfigData config = mock(ConfigData.class);
        InteractiveRunner runner = new InteractiveRunner(config);

        String result = runner.getCommand();

        assertEquals("unknown", result);
    }

    @Test
    void testCommandWithSpaces() {
        System.setIn(new ByteArrayInputStream("   stop   \n".getBytes()));

        ConfigData config = mock(ConfigData.class);
        InteractiveRunner runner = new InteractiveRunner(config);

        String result = runner.getCommand();

        assertEquals("stop", result);
    }

    @Test
    void testNoInput() {
        System.setIn(new ByteArrayInputStream(new byte[0]));

        ConfigData config = mock(ConfigData.class);
        InteractiveRunner runner = new InteractiveRunner(config);

        String result = runner.getCommand();

        assertEquals("\\q", result);
    }

    @Test
    void testPrintInfo() {
        ConfigData config = mock(ConfigData.class);
        InteractiveRunner runner = new InteractiveRunner(config);

        assertDoesNotThrow(runner::printInfo);
    }

    @Test
    void testRegisterAppOptionsFullView() {
        String input = String.join("\n",
                "1",
                "2",
                "0",
                "1",
                "1",
                "1",
                "5",
                "10"
        ) + "\n";

        System.setIn(new ByteArrayInputStream(input.getBytes()));

        ConfigData config = mock(ConfigData.class);

        NodeAPI api1 = mock(NodeAPI.class);
        NodeAPI api2 = mock(NodeAPI.class);

        when(api1.name()).thenReturn("api1");
        when(api2.name()).thenReturn("api2");

        when(config.apis()).thenReturn(List.of(api1, api2));

        InteractiveRunner runner = new InteractiveRunner(config);

        AppOptions options = runner.registrAppOptions();

        assertAll(
                () -> assertNotNull(options),
                () -> assertEquals(List.of(api1, api2), options.apis()),
                () -> assertEquals("JSON", options.outputFormat()),
                () -> assertTrue(options.isNewFile()),
                () -> assertEquals("full", options.viewFormat()),
                () -> assertEquals(5, options.maxTaskNum()),
                () -> assertEquals(10, options.poolingInterval())
        );
    }

    @Test
    void testRegisterAppOptionsFilteredView() {
        String input = String.join("\n",
                "1",
                "0",
                "2",
                "2",
                "2",
                "1",
                "3",
                "20"
        ) + "\n";

        System.setIn(new ByteArrayInputStream(input.getBytes()));

        ConfigData config = mock(ConfigData.class);

        NodeAPI api1 = mock(NodeAPI.class);

        when(api1.name()).thenReturn("api1");
        when(config.apis()).thenReturn(List.of(api1));

        InteractiveRunner runner = new InteractiveRunner(config);

        AppOptions options = runner.registrAppOptions();

        assertAll(
                () -> assertNotNull(options),
                () -> assertEquals(List.of(api1), options.apis()),
                () -> assertEquals("CSV", options.outputFormat()),
                () -> assertFalse(options.isNewFile()),
                () -> assertEquals("api: api1", options.viewFormat()),
                () -> assertEquals(3, options.maxTaskNum()),
                () -> assertEquals(20, options.poolingInterval())
        );
    }

    @Test
    void testRegisterAppOptionsWithBadInputs() {
        String input = String.join("\n",
                "abc",
                "100",
                "1",
                "1",
                "0",
                "7",
                "1",
                "9",
                "1",
                "8",
                "1",
                "17",
                "4",
                "5",
                "10"
        ) + "\n";

        System.setIn(new ByteArrayInputStream(input.getBytes()));

        ConfigData config = mock(ConfigData.class);

        NodeAPI api1 = mock(NodeAPI.class);

        when(api1.name()).thenReturn("api1");
        when(config.apis()).thenReturn(List.of(api1));

        InteractiveRunner runner = new InteractiveRunner(config);

        AppOptions options = runner.registrAppOptions();

        assertAll(
                () -> assertNotNull(options),
                () -> assertEquals(List.of(api1), options.apis()),
                () -> assertEquals("JSON", options.outputFormat()),
                () -> assertTrue(options.isNewFile()),
                () -> assertEquals("full", options.viewFormat()),
                () -> assertEquals(4, options.maxTaskNum()),
                () -> assertEquals(10, options.poolingInterval())
        );
    }

    @Test
    void testPrintInvalidNumberForViewApi() {
        ConfigData config = mockConfigWithOneApi();
        String input = """
            1
            0
            1
            1
            2
            2
            1
            2
            10
            """;

        System.setIn(new ByteArrayInputStream(input.getBytes()));

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        InteractiveRunner runner = new InteractiveRunner(config);

        runner.registrAppOptions();

        assertTrue(output.toString().contains("Invalid number. Please choose from the list above."));
    }

    @Test
    void testPrintMessageWhenViewApiInputIsNotNumber() {
        ConfigData config = mockConfigWithOneApi();

        String input = """
            1
            0
            1
            1
            2
            abc
            1
            2
            10
            """;

        System.setIn(new ByteArrayInputStream(input.getBytes()));

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        InteractiveRunner runner = new InteractiveRunner(config);

        runner.registrAppOptions();

        assertTrue(output.toString().contains("Please enter a valid number."));
    }

    @Test
    void testPrintMessageWhenMaxTaskNumberOutOfRange() {
        ConfigData config = mockConfigWithOneApi();
        String input = """
            1
            0
            1
            1
            1
            20
            2
            10
            """;

        System.setIn(new ByteArrayInputStream(input.getBytes()));

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        InteractiveRunner runner = new InteractiveRunner(config);

        runner.registrAppOptions();

        assertTrue(output.toString().contains("Number should be in range 1 to 16"));
    }

    @Test
    void testPrintMessageWhenPollingIntervalOutOfRange() {
        ConfigData config = mockConfigWithOneApi();
        String input = """
            1
            0
            1
            1
            1
            2
            5
            10
            """;

        System.setIn(new ByteArrayInputStream(input.getBytes()));

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        InteractiveRunner runner = new InteractiveRunner(config);

        runner.registrAppOptions();

        assertTrue(output.toString().contains("Number should be in range 10 to 86400 seconds"));
    }

    @Test
    void testPrintMessageWhenApiInputIsNotNumber() {
        ConfigData config = mockConfigWithOneApi();
        String input = """
            abc
            1
            0
            1
            1
            1
            2
            10
            """;

        System.setIn(new ByteArrayInputStream(input.getBytes()));

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        InteractiveRunner runner = new InteractiveRunner(config);

        runner.registrAppOptions();

        assertTrue(output.toString().contains("enter a number from the list."));
    }

    @Test
    void testPrintMessageWhenApiNumberIsInvalid() {
        ConfigData config = mockConfigWithOneApi();
        String input = """
            99
            1
            0
            1
            1
            1
            2
            10
            """;

        System.setIn(new ByteArrayInputStream(input.getBytes()));

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        InteractiveRunner runner = new InteractiveRunner(config);

        runner.registrAppOptions();

        assertTrue(output.toString().contains("enter a valid number from the list."));
    }

    @Test
    void testPrintMessageWhenApiAlreadySelected() {
        ConfigData config = mockConfigWithOneApi();
        String input = """
            1
            1
            0
            1
            1
            1
            2
            10
            """;

        System.setIn(new ByteArrayInputStream(input.getBytes()));

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        InteractiveRunner runner = new InteractiveRunner(config);

        runner.registrAppOptions();

        assertTrue(output.toString().contains("is already selected."));
    }

    @Test
    void testPrintMessageWhenNoApiSelected() {
        ConfigData config = mockConfigWithOneApi();

        String input = """
            0
            1
            0
            1
            1
            1
            2
            10
            """;

        System.setIn(new ByteArrayInputStream(input.getBytes()));

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        InteractiveRunner runner = new InteractiveRunner(config);

        runner.registrAppOptions();

        assertTrue(output.toString().contains("select at least one API."));
    }

    private ConfigData mockConfigWithOneApi() {
        ConfigData config = mock(ConfigData.class);

        NodeAPI api = mock(NodeAPI.class);
        when(api.name()).thenReturn("testApi");
        when(api.url()).thenReturn("http://test.com");

        when(config.apis()).thenReturn(List.of(api));

        return config;
    }
}