package io.github.dumijdev.kube4j.builder.service;

import io.github.dumijdev.kube4j.builder.controller.models.BuildResult;
import io.github.dumijdev.kube4j.builder.controller.models.NewBuildRequest;
import org.springframework.core.io.Resource;

import java.util.Optional;

public interface BuilderService {
  BuildResult startBuild(NewBuildRequest buildRequest);
  BuildResult buildStatus(String buildId);
  Optional<Resource> getResource(String imageName);
}
