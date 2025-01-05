package io.github.dumijdev.kube4j.builder.config;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Configuration
public class MapDBConfig {
  private final Logger logger = LoggerFactory.getLogger(MapDBConfig.class);
  @Bean
  public DB createDB() throws IOException {
    var dbFile = Paths.get(System.getProperty("user.home"), ".kube4j", "repo.db");

    logger.info("Creating directories: {}", dbFile.toAbsolutePath());
    Files.createDirectories(dbFile.getParent());

    return DBMaker.fileDB(dbFile.toFile())
        .checksumHeaderBypass()
        .closeOnJvmShutdown()
        .transactionEnable()
        .make();
  }
}
