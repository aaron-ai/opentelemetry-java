package io.opentelemetry.context;


public interface Function<T, R> {
  R apply(T t);
}
