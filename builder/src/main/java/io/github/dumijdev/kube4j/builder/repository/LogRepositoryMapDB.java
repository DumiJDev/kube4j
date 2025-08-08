package io.github.dumijdev.kube4j.builder.repository;

import io.github.dumijdev.kube4j.builder.constants.PathConstants;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.serializer.SerializerString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentMap;

@Repository
public class LogRepositoryMapDB implements LogRepository {

  private static final Logger logger = LoggerFactory.getLogger(LogRepositoryMapDB.class);
  private final DB db;
  private final ConcurrentMap<String, String> mapDB;

  public LogRepositoryMapDB() throws IOException {
    var dbFile = Paths.get(PathConstants.Database.LOGS_PATH);

    if (!Files.exists(dbFile)) {
      logger.info("Creating DB file {}", dbFile);
      Files.createDirectories(dbFile.getParent());
    }

    this.db = DBMaker
        .fileDB(dbFile.toFile())
        .checksumHeaderBypass()
        .closeOnJvmShutdown()
        .transactionEnable()
        .concurrencyScale(16) // Define o nível de concorrência
        .make();

    this.mapDB = db.hashMap("builder", new SerializerString(), new SerializerString())
        .createOrOpen();
  }

  @Override
  public String getLog(String buildId) {
    logger.info("get log for build {}", buildId);
    return mapDB.getOrDefault(buildId, "No logs found.");
  }

  @Override
  public void delete(String buildId) {
    logger.info("delete log for build {}", buildId);
    mapDB.remove(buildId);
    db.commit();
  }

  @Override
  public void deleteAll() {
    logger.info("delete all logs");
    mapDB.clear();
    db.commit();
  }

  @Override
  public void save(String buildId, String message) {
    logger.info("save log for build {}", buildId);
    mapDB.put(buildId, message);
    db.commit();
  }

  @Override
  public boolean existsById(String logId) {
    return mapDB.containsKey(logId);
  }
}
