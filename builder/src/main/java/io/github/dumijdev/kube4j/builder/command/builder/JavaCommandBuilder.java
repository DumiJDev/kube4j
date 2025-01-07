package io.github.dumijdev.kube4j.builder.command.builder;

import java.util.List;

// Concrete CommandBuilder for Java
public class JavaCommandBuilder implements CommandBuilder {

  @Override
  public List<String> args() {
    return List.of("--no-fallback", "--enable-sbom", "-jar");
  }
}
