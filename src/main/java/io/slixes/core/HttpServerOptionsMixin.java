package io.slixes.core;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class HttpServerOptionsMixin {


  @JsonIgnore
  private boolean ssl;

}
