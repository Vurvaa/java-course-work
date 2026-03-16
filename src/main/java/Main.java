import configReader.ConfigReader;
import models.service.config.ConfigData;
import connector.services.Server;
import consoleUI.AppOptions;
import consoleUI.AutomaticRunner;
import consoleUI.InteractiveRunner;
import consoleUI.AppRunner;
import org.apache.commons.cli.*;

public class Main {
    public static void main(String[] args) {
        try {
            String programFormat = parseProgramFormat(args);

            ConfigReader reader = new ConfigReader();
            ConfigData config = reader.readConfig();

            AppRunner appRunner = chooseProgramFormat(programFormat, config);
            AppOptions options = appRunner.start();

            Server server = new Server(options);
            server.start();
        } catch (ParseException e) {
            System.out.println("сommand line error: " + e.getMessage());
        } catch (IllegalStateException | NullPointerException | IllegalArgumentException e) {
            System.out.println("configuration error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("fatal system error: " + e.getMessage());
            e.printStackTrace();
        }

    }

    private static String parseProgramFormat(String[] args) throws ParseException {
        Options options = new Options();
        options.addOption(new Option("f", "format", true, "working format of the program"));

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        if (cmd.getArgs().length > 0)
            throw new ParseException("unknown parameters: " + String.join(", ", cmd.getArgs()));

        return cmd.getOptionValue("f", "automatic");
    }

    private static AppRunner chooseProgramFormat(String format, ConfigData config) {
        switch (format) {
            case "automatic":
                return new AutomaticRunner(config);

            case "interactive":
                return new InteractiveRunner(config);

            default:
                throw new IllegalStateException("unknown program format");
        }
    }
}
