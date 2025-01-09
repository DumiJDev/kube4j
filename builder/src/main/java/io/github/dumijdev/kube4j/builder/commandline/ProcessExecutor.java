package io.github.dumijdev.kube4j.builder.commandline;

import io.github.dumijdev.kube4j.builder.logs.LogCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class ProcessExecutor {
  private final Logger log = LoggerFactory.getLogger(ProcessExecutor.class);
  private final LogCollector logCollector;

  public ProcessExecutor(LogCollector logCollector) {
    assert logCollector != null : "logCollector cannot be null";
    this.logCollector = logCollector;
  }

  public static ProcessExecutor with(LogCollector logCollector) {
    return new ProcessExecutor(logCollector);
  }

  public void executeProcess(List<String> command) throws IOException, InterruptedException {
    executeProcess(command, null);
  }

  public void executeProcess(List<String> command, File workingDirectory) throws IOException, InterruptedException {
    ProcessBuilder processBuilder = new ProcessBuilder(command).redirectErrorStream(true);
    if (workingDirectory != null) {
      processBuilder.directory(workingDirectory);
    }

    Process process = processBuilder.start();
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
      String line;
      log.info("Process output:");
      while ((line = reader.readLine()) != null) {
        logCollector.collect(line);
        System.out.println(line);
      }
    } catch (IOException e) {
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
        String line;
        log.info("Process error:");
        while ((line = reader.readLine()) != null) {
          logCollector.collect(line);
          System.out.println(line);
        }
      }
    }

    int exitCode = process.waitFor();
    logCollector.finish();
    if (exitCode != 0) {
      throw new RuntimeException("Process failed with exit code: " + exitCode);
    }
  }
}
