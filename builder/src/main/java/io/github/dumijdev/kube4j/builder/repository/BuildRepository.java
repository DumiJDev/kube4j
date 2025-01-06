package io.github.dumijdev.kube4j.builder.repository;

import org.springframework.core.io.Resource;

import java.util.Optional;

public interface BuildRepository {
  Optional<Resource> findNativeImage(String imageName);

  void deleteNativeImage(String imageName);

  void saveNativeImage(String imageName, byte[] content);
}
