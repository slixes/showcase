package io.slixes.core.test;

import io.slixes.core.Slixes;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.JksOptions;
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


  @BeforeAll
  static void boot() {
    System.out.println("Executed before anything");
  }

  @AfterAll
  static void shutdown() {
    System.out.println("Executed last");
  }


  @BeforeEach
  void preTest() {
    System.out.println("pre");
  }

  @AfterEach
  void postTest() {
    System.out.println("post");
  }


  @Test
  public void stupidTest() {
    Assertions.assertEquals(6, 6);
    HttpServerOptions options = new HttpServerOptions();
    options.setPort(8080);
    String encode = Json.encode(options);
    HttpServerOptions httpClientOptions = Json.decodeValue(encode, HttpServerOptions.class);
    Assertions.assertEquals(httpClientOptions, options);
    JksOptions jksOptions = new JksOptions().setPath("/opt/usr/services/myservice.jks").setPassword("testpassword");
    String jksOptionsEncoded = Json.encode(jksOptions);
    JksOptions jksOptionsDecoded = Json.decodeValue(jksOptionsEncoded, JksOptions.class);
    Assertions.assertEquals(jksOptionsDecoded, jksOptions);

  }


  @Test
  public void testSlixesLoading() throws Throwable {
    final Vertx vertx = Vertx.vertx();
    VertxTestContext testContext = new VertxTestContext();
    vertx.fileSystem().readFile("service.json", asyncConfigRead -> {
      Assertions.assertTrue(asyncConfigRead.succeeded());
      final Buffer result = asyncConfigRead.result();
      JsonObject config = result.toJsonObject();
      Map<String, HttpServer> boot = Slixes.boot(vertx, config);
      Assertions.assertEquals(3, boot.size());
      boot.entrySet().forEach(System.out::println);
      testContext.completeNow();
    });
    Assertions.assertTrue(testContext.awaitCompletion(1, TimeUnit.SECONDS));
    Assertions.assertFalse(testContext.failed());
  }
}

