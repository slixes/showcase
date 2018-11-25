package io.slixes.core;

import io.vertx.core.Vertx;
import io.vertx.core.VertxException;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public interface Slixes {

  String HTTP = "http";

  static Map<String, HttpServer> boot(Vertx vertx, JsonObject config) throws SlixesException {
    if (config.isEmpty() || !config.containsKey("http")) {
      throw new SlixesException("HTTP configuration is not available");
    } else {
      try {
        JsonArray http = config.getJsonArray(HTTP);
        Map<String, HttpServer> httpServers = new HashMap<>();
        http.forEach(httpConfig -> {
          JsonObject httpClientOptionsJson = JsonObject.mapFrom(httpConfig);
          HttpServerOptions serverOptions = httpClientOptionsJson.mapTo(HttpServerOptions.class);
          HttpServer httpServer = vertx.createHttpServer(serverOptions);
          httpServers.put(UUID.randomUUID().toString(), httpServer);
        });
        return httpServers;
      } catch (Exception ex) {
        throw new SlixesException("HTTP configuration is invalid", ex);
      }
    }
  }
}
