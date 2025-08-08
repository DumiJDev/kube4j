package io.github.dumijdev.kube4j.builder.logs;

@FunctionalInterface
public interface LogStreamer {
  void consumeStream(String consumer);
}
