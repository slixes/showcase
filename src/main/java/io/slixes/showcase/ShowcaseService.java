package io.slixes.showcase;

import io.slixes.core.Slixes;
import io.slixes.showcase.handlers.PingHandler;
import io.slixes.showcase.handlers.LoginHandler;
import io.slixes.showcase.handlers.LogoutHandler;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.oauth2.OAuth2Auth;
import io.vertx.ext.auth.oauth2.OAuth2FlowType;
import io.vertx.ext.auth.oauth2.providers.KeycloakAuth;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.LoggerHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ShowcaseService extends AbstractVerticle {

  @Override
  public void start(Future<Void> startFuture) {
    Logger logger = LogManager.getLogger();
    logger.debug("Debug message");

    logger.info("yo");
    final ConfigRetriever retriever = ConfigRetriever.create(vertx);
    final Router router = Router.router(vertx);
    retriever.getConfig(configHandler -> {
      if (configHandler.succeeded()) {
        final JsonObject config = configHandler.result();
        Slixes.boot(vertx, router, config, ar -> {
          if (ar.succeeded()) {
            final JsonObject keycloakJson = config.getJsonObject("auth").getJsonObject("keycloak");
            final OAuth2Auth oauth2 = KeycloakAuth
                .create(vertx, OAuth2FlowType.PASSWORD, keycloakJson);
            router.route().handler(LoggerHandler.create());
            router.route().handler(BodyHandler.create());
            router.route("/ping").handler(PingHandler.create());
            router.post("/login").handler(LoginHandler.create(oauth2));
            router.route("/logout").handler(LogoutHandler.create());
            startFuture.complete();
          } else {
            System.err.println("Error booting service [{}]" + startFuture.cause().getMessage());
            startFuture.fail(ar.cause());
            vertx.close();
          }
        });
      } else {
        startFuture.fail(configHandler.cause());
      }
    });
  }
}
