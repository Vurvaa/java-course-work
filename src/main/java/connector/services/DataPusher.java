package connector.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import models.service.notes.CommonModel;
import models.service.notes.CsvContent;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.*;

public class DataPusher {
    private static final String JSON_FILE = "output.json";
    private static final String CSV_FILE = "output.csv";

    private final ObjectMapper mapper = new ObjectMapper();
    private final ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());

    public synchronized void pushJSON(CommonModel<?> data) {
        File file = new File(JSON_FILE);
        List<CommonModel<?>> dataToWrite = new ArrayList<>();

        try {
            if (file.exists() && file.length() > 0) {
                List<CommonModel<?>> existingData =
                        mapper.readValue(file, new TypeReference<>() {});
                dataToWrite.addAll(existingData);
            }

            dataToWrite.add(data);

            writer.writeValue(file, dataToWrite);

        } catch (IOException e) {
            System.out.println("JSON error: I/O failure while saving data: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("JSON error: An unexpected system error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public synchronized void pushCSV(List<Map<String, String>> newRows, List<String> newHeaders) {
        File file = new File(CSV_FILE);

        List<Map<String, String>> allRows = new ArrayList<>();
        List<String> existingHeaders = new ArrayList<>();

        try {
            if (file.exists() && file.length() > 0) {
                CsvContent existingContent = readCsvContent();
                allRows.addAll(existingContent.rows());
                existingHeaders.addAll(existingContent.headers());
            }

            List<String> finalHeaders = buildHeaders(existingHeaders, newHeaders);

            allRows.addAll(newRows);

            writeCSV(allRows, finalHeaders);
        } catch (Exception e) {
            System.out.println("CSV error: failed to push data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void prepareFile(String outputFormat, boolean isNewFile) {
        if (!isNewFile)
            return;

        if ("JSON".equals(outputFormat))
            prepareJsonFile();
        else if ("CSV".equals(outputFormat))
            prepareCsvFile();
    }

    private List<String> buildHeaders(List<String> existingHeaders, List<String> newHeaders) {
        LinkedHashSet<String> result = new LinkedHashSet<>();

        result.add("id");
        result.add("source");
        result.add("timestamp");

        for (String header : existingHeaders) {
            if (!header.equals("id") && !header.equals("source") && !header.equals("timestamp"))
                result.add(header);
        }

        for (String header : newHeaders)
            if (!header.equals("id") && !header.equals("source") && !header.equals("timestamp")) {
                result.add(header);
        }

        return new ArrayList<>(result);
    }

    private CsvContent readCsvContent() {
        List<Map<String, String>> rows = new ArrayList<>();
        List<String> headers = new ArrayList<>();

        File file = new File(CSV_FILE);
        if (!file.exists() || file.length() == 0)
            return new CsvContent(headers, rows);

        try (Reader reader = new FileReader(file);
             CSVParser parser = CSVFormat.DEFAULT.builder()
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .build()
                     .parse(reader)) {

            headers.addAll(parser.getHeaderMap().keySet());

            for (CSVRecord record : parser)
                rows.add(new HashMap<>(record.toMap()));

        } catch (IOException e) {
            System.err.println("CSV error with reading: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("CSV unexpected error: " + e.getMessage());
            e.printStackTrace();
        }

        return new CsvContent(headers, rows);
    }

    private void writeCSV(List<Map<String, String>> allRows, List<String> headers) {
        try (FileWriter out = new FileWriter(CSV_FILE, false);
             CSVPrinter printer = new CSVPrinter(
                     out,
                     CSVFormat.DEFAULT.builder()
                             .setHeader(headers.toArray(new String[0]))
                             .build()
             )) {

            for (Map<String, String> row : allRows) {
                List<String> record = new ArrayList<>();

                for (String header : headers)
                    record.add(row.getOrDefault(header, ""));

                printer.printRecord(record);
            }
        } catch (IOException e) {
            System.err.println("CSV error with writing: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void prepareJsonFile() {
        File file = new File(JSON_FILE);

        try {
            writer.writeValue(file, new ArrayList<>());
        } catch (IOException e) {
            System.err.println("JSON error: can not prepare file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void prepareCsvFile() {
        File file = new File(CSV_FILE);

        try (FileWriter out = new FileWriter(file, false)) {
            out.write("");
        } catch (IOException e) {
            System.err.println("CSV error: can not prepare file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
