package io.slixes.core;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.ArrayList;
import java.util.List;

public interface HttpServerCreator {

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
          final HttpServer httpServer = vertx.createHttpServer(serverOptions);
          if (httpServer == null) {
            System.out.println("I found the fucker ");
          }
        } catch (Exception ex) {
          ex.printStackTrace();
        }

      });
      return httpServers;
    } catch (Exception ex) {
      throw new SlixesException("Invalid configuration", ex);
    }
  }
}
