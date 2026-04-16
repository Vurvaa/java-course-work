package models.service.notes;

import java.util.List;
import java.util.Map;

public record CsvContent(List<String> headers, List<Map<String, String>> rows) {
}
