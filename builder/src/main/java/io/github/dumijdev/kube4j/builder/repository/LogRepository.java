package io.github.dumijdev.kube4j.builder.repository;

public interface LogRepository {
  String getLog(String buildId);

  void delete(String buildId);

  void deleteAll();

  void save(String buildId, String message);
}
