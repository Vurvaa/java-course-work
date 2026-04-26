package connector.concurrency;

import models.service.config.NodeAPI;
import java.io.IOException;

public class ApiTask implements Runnable {
    private final Controller controller;
    private final String apiKey;
    private final NodeAPI api;
    private final int intervalSeconds;

    public ApiTask(Controller controller, NodeAPI api, String apiKey, int intervalSeconds) {
        this.controller = controller;
        this.api = api;
        this.apiKey = apiKey;
        this.intervalSeconds = intervalSeconds;
    }

    @Override
    public void run() {
        if (!controller.canHandleApi(apiKey, intervalSeconds))
            return;

        try {
            controller.handleApi(api);
        } catch (IOException e) {
            System.out.println("failed receiving a response from the api: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("api processing failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
