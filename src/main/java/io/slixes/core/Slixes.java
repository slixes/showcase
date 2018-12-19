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
    System.out.println("1");
    try {
      final Map<String, HttpServer> stringHttpServerMap = HttpServerCreator.create(vertx, config);
      System.out.println("2");
      final CountDownLatch latch = new CountDownLatch(stringHttpServerMap.size());

      System.out.println("latch contains: " + latch.getCount());
      System.out.println("3");
      stringHttpServerMap.entrySet()
          .forEach(entry -> {
            System.out.println(entry.getKey());
            entry.getValue().requestHandler(router).listen(ar -> {
              System.out.println(ar.result());
              System.out.println("Speaking from the future");
              if (ar.succeeded()) {
                System.out.println("succeeded");
                latch.countDown();
                System.out.println("latch is now: " + latch.getCount());
                if (latch.getCount() == 0) {
                  System.out.println("done");
                  future.complete();
                }
              } else {
                System.out.println("failed");
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
