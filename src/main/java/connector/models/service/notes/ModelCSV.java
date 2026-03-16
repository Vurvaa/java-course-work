package connector.models.service.notes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ModelCSV<T> {
    private UUID uuid;
    private String source;
    private LocalDateTime timeStamp;
    private List<T> data;

    public ModelCSV(UUID id, LocalDateTime timeStamp, String source, List<T> data) {
        this.uuid = id;
        this.timeStamp = timeStamp;
        this.source = source;
        this.data = data;
    }
}
