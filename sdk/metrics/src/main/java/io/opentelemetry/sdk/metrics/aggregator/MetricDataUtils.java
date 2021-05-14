/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.sdk.metrics.aggregator;

import io.opentelemetry.api.metrics.common.Labels;
import io.opentelemetry.sdk.metrics.data.DoubleHistogramPointData;
import io.opentelemetry.sdk.metrics.data.DoublePointData;
import io.opentelemetry.sdk.metrics.data.DoubleSummaryPointData;
import io.opentelemetry.sdk.metrics.data.LongPointData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

final class MetricDataUtils {
  private MetricDataUtils() {}

  static List<LongPointData> toLongPointList(
      Map<Labels, Long> accumulationMap, long startEpochNanos, long epochNanos) {
    List<LongPointData> points = new ArrayList<LongPointData>(accumulationMap.size());
    for (Map.Entry<Labels, Long> entry : accumulationMap.entrySet()) {
      final Labels labels = entry.getKey();
      final Long accumulation = entry.getValue();
      points.add(LongPointData.create(startEpochNanos, epochNanos, labels, accumulation));
    }
    return points;
  }

  static List<DoublePointData> toDoublePointList(
      Map<Labels, Double> accumulationMap, long startEpochNanos, long epochNanos) {
    List<DoublePointData> points = new ArrayList<DoublePointData>(accumulationMap.size());

    for (Map.Entry<Labels, Double> entry : accumulationMap.entrySet()) {
      final Labels labels = entry.getKey();
      final Double accumulation = entry.getValue();
      points.add(DoublePointData.create(startEpochNanos, epochNanos, labels, accumulation));
    }
    return points;
  }

  static List<DoubleSummaryPointData> toDoubleSummaryPointList(
      Map<Labels, MinMaxSumCountAccumulation> accumulationMap,
      long startEpochNanos,
      long epochNanos) {
    List<DoubleSummaryPointData> points = new ArrayList<DoubleSummaryPointData>(accumulationMap.size());

    for (Map.Entry<Labels, MinMaxSumCountAccumulation> entry : accumulationMap
        .entrySet()) {
      final Labels labels = entry.getKey();
      final MinMaxSumCountAccumulation aggregator = entry.getValue();
      points.add(aggregator.toPoint(startEpochNanos, epochNanos, labels));
    }
    return points;
  }

  static List<DoubleHistogramPointData> toDoubleHistogramPointList(
      Map<Labels, HistogramAccumulation> accumulationMap,
      long startEpochNanos,
      long epochNanos,
      List<Double> boundaries) {
    List<DoubleHistogramPointData> points = new ArrayList<DoubleHistogramPointData>(accumulationMap.size());

    for (Map.Entry<Labels, HistogramAccumulation> entry : accumulationMap
        .entrySet()) {
      final Labels labels = entry.getKey();
      final HistogramAccumulation aggregator = entry.getValue();
      List<Long> counts = new ArrayList<Long>(aggregator.getCounts().length);
      for (long v : aggregator.getCounts()) {
        counts.add(v);
      }
      points.add(
          DoubleHistogramPointData.create(
              startEpochNanos, epochNanos, labels, aggregator.getSum(), boundaries, counts));
    }
    return points;
  }
}
