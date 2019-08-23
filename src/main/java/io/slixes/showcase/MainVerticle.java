package io.slixes.showcase;

import io.slixes.showcase.EndPoints;
import io.slixes.swagger.SwaggerHandler;
import io.slixes.swagger.Util;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;

public class MainVerticle extends AbstractVerticle {

  public static final String APPLICATION_JSON = "application/json";
  private static final int PORT = 8080;
  private static final String HOST = "localhost";
  private HttpServer server;

  private EndPoints endPoints;

  @Override
  public void start(Future<Void> startFuture) {

    endPoints = new EndPoints();

    server = vertx.createHttpServer(createOptions());

    Router router = Util.router();
    router.get("/swagger").handler(SwaggerHandler.create(router));

    // Routing section - this is where we declare which end points we want to use
    router.get("/products").handler(endPoints::fetchAllProducts);
    router.get("/product/:productId").handler(endPoints::fetchProduct);
    router.post("/product").handler(endPoints::addProduct);
    router.put("/product").handler(endPoints::putProduct);
    router.delete("/product/:deleteProductId").handler(endPoints::deleteProduct);

    server.requestHandler(router).listen(result -> {
      if (result.succeeded()) {
        startFuture.complete();
      } else {
        startFuture.fail(result.cause());
      }
    });
  }

  @Override
  public void stop(Future<Void> future) {
    if (server == null) {
      future.complete();
      return;
    }
    server.close(future.completer());
  }

  private HttpServerOptions createOptions() {
    HttpServerOptions options = new HttpServerOptions();
    options.setHost(HOST);
    options.setPort(PORT);
    return options;
  }
}
