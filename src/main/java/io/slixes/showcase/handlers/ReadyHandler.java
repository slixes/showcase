package io.slixes.showcase.handlers;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public interface ReadyHandler extends Handler<RoutingContext> {

  static Handler<RoutingContext> create() {
    return routingContext -> routingContext.response().end("Ready");
  }
}
