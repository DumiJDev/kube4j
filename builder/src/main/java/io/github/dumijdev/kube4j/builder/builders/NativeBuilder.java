package io.github.dumijdev.kube4j.builder.builders;

import io.github.dumijdev.kube4j.builder.constants.PathConstants;
import io.github.dumijdev.kube4j.builder.nativeimage.NativeImageWrapper;
import io.github.dumijdev.kube4j.builder.utils.GitUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class NativeBuilder implements BuilderListener {
  private final Logger log = LoggerFactory.getLogger(NativeBuilder.class);

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

      var nativeImagePath = NativeImageWrapper.buildNativeImage(
          message.lang().orElse("java").toLowerCase(),
          projectDir.getAbsolutePath(),
          PathConstants.NATIVE_BUILT_PATH,
          message.mainFile().orElse(null),
          message.appName()
      );

      nativeImagePath.ifPresentOrElse(
          this::printNativeImageBuiltWithSuccessful,
          this.printNativeImageBuiltWithFailed(projectDir)
      );

    } catch (IOException | InterruptedException e) {
      log.error("Failed to build native image", e);
    }
  }

  private void printNativeImageBuiltWithSuccessful(File file) {
    log.info("Native image built to: {}", file.getAbsolutePath());
  }

  private Runnable printNativeImageBuiltWithFailed(File file) {
    return () -> log.error("Occurred an error when trying to build native image from: {}", file.getAbsolutePath());
  }
}
