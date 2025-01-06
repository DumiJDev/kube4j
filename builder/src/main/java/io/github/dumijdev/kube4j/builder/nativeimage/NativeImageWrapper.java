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
    String jarPath = projectPath;

    if (lang == JAVA && !projectPath.endsWith(".jar")) {
      LOG.info("Compiling Java project at path: {}", projectPath);
      jarPath = compileJavaWithMaven(projectPath);
    }

    List<String> command = commandBuilder.buildCommands(lang == JAVA ? jarPath : mainFile, targetName);

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

  private static String compileJavaWithMaven(String projectPath) throws IOException, InterruptedException {
    File projectDir = new File(projectPath);
    if (!projectDir.isDirectory()) {
      throw new IllegalArgumentException("Invalid Maven project directory: " + projectPath);
    }

    var isWindows = System.getProperty("os.name").toLowerCase().contains("win");
    List<String> command = List.of(isWindows ? "mvn.cmd" : "mvn", "clean", "install", "package", "-DskipTests");
    LOG.info("Executing Maven command: {}", String.join(" ", command));

    ProcessExecutor.executeProcess(command, projectDir);

    return findJarInTarget(projectDir).orElseThrow(() -> new RuntimeException("No JAR file found in target directory."));
  }

  private static Optional<String> findJarInTarget(File projectDir) {
    File targetDir = new File(projectDir, "target");
    File[] jarFiles = targetDir.listFiles((dir, name) -> name.endsWith(".jar"));

    if (jarFiles == null || jarFiles.length == 0) {
      return Optional.empty();
    }

    return Optional.of(jarFiles[0].getAbsolutePath());
  }

  private static boolean isNullOrEmpty(String str) {
    return str == null || str.isEmpty();
  }
}

