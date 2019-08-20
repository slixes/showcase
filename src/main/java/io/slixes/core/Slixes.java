package io.slixes.core;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public interface Slixes {
  Vertx vertx = Vertx.currentContext().owner();

  static void boot(Router router, Handler<AsyncResult<Void>> handler) {
    Promise<Void> promise = Promise.promise();
    promise.future().setHandler(handler);
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

  private static Promise<HttpServer> httpServerCreator(final JsonObject httpConfig, Router router, Handler<AsyncResult<HttpServer>> handler) {
    Promise<HttpServer> promise = Promise.promise();
    promise.future().setHandler(handler);
    HttpServerCreator.create(httpConfig, createHandler -> {
      if (createHandler.succeeded()) {
        createHandler.result().requestHandler(router).listen(ar -> {
          if (ar.succeeded()) {
            promise.complete(createHandler.result());
          } else {
            promise.fail(ar.cause());
          }
        });
      } else {
        promise.fail(createHandler.cause());
      }
    });
    return promise;
  }
}
