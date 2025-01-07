package io.github.dumijdev.kube4j.builder.command.builder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static io.github.dumijdev.kube4j.builder.constants.PathConstants.NATIVE_BUILT_PATH;

// Strategy interface for building commands
public interface CommandBuilder {
  default List<String> buildCommands(File mainFile, String targetName) {
    var sb = new StringBuilder();
    var args = args();
    var commands = new ArrayList<String>();
    commands.add("native-image");

    if (args != null && !args.isEmpty()) {
      commands.addAll(args);
    }

    commands.add(mainFile.getAbsolutePath());
    commands.add("-o");
    commands.add(new File(NATIVE_BUILT_PATH, targetName).getAbsolutePath());

    return commands;
  }

  List<String> args();
}
