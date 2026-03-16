package models.custom;

import java.util.List;
import java.util.Map;

public interface Mappable {
    List<Map<String, String>> toCsvMaps();
}
