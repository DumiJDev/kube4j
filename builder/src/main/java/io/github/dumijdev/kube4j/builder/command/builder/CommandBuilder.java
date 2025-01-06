package io.github.dumijdev.kube4j.builder.command.builder;

import java.util.List;

// Strategy interface for building commands
public interface CommandBuilder {
  List<String> buildCommands(String mainFile, String targetName);
}
