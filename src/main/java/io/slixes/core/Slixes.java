package io.slixes.core;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import java.util.Map;

public interface Slixes {

  static Map<String, HttpServer> boot(Vertx vertx, JsonObject config) throws SlixesException {
    if (config.isEmpty() || !config.containsKey(SlixesType.HTTP.name().toLowerCase())) {
      throw new SlixesException("HTTP configuration is not available");
    } else {
      try {
        return HttpServerCreator.create(vertx, config);
      } catch (Exception ex) {
        throw new SlixesException("HTTP configuration is invalid", ex);
      }
    }
  }

  static void boot(Vertx vertx, JsonObject config,
      Handler<AsyncResult<Map<String, HttpServer>>> handler) {
    Future<Map<String, HttpServer>> future = Future.future();
    future.setHandler(handler);
    if (config.isEmpty() || !config.containsKey(SlixesType.HTTP.name().toLowerCase())) {
      future.fail(new SlixesException("HTTP configuration is not available"));
    } else {
      try {
        future.complete(HttpServerCreator.create(vertx, config));
      } catch (Exception ex) {
        future.fail(new SlixesException("HTTP configuration is invalid", ex));
      }
    }
  }
}
