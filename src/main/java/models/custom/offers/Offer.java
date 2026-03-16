package models.custom.offers;

import models.custom.Mappable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record Offer(Info info,
                    Price cheapestPriceEver,
                    List<Deal> deals) implements Mappable {
    @Override
    public List<Map<String, String>> toCsvMaps() {
        List<Map<String, String>> rows = new ArrayList<>();

        if (deals == null || deals.isEmpty()) {
            rows.add(createShopRow(null));
        } else {
            for (Deal deal : deals) {
                rows.add(createShopRow(deal));
            }
        }
        return rows;
    }

    private Map<String, String> createShopRow(Deal deal) {
        Map<String, String> map = new HashMap<>();

        map.put("shop_title", info != null ? info.title() : "");
        map.put("shop_steam_id", info != null ? info.steamAppID() : "");

        map.put("shop_best_price_ever", cheapestPriceEver != null ? cheapestPriceEver.price() : "");

        if (deal != null) {
            map.put("shop_store_id", deal.storeID());
            map.put("shop_current_price", deal.price());
            map.put("shop_retail_price", deal.retailPrice());
            map.put("shop_savings", deal.savings() + "%");
        } else {
            map.put("shop_store_id", "");
            map.put("shop_current_price", "");
            map.put("shop_retail_price", "");
            map.put("shop_savings", "");
        }

        return map;
    }
}
