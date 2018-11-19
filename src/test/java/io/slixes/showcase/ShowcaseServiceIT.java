package io.slixes.showcase;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
public class ShowcaseServiceIT {


  @Test
  void start_server() {
    VertxTestContext testContext = new VertxTestContext();

    Vertx vertx = Vertx.vertx();
    vertx.createHttpServer()
      .requestHandler(req -> req.response().end("Ok"))
      .listen(9800, ar -> {
        // (we can check here if the server started or not)



      });


  }
}
