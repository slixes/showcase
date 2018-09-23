package io.slixes.showcase.handlers;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.slixes.showcase.SQLTool;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.web.RoutingContext;

import java.util.UUID;

public class CreateHandler implements Handler<RoutingContext> {
  private final JDBCClient client;

  public CreateHandler(JDBCClient client) {
    this.client = client;
  }

  @Override
  public void handle(RoutingContext routingContext) {
    client.getConnection(connection -> {
      if (connection.failed()) {
        routingContext.response()
          .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
          .end("error");
        return;
      }

      JsonObject requestBody = routingContext.getBodyAsJson();


      // start a transaction
      SQLTool.startTx(connection.result(), beginTrans -> {
        // insert some test data

        String executeStatament = "insert into users values('" +
          UUID.randomUUID().toString() + "', '" +
          requestBody.getString("birth_date") + "' , '" +
          requestBody.getString("first_name") + "' , '" +
          requestBody.getString("last_name") + "' , '" +
          requestBody.getString("gender") +
          "')";

        SQLTool.execute(connection.result(), executeStatament, insert -> {
          // commit data
          System.out.println("Operation result: " + insert.booleanValue());

          if(insert.booleanValue()) {

            SQLTool.endTx(connection.result(), handler -> {

              connection.result().close(done -> {
                if (done.failed()) {
                  throw new RuntimeException(done.cause());
                }
                routingContext.response().end("all done");
              });
            });
          } else {
            routingContext.response().end("Error");
          }
        });
      });
    });
  }
}
