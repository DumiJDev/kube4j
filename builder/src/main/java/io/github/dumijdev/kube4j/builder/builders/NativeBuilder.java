package io.github.dumijdev.kube4j.builder.builders;

import io.github.dumijdev.kube4j.builder.constants.PathConstants;
import io.github.dumijdev.kube4j.builder.logs.LogManager;
import io.github.dumijdev.kube4j.builder.nativeimage.NativeImageWrapper;
import io.github.dumijdev.kube4j.builder.repository.BuildStatusRepository;
import io.github.dumijdev.kube4j.builder.utils.GitUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

@Component
public class NativeBuilder implements BuilderListener {
  private final Logger log = LoggerFactory.getLogger(NativeBuilder.class);
  private final LogManager logManager;
  private final BuildStatusRepository buildStatusRepository;

  public NativeBuilder(LogManager logManager, BuildStatusRepository buildStatusRepository) {
    this.logManager = logManager;
    this.buildStatusRepository = buildStatusRepository;
  }

  @Override
  public void startBuild(BuildMessage message) {
    try {
      log.info("Starting build: {}", message.id());
      var repoPath = GitUtils.cloneRepo(message.gitUrl(), message.gitBranch().orElse(null));
      if (repoPath.isEmpty()) {
        log.warn("No git repository found for {}", message.gitUrl());
        return;
      }

      var projectDir = new File(repoPath.get(), message.context().orElse(""));

      try (var collector = logManager.createCollector(message.id())) {
        var nativeImagePath = NativeImageWrapper.with(collector).buildNativeImage(
            message.lang().orElse("java").toLowerCase(),
            projectDir.getAbsolutePath(),
            PathConstants.NATIVE_BUILT_PATH,
            message.mainFile().orElse(null),
            message.appName()
        );

        nativeImagePath.ifPresentOrElse(
            this.printNativeImageBuiltWithSuccessful(message.id()),
            this.printNativeImageBuiltWithFailed(projectDir, message.id())
        );
      }

    } catch (IOException | InterruptedException e) {
      log.error("Failed to build native image", e);
    }
  }

  private Consumer<File> printNativeImageBuiltWithSuccessful(String buildId) {
    return file -> {
      log.info("Native image built to: {}", file.getAbsolutePath());
      buildStatusRepository.save(buildId, BuildStatusRepository.BuildStatus.SUCCESS);
    };
  }

  private Runnable printNativeImageBuiltWithFailed(File file, String buildId) {
    return () -> {
      log.error("Occurred an error when trying to build native image from: {}", file.getAbsolutePath());
      buildStatusRepository.save(buildId, BuildStatusRepository.BuildStatus.FAILURE);
    };
  }
}
