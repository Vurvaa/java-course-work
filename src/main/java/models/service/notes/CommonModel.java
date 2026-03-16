package models.service.notes;

import java.util.List;
import java.util.UUID;

public class CommonModel<T> {
    private UUID uuid;
    private String source;
    private String timeStamp;
    private List<T> data;

    public CommonModel() {}

    public void setData(List<T> data) {
        this.data = data;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public CommonModel(UUID id, String timeStamp, String source, List<T> data) {
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
