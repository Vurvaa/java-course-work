package models.service.config;

import java.util.List;

public record ConfigData(List<NodeAPI> apis,
                         String outputFormat) {
}
