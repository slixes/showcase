package io.slixes.showcase;

import io.slixes.core.Slixes;
import io.slixes.showcase.handlers.InfoHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.JksOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class ShowcaseService extends AbstractVerticle {

  @Override
  public void start(Future<Void> startFuture) {

    HttpServerOptions httpsOptions = new HttpServerOptions()
        .setPort(8443)
        .setSsl(true)
        .setKeyStoreOptions(new JksOptions().setPath("test.jks").setPassword("password"))
        .setLogActivity(true);

    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());

    router.route("/ping").handler(routingContext -> routingContext.response().end("Alive!"));
    router.route("/info").handler(InfoHandler.create());

    Slixes.boot(vertx, JsonObject.mapFrom(httpsOptions), ar -> {
      if (ar.succeeded()) {
        startFuture.complete();
      } else {
        startFuture.fail(ar.cause());
      }
    });

  }



}
