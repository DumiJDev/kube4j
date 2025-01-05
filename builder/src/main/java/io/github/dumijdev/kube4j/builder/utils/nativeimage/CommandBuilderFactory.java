package io.github.dumijdev.kube4j.builder.utils.nativeimage;

// Factory to get CommandBuilder for a specific language
public class CommandBuilderFactory {
  public static CommandBuilder getCommandBuilder(Language language) {
    return switch (language) {
      case JAVA -> new JavaCommandBuilder();
      case PYTHON -> new PythonCommandBuilder();
      case RUBY -> new RubyCommandBuilder();
      default -> throw new IllegalArgumentException("Unsupported language: " + language);
    };
  }
}
