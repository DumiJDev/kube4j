package io.github.dumijdev.kube4j.builder.logs;

import java.util.Iterator;
import java.util.List;

public class LogListener {
  private final LogCollector logCollector;
  private int offset = 0;

  public LogListener(LogCollector logCollector) {
    this.logCollector = logCollector;
  }

  public Iterable<String> readLogs() {
    return () -> new Iterator<>() {
      private List<String> currentBatch = fetchNextBatch();

      @Override
      public boolean hasNext() {
        if (currentBatch.isEmpty() && !logCollector.isFinished()) {
          currentBatch = fetchNextBatch();
        }
        return !currentBatch.isEmpty();
      }

      @Override
      public String next() {
        if (currentBatch.isEmpty()) {
          throw new IllegalStateException("No more logs available.");
        }
        return currentBatch.removeFirst();
      }

      private List<String> fetchNextBatch() {
        List<String> logs = logCollector.getLogsFromOffset(offset);
        offset += logs.size();
        return logs;
      }
    };
  }
}
