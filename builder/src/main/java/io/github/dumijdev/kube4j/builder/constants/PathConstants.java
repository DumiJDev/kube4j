package io.github.dumijdev.kube4j.builder.constants;

public class PathConstants {
  public static final String APP_BASE_PATH = System.getProperty("user.home") + "/.kube4j";
  public static final String NATIVE_BUILT_PATH = APP_BASE_PATH + "/build/blobs";
  public static final String CLONE_PATH = "/tmp/repo-";
}
