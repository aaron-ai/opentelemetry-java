package io.opentelemetry.sdk.metrics.common;

public interface BiFunction<T, U, R> {
  R apply(T t, U u);
}
