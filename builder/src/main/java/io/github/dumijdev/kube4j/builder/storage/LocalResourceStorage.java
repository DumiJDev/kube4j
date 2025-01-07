package io.github.dumijdev.kube4j.builder.storage;

import io.github.dumijdev.kube4j.builder.constants.PathConstants;
import io.github.dumijdev.kube4j.builder.utils.ValidationUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

@Component
public class LocalResourceStorage implements ResourceStorage {

  private static void validName(String name) {
    if (!ValidationUtils.isValidNativeName(name)) {
      throw new IllegalArgumentException("Invalid native name " + name);
    }
  }

  @Override
  public Optional<Resource> find(String name) {
    validName(name);

    var path = Paths.get(PathConstants.NATIVE_BUILT_PATH, name);
    if (!Files.exists(path)) {
      return Optional.empty();
    }

    if (!Files.isRegularFile(path)) {
      return Optional.empty();
    }

    if (!Files.isReadable(path)) {
      return Optional.empty();
    }

    var file = new FileSystemResource(path.toFile());

    return Optional.of(file);
  }

  @Override
  public void save(String name, byte[] content) throws IOException {
    validName(name);

    var file = new File(PathConstants.NATIVE_BUILT_PATH, name);
    try (FileOutputStream fos = new FileOutputStream(file)) {
      fos.write(content);
    }
  }

  @Override
  public void delete(String name) throws IOException {
    validName(name);

    Files.delete(Paths.get(PathConstants.NATIVE_BUILT_PATH, name));

  }
}
