package connector.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import connector.concurrency.ApiHandler;
import connector.concurrency.Controller;
import models.custom.Mappable;
import models.service.config.NodeAPI;
import models.service.notes.CommonModel;
import consoleUI.AppOptions;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.*;

public class Server implements ApiHandler {
    private final AppOptions options;
    private final Connector connector;
    private final DataTransformer transformer;
    private final DataPusher pusher;
    private final Controller controller;

    public Server(AppOptions options) {
        if (options == null) throw new NullPointerException("app options can not be null");

        this.options = options;
        this.connector = new Connector();
        this.transformer = new DataTransformer();
        this.pusher = new DataPusher();
        this.controller = new Controller(options.maxTaskNum(), this);
    }

    public void start() {
        if (!controller.isRunning()) {
            pusher.prepareFile(options.outputFormat(), options.isNewFile());
            controller.startPoll(options.apis(), options.poolingInterval());
        } else
            System.out.println("Polling has already been started");
    }

    public void stop() {
        if (controller.isRunning()) {
            controller.stopPoll();
            viewDisplay();
        } else
            System.out.println("Polling has already been stopped");
    }

    public void shutdown() {
        controller.shutdown();
    }

    @Override
    public void handleApi(NodeAPI api) throws Exception {
        byte[] body = connector.getResponseBody(api.url());
        var commonModel = transformer.transform(body, api.name());

        if (options.outputFormat().equals("JSON"))
            pusher.pushJSON(commonModel);
        else if (options.outputFormat().equals("CSV"))
            handleAndPushCSV(commonModel, api.name());
        else
            throw new IllegalArgumentException("unknown working format");
    }

    private void handleAndPushCSV(CommonModel<?> commonModel, String apiName) {
        var specificTransformer = transformer.getTransformerForName(apiName);

        List<String> headers = new ArrayList<>(List.of("id", "source", "timestamp"));
        headers.addAll(specificTransformer.getHeadersCSV());

        List<Map<String, String>> currentApiRows = new ArrayList<>();

        for (Object item : commonModel.getData()) {
            if (item instanceof Mappable m) {
                for (Map<String, String> originalRowMap : m.toCsvMaps()) {
                    Map<String, String> rowMap = new HashMap<>(originalRowMap);
                    rowMap.put("id", commonModel.getUUID().toString());
                    rowMap.put("source", commonModel.getSource());
                    rowMap.put("timestamp", commonModel.getTimeStamp().toString());

                    currentApiRows.add(rowMap);
                }
            }
        }

        pusher.pushCSV(currentApiRows, headers);
    }

    private void viewDisplay() {
        String viewFormat = options.viewFormat();
        if (viewFormat == null || viewFormat.isEmpty() || viewFormat.equalsIgnoreCase("empty")) return;

        System.out.println("\n--- Data Preview ---\n");
        String target = viewFormat.equalsIgnoreCase("full") ? "full" : viewFormat.replace("api: ", "");

        if (options.outputFormat().equals("JSON"))
            printJSONPreview(target);
        else
            printCSVPreview(target);
    }

    private void printJSONPreview(String target) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            File file = new File("output.json");
            if (!file.exists()) return;

            List<CommonModel<?>> allData = mapper.readValue(file, new TypeReference<>() {
            });

            for (CommonModel<?> model : allData) {
                if (target.equals("full") || model.getSource().equalsIgnoreCase(target))
                    System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(model));
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private void printCSVPreview(String target) {
        File file = new File("output.csv");
        if (!file.exists() || file.length() == 0) {
            System.out.println("No data available to display.");
            return;
        }

        try (Reader reader = new FileReader(file); CSVParser parser = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(false).build().parse(reader)) {

            boolean headerPrinted = false;

            for (CSVRecord record : parser) {
                if (!headerPrinted) {
                    System.out.println(String.join(",", record.getParser().getHeaderNames()));
                    headerPrinted = true;
                }

                if (target.equalsIgnoreCase("full")) {
                    printRecord(record);
                } else {
                    String source = record.get("source");
                    if (source.equalsIgnoreCase(target)) printRecord(record);
                }
            }

        } catch (IOException e) {
            System.err.println("error reading CSV preview: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("CSV structure error: " + e.getMessage());
        }
    }

    private void printRecord(CSVRecord record) {
        List<String> values = new ArrayList<>();
        for (String val : record)
            values.add(val);

        System.out.println(String.join(",", values));
    }
}
