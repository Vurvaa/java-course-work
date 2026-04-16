package consoleUI;

import models.service.config.ConfigData;

public abstract class AppRunner {
    protected final ConfigData config;

    public AppRunner(ConfigData config) {
        if (config == null)
            throw new NullPointerException("program config can not be null");

        this.config = config;
    }

    public abstract String getCommand();

    public abstract AppOptions registrAppOptions();

    public abstract void printInfo();
}
