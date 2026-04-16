package connector.concurrency;

import models.service.config.NodeAPI;
import java.io.IOException;


public class ApiTask implements Runnable {
    private final ApiHandler handler;
    private final NodeAPI api;

    public ApiTask(ApiHandler handler, NodeAPI api) {
        this.handler = handler;
        this.api = api;
    }

    @Override
    public void run() {
        try {
            handler.handleApi(api);
        } catch (IOException e) {
            System.out.println("failed receiving a response from the api");
        } catch (Exception e) {
            System.out.println("api processing failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
