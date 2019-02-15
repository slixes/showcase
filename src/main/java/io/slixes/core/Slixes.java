package io.slixes.core;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public interface Slixes {


  Vertx vertx = Vertx.currentContext().owner();

  static void boot(Router router, Handler<AsyncResult<Void>> handler) {

    Future<Void> future = Future.future();
    future.setHandler(handler);

    configHandler().compose(jsonObject -> httpServerCreator(jsonObject, router)).setHandler(ar -> {
      if (ar.succeeded()) {
        future.complete();
      } else {
        future.fail(ar.cause());
      }
    });
  }


  private static Future<JsonObject> configHandler() {
    Future<JsonObject> future = Future.future();
    final ConfigRetriever retriever = ConfigRetriever.create(vertx);
    retriever.getConfig(configHandler -> {
      if (configHandler.succeeded()) {
        future.complete(configHandler.result());
      } else {
        future.fail(configHandler.cause());
      }
    });

    retriever.listen(configChange -> {
      System.out.println("Configuration change detected");
      if (!configChange.getNewConfiguration().equals(configChange.getPreviousConfiguration())) {
        //TODO: make this async
        vertx.close();
      }
    });
    return future;

  }

  private static Future<Void> httpServerCreator(final JsonObject httpConfig, Router router) {
    Future<Void> future = Future.future();
    try {
      final List<HttpServer> stringHttpServerMap = HttpServerCreator.create(vertx, httpConfig);

      if (!stringHttpServerMap.isEmpty()) {
        final CountDownLatch latch = new CountDownLatch(stringHttpServerMap.size());
        stringHttpServerMap.forEach(entry ->
          entry.requestHandler(router).listen(ar -> {
            if (ar.succeeded()) {
              latch.countDown();
              if (latch.getCount() == 0) {
                future.complete();
              }
            } else {
              future.fail(ar.cause());
            }
          }));
      } else {
        future.fail("Nothing to boot, make sure the configuration contains at least one http configuration entry");
      }
    } catch (SlixesException e) {
      future.fail(e);
    }
    return future;
  }
}
