package io.slixes.showcase.handlers;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

public interface ChainedHandler extends Handler<RoutingContext> {


  static Handler<RoutingContext> create() {

    return routingContext ->
      dumbOne()
        .compose(aVoid -> anAsyncAction())
        .compose(s -> anotherAsyncAction(s)).setHandler(ar -> routingContext.response().end(ar.succeeded() ? ar.result() : ar.cause().getMessage()));

  }


  private static Future<Void> dumbOne() {
    Future<Void> future = Future.future();
    // mimic something that take times
    Vertx.currentContext().owner().setTimer(100, l -> {
      System.out.println("dumbOne completed");
      future.complete();
    });
    return future;
  }

  private static Future<String> anAsyncAction() {
    Future<String> future = Future.future();
    // mimic something that take times
    Vertx.currentContext().owner().setTimer(100, l -> future.complete("world"));
    System.out.println("anAsyncAction completed");
    return future;

  }

  private static Future<String> anotherAsyncAction(String name) {
    Future<String> future = Future.future();
    // mimic something that take times
    Vertx.currentContext().owner().setTimer(100, l -> future.complete("hello " + name));
    System.out.println("anotherAsyncAction completed");
    return future;
  }

}
