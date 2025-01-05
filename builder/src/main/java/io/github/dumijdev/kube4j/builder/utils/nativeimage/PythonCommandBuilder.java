package io.github.dumijdev.kube4j.builder.utils.nativeimage;

import java.io.File;
import java.util.List;

import static io.github.dumijdev.kube4j.builder.utils.nativeimage.NativeImageUtils.BASE_PATH;

// Concrete CommandBuilder for Python
public class PythonCommandBuilder implements CommandBuilder {
  @Override
  public List<String> buildCommands(String inputFile, String mainFile, String targetName) {
    if (mainFile == null || mainFile.isEmpty()) {
      throw new IllegalArgumentException("Main file must be specified for Python.");
    }
    return List.of("native-image", "--language:python", mainFile, "-o", new File(BASE_PATH, targetName).getAbsolutePath());
  }
}
