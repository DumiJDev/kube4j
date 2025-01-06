package io.github.dumijdev.kube4j.builder.controller.models;

import java.util.Optional;

public record NewBuildRequest(String appName, String gitUrl, Optional<String> gitBranch, Optional<String> lang,
                              Optional<String> mainFile) {
}
