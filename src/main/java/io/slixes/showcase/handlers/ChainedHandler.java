package io.slixes.showcase.handlers;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

public interface ChainedHandler extends Handler<RoutingContext> {


  static Handler<RoutingContext> create() {

    return routingContext ->
      dumbOne()
        .compose(aVoid -> anAsyncAction())
        .compose(ChainedHandler::anotherAsyncAction).setHandler(ar -> routingContext.response().end(ar.succeeded() ? ar.result() : ar.cause().getMessage()));
  }

  private static Future<Void> dumbOne() {
    Promise<Void> promise = Promise.promise();
    Future<Void> future = promise.future();
    // mimic something that take times
    Vertx.currentContext().owner().setTimer(100, l -> {
      System.out.println("dumbOne completed");
      promise.complete();
    });
    return future;
  }

  private static Future<String> anAsyncAction() {
    Promise<String> promise = Promise.promise();
    // mimic something that take times
    Vertx.currentContext().owner().setTimer(100, l -> promise.complete("world"));
    System.out.println("anAsyncAction completed");
    return promise.future();
  }

  private static Future<String> anotherAsyncAction(String name) {
    Promise<String> promise = Promise.promise();
    // mimic something that take times
    Vertx.currentContext().owner().setTimer(100, l -> promise.complete("hello " + name));
    System.out.println("anotherAsyncAction completed");
    return promise.future();
  }

}
