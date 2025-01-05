package io.github.dumijdev.kube4j.builder.utils;

import java.util.regex.Pattern;

public abstract class ValidationUtils {
  private static final Pattern nativePattern = Pattern.compile("^[a-z0-9]([a-z0-9._-]{0,253}[a-z0-9])?(:[a-z0-9._-]+)?$");

  private ValidationUtils() {}

  public static boolean isValidNativeName(String name) {
    return nativePattern.matcher(name).matches();
  }
}
