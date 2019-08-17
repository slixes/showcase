package io.slixes.core;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

import java.util.concurrent.CountDownLatch;

public interface Slixes {

  Vertx vertx = Vertx.currentContext().owner();

  static void boot(Router router, Handler<AsyncResult<Void>> handler) {
    Promise<Void> promise = Promise.promise();
    Future<Void> future = promise.future();
    future.setHandler(handler);
    configHandler().future()
      .compose(jsonObject -> httpServerCreator(jsonObject, router, bootHandler -> {
        if (bootHandler.succeeded()) {
          promise.complete();
        } else {
          promise.fail(bootHandler.cause());
        }
      }).future())
      .setHandler(ar -> {
        if (ar.succeeded()) {
          promise.complete();
        } else {
          promise.fail(ar.cause());
        }
      });
  }

  private static Promise<JsonObject> configHandler() {
    Promise<JsonObject> promise = Promise.promise();
    Future<JsonObject> future = promise.future();
    //TODO: Allow passing of config retriever configuration
    final ConfigRetriever retriever = ConfigRetriever.create(vertx);
    retriever.getConfig(configHandler -> {
      if (configHandler.succeeded()) {
        promise.complete(configHandler.result());
      } else {
        promise.fail(configHandler.cause());
      }
    });

    retriever.listen(configChange -> {
      System.out.println("Configuration change detected");
      if (!configChange.getNewConfiguration().equals(configChange.getPreviousConfiguration())) {
        vertx.close(voidAsyncResult -> {
          //TODO: Add logging here
        });
      }
    });
    return promise;
  }

  private static Promise<Void> httpServerCreator(final JsonObject httpConfig, Router router, Handler<AsyncResult<Void>> handler) {

    Promise<Void> promise = Promise.promise();
    Future<Void> future = promise.future();
    future.setHandler(handler);

    HttpServerCreator.create(httpConfig, createHandler -> {
      if (createHandler.succeeded()) {
        final CountDownLatch latch = new CountDownLatch(createHandler.result().size());
        createHandler.result().forEach(entry ->
          entry.requestHandler(router).listen(ar -> {
            if (ar.succeeded()) {
              latch.countDown();
              if (latch.getCount() == 0) {
                promise.complete();
              }
            } else {
              promise.fail(ar.cause());

            }
          }));
      } else {
        promise.fail(createHandler.cause());
      }
    });
    return promise;
  }
}
