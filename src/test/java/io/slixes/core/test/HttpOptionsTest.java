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
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

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

    Assertions.assertTrue(testContext.awaitCompletion(1, TimeUnit.SECONDS));
    Assertions.assertFalse(testContext.failed());
  }


  @Test
  public void httpOptionsTest() {
    HttpServerOptions httpOptions = new HttpServerOptions();
    httpOptions.setHost("0.0.0.0");
    httpOptions.setPort(8080);
    httpOptions.setSsl(true);
    httpOptions
        .setKeyStoreOptions(new JksOptions().setPath("/tmp/test.jks").setPassword("testpassword"));

    final String httpOptionsJson = Json.encode(httpOptions);

    Assertions.assertNotNull(httpOptionsJson);

    HttpServerOptions decodedhttpOptions = Json
        .decodeValue(httpOptionsJson, HttpServerOptions.class);

    Assertions.assertEquals(httpOptions, decodedhttpOptions);

  }
}

