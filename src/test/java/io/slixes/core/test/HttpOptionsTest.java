package io.slixes.core.test;

import io.slixes.core.Slixes;
import io.slixes.core.SlixesException;
import io.vertx.core.Vertx;
import io.vertx.core.VertxException;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.JksOptions;
import io.vertx.ext.web.Router;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@ExtendWith(VertxExtension.class)
public class HttpOptionsTest {


  @Test
  public void testSlixesLoading() throws InterruptedException {
    final Vertx vertx = Vertx.vertx();
    VertxTestContext testContext = new VertxTestContext();

    Router router = Router.router(vertx);

    Checkpoint readCheckpoint = testContext.checkpoint(4);

    vertx.fileSystem().readFile("service.json", asyncConfigRead -> {
      Assertions.assertTrue(asyncConfigRead.succeeded());
      Map<String, HttpServer> boot = null;
      try {
        boot = Slixes.boot(vertx, asyncConfigRead.result().toJsonObject());
      } catch (SlixesException e) {
      }
      Assertions.assertEquals(3, boot.size());
      boot.entrySet().forEach(System.out::println);
      readCheckpoint.flag();
    });





    vertx.fileSystem().readFile("service-invalid.json", asyncConfigRead -> {
      Assertions.assertTrue(asyncConfigRead.succeeded());
      Assertions.assertThrows(SlixesException.class, () -> Slixes.boot(vertx, asyncConfigRead.result().toJsonObject()));
      readCheckpoint.flag();
    });

    vertx.fileSystem().readFile("service-empty.json", asyncConfigRead -> {
      Assertions.assertTrue(asyncConfigRead.succeeded());
      Assertions.assertThrows(SlixesException.class, () -> Slixes.boot(vertx, asyncConfigRead.result().toJsonObject()));
      readCheckpoint.flag();
    });

    vertx.fileSystem().readFile("service-error.json", asyncConfigRead -> {
      Assertions.assertTrue(asyncConfigRead.succeeded());
      Assertions.assertThrows(SlixesException.class, () -> Slixes.boot(vertx, asyncConfigRead.result().toJsonObject()));
      readCheckpoint.flag();
    });

    Assertions.assertTrue(testContext.awaitCompletion(1, TimeUnit.SECONDS));
    Assertions.assertFalse(testContext.failed());
  }
}

