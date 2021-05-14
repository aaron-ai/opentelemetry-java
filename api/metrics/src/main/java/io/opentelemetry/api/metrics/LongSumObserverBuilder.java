/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.api.metrics;


import io.opentelemetry.api.metrics.common.Consumer;

/** Builder class for {@link LongSumObserver}. */
public interface LongSumObserverBuilder
    extends AsynchronousInstrumentBuilder<AsynchronousInstrument.LongResult> {
  @Override
  LongSumObserverBuilder setDescription(String description);

  @Override
  LongSumObserverBuilder setUnit(String unit);

  @Override
  LongSumObserverBuilder setUpdater(Consumer<AsynchronousInstrument.LongResult> updater);

  @Override
  LongSumObserver build();
}
