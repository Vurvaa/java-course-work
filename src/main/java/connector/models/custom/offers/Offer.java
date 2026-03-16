package connector.models.custom.offers;

import java.util.List;

public record Offer(Info info,
                    Price cheapestPriceEver,
                    List<Deal> deals) {
}
