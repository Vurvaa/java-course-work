package consoleUI;

import models.service.config.ConfigData;
import models.service.config.NodeAPI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AutomaticRunnerTest {

    private final InputStream oldIn = System.in;

    @AfterEach
    void returnSystemIn() {
        System.setIn(oldIn);
    }

    @ParameterizedTest
    @ValueSource(strings = {"start", "\\q"})
    void testGoodCommands(String command) {
        System.setIn(new ByteArrayInputStream((command + "\n").getBytes()));

        ConfigData config = mock(ConfigData.class);
        AutomaticRunner runner = new AutomaticRunner(config);

        String result = runner.getCommand();

        assertEquals(command, result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"hello", "stop", "123", "", "START"})
    void testBadCommands(String command) {
        System.setIn(new ByteArrayInputStream((command + "\n").getBytes()));

        ConfigData config = mock(ConfigData.class);
        AutomaticRunner runner = new AutomaticRunner(config);

        String result = runner.getCommand();

        assertEquals("unknown", result);
    }

    @Test
    void testCommandWithSpaces() {
        System.setIn(new ByteArrayInputStream("   start   \n".getBytes()));

        ConfigData config = mock(ConfigData.class);
        AutomaticRunner runner = new AutomaticRunner(config);

        String result = runner.getCommand();

        assertEquals("start", result);
    }

    @Test
    void testNoInput() {
        System.setIn(new ByteArrayInputStream(new byte[0]));

        ConfigData config = mock(ConfigData.class);
        AutomaticRunner runner = new AutomaticRunner(config);

        String result = runner.getCommand();

        assertEquals("\\q", result);
    }

    @Test
    void testPrintInfo() {
        ConfigData config = mock(ConfigData.class);
        AutomaticRunner runner = new AutomaticRunner(config);

        assertDoesNotThrow(runner::printInfo);
    }

    @Test
    void testRegisterAppOptions() {
        ConfigData config = mock(ConfigData.class);

        NodeAPI api1 = mock(NodeAPI.class);
        NodeAPI api2 = mock(NodeAPI.class);

        when(api1.name()).thenReturn("api1");
        when(api2.name()).thenReturn("api2");

        when(config.apis()).thenReturn(List.of(api1, api2));
        when(config.outputFormat()).thenReturn("json");
        when(config.maxTaskNum()).thenReturn(5);
        when(config.pollingInterval()).thenReturn(10);

        AutomaticRunner runner = new AutomaticRunner(config);

        AppOptions options = runner.registrAppOptions();

        assertAll(
                () -> assertNotNull(options),
                () -> assertEquals(List.of(api1, api2), options.apis()),
                () -> assertEquals("json", options.outputFormat()),
                () -> assertTrue(options.isNewFile()),
                () -> assertEquals("empty", options.viewFormat()),
                () -> assertEquals(5, options.maxTaskNum()),
                () -> assertEquals(10, options.poolingInterval())
        );
    }
}