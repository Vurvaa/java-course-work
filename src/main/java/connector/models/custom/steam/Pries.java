package connector.models.custom.steam;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Pries(String currency,
                    int initial,
                    int discount_percent) {
}
