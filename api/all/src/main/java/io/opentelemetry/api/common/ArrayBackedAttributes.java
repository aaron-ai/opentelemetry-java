/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.api.common;

import io.opentelemetry.api.internal.BiConsumer;
import io.opentelemetry.api.internal.ImmutableKeyValuePairs;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import javax.annotation.concurrent.Immutable;

@Immutable
final class ArrayBackedAttributes extends Attributes {

  // We only compare the key name, not type, when constructing, to allow deduping keys with the
  // same name but different type.
  private static final Comparator<AttributeKey<?>> KEY_COMPARATOR_FOR_CONSTRUCTION = new Comparator<AttributeKey<?>>() {
    @Override
    public int compare(AttributeKey<?> o1, AttributeKey<?> o2) {
      return o1.getKey().compareTo(o2.getKey());
    }
  };

  static final Attributes EMPTY = Attributes.builder().build();

  private final ImmutableKeyValuePairs<AttributeKey<?>, Object> immutableKeyValuePairs;

  private ArrayBackedAttributes(Object[] data, Comparator<AttributeKey<?>> keyComparator) {
    immutableKeyValuePairs = new ImmutableKeyValuePairs<AttributeKey<?>, Object>(data, keyComparator);
  }

  @Override
  public AttributesBuilder toBuilder() {
    return new ArrayBackedAttributesBuilder(new ArrayList<Object>(immutableKeyValuePairs.data()));
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T get(AttributeKey<T> key) {
    return (T) immutableKeyValuePairs.get(key);
  }

  @Override
  public void forEach(BiConsumer<? super AttributeKey<?>, ? super Object> consumer) {
    immutableKeyValuePairs.forEach(consumer);
  }

  @Override
  public int size() {
    return immutableKeyValuePairs.size();
  }

  @Override
  public boolean isEmpty() {
    return immutableKeyValuePairs.isEmpty();
  }

  @Override
  public Map<AttributeKey<?>, Object> asMap() {
    return immutableKeyValuePairs.asMap();
  }

  static Attributes sortAndFilterToAttributes(Object... data) {
    // null out any empty keys or keys with null values
    // so they will then be removed by the sortAndFilter method.
    for (int i = 0; i < data.length; i += 2) {
      AttributeKey<?> key = (AttributeKey<?>) data[i];
      if (key != null && key.getKey().isEmpty()) {
        data[i] = null;
      }
    }
    return new ArrayBackedAttributes(data, KEY_COMPARATOR_FOR_CONSTRUCTION);
  }
}
