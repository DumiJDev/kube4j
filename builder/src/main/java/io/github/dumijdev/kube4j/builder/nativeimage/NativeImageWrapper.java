package io.github.dumijdev.kube4j.builder.nativeimage;

import io.github.dumijdev.kube4j.builder.command.builder.CommandBuilder;
import io.github.dumijdev.kube4j.builder.command.builder.CommandBuilderFactory;
import io.github.dumijdev.kube4j.builder.command.builder.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static io.github.dumijdev.kube4j.builder.command.builder.Language.JAVA;

public abstract class NativeImageWrapper {
  private static final Logger LOG = LoggerFactory.getLogger(NativeImageWrapper.class);

  public static Optional<File> buildNativeImage(
      String language, String projectPath, String outputPath, String mainFile, String targetName
  ) throws IOException, InterruptedException {

    validateInputs(language, projectPath, outputPath, targetName);
    prepareOutputDirectory(outputPath);

    Language lang = Language.from(language);
    CommandBuilder commandBuilder = CommandBuilderFactory.createCommandBuilder(lang);
    File mainArtifactFile = new File(mainFile);

    if (lang == JAVA && !projectPath.endsWith(".jar")) {
      LOG.info("Compiling Java project at path: {}", projectPath);
      var depManager = dependenciesManager(new File(projectPath)).orElseThrow(() -> new RuntimeException("No dependencies manager found."));

      mainArtifactFile = switch (depManager) {
        case "maven" -> compileJavaWithMaven(projectPath);
        case "gradle" -> compileJavaWithGradle(projectPath);
        default -> throw new IllegalStateException("Unexpected value: " + depManager);
      };
    }

    List<String> command = commandBuilder.buildCommands(mainArtifactFile, targetName);

    ProcessExecutor.executeProcess(command);

    File outputFile = new File(outputPath, targetName);
    LOG.info("Native image generated successfully at: {}", outputFile.getAbsolutePath());

    return Optional.of(outputFile);
  }

  private static void validateInputs(String language, String projectPath, String outputPath, String targetName) {
    LOG.info("Validating inputs...");
    if (isNullOrEmpty(language) || isNullOrEmpty(projectPath) || isNullOrEmpty(outputPath) || isNullOrEmpty(targetName)) {
      throw new IllegalArgumentException("All parameters must be specified.");
    }
    if (!Files.exists(Paths.get(projectPath))) {
      throw new IllegalArgumentException("Invalid project path: " + projectPath);
    }
    LOG.info("Inputs validated successfully.");
  }

  private static void prepareOutputDirectory(String outputPath) throws IOException {
    File outputDir = new File(outputPath);
    if (!outputDir.exists()) {
      LOG.info("Creating output directory: {}", outputPath);
      Files.createDirectories(outputDir.toPath());
    }
  }

  private static File compileJavaWithMaven(String projectPath) throws IOException, InterruptedException {
    File projectDir = new File(projectPath);
    if (!projectDir.isDirectory()) {
      throw new IllegalArgumentException("Invalid Maven project directory: " + projectPath);
    }

    List<String> command = List.of(isWindows() ? "mvn.cmd" : "mvn", "clean", "install", "package", "-DskipTests");
    LOG.info("Executing Maven command: {}", String.join(" ", command));

    ProcessExecutor.executeProcess(command, projectDir);

    return findJarInTarget(projectDir, "target").orElseThrow(() -> new RuntimeException("No JAR file found in target directory."));
  }

  private static File compileJavaWithGradle(String projectPath) throws IOException, InterruptedException {
    File projectDir = new File(projectPath);
    if (!projectDir.isDirectory()) {
      throw new IllegalArgumentException("Invalid Gradle project directory: " + projectPath);
    }

    List<String> command = List.of(isWindows() ? "gradle.cmd" : "gradle");
    LOG.info("Executing Gradle command: {}", String.join(" ", command));

    ProcessExecutor.executeProcess(command, projectDir);

    return findJarInTarget(projectDir, "build").orElseThrow(() -> new RuntimeException("No JAR file found in build directory"));
  }

  private static boolean isWindows() {
    return System.getProperty("os.name").toLowerCase().contains("win");
  }

  private static Optional<File> findJarInTarget(File projectDir, String buildDir) {
    File targetDir = new File(projectDir, buildDir);
    File[] jarFiles = targetDir.listFiles((dir, name) -> name.endsWith(".jar"));

    if (jarFiles == null || jarFiles.length == 0) {
      return Optional.empty();
    }

    return Optional.of(jarFiles[0]);
  }

  private static Optional<String> dependenciesManager(File projectDir) {
    var depsFound = projectDir.listFiles((dir, name) -> List.of("pom.xml", "gradle.build").contains(name));

    if (depsFound == null) {
      return Optional.empty();
    }

    for (var dep : depsFound) {
      if (dep.isDirectory()) {
        continue;
      }

      if (dep.getName().equalsIgnoreCase("pom.xml")) return Optional.of("maven");
      else if (dep.getName().equalsIgnoreCase("gradle.build")) return Optional.of("gradle");
    }

    return Optional.empty();
  }

  private static boolean isNullOrEmpty(String str) {
    return str == null || str.isEmpty();
  }
}

