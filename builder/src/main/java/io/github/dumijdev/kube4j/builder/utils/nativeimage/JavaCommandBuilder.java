package io.github.dumijdev.kube4j.builder.utils.nativeimage;

import java.io.File;
import java.util.List;

import static io.github.dumijdev.kube4j.builder.utils.nativeimage.NativeImageUtils.BASE_PATH;

// Concrete CommandBuilder for Java
public class JavaCommandBuilder implements CommandBuilder {
  @Override
  public List<String> buildCommands(String inputFile, String mainFile, String targetName) {
    return List.of("native-image", "-jar", inputFile, "-o", new File(BASE_PATH, targetName).getAbsolutePath());
  }
}