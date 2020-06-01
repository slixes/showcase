package io.slixes.auth.handlers;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public interface LogoutHandler extends Handler<RoutingContext> {
  void logout();
}
