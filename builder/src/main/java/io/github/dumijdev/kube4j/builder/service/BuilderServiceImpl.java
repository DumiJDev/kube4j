package io.github.dumijdev.kube4j.builder.service;

import io.github.dumijdev.kube4j.builder.controller.models.BuildResult;
import io.github.dumijdev.kube4j.builder.controller.models.NewBuildRequest;
import io.github.dumijdev.kube4j.builder.storage.ResourceStorage;
import io.github.dumijdev.kube4j.builder.builders.BuilderManager;
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
  private final Map<String, String> buildStatus;
  private final ResourceStorage storage;
  private final BuilderManager builderManager;

  public BuilderServiceImpl(DB db, ResourceStorage storage, BuilderManager builderManager) {
    this.db = db;
    this.storage = storage;
    this.builderManager = builderManager;
    buildStatus = this.db.hashMap("buildStatus", new SerializerString(), new SerializerString()).createOrOpen();
  }

  @Override
  public BuildResult startBuild(NewBuildRequest buildRequest) {

    var buildId = UUID.randomUUID().toString();

    builderManager.addBuild(buildId, buildRequest);

    return new BuildResult(buildId, "building");
  }

  @Override
  public BuildResult buildStatus(String buildId) {
    return new BuildResult(buildId, buildStatus.getOrDefault(buildId, "unknown"));
  }

  @Override
  public Optional<Resource> getResource(String imageName) {
    if (imageName == null || imageName.isEmpty()) {
      return Optional.empty();
    }

    return storage.find(imageName);
  }
}
