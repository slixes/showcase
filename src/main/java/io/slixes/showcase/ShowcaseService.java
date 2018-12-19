package io.slixes.showcase;

import io.slixes.core.Slixes;
import io.slixes.showcase.handlers.InfoHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class ShowcaseService extends AbstractVerticle {

  @Override
  public void start(Future<Void> startFuture) {
    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());

    router.route("/ping").handler(routingContext -> routingContext.response().end("Alive!"));
    router.route("/info").handler(InfoHandler.create());

//    final JsonObject http = JsonObject.mapFrom(config().getJsonObject("http"));

//    HttpServerOptions httpServerOptions = new HttpServerOptions(http);

//    HttpServerOptions httpServerOptions = new HttpServerOptions().setPort(8443).setSsl(true)
//        .setKeyStoreOptions(new JksOptions().setPassword("testpassword").setPath("test.jks"));

//    vertx.createHttpServer(httpServerOptions).requestHandler(router).listen(ar -> {
//      System.out.println(null == ar);
//      if (ar.succeeded()) {
//        System.out.println("It's all good folks");
//        final HttpServer result = ar.result();
//        System.out.println(result.actualPort());
//      } else {
//        System.out.println("Something is fucked up");
//        ar.cause().printStackTrace();
//      }
//    });
//
    Slixes.boot(vertx, router, config(), ar -> {
      if (ar.succeeded()) {
        startFuture.complete();
      } else {
        System.out.println("Something got messed up");
        startFuture.fail(ar.cause());
        vertx.close();
      }
    });

  }

}
