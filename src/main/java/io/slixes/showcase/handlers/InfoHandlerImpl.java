package io.slixes.showcase.handlers;

import io.vertx.ext.web.RoutingContext;

public class InfoHandlerImpl implements InfoHandler<RoutingContext> {
  @Override
  public void handle(RoutingContext routingContext) {
    routingContext.response().end("ok");
  }
}
