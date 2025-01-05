package io.github.dumijdev.kube4j.builder.utils;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

public abstract class GitUtils {
  private static final String CLONE_PATH = "/temp/repo-";
  private static final Logger LOG = LoggerFactory.getLogger(GitUtils.class);

  private GitUtils() {}


  public static Optional<File> cloneRepo(String gitUrl, String branch) {
    var path = new File(CLONE_PATH + UUID.randomUUID());
    LOG.info("Cloning repository at {}", path.getAbsolutePath());
    var cloneCommand = Git.cloneRepository()
        .setURI(gitUrl)
        .setDirectory(path);

    Optional.ofNullable(branch).ifPresent(cloneCommand::setBranch);

    try (var git = cloneCommand.call()) {
      LOG.info("Cloned repository at {}", path.getAbsolutePath());
      if (branch != null) {
        LOG.info("Switching to branch {}", branch);
        git.checkout().setName(branch).call();
        LOG.info("Switched to branch {}", branch);
      }

      return Optional.of(path);
    } catch (GitAPIException e) {
      LOG.error("Cloning failed", e);
      return Optional.empty();
    }
  }
}
