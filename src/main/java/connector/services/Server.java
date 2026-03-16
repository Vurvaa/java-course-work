package connector.services;

import connector.models.service.config.ConfigData;
import connector.models.service.config.NodeAPI;
import consoleUI.AppOptions;

public class Server {
    private final Connector connector;
    private final DataTransformer transformer;
    private final DataPusher pusher;
    private final AppOptions options;

    public Server(AppOptions options) {
        if (options == null)
            throw new NullPointerException("config or app options can not be null");

        this.connector = new Connector();
        this.transformer = new DataTransformer();
        this.pusher = new DataPusher();
        this.options = options;
    }

    public void start() {
        switch (options.outputFormat()) {
            case "JSON":
                handleJSON();
                break;

            case "CSV":
                handleCSV();
                break;

            default:
                throw new IllegalStateException("unknown output format");
        }
    }

    private void handleCSV() {

    }

    private void handleJSON() {
        try {
            var apis = options.apis();
            for (NodeAPI api : apis) {
                byte[] body = connector.getResponseBody(api.url());

                var data = transformer.transformInJSON(body, api.name());

                pusher.saveJSON(data);
            }

            pusher.writeJSON();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
