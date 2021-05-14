/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.api.metrics;


import io.opentelemetry.api.metrics.common.Consumer;

/** Builder class for {@link DoubleUpDownSumObserver}. */
public interface DoubleUpDownSumObserverBuilder
    extends AsynchronousInstrumentBuilder<AsynchronousInstrument.DoubleResult> {
  @Override
  DoubleUpDownSumObserverBuilder setDescription(String description);

  @Override
  DoubleUpDownSumObserverBuilder setUnit(String unit);

  @Override
  DoubleUpDownSumObserverBuilder setUpdater(Consumer<AsynchronousInstrument.DoubleResult> updater);

  @Override
  DoubleUpDownSumObserver build();
}
