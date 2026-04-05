package consoleUI;

import models.service.config.ConfigData;
import models.service.config.NodeAPI;


import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class InteractiveRunner extends AppRunner {
    private final Scanner scanner = new Scanner(System.in);

    public InteractiveRunner(ConfigData config) {
        super(config);
    }

    @Override
    public AppOptions start() {
        System.out.println("- - - Game Data Aggregator - - -");

        List<NodeAPI> apis = selectAPI();
        String outputFormat = selectOutputFormat();
        boolean isNewFile = selectWriteFormat();
        String viewFormat = selectFileVieFormat(apis);

        return new AppOptions(apis, outputFormat, isNewFile, viewFormat);
    }

    private void showAvailableAPI() {
        System.out.println("Available APIs:");

        int count = 1;
        for (var api : config.apis())
            System.out.printf("  %d. %s%n", count++, api.name());

        System.out.println("  0. Finish selection");
    }

    private List<NodeAPI> selectAPI() {
        List<String> selectedApis = new ArrayList<>();
        List<String> availableApis = new ArrayList<>();
        for (var api : config.apis())
            availableApis.add(api.name());

        boolean running = true;

        while (running) {
            showAvailableAPI();
            String input = scanner.nextLine();

            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("enter a number from the list.");
                continue;
            }

            if (choice == 0) {
                if (selectedApis.isEmpty()) {
                    System.out.println("select at least one API.");
                    continue;
                }
                running = false;
                continue;
            }

            if (choice < 1 || choice > availableApis.size()) {
                System.out.println("enter a valid number from the list.");
                continue;
            }

            String selectedApi = availableApis.get(choice - 1);

            if (selectedApis.contains(selectedApi)) {
                System.out.println(selectedApi + " is already selected.");
                continue;
            }

            selectedApis.add(selectedApi);
            System.out.println(selectedApi + " added successfully.");
        }

        return filterAPI(selectedApis);
    }

    private List<NodeAPI> filterAPI(List<String> selectedNames) {
        List<NodeAPI> filtered = new ArrayList<>();

        for (NodeAPI api : config.apis()) {
            if (selectedNames.contains(api.name())) {
                filtered.add(api);
            }
        }
        return filtered;
    }

    private String selectOutputFormat() {
        System.out.println("Select output file format:");
        System.out.println("1. JSON");
        System.out.println("2. CSV");

        while (true) {
            String input = scanner.nextLine();
            switch (input) {
                case "1":
                    return "JSON";

                case "2":
                    return "CSV";

                default:
                    System.out.println("Invalid choice. Please enter 1 for JSON or 2 for CSV.");
            }
        }
    }

    private boolean selectWriteFormat() {
        System.out.println("Select file write mode:");
        System.out.println("1. Overwrite (create a new file)");
        System.out.println("2. Append (add to existing file)");

        while (true) {
            String input = scanner.nextLine();
            switch (input) {
                case "1":
                    return true;

                case "2":
                    return false;

                default:
                    System.out.println("Invalid choice. Please enter 1 for Overwrite or 2 for Append.");
            }
        }
    }

    private String selectFileVieFormat(List<NodeAPI> selectedApis) {
        System.out.println("Select data view mode:");
        System.out.println("1. Full (full file contents)");
        System.out.println("2. Filtered (specific API only)");

        while (true) {
            String input = scanner.nextLine();
            switch (input) {
                case "1":
                    return "full";
                case "2":
                    String apiName = selectViewAPI(selectedApis);
                    return "api: " + apiName;
                default:
                    System.out.println("Invalid choice. Please enter 1 or 2.");
            }
        }
    }

    private String selectViewAPI(List<NodeAPI> selectedApis) {
        while (true) {
            System.out.println("Select one of the previously chosen APIs:");
            for (int i = 0; i < selectedApis.size(); i++) {
                System.out.printf("  %d. %s%n", i + 1, selectedApis.get(i).name());
            }

            System.out.print("Enter number: ");
            String input = scanner.nextLine();
            try {
                int choice = Integer.parseInt(input);
                if (choice >= 1 && choice <= selectedApis.size()) {
                    String name = selectedApis.get(choice - 1).name();
                    System.out.println("Selected for view: " + name);
                    return name;
                }
                System.out.println("Invalid number. Please choose from the list above.");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}
