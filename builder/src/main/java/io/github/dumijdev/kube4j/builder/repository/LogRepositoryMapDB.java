package io.github.dumijdev.kube4j.builder.repository;

import io.github.dumijdev.kube4j.builder.constants.PathConstants;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.serializer.SerializerString;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

@Repository
public class LogRepositoryMapDB implements LogRepository {
  private final DB db;
  private final Map<String, String> mapDB;

  public LogRepositoryMapDB() throws IOException {
    var dbFile = Paths.get(PathConstants.Database.LOGS_PATH);

    if (!Files.exists(dbFile)) {
      logger.info("Creating DB file {}", dbFile);
      Files.createDirectories(dbFile.getParent());
    }

    this.db = DBMaker.fileDB(dbFile.toFile()).checksumHeaderBypass().closeOnJvmShutdown().transactionEnable().make();
    mapDB = db.hashMap("builder", new SerializerString(), new SerializerString()).createOrOpen();
  }

  @Override
  public String getLog(String buildId) {
    logger.info("get log for build {}", buildId);
    return mapDB.getOrDefault(buildId, "No logs found.");
  }

  @Override
  public void delete(String buildId) {
    mapDB.remove(buildId);
    db.commit();
  }

  @Override
  public void deleteAll() {
    mapDB.clear();
    db.commit();
  }

  @Override
  public void save(String buildId, String message) {
    logger.info("save log for build {}", buildId);
    mapDB.put(buildId, message);
    db.commit();
  }
}
