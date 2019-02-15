package io.slixes.showcase.handlers;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

public interface ChainedHandler extends Handler<RoutingContext> {


  static Handler<RoutingContext> create() {

    return routingContext -> anAsyncAction().compose(s -> anotherAsyncAction(s)).setHandler(ar -> routingContext.response().end(ar.succeeded() ? ar.result() : ar.cause().getMessage()));
  }


  private static Future<String> anAsyncAction() {
    Future<String> future = Future.future();
    // mimic something that take times
    Vertx.currentContext().owner().setTimer(100, l -> future.complete("world"));
    return future;
  }

  private static Future<String> anotherAsyncAction(String name) {
    Future<String> future = Future.future();
    // mimic something that take times
    Vertx.currentContext().owner().setTimer(100, l -> future.complete("hello " + name));
    return future;
  }

}
