package com.pedido.api.exception;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;

@Slf4j
@AllArgsConstructor
public enum ErrorCode implements IExceptionCode {

  CLIENTE_NOT_FOUND(404, HttpStatus.NOT_FOUND, Level.ERROR),
  PEDIDO_MAX_LIMIT(412, HttpStatus.PRECONDITION_FAILED, Level.ERROR),
  PEDIDO_NOT_EMPTY(412, HttpStatus.PRECONDITION_FAILED, Level.ERROR),
  NRO_CONTROLE_ALREADY_EXIST(412, HttpStatus.PRECONDITION_FAILED, Level.ERROR);

  private static final String BASE_NAME = "exception.messages";

  private final Integer internalCode;

  private final HttpStatus httpStatus;

  private final Level severity;

  @Override
  public String getName() {
    return this.name();
  }

  @Override
  public int getHttpStatus() {
    return httpStatus.value();
  }

  @Override
  public Level getSeverity() {
    return severity;
  }

  @Override
  public Integer getInternalCode() {
    return internalCode;
  }

  @Override
  public String getMessage(Object... args) {
    ResourceBundle messages = ResourceBundle.getBundle(BASE_NAME,
        ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES));
    if (!messages.containsKey(this.name())) {
      log.warn("Nao existe key: {}", this.name());
      return String.format("???%s???", this.name());
    }
    return MessageFormat.format(parseStringEncoding(messages.getString(this.name()), StandardCharsets.ISO_8859_1), args);
  }

  private static String parseStringEncoding(String text, Charset charset) {
    return new String(text.getBytes(charset), StandardCharsets.UTF_8);
  }

}
