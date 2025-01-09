package io.github.dumijdev.kube4j.builder.builders;

import io.github.dumijdev.kube4j.builder.constants.PathConstants;
import io.github.dumijdev.kube4j.builder.logs.LogManager;
import io.github.dumijdev.kube4j.builder.nativeimage.NativeImageWrapper;
import io.github.dumijdev.kube4j.builder.utils.GitUtils;
import org.mapdb.DB;
import org.mapdb.serializer.SerializerString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;

@Component
public class NativeBuilder implements BuilderListener {
  private final Logger log = LoggerFactory.getLogger(NativeBuilder.class);
  private final LogManager logManager;
  private final DB db;
  private final Map<String, String> buildStatus;

  public NativeBuilder(LogManager logManager, DB db) {
    this.logManager = logManager;
    this.db = db;
    this.buildStatus = this.db.hashMap("buildStatus", new SerializerString(), new SerializerString()).createOrOpen();
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

      var nativeImagePath = NativeImageWrapper.with(logManager.createCollector(message.id())).buildNativeImage(
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

    } catch (IOException | InterruptedException e) {
      log.error("Failed to build native image", e);
    }
  }

  private Consumer<File> printNativeImageBuiltWithSuccessful(String buildId) {
    return file -> {
      log.info("Native image built to: {}", file.getAbsolutePath());
      buildStatus.put(buildId, "success");
      db.commit();
    };
  }

  private Runnable printNativeImageBuiltWithFailed(File file, String buildId) {
    return () -> {
      log.error("Occurred an error when trying to build native image from: {}", file.getAbsolutePath());
      buildStatus.put(buildId, "failed");
      db.commit();
    };
  }
}
