/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.api.baggage;

import io.opentelemetry.api.internal.BiConsumer;
import io.opentelemetry.api.internal.ImmutableKeyValuePairs;
import io.opentelemetry.api.internal.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
final class ImmutableBaggage extends Baggage {

  private static final Baggage EMPTY = new ImmutableBaggage.Builder().build();

  private final ImmutableKeyValuePairs<String, BaggageEntry> immutableKeyValuePairs;

  private ImmutableBaggage(Object[] data) {
    immutableKeyValuePairs = new ImmutableKeyValuePairs<String, BaggageEntry>(data);
  }

  public static Baggage empty() {
    return EMPTY;
  }

  public static BaggageBuilder builder() {
    return new Builder();
  }

  @Override
  public int size() {
    return immutableKeyValuePairs.size();
  }

  @Override
  public void forEach(BiConsumer<? super String, ? super BaggageEntry> consumer) {
    immutableKeyValuePairs.forEach(consumer);
  }

  @Override
  public Map<String, BaggageEntry> asMap() {
    return immutableKeyValuePairs.asMap();
  }

  @Nullable
  @Override
  public String getEntryValue(String entryKey) {
    BaggageEntry entry = immutableKeyValuePairs.get(entryKey);
    return entry != null ? entry.getValue() : null;
  }

  @Override
  public BaggageBuilder toBuilder() {
    return new Builder(new ArrayList<Object>(immutableKeyValuePairs.data()));
  }

  private static Baggage sortAndFilterToBaggage(Object[] data) {
    return new ImmutableBaggage(data);
  }

  // TODO: Migrate to AutoValue.Builder
  // @AutoValue.Builder
  static class Builder extends BaggageBuilder {

    private final List<Object> data;

    Builder() {
      this.data = new ArrayList<Object>();
    }

    Builder(List<Object> data) {
      this.data = data;
    }

    @Override
    public BaggageBuilder put(String key, String value, BaggageEntryMetadata entryMetadata) {
      if (!isKeyValid(key) || !isValueValid(value) || entryMetadata == null) {
        return this;
      }
      data.add(key);
      data.add(ImmutableEntry.create(value, entryMetadata));

      return this;
    }

    @Override
    public BaggageBuilder remove(String key) {
      if (key == null) {
        return this;
      }
      data.add(key);
      data.add(null);
      return this;
    }

    @Override
    public Baggage build() {
      return sortAndFilterToBaggage(data.toArray());
    }
  }

  /**
   * Determines whether the given {@code String} is a valid entry key.
   *
   * @param name the entry key name to be validated.
   * @return whether the name is valid.
   */
  private static boolean isKeyValid(String name) {
    return name != null && !name.isEmpty() && StringUtils.isPrintableString(name);
  }

  /**
   * Determines whether the given {@code String} is a valid entry value.
   *
   * @param value the entry value to be validated.
   * @return whether the value is valid.
   */
  private static boolean isValueValid(String value) {
    return value != null && StringUtils.isPrintableString(value);
  }
}
