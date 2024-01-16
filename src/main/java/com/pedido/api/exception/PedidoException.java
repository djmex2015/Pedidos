package com.pedido.api.exception;

import java.io.Serial;
import java.util.Arrays;

public  class PedidoException extends Exception {

  @Serial
  private static final long serialVersionUID = -1L;

  private final IExceptionCode code;

  private final Object[] params;

  public PedidoException(IExceptionCode cod, Object... params) {
    this(null, cod, params);
  }

  protected PedidoException(Exception cause, IExceptionCode cod, Object... params) {
    super(cause);
    this.code = cod;
    this.params = Arrays.copyOf(params, params.length);
  }

  @Override
  public String getMessage() {
    if (params.length > 0) {
      return code.getMessage( params);
    }
    return code.getMessage();
  }

}
