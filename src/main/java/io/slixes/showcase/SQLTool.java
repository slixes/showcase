package io.slixes.showcase;

import io.vertx.core.Handler;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;

public class SQLTool {

  public static void execute(SQLConnection conn, String sql, Handler<Boolean> done) {
    conn.execute(sql, res -> {
      if (res.failed()) {
        throw new RuntimeException(res.cause());
      }
      done.handle(res.succeeded());
    });
  }

  public static void query(SQLConnection conn, String sql, Handler<ResultSet> done) {
    conn.query(sql, res -> {
      if (res.failed()) {
        throw new RuntimeException(res.cause());
      }

      done.handle(res.result());
    });
  }

  public static void startTx(SQLConnection conn, Handler<ResultSet> done) {
    conn.setAutoCommit(false, res -> {
      if (res.failed()) {
        throw new RuntimeException(res.cause());
      }

      done.handle(null);
    });
  }

  public static void endTx(SQLConnection conn, Handler<ResultSet> done) {
    conn.commit(res -> {
      if (res.failed()) {
        throw new RuntimeException(res.cause());
      }

      done.handle(null);
    });
  }

  public static void rollbackTx(SQLConnection conn, Handler<ResultSet> done) {
    conn.rollback(res -> {
      if (res.failed()) {
        throw new RuntimeException(res.cause());
      }

      done.handle(null);
    });
  }
}
