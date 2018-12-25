package io.slixes.core;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public interface Slixes {

  static JsonObject boot(Vertx vertx, Router router, JsonObject config)
      throws SlixesException {
    AtomicReference<JsonObject> result = new AtomicReference<>();
    final List<HttpServer> serverList = HttpServerCreator.create(vertx, config);
    serverList.forEach(entry -> entry.requestHandler(router).listen(ar -> {
      if (ar.succeeded()) {
        result.set(new JsonObject().put("result", true));
      }
      if (ar.failed()) {
        JsonObject errorJson = new JsonObject()
            .put("result", false)
            .put("error", new JsonObject()
                .put("message", ar.cause().getMessage()));
        result.set(errorJson);
      }
    }));
    return result.get();
  }

  static void boot(Vertx vertx, Router router, JsonObject config,
      Handler<AsyncResult<Void>> handler) {
    Future<Void> future = Future.future();
    future.setHandler(handler);
    try {
      final List<HttpServer> stringHttpServerMap = HttpServerCreator.create(vertx, config);
      if (!stringHttpServerMap.isEmpty()) {
        final CountDownLatch latch = new CountDownLatch(stringHttpServerMap.size());
        stringHttpServerMap.forEach(entry -> entry.requestHandler(router).listen(ar -> {
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
        future.fail("Nothing to boot");
      }
    } catch (SlixesException ex) {
      future.fail(ex);
    }
  }
}
