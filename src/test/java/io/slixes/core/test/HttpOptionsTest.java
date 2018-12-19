package io.slixes.core.test;

import io.slixes.core.Slixes;
import io.slixes.core.SlixesException;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.Json;
import io.vertx.core.net.JksOptions;
import io.vertx.ext.web.Router;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(VertxExtension.class)
public class HttpOptionsTest {

  static final Logger LOG = LoggerFactory.getLogger(HttpOptionsTest.class);


  @Test
  public void testSlixesLoading() {
    final Vertx vertx = Vertx.vertx();
    VertxTestContext testContext = new VertxTestContext();

    Router router = Router.router(vertx);

    Checkpoint readCheckpoint = testContext.checkpoint(4);

    vertx.fileSystem().readFile("service.json", asyncConfigRead -> {
      Assertions.assertTrue(asyncConfigRead.succeeded());
      try {
        Slixes.boot(vertx, router, asyncConfigRead.result().toJsonObject());
      } catch (SlixesException e) {
      }
      readCheckpoint.flag();
    });

    vertx.fileSystem().readFile("service-invalid.json", asyncConfigRead -> {
      Assertions.assertTrue(asyncConfigRead.succeeded());
      Assertions.assertThrows(ClassCastException.class,
          () -> Slixes.boot(vertx, router, asyncConfigRead.result().toJsonObject()));
      readCheckpoint.flag();
    });

    vertx.fileSystem().readFile("service-empty.json", asyncConfigRead -> {
      Assertions.assertTrue(asyncConfigRead.succeeded());
      Assertions.assertThrows(SlixesException.class,
          () -> Slixes.boot(vertx, router, asyncConfigRead.result().toJsonObject()));
      readCheckpoint.flag();
    });

    vertx.fileSystem().readFile("service-error.json", asyncConfigRead -> {
      Assertions.assertTrue(asyncConfigRead.succeeded());
      Assertions.assertThrows(SlixesException.class,
          () -> Slixes.boot(vertx, router, asyncConfigRead.result().toJsonObject()));
      readCheckpoint.flag();
    });

    Assertions.assertFalse(testContext.failed());
  }


  @Test
  public void testHttpOptionsSerialization() {

    final HttpServerOptions serverOptions = new HttpServerOptions()
        .setPort(8443)
        .setSsl(true)
        .setKeyStoreOptions(new JksOptions().setPath("test.jks").setPassword("test"));

    System.out.println(Json.encode(serverOptions));


  }
}

