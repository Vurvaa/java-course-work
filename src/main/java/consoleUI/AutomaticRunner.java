package consoleUI;

import connector.models.service.config.ConfigData;

import java.util.ArrayList;
import java.util.List;

public class AutomaticRunner extends AppRunner {
    private static final String WRITE_FORMAT = "new";
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

        return new AppOptions(config.apis(), config.outputFormat(), WRITE_FORMAT, VIEW_FORMAT);
    }


}
