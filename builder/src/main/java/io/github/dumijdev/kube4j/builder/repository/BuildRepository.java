package io.github.dumijdev.kube4j.builder.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.util.Optional;

public interface BuildRepository {
  Logger logger = LoggerFactory.getLogger(BuildRepository.class);

  Optional<Resource> findNativeImage(String imageName);

  void deleteNativeImage(String imageName);

  void saveNativeImage(String imageName, byte[] content);
}
