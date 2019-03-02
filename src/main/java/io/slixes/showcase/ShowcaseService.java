package io.slixes.showcase;

import io.slixes.core.Slixes;
import io.slixes.showcase.handlers.ChainedHandler;
import io.slixes.showcase.handlers.LogoutHandler;
import io.slixes.showcase.handlers.PingHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.LoggerFormat;
import io.vertx.ext.web.handler.LoggerHandler;

public class ShowcaseService extends AbstractVerticle {

  @Override
  public void start(Future<Void> startFuture) {

    try {



      final Router router = Router.router(vertx);

      Slixes.boot(router, ar -> {

        if (ar.succeeded()) {
//              final JsonObject keycloakJson = config.getJsonObject("auth")
//                  .getJsonObject("keycloak");
//              final OAuth2Auth oauth2 = KeycloakAuth
//                  .create(vertx, OAuth2FlowType.PASSWORD, keycloakJson);
          router.route().handler(LoggerHandler.create(true, LoggerFormat.SHORT));
          router.route().handler(BodyHandler.create());
          router.route("/ping").handler(PingHandler.create());
//              router.post("/login").handler(LoginHandler.create(oauth2));
          router.route("/logout").handler(LogoutHandler.create());

          router.route("/chain").handler(ChainedHandler.create());
          startFuture.complete();
        } else {
          System.err.println("Error booting service [{}]" + startFuture.cause().getMessage());
          startFuture.fail(ar.cause());
          vertx.close();
        }
      });
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  @Override
  public void stop(Future<Void> stopFuture) {
    System.out.println("Shut down hook invoked");
    stopFuture.tryComplete();
  }
}
