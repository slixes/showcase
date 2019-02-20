package io.slixes.showcase.handlers;

import io.slixes.core.SlixesLauncher;
import io.vertx.core.Handler;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface PingHandler extends Handler<RoutingContext> {

  Logger logger = LogManager.getLogger(SlixesLauncher.class);




  static Handler<RoutingContext> create() {

    return routingContext -> {
      logger.info("test from log4j2");
      logger.info("test vertx logger");
      routingContext.response().end("Alive");
    };

  }
}
