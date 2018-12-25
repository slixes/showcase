package io.slixes.showcase;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.slixes.core.Slixes;
import io.slixes.showcase.handlers.InfoHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.oauth2.OAuth2Auth;
import io.vertx.ext.auth.oauth2.OAuth2FlowType;
import io.vertx.ext.auth.oauth2.providers.KeycloakAuth;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class ShowcaseService extends AbstractVerticle {

  @Override
  public void start(Future<Void> startFuture) {
    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());

    router.route("/ping").handler(routingContext -> routingContext.response().end("Alive!"));
    router.route("/info").handler(InfoHandler.create());

    JsonObject keycloakJson = new JsonObject()
        .put("realm", "Slixes") // (1)
        .put("realm-public-key",
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAroOSdyHUkFVkdjZHOZCDB8hYpXDjJAP5vsLtthS+u+crSZULH8ZKFw0yQf9FtQ+W5RPIVXUzTyPj76VOoiBXUWQ7WzCaGTbVEN3GVRnEGCMt13lV8l6CKZGAu9j3arU4Fy47Z3bFkkS4yqaS9aaKUCkOiFdcYTVL6ybzjVp1RTp0YLPud//kUfVfGytJ2x4O5LOJMO2w5znORnrfA4ieauYFO/qYO+EZRM3fJ/ALm6dgAsfNmLef7AZTqS18ps7iUWyNkgli/zJgZfBX0Y+vHoRikINbxO1hJUjVCOg1zI0hDxy6o9+DaTqjhbNqheceTSOgdCZBvTBOpMEBQ7w6fQIDAQAB") // (2)
        .put("auth-server-url", "http://127.0.0.1:8080/auth")
        .put("ssl-required", "external")
        .put("resource", "service-account") // (3)
        .put("credentials",
            new JsonObject().put("secret", "8b0a962d-abc4-4417-8507-85d95f294844")); // (4)
    OAuth2Auth oauth2 = KeycloakAuth.create(vertx, OAuth2FlowType.PASSWORD, keycloakJson);

    router.post("/login").produces("application/json").handler(rc -> {
      System.err.println("received body ::: '" + rc.getBodyAsString() + "'");
      JsonObject userJson = rc.getBodyAsJson();
      System.err.println("User ::: " + userJson.encode());

      oauth2.authenticate(userJson, res -> {
        if (res.failed()) {
          System.err.println("Access token error: {} " + res.cause().getMessage());
          rc.response().setStatusCode(HttpResponseStatus.UNAUTHORIZED.code()).end();
        } else {
          final User user = res.result();
          System.out.println("Success: we have found user: " + user.principal().encodePrettily());
          rc.response().end(user.principal().encodePrettily());
        }
      });
    });

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
