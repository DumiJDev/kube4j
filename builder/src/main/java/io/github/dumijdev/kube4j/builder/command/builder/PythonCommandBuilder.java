package io.github.dumijdev.kube4j.builder.command.builder;

import java.io.File;
import java.util.List;

import static io.github.dumijdev.kube4j.builder.constants.PathConstants.NATIVE_BUILT_PATH;


// Concrete CommandBuilder for Python
public class PythonCommandBuilder implements CommandBuilder {
  @Override
  public List<String> buildCommands(String mainFile, String targetName) {
    if (mainFile == null || mainFile.isEmpty()) {
      throw new IllegalArgumentException("Main file must be specified for Python.");
    }
    return List.of("native-image", "--language:python", mainFile, "-o", new File(NATIVE_BUILT_PATH, targetName).getAbsolutePath());
  }
}
