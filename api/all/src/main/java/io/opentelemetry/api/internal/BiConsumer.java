package io.opentelemetry.api.internal;

public interface BiConsumer<T, U> {
  void accept(T t, U u);
}
