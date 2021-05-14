/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.api.baggage;

/**
 * A builder of {@link Baggage}.
 *
 * @see Baggage#builder()
 */
public abstract class BaggageBuilder {

  /**
   * Adds the key/value pair and metadata regardless of whether the key is present.
   *
   * @param key the {@code String} key which will be set.
   * @param value the {@code String} value to set for the given key.
   * @param entryMetadata the {@code BaggageEntryMetadata} metadata to set for the given key.
   * @return this
   */
  public abstract BaggageBuilder put(String key, String value, BaggageEntryMetadata entryMetadata);

  /**
   * Adds the key/value pair with empty metadata regardless of whether the key is present.
   *
   * @param key the {@code String} key which will be set.
   * @param value the {@code String} value to set for the given key.
   * @return this
   */
  public BaggageBuilder put(String key, String value) {
    return put(key, value, BaggageEntryMetadata.empty());
  }

  /**
   * Removes the key if it exists.
   *
   * @param key the {@code String} key which will be removed.
   * @return this
   */
  public abstract BaggageBuilder remove(String key);

  /**
   * Creates a {@code Baggage} from this builder.
   *
   * @return a {@code Baggage} with the same entries as this builder.
   */
  public abstract Baggage build();
}
