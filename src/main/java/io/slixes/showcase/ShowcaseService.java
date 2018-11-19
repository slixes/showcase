package io.slixes.showcase;

import io.slixes.showcase.handlers.InfoHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.oauth2.AccessToken;
import io.vertx.ext.auth.oauth2.OAuth2Auth;
import io.vertx.ext.auth.oauth2.OAuth2FlowType;
import io.vertx.ext.auth.oauth2.providers.KeycloakAuth;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class ShowcaseService extends AbstractVerticle {

  @Override
  public void start() {
    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());

    JsonObject keycloakJson = new JsonObject()
        .put("realm", "showcase")
        .put("realm-public-key",
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiMv3gDOm6vTlOK/X0YfwJc1e7yBBl7bmsuhBQYngCB262/RmJrIlWLN4c2OVG7BT6hh9OEPMmabVlawUvNgApRlkntMHK4/nzanjUZFwbxuXPaYXn691d8/Z/k7pV/lB/ERtrHOwdZ0p1l6awmbaj6iqSecFVOJFf0x5MAHb69s5I1yj4yimTu4enWpZXaiKTG4cJJV3tMj08AYBrd2Q7ZO3UF76NZiNDg/C7nWQB5QPQMAffs9Fn4KTH9wr5YYKODK2y1b4WMH6huOnViBeId5hJIyjzG5EqJFda5HSXK0l0rGbTG2zs6sPkm9wG3rhf46ICzHDRbif2fopQ9fVGwIDAQAB")
        .put("auth-server-url", "http://localhost:8080/auth")
        .put("ssl-required", "external")
        .put("resource", "frontend")
        .put("credentials", new JsonObject()
            .put("secret", "2e_ezE5lHze0x8jq2CBZVTlzP8Pr9FUgNz5yKifOO_s"));

    // Initialize the OAuth2 Library
    OAuth2Auth oauth2 = KeycloakAuth.create(vertx, OAuth2FlowType.PASSWORD, keycloakJson);

    // first get a token (authenticate)
    oauth2
        .authenticate(new JsonObject().put("username", "fmatar").put("password", "sm00th0perat0r"),
            res -> {
              if (res.failed()) {
                System.out.println(res.cause());
              } else {
                AccessToken token = (AccessToken) res.result();

                // now check for permissions
                token.isAuthorised("account:manage-account", r -> {
                  if (r.result()) {
                    // this user is authorized to manage its account
                  }
                });
              }
            });

    router.route("/ping")
        .handler(routingContext -> routingContext.response().end("Alive!"));
    router.route("/info").handler(InfoHandler.create());

    HttpServer server = vertx.createHttpServer();
    server.requestHandler(router::accept).listen(8090);
  }
}
