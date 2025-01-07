package io.github.dumijdev.kube4j.builder.builders;

import io.github.dumijdev.kube4j.builder.controller.models.NewBuildRequest;

import java.util.Optional;

public record BuildMessage(String id, String appName, String gitUrl, Optional<String> gitBranch, Optional<String> lang,
                           Optional<String> mainFile, Optional<String> context) {

  public BuildMessage(String id, NewBuildRequest request) {
    this(id, request.appName(), request.gitUrl(), request.gitBranch(), request.lang(), request.mainFile(), request.context());
  }
}
