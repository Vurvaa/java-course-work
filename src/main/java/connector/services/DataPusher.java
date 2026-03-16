package connector.services;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import connector.models.service.notes.ModelCSV;
import connector.models.service.notes.ModelJSON;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DataPusher {
    private List<ModelJSON<?>> models = new ArrayList<>();

    private final ObjectMapper mapper = new ObjectMapper();
    private final ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());

    public void writeJSON() {
        try {
            writer.writeValue(new File("output.json"), models);

            System.out.println("\n----Done----");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveJSON(ModelJSON<?> data) {
        models.add(data);
    }

    public <T> void writeCSV(ModelCSV<T> data) {

    }


}
