package consoleUI;

import models.service.config.ConfigData;


public class AutomaticRunner extends AppRunner {
    private static final boolean IS_NEW_FILE = true;
    private static final String VIEW_FORMAT = "empty";


    public AutomaticRunner(ConfigData config) {
        super(config);
    }

    @Override
    public AppOptions start() {
        System.out.println("Application started in automatic mode.");
        System.out.println("API endpoints to be used:");

        int count = 1;
        for (var api : config.apis())
            System.out.printf("  %d. %s%n", count++, api.name());

        System.out.printf("%nConfigured output format: %s%n", config.outputFormat());

        return new AppOptions(config.apis(), config.outputFormat(), IS_NEW_FILE, VIEW_FORMAT);
    }
}
