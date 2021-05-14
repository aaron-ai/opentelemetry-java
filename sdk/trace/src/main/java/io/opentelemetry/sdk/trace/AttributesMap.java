/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.sdk.trace;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.api.internal.BiConsumer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/** A map with a fixed capacity that drops attributes when the map gets full. */
@SuppressWarnings({"rawtypes", "unchecked"})
final class AttributesMap extends Attributes {

  private static final long serialVersionUID = -5072696312123632376L;

  private final long capacity;
  private int totalAddedValues = 0;
  private HashMap<AttributeKey<?>, Object> map;

  AttributesMap(long capacity) {
    this.capacity = capacity;
  }


  public <T> void put(AttributeKey<T> key, T value) {
    if (key == null || key.getKey() == null || value == null) {
      return;
    }
    totalAddedValues++;
    if (size() >= capacity && !map.containsKey(key)) {
      return;
    }
    map.put(key, value);
  }

  int getTotalAddedValues() {
    return totalAddedValues;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T get(AttributeKey<T> key) {
    return (T) map.get(key);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public void forEach(BiConsumer<? super AttributeKey<?>, ? super Object> consumer) {
    for (Map.Entry<AttributeKey<?>, Object> entry : map.entrySet()) {
      AttributeKey key = entry.getKey();
      Object value = entry.getValue();
      consumer.accept((AttributeKey<?>) key, value);
    }
  }

  @Override
  public int size() {
    return map.size();
  }

  @Override
  public boolean isEmpty() {
    return map.isEmpty();
  }

  @Override
  public Map<AttributeKey<?>, Object> asMap() {
    // Because Attributes is marked Immutable, IDEs may recognize this as redundant usage. However,
    // this class is private and is actually mutable, so we need to wrap with unmodifiableMap
    // anyways. We implement the immutable Attributes for this class to support the
    // Attributes.builder().putAll usage - it is tricky but an implementation detail of this private
    // class.
    return Collections.unmodifiableMap(map);
  }

  @Override
  public AttributesBuilder toBuilder() {
    return Attributes.builder().putAll(this);
  }

  @Override
  public String toString() {
    return "AttributesMap{"
        + "data="
        + super.toString()
        + ", capacity="
        + capacity
        + ", totalAddedValues="
        + totalAddedValues
        + '}';
  }

  Attributes immutableCopy() {
    return Attributes.builder().putAll(this).build();
  }
}
