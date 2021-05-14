/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.sdk.metrics;

import io.opentelemetry.api.metrics.LongUpDownSumObserver;
import io.opentelemetry.api.metrics.LongUpDownSumObserverBuilder;
import io.opentelemetry.sdk.metrics.common.BiFunction;
import io.opentelemetry.sdk.metrics.common.InstrumentDescriptor;
import io.opentelemetry.sdk.metrics.common.InstrumentType;
import io.opentelemetry.sdk.metrics.common.InstrumentValueType;

final class LongUpDownSumObserverSdk extends AbstractAsynchronousInstrument
    implements LongUpDownSumObserver {

  LongUpDownSumObserverSdk(
      InstrumentDescriptor descriptor, AsynchronousInstrumentAccumulator accumulator) {
    super(descriptor, accumulator);
  }

  static final class Builder
      extends AbstractLongAsynchronousInstrumentBuilder<LongUpDownSumObserverSdk.Builder>
      implements LongUpDownSumObserverBuilder {

    Builder(
        String name,
        MeterProviderSharedState meterProviderSharedState,
        MeterSharedState meterSharedState) {
      super(
          name,
          InstrumentType.UP_DOWN_SUM_OBSERVER,
          InstrumentValueType.LONG,
          meterProviderSharedState,
          meterSharedState);
    }

    @Override
    Builder getThis() {
      return this;
    }

    @Override
    public LongUpDownSumObserverSdk build() {
      return buildInstrument(
          new BiFunction<InstrumentDescriptor, AsynchronousInstrumentAccumulator, LongUpDownSumObserverSdk>() {
            @Override
            public LongUpDownSumObserverSdk apply(InstrumentDescriptor instrumentDescriptor,
                AsynchronousInstrumentAccumulator asynchronousInstrumentAccumulator) {
              return new LongUpDownSumObserverSdk(instrumentDescriptor, asynchronousInstrumentAccumulator);
            }
          });
    }
  }
}
