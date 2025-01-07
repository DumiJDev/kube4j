package io.github.dumijdev.kube4j.builder.command.builder;

import java.io.File;
import java.util.List;


// Concrete CommandBuilder for Python
public class PythonCommandBuilder implements CommandBuilder {
  @Override
  public List<String> buildCommands(File mainFile, String targetName) {
    if (mainFile == null) {
      throw new IllegalArgumentException("Main file must be specified for Python.");
    }
    return CommandBuilder.super.buildCommands(mainFile, targetName);
  }

  @Override
  public List<String> args() {
    return List.of("--language:python");
  }
}
