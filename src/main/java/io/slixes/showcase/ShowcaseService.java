package io.slixes.showcase;

import io.slixes.core.Slixes;
import io.slixes.showcase.handlers.ChainedHandler;
import io.slixes.showcase.handlers.LogoutHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.HealthChecks;
import io.vertx.ext.healthchecks.Status;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.LoggerFormat;
import io.vertx.ext.web.handler.LoggerHandler;

public class ShowcaseService extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) {

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
//              router.post("/login").handler(LoginHandler.create(oauth2));
          router.route("/logout").handler(LogoutHandler.create());
          router.route("/chain").handler(ChainedHandler.create());

          HealthCheckHandler healthCheckHandler = HealthCheckHandler.createWithHealthChecks(HealthChecks.create(vertx));

          healthCheckHandler.register("healthcheck", 2000, future -> {
            System.out.println("Health ok");
            future.complete(Status.OK());
          });
          router.route("/health").handler(healthCheckHandler);

          HealthCheckHandler readinessHandler = HealthCheckHandler.createWithHealthChecks(HealthChecks.create(vertx));
          readinessHandler.register("ready", 2000, future -> {
            System.out.println("Service is ready ");
            future.complete(Status.OK());
          });
          router.route("/ready").handler(readinessHandler);

          startPromise.complete();
        } else {
          System.err.println("Error booting service [{}]" + startPromise.future().cause().getMessage());
          startPromise.fail(ar.cause());
          vertx.close();
        }
      });
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  @Override
  public void stop(Promise<Void> stopPromise) {
    System.out.println("Shut down hook invoked");
    stopPromise.tryComplete();
  }
}
