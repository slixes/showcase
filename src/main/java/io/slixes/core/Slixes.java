package io.slixes.core;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

import java.util.concurrent.CountDownLatch;

public interface Slixes {

  Vertx vertx = Vertx.currentContext().owner();

  static void boot(Router router, Handler<AsyncResult<Void>> handler) {
    Future<Void> future = Future.future();
    future.setHandler(handler);

    configHandler().compose(jsonObject -> httpServerCreator(jsonObject, router, bootHandler -> {
      if (bootHandler.succeeded()) {
        future.complete();
      } else {
        future.fail(bootHandler.cause());
      }
    })).setHandler(ar -> {
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

  private static Future<Void> httpServerCreator(final JsonObject httpConfig, Router router, Handler<AsyncResult<Void>> handler) {
    Future<Void> future = Future.future();
    future.setHandler(handler);

    HttpServerCreator.create(httpConfig, createHandler -> {
      if (createHandler.succeeded()) {
        final CountDownLatch latch = new CountDownLatch(createHandler.result().size());
        createHandler.result().forEach(entry ->
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
        future.fail(createHandler.cause());
      }
    });
    return future;
  }
}
