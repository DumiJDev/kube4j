package io.github.dumijdev.kube4j.builder.nativeimage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class ProcessExecutor {
  private static final Logger log = LoggerFactory.getLogger(ProcessExecutor.class);

  public static void executeProcess(List<String> command) throws IOException, InterruptedException {
    executeProcess(command, null);
  }

  public static void executeProcess(List<String> command, File workingDirectory) throws IOException, InterruptedException {
    ProcessBuilder processBuilder = new ProcessBuilder(command).redirectErrorStream(true);
    if (workingDirectory != null) {
      processBuilder.directory(workingDirectory);
    }

    Process process = processBuilder.start();
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
      String line;
      log.info("Process output:");
      while ((line = reader.readLine()) != null) {
        System.out.println(line);
      }
    } catch (IOException e) {
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
        String line;
        log.info("Process error:");
        while ((line = reader.readLine()) != null) {
          System.out.println(line);
        }
      }
    }

    int exitCode = process.waitFor();
    if (exitCode != 0) {
      throw new RuntimeException("Process failed with exit code: " + exitCode);
    }
  }
}
