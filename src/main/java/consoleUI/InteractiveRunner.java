package consoleUI;

import connector.models.service.config.ConfigData;
import connector.models.service.config.NodeAPI;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class InteractiveRunner extends AppRunner {
    public InteractiveRunner(ConfigData config) {
        super(config);
    }

    @Override
    public AppOptions start() {
        System.out.println("- - - Game Data Aggregator - - -");

        List<NodeAPI> apis = selectAPI();
        String outputFormat = selectOutputFormat();
        String writeFormat = selectWriteFormat();
        String viewFormat = selectFileVieFormat();

        return new AppOptions(apis, outputFormat, writeFormat, viewFormat);
    }

    private void showAvailableAPI() {
        System.out.println("Available APIs:");

        int count = 1;
        for (var api : config.apis())
            System.out.printf("  %d. %s%n", count++, api.name());

        System.out.println("0. Finish selection");

        System.out.println("choose one or more options");
    }

    private List<NodeAPI> selectAPI() {
        List<String> selectedApis = new ArrayList<>();
        List<String> availableApis = new ArrayList<>();
        for (var api : config.apis())
            availableApis.add(api.name());

        boolean running = true;

        Scanner scanner = new Scanner(System.in);
        while (running) {
            showAvailableAPI();
            String input = scanner.nextLine();

            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number from the list.");
                continue;
            }

            if (choice == 0) {
                if (selectedApis.isEmpty()) {
                    System.out.println("Please select at least one API.");
                    continue;
                }
                running = false;
                continue;
            }

            if (choice < 1 || choice > availableApis.size()) {
                System.out.println("Please enter a valid number from the list.");
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

        scanner.close();

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
        System.out.println("Available output format:");
        System.out.println("1. JSON");
        System.out.println("2. CSV");

        String outputFormat = "";

        try (Scanner scanner = new Scanner(System.in)) {
          int choice = scanner.nextInt();
          switch (choice) {
              case 1:
                  outputFormat = "JSON";
                  break;

              case 2:
                  outputFormat = "CSV";
                  break;

              default:
                  throw new IllegalArgumentException("unexpected output format choice");
          }
        } catch (InputMismatchException e) {
            System.out.println(e.getMessage());
        }

        return outputFormat;
    }

    private String selectWriteFormat() {
        System.out.println("Available output format:");
        System.out.println("1. Create a new file");
        System.out.println("2. Append to existing file");

        String writeFormat = "";

        try (Scanner scanner = new Scanner(System.in)) {
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    writeFormat = "new";
                    break;

                case 2:
                    writeFormat = "append";
                    break;

                default:
                    throw new IllegalArgumentException("unexpected output format choice");
            }
        } catch (InputMismatchException e) {
            System.out.println(e.getMessage());
        }

        return writeFormat;
    }

    private String selectFileVieFormat() {
        System.out.println("Available output format:");
        System.out.println("1. Create a new file");
        System.out.println("2. Append to existing file");

        String viewFormat = "";

        try (Scanner scanner = new Scanner(System.in)) {
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    viewFormat = "full";
                    break;

                case 2:
                    viewFormat = "api: " + chooseAPI();
                    showAvailableAPI();


                default:
                    throw new IllegalArgumentException("unexpected output format choice");
            }
        } catch (InputMismatchException e) {
            System.out.println(e.getMessage());
        }

        return viewFormat;
    }

    private String chooseAPI() {
        var availableApis = config.apis();
        showAvailableAPI();

        System.out.print("Enter API number: ");
        Scanner scanner = new Scanner(System.in);

        try {
            int choice = Integer.parseInt(scanner.nextLine());

            if (choice < 1 || choice > availableApis.size()) {
                throw new IllegalArgumentException("index out of bounds: " + choice);
            }

            String selected = availableApis.get(choice - 1).url();
            System.out.println("Selected: " + selected);
            return selected;

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("input is not a valid number", e);
        }
    }

}
