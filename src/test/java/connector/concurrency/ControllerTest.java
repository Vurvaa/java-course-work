package connector.concurrency;

import models.service.config.NodeAPI;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ControllerTest {

    @ParameterizedTest
    @ValueSource(ints = {0, 17, -1, 100})
    void testConstructorBadThreadCount(int threads) {
        ApiHandler handler = mock(ApiHandler.class);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Controller(threads, handler)
        );

        assertEquals("invalid value for the number of threads", exception.getMessage());
    }

    @Test
    void testConstructorWithNullHandler() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new Controller(1, null)
        );

        assertEquals("handler can not be null", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 16})
    void testConstructorGoodThreadCount(int threads) {
        ApiHandler handler = mock(ApiHandler.class);

        Controller controller = new Controller(threads, handler);

        assertFalse(controller.isRunning());

        controller.shutdown();
    }

    @Test
    void testStartPollWithNullApiList() {
        ApiHandler handler = mock(ApiHandler.class);
        Controller controller = new Controller(1, handler);

        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> controller.startPoll(null, 10)
        );

        assertEquals("api list can not be null", exception.getMessage());

        controller.shutdown();
    }

    @Test
    void testStartPollWithEmptyApiList() {
        ApiHandler handler = mock(ApiHandler.class);
        Controller controller = new Controller(1, handler);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> controller.startPoll(List.of(), 10)
        );

        assertEquals("api list can not be empty", exception.getMessage());

        controller.shutdown();
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 86401})
    void testStartPollWithBadInterval(int interval) {
        ApiHandler handler = mock(ApiHandler.class);
        Controller controller = new Controller(1, handler);

        NodeAPI api = mock(NodeAPI.class);
        when(api.name()).thenReturn("steam");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> controller.startPoll(List.of(api), interval)
        );

        assertEquals("invalid time for the polling period", exception.getMessage());

        controller.shutdown();
    }

    @Test
    void testStartPollMakesControllerRunning() {
        ApiHandler handler = mock(ApiHandler.class);
        Controller controller = new Controller(1, handler);

        NodeAPI api = mock(NodeAPI.class);
        when(api.name()).thenReturn("steam");

        controller.startPoll(List.of(api), 10);

        assertTrue(controller.isRunning());

        controller.shutdown();
    }

    @Test
    void testStopPollWhenRunning() {
        ApiHandler handler = mock(ApiHandler.class);
        Controller controller = new Controller(1, handler);

        NodeAPI api = mock(NodeAPI.class);
        when(api.name()).thenReturn("steam");

        controller.startPoll(List.of(api), 10);
        controller.stopPoll();

        assertFalse(controller.isRunning());

        controller.shutdown();
    }

    @Test
    void testStopPollWhenNotRunning() {
        ApiHandler handler = mock(ApiHandler.class);
        Controller controller = new Controller(1, handler);

        assertDoesNotThrow(controller::stopPoll);
        assertFalse(controller.isRunning());

        controller.shutdown();
    }

    @Test
    void testStartPollWhenAlreadyRunning() {
        ApiHandler handler = mock(ApiHandler.class);
        Controller controller = new Controller(1, handler);

        NodeAPI api = mock(NodeAPI.class);
        when(api.name()).thenReturn("steam");

        controller.startPoll(List.of(api), 10);
        controller.startPoll(List.of(api), 10);

        assertTrue(controller.isRunning());

        controller.shutdown();
    }

    @Test
    void testHandlerCalled() throws Exception {
        ApiHandler handler = mock(ApiHandler.class);
        Controller controller = new Controller(1, handler);

        NodeAPI api = mock(NodeAPI.class);
        when(api.name()).thenReturn("steam");

        CountDownLatch latch = new CountDownLatch(1);

        doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(handler).handleApi(api);

        controller.startPoll(List.of(api), 10);

        boolean called = latch.await(1, TimeUnit.SECONDS);

        controller.shutdown();

        assertTrue(called);
        verify(handler, atLeastOnce()).handleApi(api);
    }

    @Test
    void testHandlerThrowsIOException() throws Exception {
        ApiHandler handler = mock(ApiHandler.class);
        Controller controller = new Controller(1, handler);

        NodeAPI api = mock(NodeAPI.class);
        when(api.name()).thenReturn("steam");

        CountDownLatch latch = new CountDownLatch(1);

        doAnswer(invocation -> {
            latch.countDown();
            throw new IOException("test error");
        }).when(handler).handleApi(api);

        controller.startPoll(List.of(api), 10);

        boolean called = latch.await(1, TimeUnit.SECONDS);

        controller.shutdown();

        assertTrue(called);
        verify(handler, atLeastOnce()).handleApi(api);
    }

    @Test
    void testHandlerThrowsRuntimeException() throws Exception {
        ApiHandler handler = mock(ApiHandler.class);
        Controller controller = new Controller(1, handler);

        NodeAPI api = mock(NodeAPI.class);
        when(api.name()).thenReturn("steam");

        CountDownLatch latch = new CountDownLatch(1);

        doAnswer(invocation -> {
            latch.countDown();
            throw new RuntimeException("bad processing");
        }).when(handler).handleApi(api);

        controller.startPoll(List.of(api), 10);

        boolean called = latch.await(1, TimeUnit.SECONDS);

        controller.shutdown();

        assertTrue(called);
        verify(handler, atLeastOnce()).handleApi(api);
    }

    @Test
    void testShutdown() {
        ApiHandler handler = mock(ApiHandler.class);
        Controller controller = new Controller(1, handler);

        assertDoesNotThrow(controller::shutdown);
        assertFalse(controller.isRunning());
    }
}