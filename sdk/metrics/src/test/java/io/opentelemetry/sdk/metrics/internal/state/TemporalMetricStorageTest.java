/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.sdk.metrics.internal.state;

import static io.opentelemetry.sdk.testing.assertj.metrics.MetricAssertions.assertThat;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.sdk.common.InstrumentationLibraryInfo;
import io.opentelemetry.sdk.metrics.common.InstrumentDescriptor;
import io.opentelemetry.sdk.metrics.common.InstrumentType;
import io.opentelemetry.sdk.metrics.common.InstrumentValueType;
import io.opentelemetry.sdk.metrics.data.AggregationTemporality;
import io.opentelemetry.sdk.metrics.exemplar.ExemplarReservoir;
import io.opentelemetry.sdk.metrics.internal.aggregator.Aggregator;
import io.opentelemetry.sdk.metrics.internal.aggregator.AggregatorFactory;
import io.opentelemetry.sdk.metrics.internal.aggregator.DoubleAccumulation;
import io.opentelemetry.sdk.metrics.internal.descriptor.MetricDescriptor;
import io.opentelemetry.sdk.metrics.internal.export.CollectionHandle;
import io.opentelemetry.sdk.resources.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TemporalMetricStorageTest {
  private static final InstrumentDescriptor DESCRIPTOR =
      InstrumentDescriptor.create(
          "name", "description", "unit", InstrumentType.COUNTER, InstrumentValueType.DOUBLE);
  private static final InstrumentDescriptor ASYNC_DESCRIPTOR =
      InstrumentDescriptor.create(
          "name", "description", "unit", InstrumentType.OBSERVABLE_SUM, InstrumentValueType.DOUBLE);
  private static final MetricDescriptor METRIC_DESCRIPTOR =
      MetricDescriptor.create("name", "description", "unit");
  private static final Aggregator<DoubleAccumulation> CUMULATIVE_SUM =
      AggregatorFactory.sum(AggregationTemporality.CUMULATIVE)
          .create(
              Resource.empty(),
              InstrumentationLibraryInfo.create("test", "1.0"),
              DESCRIPTOR,
              METRIC_DESCRIPTOR,
              ExemplarReservoir::noSamples);
  private static final Aggregator<DoubleAccumulation> DELTA_SUM =
      AggregatorFactory.sum(AggregationTemporality.DELTA)
          .create(
              Resource.empty(),
              InstrumentationLibraryInfo.create("test", "1.0"),
              DESCRIPTOR,
              METRIC_DESCRIPTOR,
              ExemplarReservoir::noSamples);

  private static final Aggregator<DoubleAccumulation> ASYNC_CUMULATIVE_SUM =
      AggregatorFactory.sum(AggregationTemporality.CUMULATIVE)
          .create(
              Resource.empty(),
              InstrumentationLibraryInfo.create("test", "1.0"),
              ASYNC_DESCRIPTOR,
              METRIC_DESCRIPTOR,
              ExemplarReservoir::noSamples);

  private static final Aggregator<DoubleAccumulation> ASYNC_DELTA_SUM =
      AggregatorFactory.sum(AggregationTemporality.DELTA)
          .create(
              Resource.empty(),
              InstrumentationLibraryInfo.create("test", "1.0"),
              ASYNC_DESCRIPTOR,
              METRIC_DESCRIPTOR,
              ExemplarReservoir::noSamples);

  private CollectionHandle collector1;
  private CollectionHandle collector2;
  private Set<CollectionHandle> allCollectors;

  @BeforeEach
  void setup() {
    Supplier<CollectionHandle> supplier = CollectionHandle.createSupplier();
    collector1 = supplier.get();
    collector2 = supplier.get();
    allCollectors = CollectionHandle.mutableSet();
    allCollectors.add(collector1);
    allCollectors.add(collector2);
  }

  private static Map<Attributes, DoubleAccumulation> createMeasurement(double value) {
    Map<Attributes, DoubleAccumulation> measurement = new HashMap<>();
    measurement.put(Attributes.empty(), DoubleAccumulation.create(value));
    return measurement;
  }

  @Test
  void synchronousCumulative_joinsWithLastMeasurementForCumulative() {
    TemporalMetricStorage<DoubleAccumulation> storage =
        new TemporalMetricStorage<>(CUMULATIVE_SUM, /* isSynchronous= */ true);
    // Send in new measurement at time 10 for collector 1
    assertThat(storage.buildMetricFor(collector1, createMeasurement(3), 0, 10))
        .hasDoubleSum()
        .isCumulative()
        .points()
        .isNotEmpty()
        .satisfiesExactly(
            point -> assertThat(point).hasStartEpochNanos(0).hasEpochNanos(10).hasValue(3));
    // Send in new measurement at time 30 for collector 1
    assertThat(storage.buildMetricFor(collector1, createMeasurement(3), 0, 30))
        .hasDoubleSum()
        .isCumulative()
        .points()
        .isNotEmpty()
        .satisfiesExactly(
            point -> assertThat(point).hasStartEpochNanos(0).hasEpochNanos(30).hasValue(6));
    // Send in new measurement at time 40 for collector 2
    assertThat(storage.buildMetricFor(collector2, createMeasurement(4), 0, 60))
        .hasDoubleSum()
        .isCumulative()
        .points()
        .isNotEmpty()
        .satisfiesExactly(
            point -> assertThat(point).hasStartEpochNanos(0).hasEpochNanos(60).hasValue(4));
    // Send in new measurement at time 35 for collector 1
    assertThat(storage.buildMetricFor(collector1, createMeasurement(2), 0, 35))
        .hasDoubleSum()
        .isCumulative()
        .points()
        .isNotEmpty()
        .satisfiesExactly(
            point -> assertThat(point).hasStartEpochNanos(0).hasEpochNanos(35).hasValue(8));
  }

  @Test
  void synchronousDelta_useLastTimestamp() {
    TemporalMetricStorage<DoubleAccumulation> storage =
        new TemporalMetricStorage<>(DELTA_SUM, /* isSynchronous= */ true);
    // Send in new measurement at time 10 for collector 1
    assertThat(storage.buildMetricFor(collector1, createMeasurement(3), 0, 10))
        .hasDoubleSum()
        .isDelta()
        .points()
        .isNotEmpty()
        .satisfiesExactly(
            point -> assertThat(point).hasStartEpochNanos(0).hasEpochNanos(10).hasValue(3));
    // Send in new measurement at time 30 for collector 1
    assertThat(storage.buildMetricFor(collector1, createMeasurement(3), 0, 30))
        .hasDoubleSum()
        .isDelta()
        .points()
        .isNotEmpty()
        .satisfiesExactly(
            point -> assertThat(point).hasStartEpochNanos(10).hasEpochNanos(30).hasValue(3));
    // Send in new measurement at time 40 for collector 2
    assertThat(storage.buildMetricFor(collector2, createMeasurement(4), 0, 60))
        .hasDoubleSum()
        .isDelta()
        .points()
        .isNotEmpty()
        .satisfiesExactly(
            point -> assertThat(point).hasStartEpochNanos(0).hasEpochNanos(60).hasValue(4));
    // Send in new measurement at time 35 for collector 1
    assertThat(storage.buildMetricFor(collector1, createMeasurement(2), 0, 35))
        .hasDoubleSum()
        .isDelta()
        .points()
        .isNotEmpty()
        .satisfiesExactly(
            point -> assertThat(point).hasStartEpochNanos(30).hasEpochNanos(35).hasValue(2));
  }

  @Test
  void asynchronousCumulative_doesNotJoin() {
    TemporalMetricStorage<DoubleAccumulation> storage =
        new TemporalMetricStorage<>(ASYNC_CUMULATIVE_SUM, /* isSynchronous= */ false);
    // Send in new measurement at time 10 for collector 1
    assertThat(storage.buildMetricFor(collector1, createMeasurement(3), 0, 10))
        .hasDoubleSum()
        .isCumulative()
        .points()
        .isNotEmpty()
        .satisfiesExactly(
            point -> assertThat(point).hasStartEpochNanos(0).hasEpochNanos(10).hasValue(3));
    // Send in new measurement at time 30 for collector 1
    assertThat(storage.buildMetricFor(collector1, createMeasurement(3), 0, 30))
        .hasDoubleSum()
        .isCumulative()
        .points()
        .isNotEmpty()
        .satisfiesExactly(
            point -> assertThat(point).hasStartEpochNanos(0).hasEpochNanos(30).hasValue(3));
    // Send in new measurement at time 40 for collector 2
    assertThat(storage.buildMetricFor(collector2, createMeasurement(4), 0, 60))
        .hasDoubleSum()
        .isCumulative()
        .points()
        .isNotEmpty()
        .satisfiesExactly(
            point -> assertThat(point).hasStartEpochNanos(0).hasEpochNanos(60).hasValue(4));
    // Send in new measurement at time 35 for collector 1
    assertThat(storage.buildMetricFor(collector1, createMeasurement(2), 0, 35))
        .hasDoubleSum()
        .isCumulative()
        .points()
        .isNotEmpty()
        .satisfiesExactly(
            point -> assertThat(point).hasStartEpochNanos(0).hasEpochNanos(35).hasValue(2));
  }

  @Test
  void asynchronousDelta_diffsLastTimestamp() {
    TemporalMetricStorage<DoubleAccumulation> storage =
        new TemporalMetricStorage<>(ASYNC_DELTA_SUM, /* isSynchronous= */ false);
    // Send in new measurement at time 10 for collector 1
    assertThat(storage.buildMetricFor(collector1, createMeasurement(3), 0, 10))
        .hasDoubleSum()
        .isDelta()
        .points()
        .isNotEmpty()
        .satisfiesExactly(
            point -> assertThat(point).hasStartEpochNanos(0).hasEpochNanos(10).hasValue(3));
    // Send in new measurement at time 30 for collector 1
    assertThat(storage.buildMetricFor(collector1, createMeasurement(3), 0, 30))
        .hasDoubleSum()
        .isDelta()
        .points()
        .isNotEmpty()
        .satisfiesExactly(
            point -> assertThat(point).hasStartEpochNanos(10).hasEpochNanos(30).hasValue(0));
    // Send in new measurement at time 40 for collector 2
    assertThat(storage.buildMetricFor(collector2, createMeasurement(4), 0, 60))
        .hasDoubleSum()
        .isDelta()
        .points()
        .isNotEmpty()
        .satisfiesExactly(
            point -> assertThat(point).hasStartEpochNanos(0).hasEpochNanos(60).hasValue(4));
    // Send in new measurement at time 35 for collector 1
    assertThat(storage.buildMetricFor(collector1, createMeasurement(2), 0, 35))
        .hasDoubleSum()
        .isDelta()
        .points()
        .isNotEmpty()
        .satisfiesExactly(
            point -> assertThat(point).hasStartEpochNanos(30).hasEpochNanos(35).hasValue(-1));
  }
}