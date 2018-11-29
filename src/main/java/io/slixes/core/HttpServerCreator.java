package io.slixes.core;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public interface HttpServerCreator {

  static Map<String, HttpServer> create(Vertx vertx, JsonObject cfg) throws SlixesException {
    if (cfg.isEmpty() || !cfg.containsKey(SlixesType.HTTP.name().toLowerCase())) {
      throw new SlixesException("HTTP configuration is not available");
    }

    JsonArray http = cfg.getJsonArray(SlixesType.HTTP.name().toLowerCase());
    Map<String, HttpServer> httpServers = new HashMap<>();
    http.forEach(httpConfig -> {
      JsonObject httpClientOptionsJson = JsonObject.mapFrom(httpConfig);
      HttpServerOptions serverOptions = httpClientOptionsJson.mapTo(HttpServerOptions.class);
      HttpServer httpServer = vertx.createHttpServer(serverOptions);
      httpServers.put(UUID.randomUUID().toString(), httpServer);
    });
    return httpServers;
  }
}
