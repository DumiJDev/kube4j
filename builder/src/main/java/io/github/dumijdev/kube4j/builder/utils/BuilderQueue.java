package io.github.dumijdev.kube4j.builder.utils;

import io.github.dumijdev.kube4j.builder.controller.models.NewBuildRequest;
import io.github.dumijdev.kube4j.builder.utils.nativeimage.NativeImageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;

@Component
public class BuilderQueue {
  private final Queue<NewBuildRequest> QUEUE = new ConcurrentLinkedQueue<>();
  private final Map<NewBuildRequest, String> BUILDER_MAP = new ConcurrentHashMap<>();
  private final ExecutorService EXECUTOR = Executors.newFixedThreadPool(3);
  private final Logger logger = LoggerFactory.getLogger(BuilderQueue.class);

  public BuilderQueue() {
    CompletableFuture.runAsync(this::build, EXECUTOR);
  }

  public void add(NewBuildRequest newBuildRequest, String builderId) {
    logger.info("Adding new build request to queue: {}", newBuildRequest);
    QUEUE.add(newBuildRequest);
    BUILDER_MAP.put(newBuildRequest, builderId);
    logger.info("New build request has been added to queue: {}", newBuildRequest);
  }

  private void build() {
    while (true) {
      logger.debug("Pooling new build request");
      NewBuildRequest newBuildRequest = QUEUE.poll();
      if (newBuildRequest == null) {
        continue;
      }

      logger.info("New build will be started: {}", newBuildRequest);
        try {
          var builderId = BUILDER_MAP.get(newBuildRequest);
          var clonedRepo = GitUtils.cloneRepo(newBuildRequest.gitUrl(), newBuildRequest.gitBranch().orElse(null));
          if (clonedRepo.isPresent()) {
            var nativeImageBuilt = NativeImageUtils.buildNativeImage(newBuildRequest.lang().orElse("java"), clonedRepo.get().getAbsolutePath(), "/etc/kube4j/generated", null, newBuildRequest.appName());
            nativeImageBuilt.ifPresent(file -> logger.info("Native image was built to: {}", file));
          }
        } catch (IOException | InterruptedException e) {
          logger.error(e.getMessage(), e);
        };
    }
  }
}
