package io.github.dumijdev.kube4j.builder.controller.models;

import java.util.Optional;

import static java.util.Optional.ofNullable;

public record NewBuildRequest(String appName, String gitUrl, Optional<String> gitBranch, Optional<String> lang,
                              Optional<String> mainFile, Optional<String> context) {


  public static final class NewBuildRequestBuilder {
    private String appName;
    private String gitUrl;
    private String gitBranch;
    private String lang;
    private String mainFile;
    private String context;

    private NewBuildRequestBuilder() {
    }

    public static NewBuildRequestBuilder aNewBuildRequest() {
      return new NewBuildRequestBuilder();
    }

    public NewBuildRequestBuilder appName(String appName) {
      this.appName = appName;
      return this;
    }

    public NewBuildRequestBuilder gitUrl(String gitUrl) {
      this.gitUrl = gitUrl;
      return this;
    }

    public NewBuildRequestBuilder gitBranch(String gitBranch) {
      this.gitBranch = gitBranch;
      return this;
    }

    public NewBuildRequestBuilder lang(String lang) {
      this.lang = lang;
      return this;
    }

    public NewBuildRequestBuilder mainFile(String mainFile) {
      this.mainFile = mainFile;
      return this;
    }

    public NewBuildRequestBuilder context(String context) {
      this.context = context;
      return this;
    }

    public NewBuildRequest build() {
      return new NewBuildRequest(appName, gitUrl,
          ofNullable(gitBranch), ofNullable(lang), ofNullable(mainFile), ofNullable(context));
    }
  }
}
