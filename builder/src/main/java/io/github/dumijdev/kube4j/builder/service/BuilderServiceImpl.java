package io.github.dumijdev.kube4j.builder.service;

import io.github.dumijdev.kube4j.builder.builders.BuilderManager;
import io.github.dumijdev.kube4j.builder.controller.models.BuildResult;
import io.github.dumijdev.kube4j.builder.controller.models.NewBuildRequest;
import io.github.dumijdev.kube4j.builder.logs.LogManager;
import io.github.dumijdev.kube4j.builder.logs.LogStreamer;
import io.github.dumijdev.kube4j.builder.storage.ResourceStorage;
import org.mapdb.DB;
import org.mapdb.serializer.SerializerString;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class BuilderServiceImpl implements BuilderService {
  private final DB db;
  private final Map<String, String> buildsMap;
  private final Map<String, String> logsMap;
  private final ResourceStorage storage;
  private final BuilderManager builderManager;
  private final LogManager logManager;

  public BuilderServiceImpl(DB db, ResourceStorage storage, BuilderManager builderManager, LogManager logManager) {
    this.db = db;
    this.storage = storage;
    this.builderManager = builderManager;
    this.logManager = logManager;
    buildsMap = this.db.hashMap("buildStatus", new SerializerString(), new SerializerString()).createOrOpen();
    logsMap = this.db.hashMap("log", new SerializerString(), new SerializerString()).createOrOpen();
  }

  @Override
  public BuildResult startBuild(NewBuildRequest buildRequest) {

    var buildId = UUID.randomUUID().toString();

    builderManager.addBuild(buildId, buildRequest);

    buildsMap.put(buildId, "building");
    db.commit();

    return new BuildResult(buildId, "building");
  }

  @Override
  public BuildResult buildStatus(String buildId) {
    return new BuildResult(buildId, buildsMap.getOrDefault(buildId, "unknown"));
  }

  @Override
  public Optional<Resource> getResource(String imageName) {
    if (imageName == null || imageName.isEmpty()) {
      return Optional.empty();
    }

    return storage.find(imageName);
  }

  @Override
  public void getBuildLogs(String buildId, LogStreamer logStreamer) {
    switch (buildsMap.get(buildId)) {
      case "building":
        var collector = logManager.getCollector(buildId);
        System.out.println(collector);

        if (collector == null) {
          System.out.println("Collector not found for build id: " + buildId);
          if (!buildsMap.get(buildId).equals("building")) {
            var logs = logsMap.getOrDefault(buildId, "");

            if ("".equals(logs)) {
              return;
            }

            System.out.println("[building - collector null] Reading logs from build id: " + buildId);
            for (var line : logs.split("\n")) {
              logStreamer.consumeStream(line);
            }
          }

          return;
        }

        System.out.println("Collector found for build id: " + buildId);
        var listener = collector.createListener();

        for (var line : listener.readLogs()) {
          logStreamer.consumeStream(line);
        }

        break;
      case "failed", "success":
        System.out.println("Logs saved");
        var logs = logsMap.getOrDefault(buildId, "");

        if ("".equals(logs)) {
          System.out.println("No logs in saved build id: " + buildId);
          return;
        }

        for (var line : logs.split("\n")) {
          logStreamer.consumeStream(line);
        }
        break;
      default:
        logStreamer.consumeStream("No logs found for build id: " + buildId);
        break;
    }
  }

}
