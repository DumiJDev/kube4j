package io.github.dumijdev.kube4j.builder.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface LogRepository {
  Logger logger = LoggerFactory.getLogger(LogRepository.class);

  String getLog(String buildId);

  void delete(String buildId);

  void deleteAll();

  void save(String buildId, String message);

  boolean existsById(String logId);
}
