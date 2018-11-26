package io.slixes.showcase;

import io.slixes.showcase.handlers.InfoHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class  ShowcaseService extends AbstractVerticle {

  @Override
  public void start(Future<Void> startFuture) {
    if (config().isEmpty()) {
      vertx.close();
    } else {
      Router router = Router.router(vertx);
      router.route().handler(BodyHandler.create());

      router.route("/ping").handler(routingContext -> routingContext.response().end("Alive!"));
      router.route("/info").handler(InfoHandler.create());

      HttpServer server = vertx.createHttpServer();

      server.requestHandler(router::accept).listen(8080, ar -> {
        if (ar.succeeded()) {
          startFuture.complete();
        }
      });
    }
  }
}
