import configReader.ConfigReader;
import connector.models.service.config.ConfigData;
import connector.services.Server;
import consoleUI.AppOptions;
import consoleUI.AutomaticRunner;
import consoleUI.InteractiveRunner;
import consoleUI.AppRunner;
import org.apache.commons.cli.*;

public class Main {
    public static void main(String[] args) {
        String programFormat = parseProgramFormat(args);

        ConfigReader reader = new ConfigReader();
        ConfigData config = reader.readConfig();

        AppRunner appRunner = chooseProgramFormat(programFormat, config);
        AppOptions options = appRunner.start();

        Server server = new Server(options);
        server.start();
    }

    private static String parseProgramFormat(String[] args) {
        String defaultFormat = "automatic";

        Option option = new Option("f", "format", true, "working format of the program");
        option.setArgs(1);

        Options options = new Options();
        options.addOption(option);

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args, false);
            if (cmd.hasOption("f"))
                defaultFormat = cmd.getOptionValue("f", defaultFormat);

            if (cmd.getArgs().length > 0)
                throw new ParseException("unknown parameters");

        } catch(ParseException e) {
            System.out.println(e.getMessage());
        }

        return defaultFormat;
    }

    private static AppRunner chooseProgramFormat(String format, ConfigData config) {
        switch (format) {
            case "automatic":
                return new AutomaticRunner(config);

            case "interactive":
                return new InteractiveRunner(config);

            default:
                throw new IllegalArgumentException("unknown program format");
        }
    }
}
