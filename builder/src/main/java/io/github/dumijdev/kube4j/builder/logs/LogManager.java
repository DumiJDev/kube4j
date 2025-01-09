package io.github.dumijdev.kube4j.builder.logs;

import org.mapdb.DB;
import org.mapdb.serializer.SerializerString;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LogManager implements LogCollectionListener {
  private final Map<String, LogCollector> collectors = new ConcurrentHashMap<>();
  private final DB db;
  private final Map<String, String> logsMap;

  public LogManager(DB db) {
    this.db = db;
    this.logsMap = this.db.hashMap("logs", new SerializerString(), new SerializerString()).createOrOpen();
  }

  // Cria um novo LogCollector e o registra pelo logId
  public LogCollector createCollector(String logId) {
    LogCollector collector = new LogCollector(this, logId);
    collectors.put(logId, collector);
    return collector;
  }

  // Recupera um LogCollector existente pelo logId
  public LogCollector getCollector(String logId) {
    return collectors.get(logId);
  }

  // Remove um LogCollector quando não é mais necessário
  public void removeCollector(String logId) {
    collectors.remove(logId);
  }

  @Override
  public void onLogsCollected(String logId) {
    var collector = getCollector(logId);
    if (collector != null) {
      logsMap.put(logId, String.join("\n", collector.getLogs()));
      db.commit();

      removeCollector(logId);
    }
  }
}
