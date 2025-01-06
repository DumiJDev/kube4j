package io.github.dumijdev.kube4j.builder.repository;

import org.mapdb.DB;
import org.mapdb.serializer.SerializerString;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class LogRepositoryMapDB implements LogRepository {
  private final DB db;
  private final Map<String, String> mapDB;

  public LogRepositoryMapDB(DB db) {
    this.db = db;
    mapDB = db.hashMap("builder", new SerializerString(), new SerializerString()).createOrOpen();
  }

  @Override
  public String getLog(String buildId) {
    return mapDB.getOrDefault(buildId, "No build found.");
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
    mapDB.put(buildId, message);
    db.commit();
  }
}
