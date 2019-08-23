package io.slixes.swagger;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.ErrorHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.HashSet;
import java.util.Set;

import static io.vertx.core.http.HttpHeaders.*;

public class Util {
  public static final String APPLICATION_JSON = "application/json";

  public static final Router router() {
    Router router = Router.router(Vertx.currentContext().owner());
    router.route().consumes(APPLICATION_JSON);
    router.route().produces(APPLICATION_JSON);
    router.route().handler(BodyHandler.create());

    Set<String> allowedHeaders = new HashSet<>();
    allowedHeaders.add("auth");
    allowedHeaders.add("Content-Type");

    Set<HttpMethod> allowedMethods = new HashSet<>();
    allowedMethods.add(HttpMethod.GET);
    allowedMethods.add(HttpMethod.POST);
    allowedMethods.add(HttpMethod.OPTIONS);
    allowedMethods.add(HttpMethod.DELETE);
    allowedMethods.add(HttpMethod.PATCH);
    allowedMethods.add(HttpMethod.PUT);

    router.route().handler(CorsHandler.create("*").allowedHeaders(allowedHeaders).allowedMethods(allowedMethods));

    router.route().handler(context -> {
      context.response().headers().add(CONTENT_TYPE, APPLICATION_JSON);
      context.next();
    });
    router.route().failureHandler(ErrorHandler.create(true));

    // Serve the Swagger JSON spec out on /swagger
//    router.get("/swagger").handler(res -> res.response().setStatusCode(200).end(Json.pretty(openAPIDoc(router))));
    // Serve the Swagger UI out on /doc/
    router.route("/doc/*").handler(StaticHandler.create().setCachingEnabled(false).setWebRoot("webroot/swagger-ui"));

    return router;
  }
}
