package io.opentelemetry.api.metrics.common;

public interface Consumer<T> {
  void accept(T t);
}
