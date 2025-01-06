package io.github.dumijdev.kube4j.builder.command.builder;

import java.io.File;
import java.util.List;

import static io.github.dumijdev.kube4j.builder.constants.PathConstants.NATIVE_BUILT_PATH;

// Concrete CommandBuilder for Ruby
public class RubyCommandBuilder implements CommandBuilder {
  @Override
  public List<String> buildCommands(String mainFile, String targetName) {
    return List.of("native-image", "--language:ruby", mainFile, "-o", new File(NATIVE_BUILT_PATH, targetName).getAbsolutePath());
  }
}
