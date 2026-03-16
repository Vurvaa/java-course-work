package consoleUI;

import connector.models.service.config.NodeAPI;

import java.util.List;

public record AppOptions(List<NodeAPI> apis,
                         String outputFormat,
                         String writeFormat,
                         String viewFormat) {
}
