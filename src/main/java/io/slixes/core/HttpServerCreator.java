package io.slixes.core;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

interface HttpServerCreator {

  static Promise<HttpServer> create(JsonObject cfg, Handler<AsyncResult<HttpServer>> handler) {
    Vertx vertx = Vertx.currentContext().owner();
    Promise<HttpServer> promise = Promise.promise();
    promise.future().setHandler(handler);
    if (cfg.isEmpty() || !cfg.containsKey(SlixesType.HTTP.name().toLowerCase())) {
      promise.fail(new SlixesException("Http configuration is missing"));
    } else {
      final JsonObject httpConfig = cfg.getJsonObject(SlixesType.HTTP.name().toLowerCase());
      HttpServer httpServer = vertx.createHttpServer(new HttpServerOptions(JsonObject.mapFrom(httpConfig)));
      promise.complete(httpServer);
    }
    return promise;
  }
}
