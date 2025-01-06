package io.github.dumijdev.kube4j.builder.command.builder;

// Enum for supported languages
public enum Language {
  JAVA, PYTHON, RUBY;

  public static Language from(String language) {
    try {
      return Language.valueOf(language.trim().toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Unsupported language: " + language);
    }
  }
}
