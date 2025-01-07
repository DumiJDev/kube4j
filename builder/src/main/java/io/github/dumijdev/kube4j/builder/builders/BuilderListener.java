package io.github.dumijdev.kube4j.builder.builders;

@FunctionalInterface
public interface BuilderListener {
  void startBuild(BuildMessage message);
}
