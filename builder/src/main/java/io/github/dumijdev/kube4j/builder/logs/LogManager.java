package io.github.dumijdev.kube4j.builder.logs;

import io.github.dumijdev.kube4j.builder.repository.LogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LogManager implements LogCollectionListener {
  private final Map<String, LogCollector> collectors = new ConcurrentHashMap<>();
  private final LogRepository logRepository;
  private final Logger logger = LoggerFactory.getLogger(LogManager.class);

  public LogManager(LogRepository logRepository) {
    this.logRepository = logRepository;
  }

  public LogCollector createCollector(String logId) {
    logger.info("Creating collector for logId: {}", logId);
    LogCollector collector = new LogCollector(this, logId);
    collectors.put(logId, collector);
    return collector;
  }

  public LogCollector getCollector(String logId) {
    logger.info("Getting collector for logId: {}", logId);
    return collectors.get(logId);
  }

  public void removeCollector(String logId) {
    logger.info("Removing collector for logId: {}", logId);
    try (var collector = collectors.remove(logId)) {
      logger.info("Collector removed: {}", logId);
    } catch (IOException ignored) {
    }
  }

  @Override
  public void onLogsCollected(String logId) {
    if (logRepository.existsById(logId)) return;
    logger.info("On logs collected for logId: {}", logId);
    var collector = getCollector(logId);

    logger.info("Collecting collector for logId: {}", logId);
    if (collector != null) {
      logger.info("Collecting collector for logId: {}", logId);
      logRepository.save(logId, String.join("\n", collector.getLogs()));

      logger.info("Removing collector on close for logId: {}", logId);
      removeCollector(logId);
    }
  }
}
