package io.slixes.showcase.handlers;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public interface PingHandler extends Handler<RoutingContext> {

  static Handler<RoutingContext> create() {
    return routingContext -> routingContext.response().end("Alive");
  }
}
