package io.opentelemetry.sdk.trace;

public interface Supplier<T> {
  T get();
}
