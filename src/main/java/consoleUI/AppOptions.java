package consoleUI;

import models.service.config.NodeAPI;

import java.util.List;

public record AppOptions(List<NodeAPI> apis,
                         String outputFormat,
                         boolean isNewFile,
                         String viewFormat) {
}
