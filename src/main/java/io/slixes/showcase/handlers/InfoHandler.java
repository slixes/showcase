package io.slixes.showcase.handlers;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public interface InfoHandler<R> extends Handler<RoutingContext> {

  static Handler<RoutingContext> create() {

    return new InfoHandlerImpl();
  }
}
