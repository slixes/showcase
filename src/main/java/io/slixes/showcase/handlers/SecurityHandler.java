package io.slixes.showcase.handlers;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public interface SecurityHandler extends Handler<RoutingContext> {

  static SecurityHandler create() {
    return routingContext -> {
      if (routingContext.request().headers().contains("a")) {
        routingContext.response()
            .end(new JsonObject().put("error", "Invalid idempotent id").toString());

      }
      routingContext.next();
    };
  }

}
