package io.slixes.core;

    import io.vertx.core.Launcher;
    import io.vertx.core.Vertx;
    import io.vertx.core.VertxOptions;

public class SlixesLauncher extends Launcher {

  public static void main(String[] args) {
    new SlixesLauncher().dispatch(args);
  }

  @Override
  public void beforeStartingVertx(VertxOptions options) {
    System.out.println("Before starting vertx");
  }

  @Override
  public void afterStartingVertx(Vertx vertx) {
    System.out.println("After starting vertx");
  }


  @Override
  public void beforeStoppingVertx(Vertx vertx) {
    System.out.println("Before stopping vertx");
  }

  @Override
  public void afterStoppingVertx() {
    System.out.println("After stopping vertx");
  }
}
