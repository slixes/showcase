package io.slixes.core;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.JksOptions;
import io.vertx.core.net.PemKeyCertOptions;
import io.vertx.core.net.PfxOptions;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public interface HttpServerCreator {

  static Map<String, HttpServer> create(Vertx vertx, JsonObject cfg) throws SlixesException {
    if (cfg.isEmpty() || !cfg.containsKey(SlixesType.HTTP.name().toLowerCase())) {
      throw new SlixesException("Missing http configuration");
    }

    AtomicReference<JksOptions> jksOptions = new AtomicReference<>();
    AtomicReference<PfxOptions> pfxOptions = new AtomicReference<>();
    AtomicReference<PemKeyCertOptions> pemKeyCertOptions = new AtomicReference<>();
    try {
      JsonArray http = cfg.getJsonArray(SlixesType.HTTP.name().toLowerCase());

      Map<String, HttpServer> httpServers = new HashMap<>();
      http.forEach(httpConfig -> {
        JsonObject httpClientOptionsJson = JsonObject.mapFrom(httpConfig);

        final JsonObject keyCertOptionsJson = httpClientOptionsJson.getJsonObject("keyCertOptions");
        final JsonObject pemKeyCertOptionsJson = httpClientOptionsJson
            .getJsonObject("pemKeyCertOptions");
        final JsonObject pfxKeyCertOptionsJson = httpClientOptionsJson
            .getJsonObject("pfxKeyCertOptions");

        if (keyCertOptionsJson != null) {
          jksOptions.set(keyCertOptionsJson.mapTo(JksOptions.class));
        }

        if (pfxKeyCertOptionsJson != null) {
          pemKeyCertOptions.set(keyCertOptionsJson.mapTo(PemKeyCertOptions.class));
        }

        if (pemKeyCertOptionsJson != null) {
          pfxOptions.set(keyCertOptionsJson.mapTo(PfxOptions.class));
        }

//        httpClientOptionsJson.remove("pemKeyCertOptions");
//        httpClientOptionsJson.remove("keyStoreOptions");
//        httpClientOptionsJson.remove("pfxKeyCertOptions");
        httpClientOptionsJson.remove("keyCertOptions");
//        httpClientOptionsJson.remove("ssl");

        HttpServerOptions serverOptions = httpClientOptionsJson.mapTo(HttpServerOptions.class);

        if (jksOptions.get() != null) {
          System.out.println(Json.encode(jksOptions.get()));
          serverOptions = serverOptions.setSsl(true).setKeyStoreOptions(jksOptions.get());

        }
        if (pemKeyCertOptions.get() != null) {
          serverOptions = serverOptions.setSsl(true).setPemKeyCertOptions(pemKeyCertOptions.get());
        }

        if (pfxOptions.get() != null) {
          serverOptions = serverOptions.setSsl(true).setPfxKeyCertOptions(pfxOptions.get());

        }
        httpServers.put(UUID.randomUUID().toString(), vertx.createHttpServer(serverOptions));
      });
      return httpServers;
    } catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }

}
