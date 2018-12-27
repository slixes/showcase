package io.slixes.showcase.handlers;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.oauth2.OAuth2Auth;
import io.vertx.ext.web.RoutingContext;

public interface LoginHandler extends Handler<RoutingContext> {

  static Handler<RoutingContext> create(OAuth2Auth oauth2) {
    return routingContext -> {
      final JsonObject userJson = routingContext.getBodyAsJson();
      oauth2.authenticate(userJson, res -> {
        if (res.failed()) {
          routingContext.response().setStatusCode(HttpResponseStatus.UNAUTHORIZED.code()).end();
        } else {
          final User user = res.result();
          routingContext.response().end(user.principal().encodePrettily());
        }
      });
    };
  }
}
