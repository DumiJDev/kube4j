package io.github.dumijdev.kube4j.builder.constants;

public class PathConstants {
  public static final String APP_BASE_PATH = System.getProperty("user.home") + "/.kube4j";
  public static final String NATIVE_BUILT_PATH = APP_BASE_PATH + "/build/blobs";
  public static final String CLONE_PATH = "/tmp/repo-";

  public static class Database {
    public static final String KUBE4J = APP_BASE_PATH + "/databases";
    public static final String BUILDS_PATH = KUBE4J + "/builds.db";
    public static final String LOGS_PATH = KUBE4J + "/logs.db";
    public static final String BUILDS_STATUS_PATH = KUBE4J + "/builds-status.db";
  }
}
