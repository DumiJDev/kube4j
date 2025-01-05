package io.github.dumijdev.kube4j.builder.utils.nativeimage;

import java.util.List;

// Strategy interface for building commands
public interface CommandBuilder {
  List<String> buildCommands(String inputFile, String mainFile, String targetName);
}
