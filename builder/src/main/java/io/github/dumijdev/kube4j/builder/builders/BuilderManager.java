package io.github.dumijdev.kube4j.builder.builders;

import io.github.dumijdev.kube4j.builder.controller.models.NewBuildRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static java.util.concurrent.CompletableFuture.runAsync;

public class BuilderManager {
  private static BuilderManager INSTANCE;
  private final Logger logger = LoggerFactory.getLogger(BuilderManager.class);
  private final List<BuilderListener> listeners = new LinkedList<>();
  private final ExecutorService executor = Executors.newCachedThreadPool();

  private BuilderManager() {
  }

  public static BuilderManager getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new BuilderManager();
    }

    return INSTANCE;
  }

  public BuilderManager addListener(BuilderListener listener) {
    logger.info("Adding builder listener {}", listener);
    listeners.add(listener);

    return this;
  }

  public void removeListener(BuilderListener listener) {
    logger.info("Removing builder listener {}", listener);
    listeners.remove(listener);
  }

  public void addBuild(String id, NewBuildRequest newBuildRequest) {
    Consumer<BuilderListener> alertListener = bl -> {
      runAsync(() -> bl.startBuild(new BuildMessage(id, newBuildRequest)), executor);
    };

    logger.info("Adding new build listener {}", id);

    listeners.forEach(alertListener);
  }
}
