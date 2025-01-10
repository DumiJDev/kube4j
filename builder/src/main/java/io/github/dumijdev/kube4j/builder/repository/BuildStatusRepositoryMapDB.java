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
import java.util.Optional;

@Repository
public class BuildStatusRepositoryMapDB implements BuildStatusRepository {
  private final DB db;
  private final Map<String, String> mapDB;

  public BuildStatusRepositoryMapDB() throws IOException {
    var dbFile = Paths.get(PathConstants.Database.BUILDS_STATUS_PATH);

    if (!Files.exists(dbFile)) {
      logger.info("Creating DB file {}", dbFile);
      Files.createDirectories(dbFile.getParent());
    }

    this.db = DBMaker.fileDB(dbFile.toFile()).checksumHeaderBypass().closeOnJvmShutdown().transactionEnable().make();
    mapDB = db.hashMap("build-status", new SerializerString(), new SerializerString()).createOrOpen();
  }

  @Override
  public void save(String buildId, BuildStatus buildStatus) {
    logger.info("Saving build status to db {}", buildId);
    mapDB.put(buildId, buildStatus.toString());
    db.commit();
  }

  @Override
  public Optional<BuildStatus> get(String buildId) {
    logger.info("Fetching build status from db {}", buildId);
    var status = mapDB.get(buildId);

    if (status == null) {
      return Optional.empty();
    }

    return Optional.of(BuildStatus.valueOf(status));
  }
}
