package io.github.dumijdev.kube4j.builder.config;

import io.github.dumijdev.kube4j.builder.builders.BuilderListener;
import io.github.dumijdev.kube4j.builder.builders.BuilderManager;
import io.github.dumijdev.kube4j.builder.logs.LogManager;
import io.github.dumijdev.kube4j.builder.repository.LogRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeansConfig {
  @Bean
  public BuilderManager builderManager(BuilderListener builderListener) {
    return BuilderManager.getInstance().addListener(builderListener);
  }

  @Bean
  public LogManager logManager(LogRepository logRepository) {
    return new LogManager(logRepository);
  }
}
