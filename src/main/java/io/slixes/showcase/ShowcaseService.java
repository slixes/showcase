package io.slixes.showcase;

import io.slixes.core.Slixes;
import io.slixes.showcase.handlers.InfoHandler;
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
import java.util.concurrent.atomic.AtomicReference;

public class ShowcaseService extends AbstractVerticle {

  @Override
  public void start(Future<Void> startFuture) {

    ConfigRetriever retriever = ConfigRetriever.create(vertx);

    Router router = Router.router(vertx);
    AtomicReference<JsonObject> config = new AtomicReference<>();

    retriever.getConfig(configHandler -> {
      if (configHandler.succeeded()) {
        config.set(configHandler.result());
        Slixes.boot(vertx, router, config.get(), ar -> {
          if (ar.succeeded()) {
            final JsonObject keycloakJson = config.get().getJsonObject("auth")
                .getJsonObject("keycloak");
            final OAuth2Auth oauth2 = KeycloakAuth
                .create(vertx, OAuth2FlowType.PASSWORD, keycloakJson);

            router.route().handler(LoggerHandler.create());
            router.route().handler(BodyHandler.create());

            router.route("/ping")
                .handler(routingContext -> routingContext.response().end("Alive!"));
            router.route("/info").handler(InfoHandler.create());
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

    retriever.listen(configChangeHandler -> {
      final JsonObject newConfiguration = configChangeHandler.getNewConfiguration();

      System.out.println("I have changed");

    });




  }
}
