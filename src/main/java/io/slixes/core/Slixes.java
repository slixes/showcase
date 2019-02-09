package io.slixes.core;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public interface Slixes {



  static void boot(Vertx vertx, Router router, Handler<AsyncResult<Void>> handler) {

    //TODO: Look into chaining the operations
    Future<Void> future = Future.future();
    future.setHandler(handler);

    final ConfigRetriever retriever = ConfigRetriever.create(vertx);
    retriever.getConfig(configHandler -> {
      if (configHandler.succeeded()) {
        try {
          final JsonObject config = configHandler.result();
          final List<HttpServer> stringHttpServerMap = HttpServerCreator.create(vertx, config);
          if (!stringHttpServerMap.isEmpty()) {
            final CountDownLatch latch = new CountDownLatch(stringHttpServerMap.size());
            stringHttpServerMap.forEach(entry ->
              entry.requestHandler(router).listen(ar -> {
                if (ar.succeeded()) {
                  latch.countDown();
                  if (latch.getCount() == 0) {
                    future.complete();
                  }
                } else {
                  future.fail(ar.cause());
                }
              }));
          } else {
            future.fail("Nothing to boot, make sure the configuration contains at least one http configuration entry");
          }
        } catch (SlixesException e) {
          future.fail(e.getCause());
        }
      } else {
        future.fail("Unable to retrieve configuration");
      }
    });

    retriever.listen(configChange -> {
      System.out.println("Configuration change detected");
      if (!configChange.getNewConfiguration().equals(configChange.getPreviousConfiguration())) {
        //TODO: make this async
        vertx.close();
      }
    });


  }
}
