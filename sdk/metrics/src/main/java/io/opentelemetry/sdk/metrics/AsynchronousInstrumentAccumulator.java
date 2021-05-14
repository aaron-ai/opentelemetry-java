/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.sdk.metrics;

import io.opentelemetry.api.metrics.AsynchronousInstrument;
import io.opentelemetry.api.metrics.common.Consumer;
import io.opentelemetry.api.metrics.common.Labels;
import io.opentelemetry.context.Context;
import io.opentelemetry.sdk.metrics.aggregator.Aggregator;
import io.opentelemetry.sdk.metrics.common.InstrumentDescriptor;
import io.opentelemetry.sdk.metrics.data.MetricData;
import io.opentelemetry.sdk.metrics.processor.LabelsProcessor;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Nullable;

final class AsynchronousInstrumentAccumulator extends AbstractAccumulator {
  private final ReentrantLock collectLock = new ReentrantLock();
  private final InstrumentProcessor<?> instrumentProcessor;
  private final Runnable metricUpdater;

  static <T> AsynchronousInstrumentAccumulator doubleAsynchronousAccumulator(
      MeterProviderSharedState meterProviderSharedState,
      MeterSharedState meterSharedState,
      InstrumentDescriptor descriptor,
      @Nullable final Consumer<AsynchronousInstrument.DoubleResult> metricUpdater) {
    final Aggregator<T> aggregator =
        getAggregator(meterProviderSharedState, meterSharedState, descriptor);
    final InstrumentProcessor<T> instrumentProcessor =
        new InstrumentProcessor<T>(aggregator, meterProviderSharedState.getStartEpochNanos());
    // TODO: Decide what to do with null updater.
    if (metricUpdater == null) {
      return new AsynchronousInstrumentAccumulator(instrumentProcessor, new Runnable() {
        @Override
        public void run() {
        }
      });
    }

    final LabelsProcessor labelsProcessor =
        getLabelsProcessor(meterProviderSharedState, meterSharedState, descriptor);
    final AsynchronousInstrument.DoubleResult result = new AsynchronousInstrument.DoubleResult() {
      @Override
      public void observe(double value, Labels labels) {
        instrumentProcessor.batch(
            labelsProcessor.onLabelsBound(Context.current(), labels),
            aggregator.accumulateDouble(value));
      }
    };

    return new AsynchronousInstrumentAccumulator(instrumentProcessor, new Runnable() {
      @Override
      public void run() {
        metricUpdater.accept(result);
      }
    });
  }

  static <T> AsynchronousInstrumentAccumulator longAsynchronousAccumulator(
      MeterProviderSharedState meterProviderSharedState,
      MeterSharedState meterSharedState,
      InstrumentDescriptor descriptor,
      @Nullable final Consumer<AsynchronousInstrument.LongResult> metricUpdater) {
    final Aggregator<T> aggregator =
        getAggregator(meterProviderSharedState, meterSharedState, descriptor);
    final InstrumentProcessor<T> instrumentProcessor =
        new InstrumentProcessor<T>(aggregator, meterProviderSharedState.getStartEpochNanos());
    // TODO: Decide what to do with null updater.
    if (metricUpdater == null) {
      return new AsynchronousInstrumentAccumulator(instrumentProcessor, new Runnable() {
        @Override
        public void run() {
        }
      });
    }

    final LabelsProcessor labelsProcessor =
        getLabelsProcessor(meterProviderSharedState, meterSharedState, descriptor);
    final AsynchronousInstrument.LongResult result = new AsynchronousInstrument.LongResult() {
      @Override
      public void observe(long value, Labels labels) {
        instrumentProcessor.batch(
            labelsProcessor.onLabelsBound(Context.current(), labels),
            aggregator.accumulateLong(value));
      };
    };
    return new AsynchronousInstrumentAccumulator(
        instrumentProcessor, new Runnable() {
      @Override
      public void run() {
        metricUpdater.accept(result);
      }
    });
  }

  private AsynchronousInstrumentAccumulator(
      InstrumentProcessor<?> instrumentProcessor, Runnable metricUpdater) {
    this.instrumentProcessor = instrumentProcessor;
    this.metricUpdater = metricUpdater;
  }

  @Override
  List<MetricData> collectAll(long epochNanos) {
    collectLock.lock();
    try {
      metricUpdater.run();
      return instrumentProcessor.completeCollectionCycle(epochNanos);
    } finally {
      collectLock.unlock();
    }
  }
}
