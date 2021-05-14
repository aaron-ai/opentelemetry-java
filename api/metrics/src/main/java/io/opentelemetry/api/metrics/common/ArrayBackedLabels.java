/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.api.metrics.common;

import io.opentelemetry.api.internal.BiConsumer;
import io.opentelemetry.api.internal.ImmutableKeyValuePairs;
import java.util.Map;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
final class ArrayBackedLabels extends Labels {
  private static final Labels EMPTY = Labels.builder().build();

  public static Labels empty() {
    return EMPTY;
  }

  @Override
  public void forEach(BiConsumer<? super String, ? super String> consumer) {
    immutableKeyValuePairs.forEach(consumer);
  }

  @Override
  public int size() {
    return immutableKeyValuePairs.size();
  }

  @Nullable
  @Override
  public String get(String key) {
    return immutableKeyValuePairs.get(key);
  }

  @Override
  public boolean isEmpty() {
    return immutableKeyValuePairs.isEmpty();
  }

  @Override
  public Map<String, String> asMap() {
    return immutableKeyValuePairs.asMap();
  }

  private final ImmutableKeyValuePairs<String, String> immutableKeyValuePairs;

  private ArrayBackedLabels(Object[] data) {
    immutableKeyValuePairs = new ImmutableKeyValuePairs<String, String>(data);
  }

  static Labels sortAndFilterToLabels(Object... data) {
    return new ArrayBackedLabels(data);
  }

  @Override
  public LabelsBuilder toBuilder() {
    return new ArrayBackedLabelsBuilder(immutableKeyValuePairs.data());
  }
}
