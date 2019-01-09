package io.slixes.showcase.handlers;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.oauth2.OAuth2Auth;
import io.vertx.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface LoginHandler extends Handler<RoutingContext> {

  Logger LOG = LogManager.getLogger(LoginHandler.class.getName());


  static Handler<RoutingContext> create(OAuth2Auth oauth2) {
    return routingContext -> {
      final JsonObject userJson = routingContext.getBodyAsJson();
      JsonObject someOtherObject = new JsonObject().put("a", "a").put("available", "never");

      userJson.put("meta", someOtherObject);

      LOG.info("--------------------------");
      LOG.info(userJson);
      LOG.info("--------------------------");

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
