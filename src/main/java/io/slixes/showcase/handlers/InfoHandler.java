package io.slixes.showcase.handlers;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public interface InfoHandler extends Handler<RoutingContext> {

  @ApiResponse
  @ApiResponses({
      @ApiResponse
  })
  static Handler<RoutingContext> create() {

    return new InfoHandlerImpl();
  }
}
