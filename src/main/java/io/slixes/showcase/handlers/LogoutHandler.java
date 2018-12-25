package io.slixes.showcase.handlers;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public interface LogoutHandler extends Handler<RoutingContext> {

  static Handler<RoutingContext> create() {
    return routingContext -> {
      routingContext.clearUser();
      routingContext.session().destroy();
      routingContext.reroute("/");
    };
  }
}
