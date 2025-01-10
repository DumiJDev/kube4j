package io.github.dumijdev.kube4j.builder.service;

import io.github.dumijdev.kube4j.builder.builders.BuilderManager;
import io.github.dumijdev.kube4j.builder.controller.models.BuildResult;
import io.github.dumijdev.kube4j.builder.controller.models.NewBuildRequest;
import io.github.dumijdev.kube4j.builder.logs.LogManager;
import io.github.dumijdev.kube4j.builder.logs.LogStreamer;
import io.github.dumijdev.kube4j.builder.repository.BuildRepository;
import io.github.dumijdev.kube4j.builder.repository.BuildStatusRepository;
import io.github.dumijdev.kube4j.builder.repository.LogRepository;
import io.github.dumijdev.kube4j.builder.storage.ResourceStorage;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class BuilderServiceImpl implements BuilderService {
  private final LogRepository logRepository;
  private final BuildStatusRepository buildStatusRepository;
  private final ResourceStorage storage;
  private final BuilderManager builderManager;
  private final LogManager logManager;

  public BuilderServiceImpl(LogRepository logRepository, BuildStatusRepository buildStatusRepository, ResourceStorage storage, BuilderManager builderManager, LogManager logManager) {
    this.logRepository = logRepository;
    this.buildStatusRepository = buildStatusRepository;
    this.storage = storage;
    this.builderManager = builderManager;
    this.logManager = logManager;
  }

  @Override
  public BuildResult startBuild(NewBuildRequest buildRequest) {

    var buildId = UUID.randomUUID().toString();

    builderManager.addBuild(buildId, buildRequest);

    buildStatusRepository.save(buildId, BuildStatusRepository.BuildStatus.BUILDING);

    return new BuildResult(buildId, BuildStatusRepository.BuildStatus.BUILDING.name());
  }

  @Override
  public BuildResult buildStatus(String buildId) {
    return new BuildResult(buildId, buildStatusRepository.get(buildId).toString());
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
    var buildStatus = buildStatusRepository.get(buildId);
    if (buildStatus.isEmpty()) {
      return;
    }

    switch (buildStatus.get()) {
      case BUILDING:
        var collector = logManager.getCollector(buildId);
        System.out.println(collector);

        if (collector == null) {
          System.out.println("Collector not found for build id: " + buildId);
          if (buildStatus.get() != BuildStatusRepository.BuildStatus.BUILDING) {
            var logs = logRepository.getLog(buildId);

            if (logs.isEmpty()) {
              System.out.println("[Empty] No logs found for build id: " + buildId);
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
      case FAILURE, SUCCESS:
        System.out.println("Logs saved");
        var logs = logRepository.getLog(buildId);

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
