package io.github.dumijdev.kube4j.builder.repository;

import org.mapdb.DB;
import org.mapdb.serializer.SerializerString;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

@Repository
public class BuildRepositoryMapDB implements BuildRepository {
  private static final File resourcesPath = new File("/etc/kube4j/blobs");
  private final ResourceLoader resourceLoader = new DefaultResourceLoader();
  private final DB db;
  private final Map<String, String> fileMap;

  public BuildRepositoryMapDB(DB db) throws IOException {
    this.db = db;
    fileMap = db.hashMap("native-images", new SerializerString(), new SerializerString()).createOrOpen();
  }

  @Override
  public Optional<Resource> findNativeImage(String imageName) {
    var path = fileMap.get(resourcesPath.getAbsolutePath() + '/' + imageName);

    if (path == null) {
      return Optional.empty();
    } else if (!Files.isRegularFile(Path.of(path))) {
      return Optional.empty();
    }

    return Optional.of(resourceLoader.getResource(formatPath(path)));
  }

  @Override
  public void deleteNativeImage(String imageName) {
    fileMap.remove(imageName);
    db.commit();
  }

  @Override
  public void saveNativeImage(String imageName, byte[] content) {


  }


  private String formatPath(String resourceName) {

    if (resourceName == null) {
      throw new IllegalArgumentException("resource name cannot be null");
    }

    if (!resourceName.matches("[a-zA-Z0-9-_]+([a-zA-Z])?]")) {

    }

    return "%s/%s".formatted(resourcesPath.getAbsolutePath(), resourceName);

  }
}
