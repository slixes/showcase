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
    serversMap.entrySet()
        .forEach(entry -> {
          System.out.println("I am here");
          final HttpServer success = entry.getValue().requestHandler(router::accept).listen(ar
              -> {
            if (ar.succeeded()) {
              System.out.println("Success");
            } else {
              System.out.println(ar.cause());
            }
          });
        });
  }

  static void boot(Vertx vertx, Router router, JsonObject config,
      Handler<AsyncResult<Void>> handler) {
    Future<Void> future = Future.future();
    future.setHandler(handler);
    try {
      final Map<String, HttpServer> stringHttpServerMap = HttpServerCreator.create(vertx, config);
      CountDownLatch latch = new CountDownLatch(stringHttpServerMap.size());
      System.out.println("here");
      stringHttpServerMap.entrySet()
          .forEach(entry ->
          {
            System.out.println("and here");
            entry.getValue().requestHandler(router::accept).listen(ar -> {
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
