package io.slixes.showcase;

import io.slixes.showcase.handlers.InfoHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class ShowcaseService extends AbstractVerticle {

  @Override
  public void start() {
    Router router = Router.router(Vertx.vertx());
    router.route().handler(BodyHandler.create());

    router.route("/ping").handler(routingContext -> routingContext.response().end("Alive!"));
    router.route("/info").handler(InfoHandler.create());

    HttpServer server = vertx.createHttpServer();
    server.requestHandler(router::accept).listen(8080);
  }
}
