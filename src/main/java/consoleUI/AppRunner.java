package consoleUI;

import connector.models.service.config.ConfigData;

public abstract class AppRunner {
    protected final ConfigData config;

    public AppRunner(ConfigData config) {
        if (config == null)
            throw new NullPointerException("program config can not be null");

        this.config = config;
    }

    public abstract AppOptions start();
}
