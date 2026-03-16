package connector.services;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.ConnectException;
import java.util.concurrent.TimeUnit;

public class Connector {
    private final OkHttpClient client;

    public Connector() {
        client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    public byte[] getResponseBody(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful())
                throw new IOException("unexpected server response: " + response.code() + " " + response.message());

            if (response.body() == null)
                throw new IOException("response body is empty for URL: " + url);

            return response.body().bytes();

        } catch (ConnectException e) {
            throw new IOException("could not connect to the server: " + url, e);
        }
    }
}
