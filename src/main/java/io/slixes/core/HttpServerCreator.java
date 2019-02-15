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


  //TODO:Consider making this method async for consistency
  static List<HttpServer> create(Vertx vertx, JsonObject cfg) throws SlixesException {
    if (cfg.isEmpty() || !cfg.containsKey(SlixesType.HTTP.name().toLowerCase())) {
      throw new SlixesException("Missing http configuration");
    }
    try {
      JsonArray http = cfg.getJsonArray(SlixesType.HTTP.name().toLowerCase());
      List<HttpServer> httpServers = new ArrayList<>();
      http.forEach(httpConfig -> {
        JsonObject httpClientOptionsJson = JsonObject.mapFrom(httpConfig);
        final HttpServerOptions serverOptions = new HttpServerOptions(httpClientOptionsJson);
        try {
          httpServers.add(vertx.createHttpServer(serverOptions));
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      });
      return httpServers;
    } catch (Exception ex) {
      throw new SlixesException("Invalid configuration", ex);
    }
  }


  static void create(Vertx vertx, JsonObject cfg, Handler<AsyncResult<List<HttpServer>>> handler) {
    Future<List<HttpServer>> future = Future.future();

    future.setHandler(handler);

    if (cfg.isEmpty() || !cfg.containsKey(SlixesType.HTTP.name().toLowerCase())) {
      future.fail(new SlixesException("Missing http configuration"));
    } else {
      final JsonArray http = cfg.getJsonArray(SlixesType.HTTP.name().toLowerCase());

      List<HttpServer> httpServers = new ArrayList<>();
      http.forEach(httpConfig -> {
        JsonObject httpClientOptionsJson = JsonObject.mapFrom(httpConfig);
        final HttpServerOptions serverOptions = new HttpServerOptions(httpClientOptionsJson);
        httpServers.add(vertx.createHttpServer(serverOptions));
      });
      future.complete(httpServers);
    }
  }
}
