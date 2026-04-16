package connector.concurrency;

import models.service.config.NodeAPI;

public interface ApiHandler {
    void handleApi(NodeAPI api) throws Exception;
}
