package io.slixes.core;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

public interface HttpServerCreator {

  static void create(JsonObject cfg, Handler<AsyncResult<List<HttpServer>>> handler) {
    Vertx vertx = Vertx.currentContext().owner();
    Future<List<HttpServer>> future = Future.future();
    future.setHandler(handler);
    if (cfg.isEmpty() || !cfg.containsKey(SlixesType.HTTP.name().toLowerCase())) {
      future.fail(new SlixesException("Http configuration is missing"));
    } else {
      final JsonArray http = cfg.getJsonArray(SlixesType.HTTP.name().toLowerCase());
      List<HttpServer> httpServers = new ArrayList<>();
      for (Object httpConfig : http) {
        httpServers.add(vertx.createHttpServer(new HttpServerOptions(JsonObject.mapFrom(httpConfig))));
      }
      future.complete(httpServers);
    }
  }
}
