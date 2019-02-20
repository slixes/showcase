package io.slixes.core;

import io.vertx.core.Launcher;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SlixesLauncher extends Launcher {

  private static final Logger logger = LogManager.getLogger(SlixesLauncher.class);

  public static void main(String[] args) {
    new SlixesLauncher().dispatch(args);
  }

  @Override
  public void beforeStartingVertx(VertxOptions options) {


    logger.info("Before start");
    logger.debug("Whatever");
    logger.fatal("Something terrible is happening");
    logger.warn("Watchout!");
    logger.error("oh no!");
  }

  @Override
  public void afterStartingVertx(Vertx vertx) {
    logger.info("After starting vertx");
  }


  @Override
  public void beforeStoppingVertx(Vertx vertx) {
    logger.info("Before stopping vertx");
  }

  @Override
  public void afterStoppingVertx() {
    logger.info("After stopping vertx");
  }
}
