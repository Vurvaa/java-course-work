package connector.services;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class Connector {
    private final OkHttpClient client = new OkHttpClient();

    public byte[] getResponseBody(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().bytes();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
