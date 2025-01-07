package io.github.dumijdev.kube4j.builder.command.builder;

import java.util.List;

// Concrete CommandBuilder for Ruby
public class RubyCommandBuilder implements CommandBuilder {

  @Override
  public List<String> args() {
    return List.of("--language:ruby");
  }
}
