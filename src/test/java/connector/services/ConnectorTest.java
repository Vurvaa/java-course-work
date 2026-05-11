package connector.services;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ConnectException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConnectorTest {

    @Test
    void testConstructorWithNullClient() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Connector(null)
        );

        assertEquals("client can not be null", exception.getMessage());
    }

    @Test
    void testGetResponseBodyGood() throws Exception {
        OkHttpClient client = mock(OkHttpClient.class);
        Call call = mock(Call.class);
        Response response = mock(Response.class);
        ResponseBody responseBody = mock(ResponseBody.class);

        String url = "https://example.com/test";
        byte[] expected = "hello".getBytes();

        when(client.newCall(any(Request.class))).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.isSuccessful()).thenReturn(true);
        when(response.body()).thenReturn(responseBody);
        when(responseBody.bytes()).thenReturn(expected);

        Connector connector = new Connector(client);

        byte[] result = connector.getResponseBody(url);

        assertArrayEquals(expected, result);

        verify(client).newCall(any(Request.class));
        verify(call).execute();
        verify(response).close();
    }

    @Test
    void testGetResponseBodyBadStatus() throws Exception {
        OkHttpClient client = mock(OkHttpClient.class);
        Call call = mock(Call.class);
        Response response = mock(Response.class);

        String url = "https://example.com/test";

        when(client.newCall(any(Request.class))).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.isSuccessful()).thenReturn(false);
        when(response.code()).thenReturn(404);
        when(response.message()).thenReturn("Not Found");

        Connector connector = new Connector(client);

        IOException exception = assertThrows(
                IOException.class,
                () -> connector.getResponseBody(url)
        );

        assertAll(
                () -> assertTrue(exception.getMessage().contains("unexpected server response")),
                () -> assertTrue(exception.getMessage().contains("404")),
                () -> assertTrue(exception.getMessage().contains("Not Found"))
        );

        verify(response).close();
    }

    @Test
    void testGetResponseBodyEmptyBody() throws Exception {
        OkHttpClient client = mock(OkHttpClient.class);
        Call call = mock(Call.class);
        Response response = mock(Response.class);

        String url = "https://example.com/test";

        when(client.newCall(any(Request.class))).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.isSuccessful()).thenReturn(true);
        when(response.body()).thenReturn(null);

        Connector connector = new Connector(client);

        IOException exception = assertThrows(
                IOException.class,
                () -> connector.getResponseBody(url)
        );

        assertTrue(exception.getMessage().contains("response body is empty for URL"));

        verify(response).close();
    }

    @Test
    void testGetResponseBodyConnectionError() throws Exception {
        OkHttpClient client = mock(OkHttpClient.class);
        Call call = mock(Call.class);

        String url = "https://example.com/test";

        when(client.newCall(any(Request.class))).thenReturn(call);
        when(call.execute()).thenThrow(new ConnectException("connection refused"));

        Connector connector = new Connector(client);

        IOException exception = assertThrows(
                IOException.class,
                () -> connector.getResponseBody(url)
        );

        assertAll(
                () -> assertTrue(exception.getMessage().contains("could not connect to the server")),
                () -> assertTrue(exception.getMessage().contains(url)),
                () -> assertNotNull(exception.getCause()),
                () -> assertEquals(ConnectException.class, exception.getCause().getClass())
        );
    }

    @Test
    void testRequestUrl() throws Exception {
        OkHttpClient client = mock(OkHttpClient.class);
        Call call = mock(Call.class);
        Response response = mock(Response.class);
        ResponseBody responseBody = mock(ResponseBody.class);

        String url = "https://example.com/test";

        when(client.newCall(any(Request.class))).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.isSuccessful()).thenReturn(true);
        when(response.body()).thenReturn(responseBody);
        when(responseBody.bytes()).thenReturn("ok".getBytes());

        Connector connector = new Connector(client);

        connector.getResponseBody(url);

        verify(client).newCall(argThat(request ->
                request.url().toString().equals(url)
        ));
    }
}