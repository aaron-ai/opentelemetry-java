/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.api.trace;

import io.opentelemetry.api.internal.BiConsumer;
import java.util.Map;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

/**
 * Carries tracing-system specific context in a list of key-value pairs. TraceState allows different
 * vendors propagate additional information and inter-operate with their legacy Id formats.
 *
 * <p>Implementation is optimized for a small list of key-value pairs.
 *
 * <p>Key is opaque string up to 256 characters printable. It MUST begin with a lowercase letter,
 * and can only contain lowercase letters a-z, digits 0-9, underscores _, dashes -, asterisks *, and
 * forward slashes /.
 *
 * <p>Value is opaque string up to 256 characters printable ASCII RFC0020 characters (i.e., the
 * range 0x20 to 0x7E) except comma , and =.
 *
 * <p>Implementations of this interface *must* be immutable and have well-defined value-based
 * equals/hashCode implementations. If an implementation does not strictly conform to these
 * requirements, behavior of the OpenTelemetry APIs and default SDK cannot be guaranteed.
 *
 * <p>Implementations of this interface that do not conform to the W3C specification risk
 * incompatibility with W3C-compatible implementations.
 *
 * <p>For these reasons, it is strongly suggested that you use the implementation that is provided
 * here via the {@link TraceState#builder}.
 */
@Immutable
public abstract class TraceState {

  /**
   * Returns the default {@code TraceState} with no entries.
   *
   * <p>This method is equivalent to calling {@code #builder().build()}, but avoids new allocations.
   *
   * @return the default {@code TraceState} with no entries.
   */
  public static TraceState getDefault() {
    return ArrayBasedTraceStateBuilder.empty();
  }

  /** Returns an empty {@code TraceStateBuilder}. */
  public static TraceStateBuilder builder() {
    return new ArrayBasedTraceStateBuilder();
  }

  /**
   * Returns the value to which the specified key is mapped, or null if this map contains no mapping
   * for the key.
   *
   * @param key with which the specified value is to be associated
   * @return the value to which the specified key is mapped, or null if this map contains no mapping
   *     for the key.
   */
  @Nullable
  public abstract String get(String key);

  /** Returns the number of entries in this {@link TraceState}. */
  public abstract int size();

  /** Returns whether this {@link TraceState} is empty, containing no entries. */
  public abstract boolean isEmpty();

  /** Iterates over all the key-value entries contained in this {@link TraceState}. */
  public abstract void forEach(BiConsumer<String, String> consumer);

  /** Returns a read-only view of this {@link TraceState} as a {@link Map}. */
  public abstract Map<String, String> asMap();

  /**
   * Returns a {@code Builder} based on this {@code TraceState}.
   *
   * @return a {@code Builder} based on this {@code TraceState}.
   */
  public abstract TraceStateBuilder toBuilder();
}
