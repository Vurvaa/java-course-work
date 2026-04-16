package consoleUI;

import models.service.config.ConfigData;

import java.util.Scanner;


public class AutomaticRunner extends AppRunner {
    private static final boolean IS_NEW_FILE = true;
    private static final String VIEW_FORMAT = "empty";

    private final Scanner scanner = new Scanner(System.in);

    public AutomaticRunner(ConfigData config) {
        super(config);
    }

    @Override
    public String getCommand() {
        if (scanner.hasNextLine()) {
            String command = scanner.nextLine().trim();
            if (command.equals("start") || command.equals("\\q")) {
                System.out.printf("command[%s] accepted\n", command);
                return command;
            }
            else
                return "unknown";
        }

        return "\\q";
    }

    @Override
    public void printInfo() {
        System.out.println("Enter 'start' to begin polling or '\\q' to exit.");

    }

    @Override
    public AppOptions registrAppOptions() {
        System.out.println("Application started in automatic mode.");
        System.out.println("API endpoints to be used:");

        int count = 1;
        for (var api : config.apis())
            System.out.printf("  %d. %s%n", count++, api.name());

        System.out.printf("%nConfigured output format: %s%n", config.outputFormat());

        return new AppOptions(config.apis(),
                config.outputFormat(),
                IS_NEW_FILE,
                VIEW_FORMAT,
                config.maxTaskNum(),
                config.pollingInterval());
    }
}
