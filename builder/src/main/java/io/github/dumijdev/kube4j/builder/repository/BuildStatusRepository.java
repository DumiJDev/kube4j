package io.github.dumijdev.kube4j.builder.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public interface BuildStatusRepository {
  Logger logger = LoggerFactory.getLogger(BuildStatusRepository.class);

  void save(String buildId, BuildStatus buildStatus);

  Optional<BuildStatus> get(String buildId);

  enum BuildStatus {
    BUILDING, SUCCESS, FAILURE
  }
}
