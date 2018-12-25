package io.slixes.showcase;

import io.slixes.core.Slixes;
import io.slixes.showcase.handlers.InfoHandler;
import io.slixes.showcase.handlers.LoginHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.oauth2.OAuth2Auth;
import io.vertx.ext.auth.oauth2.OAuth2FlowType;
import io.vertx.ext.auth.oauth2.providers.KeycloakAuth;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class ShowcaseService extends AbstractVerticle {

  @Override
  public void start(Future<Void> startFuture) {

    final JsonObject keycloakJson = config().getJsonObject("auth").getJsonObject("keycloak");
    final OAuth2Auth oauth2 = KeycloakAuth.create(vertx, OAuth2FlowType.PASSWORD, keycloakJson);

    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());

    router.route("/ping").handler(routingContext -> routingContext.response().end("Alive!"));
    router.route("/info").handler(InfoHandler.create());
    router.post("/login").handler(LoginHandler.create(oauth2));

    Slixes.boot(vertx, router, config(), ar -> {
      if (ar.succeeded()) {
        startFuture.complete();
      } else {
        System.err.println("Error booting service [{}]" + startFuture.cause().getMessage());
        startFuture.fail(ar.cause());
        vertx.close();
      }
    });
  }
}
