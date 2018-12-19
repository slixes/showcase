package io.slixes.core;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public interface Slixes {

  static void boot(Vertx vertx, Router router, JsonObject config) throws SlixesException {
    final Map<String, HttpServer> serversMap = HttpServerCreator.create(vertx, config);
    serversMap
        .entrySet().forEach(entry -> entry.getValue().requestHandler(router)
        .listen(ar -> {
          if (ar.succeeded()) {
          } else {
            System.out.println("Something got messed up here");
          }
        }));
  }

  static void boot(Vertx vertx, Router router, JsonObject config,
      Handler<AsyncResult<Void>> handler) {
    Future<Void> future = Future.future();
    future.setHandler(handler);
    try {
      final Map<String, HttpServer> stringHttpServerMap = HttpServerCreator.create(vertx, config);
      final CountDownLatch latch = new CountDownLatch(stringHttpServerMap.size());
      stringHttpServerMap.entrySet()
          .forEach(entry -> {
            entry.getValue().requestHandler(router).listen(ar -> {
              if (ar.succeeded()) {
                latch.countDown();
                if (latch.getCount() == 0) {
                  future.complete();
                }
              } else {
                future.fail(ar.cause());
              }
            });
          });
    } catch (SlixesException ex) {
      ex.printStackTrace();
      future.fail(ex);
    }
  }
}
