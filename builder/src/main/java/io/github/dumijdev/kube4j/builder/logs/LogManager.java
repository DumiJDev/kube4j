package io.github.dumijdev.kube4j.builder.logs;

import io.github.dumijdev.kube4j.builder.repository.LogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LogManager implements LogCollectionListener {
  private final Map<String, LogCollector> collectors = new ConcurrentHashMap<>();
  private final LogRepository logRepository;
  private final Logger logger = LoggerFactory.getLogger(LogManager.class);

  public LogManager(LogRepository logRepository) {
    this.logRepository = logRepository;
  }

  // Cria um novo LogCollector e o registra pelo logId
  public LogCollector createCollector(String logId) {
    logger.info("Creating collector for logId: {}", logId);
    LogCollector collector = new LogCollector(this, logId);
    collectors.put(logId, collector);
    return collector;
  }

  // Recupera um LogCollector existente pelo logId
  public LogCollector getCollector(String logId) {
    logger.info("Getting collector for logId: {}", logId);
    return collectors.get(logId);
  }

  // Remove um LogCollector quando não é mais necessário
  public void removeCollector(String logId) {
    logger.info("Removing collector for logId: {}", logId);
    collectors.remove(logId);
  }

  @Override
  public void onLogsCollected(String logId) {
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
