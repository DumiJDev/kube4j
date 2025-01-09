package io.github.dumijdev.kube4j.builder.logs;

public interface LogCollectionListener {
  void onLogsCollected(String logId);
}
