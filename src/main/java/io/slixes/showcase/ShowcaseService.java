package io.slixes.showcase;

import io.slixes.core.JksOptionsMixin;
import io.slixes.core.Slixes;
import io.slixes.showcase.handlers.InfoHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.Json;
import io.vertx.core.net.JksOptions;
import io.vertx.core.net.KeyCertOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class ShowcaseService extends AbstractVerticle {

  @Override
  public void start(Future<Void> startFuture) {

    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());

    router.route("/ping").handler(routingContext -> routingContext.response().end("Alive!"));
    router.route("/info").handler(InfoHandler.create());

    Slixes.boot(vertx, router, config(), ar -> {
      if (ar.succeeded()) {
        startFuture.complete();
      } else {
        startFuture.fail(ar.cause());
        vertx.close();
      }
    });

    Json.mapper.addMixIn(KeyCertOptions.class, JksOptionsMixin.class);
    Json.prettyMapper.addMixIn(KeyCertOptions.class, JksOptionsMixin.class);
  }


  public static void main(String... args) {

    HttpServerOptions httpsOptions = new HttpServerOptions();
    httpsOptions.setHost("0.0.0.0");
    httpsOptions.setPort(4883);
    httpsOptions.setSsl(true);
    httpsOptions
        .setKeyStoreOptions(new JksOptions().setPath("/tmp/test.jsk").setPassword("test"));

    System.out.println(Json.encode(httpsOptions));
  }


}
