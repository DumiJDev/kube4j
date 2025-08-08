package io.github.dumijdev.kube4j.builder.controller;

import com.google.protobuf.ByteString;
import io.github.dumijdev.kube4j.builder.controller.models.NewBuildRequest;
import io.github.dumijdev.kube4j.builder.logs.LogStreamer;
import io.github.dumijdev.kube4j.builder.service.*;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static java.util.Optional.ofNullable;

@Service
public class BuilderServiceGrpcImpl extends BuilderServiceGrpc.BuilderServiceImplBase {
  private final BuilderService service;
  private final Logger log = LoggerFactory.getLogger(getClass());

  public BuilderServiceGrpcImpl(BuilderService service) {
    this.service = service;
  }

  @Override
  public void startBuild(BuildRequest request, StreamObserver<BuildResponse> responseObserver) {
    log.info("Starting build");
    var result = service.startBuild(new NewBuildRequest(
            request.getAppName(),
            request.getGitUrl(),
            ofNullable(request.getGitBranch()),
            ofNullable(request.getLang()),
            ofNullable(request.getMainFile()),
            ofNullable(request.getContext())
        )
    );

    log.info("Build started");

    responseObserver.onNext(BuildResponse.newBuilder()
        .setId(result.id())
        .setStatus(result.status())
        .build()
    );
    responseObserver.onCompleted();

    log.info("Request responded");
  }

  @Override
  public void getBuildStatus(BuildStatusRequest request, StreamObserver<BuildResponse> responseObserver) {
    log.info("Get build status");
    var status = service.buildStatus(request.getBuildId());

    log.info("Build status: {}", status);

    responseObserver.onNext(BuildResponse.newBuilder()
        .setId(status.id())
        .setStatus(status.status())
        .build()
    );
    responseObserver.onCompleted();

    log.info("Request responded");
  }

  @Override
  public void getNativeImage(BuildResourceRequest request, StreamObserver<BuildResourceResponse> responseObserver) {
    log.info("Get native image");
    var resource = service.getResource(request.getImageName());
    log.info("Get native image: {}", resource);
    try {

      if (resource.isPresent()) {
        responseObserver.onNext(
            BuildResourceResponse.newBuilder()
                .setName(request.getImageName())
                .setContent(ByteString.copyFrom(resource.get().getContentAsByteArray()))
                .build()
        );
      } else {
        responseObserver.onError(new IllegalArgumentException("No resource found with name " + request.getImageName()));
      }
    } catch (IOException e) {
      responseObserver.onError(e);
    } finally {
      responseObserver.onCompleted();
      log.info("Native image sent");
    }
  }

  @Override
  public void getBuildLogs(BuildLogsRequest request, StreamObserver<BuildLogsResponse> responseObserver) {
    log.info("Get build logs");
    LogStreamer logStreamer = consumer -> responseObserver.onNext(
        BuildLogsResponse.newBuilder()
            .setLog(consumer)
            .build()
    );

    service.getBuildLogs(request.getBuildId(), logStreamer);

    responseObserver.onCompleted();
    log.info("Build logs responded");
  }
}
