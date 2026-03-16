package connector.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import models.service.notes.CommonModel;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataPusher {
    private static final String JSON_FILE = "output.json";
    private static final String CSV_FILE = "output.csv";

    private final ObjectMapper mapper = new ObjectMapper();
    private final ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());

    private List<CommonModel<?>> models = new ArrayList<>();

    public void writeJSON(boolean isNewFile) {
        File file = new File(JSON_FILE);
        List<CommonModel<?>> dataToWrite = new ArrayList<>();

        try {
            if (!isNewFile && file.exists() && file.length() > 0) {
                List<CommonModel<?>> existingData = mapper.readValue(file, new TypeReference<>() {});
                dataToWrite.addAll(existingData);
            }

            dataToWrite.addAll(models);

            writer.writeValue(file, dataToWrite);

            models.clear();
        } catch (IOException e) {
            System.err.println("JSON error: I/O failure while saving data: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("JSON error: An unexpected system error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void saveJSON(CommonModel<?> data) {
        models.add(data);
    }

    public List<Map<String, String>> readCsvAsMaps() {
        List<Map<String, String>> data = new ArrayList<>();

        File file = new File(CSV_FILE);
        if (!file.exists() || file.length() == 0) return data;

        try (Reader reader = new FileReader(file, StandardCharsets.UTF_8);
             CSVParser parser = CSVFormat.DEFAULT.builder()
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .build()
                     .parse(reader)) {

            for (CSVRecord record : parser)
                data.add(new HashMap<>(record.toMap()));

        } catch (FileNotFoundException e) {
            System.out.println("CSV Error: The file was moved or deleted during process: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("CSV Error: Critical I/O failure while reading: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("CSV Error: An unexpected error occurred: " + e.getMessage());
        }

        return data;
    }

    public void writeCSV(List<Map<String, String>> allRows, List<String> headers) {
        try (FileWriter out = new FileWriter(CSV_FILE, false);
             CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.builder()
                     .setHeader(headers.toArray(new String[0]))
                     .build())) {

            for (Map<String, String> row : allRows) {
                List<Object> record = new ArrayList<>();
                for (String header : headers) {
                    record.add(row.getOrDefault(header, ""));
                }
                printer.printRecord(record);
            }
        } catch (IOException e) {
            System.err.println("error with writing CSV: " + e.getMessage());
        }
    }
}
