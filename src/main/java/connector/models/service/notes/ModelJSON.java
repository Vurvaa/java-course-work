package connector.models.service.notes;

import java.util.List;
import java.util.UUID;

public class ModelJSON<T> {
    private UUID uuid;
    private String source;
    private String timeStamp;
    private List<T> data;

    public ModelJSON(UUID id, String timeStamp, String source, List<T> data) {
        this.uuid = id;
        this.timeStamp = timeStamp;
        this.source = source;
        this.data = data;
    }

    public List<T> getData() {
        return data;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getSource() {
        return source;
    }

    public UUID getUUID() {
        return uuid;
    }
}
