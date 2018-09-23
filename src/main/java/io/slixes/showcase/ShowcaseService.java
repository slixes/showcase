package io.slixes.showcase;

import io.slixes.showcase.handlers.CreateHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class ShowcaseService extends AbstractVerticle {

  @Override
  public void start() {


    /* Mysql driver initialization*/
    JsonObject config = new JsonObject()
      .put("url", "jdbc:mysql://localhost/showcase")
      .put("driver_class", "com.mysql.cj.jdbc.Driver")
      .put("user", "test")
      .put("password", "test")
      .put("max_pool_size", 20);
    JDBCClient client = JDBCClient.createShared(vertx, config);

    Router router = Router.router(Vertx.vertx());
    router.route().handler(BodyHandler.create());

    router.route("/v1/create")
      .method(HttpMethod.POST)
      .handler(new CreateHandler(client));

    router.route("/ping").handler(routingContext -> routingContext.response().end("Alive!"));

    HttpServer server = vertx.createHttpServer();
    server.requestHandler(router::accept).listen(8080);
  }
}
