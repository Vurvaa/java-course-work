package connector.transformer;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

public interface Transformer<T> {
    String getName();
    List<T> handleResponseBody(byte[] body, ObjectMapper mapper);
    List<String> getHeadersCSV();
}
