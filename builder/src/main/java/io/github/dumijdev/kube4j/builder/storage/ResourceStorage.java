package io.github.dumijdev.kube4j.builder.storage;

import org.springframework.core.io.Resource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

public interface ResourceStorage {
  Optional<Resource> find(String key);
  void save(String name, byte[] content) throws IOException;
  void delete(String name) throws IOException;
}
