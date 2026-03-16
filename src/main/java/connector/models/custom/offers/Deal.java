package connector.models.custom.offers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Deal(String storeID,
                   String price,
                   String retailPrice,
                   String savings) {
}
