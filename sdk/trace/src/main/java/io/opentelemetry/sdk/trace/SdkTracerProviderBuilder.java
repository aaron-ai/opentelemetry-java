/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.sdk.trace;

import io.opentelemetry.sdk.common.Clock;
import io.opentelemetry.sdk.internal.SystemClock;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import java.util.ArrayList;
import java.util.List;

/** Builder of {@link SdkTracerProvider}. */
public final class SdkTracerProviderBuilder {
  private static final Sampler DEFAULT_SAMPLER = Sampler.parentBased(Sampler.alwaysOn());

  private final List<SpanProcessor> spanProcessors = new ArrayList<SpanProcessor>();

  private Clock clock = SystemClock.getInstance();
  private IdGenerator idsGenerator = IdGenerator.random();
  private Resource resource = Resource.getDefault();
  private Supplier<SpanLimits> spanLimitsSupplier = new Supplier<SpanLimits>() {
    @Override
    public SpanLimits get() {
      return SpanLimits.getDefault();
    }
  };
  private Sampler sampler = DEFAULT_SAMPLER;

  /**
   * Assign a {@link Clock}. {@link Clock} will be used each time a {@link
   * io.opentelemetry.api.trace.Span} is started, ended or any event is recorded.
   *
   * <p>The {@code clock} must be thread-safe and return immediately (no remote calls, as contention
   * free as possible).
   *
   * @param clock The clock to use for all temporal needs.
   * @return this
   */
  public SdkTracerProviderBuilder setClock(Clock clock) {
    if (clock == null) {
      throw new NullPointerException("clock");
    }
    this.clock = clock;
    return this;
  }

  /**
   * Assign an {@link IdGenerator}. {@link IdGenerator} will be used each time a {@link
   * io.opentelemetry.api.trace.Span} is started.
   *
   * <p>The {@code idGenerator} must be thread-safe and return immediately (no remote calls, as
   * contention free as possible).
   *
   * @param idGenerator A generator for trace and span ids.
   * @return this
   */
  public SdkTracerProviderBuilder setIdGenerator(IdGenerator idGenerator) {
    if (idGenerator == null) {
      throw new NullPointerException("idGenerator");
    }
    this.idsGenerator = idGenerator;
    return this;
  }

  /**
   * Assign a {@link Resource} to be attached to all Spans created by Tracers.
   *
   * @param resource A Resource implementation.
   * @return this
   */
  public SdkTracerProviderBuilder setResource(Resource resource) {
    if (resource == null) {
      throw new NullPointerException("resource");
    }
    this.resource = resource;
    return this;
  }

  /**
   * Assign an initial {@link SpanLimits} that should be used with this SDK.
   *
   * <p>This method is equivalent to calling {@link #setSpanLimits(Supplier)} like this {@code
   * #setSpanLimits(() -> spanLimits)}.
   *
   * @param spanLimits the limits that will be used for every {@link
   *     io.opentelemetry.api.trace.Span}.
   * @return this
   */
  public SdkTracerProviderBuilder setSpanLimits(final SpanLimits spanLimits) {
    if (spanLimits == null) {
      throw new NullPointerException("spanLimits");
    }
    this.spanLimitsSupplier = new Supplier<SpanLimits>() {
      @Override
      public SpanLimits get() {
        return spanLimits;
      }
    };
    return this;
  }

  /**
   * Assign a {@link Supplier} of {@link SpanLimits}. {@link SpanLimits} will be retrieved each time
   * a {@link io.opentelemetry.api.trace.Span} is started.
   *
   * <p>The {@code spanLimitsSupplier} must be thread-safe and return immediately (no remote calls,
   * as contention free as possible).
   *
   * @param spanLimitsSupplier the supplier that will be used to retrieve the {@link SpanLimits} for
   *     every {@link io.opentelemetry.api.trace.Span}.
   * @return this
   */
  public SdkTracerProviderBuilder setSpanLimits(Supplier<SpanLimits> spanLimitsSupplier) {
    if (spanLimitsSupplier == null) {
      throw new NullPointerException("spanLimitsSupplier");
    }
    this.spanLimitsSupplier = spanLimitsSupplier;
    return this;
  }

  /**
   * Assign a {@link Sampler} to use for sampling traces. {@link Sampler} will be called each time a
   * {@link io.opentelemetry.api.trace.Span} is started.
   *
   * <p>The {@code sampler} must be thread-safe and return immediately (no remote calls, as
   * contention free as possible).
   *
   * @param sampler the {@link Sampler} to use for sampling traces.
   * @return this
   */
  public SdkTracerProviderBuilder setSampler(Sampler sampler) {
    if (sampler == null) {
      throw new NullPointerException("sampler");
    }
    this.sampler = sampler;
    return this;
  }

  /**
   * Add a SpanProcessor to the span pipeline that will be built. {@link SpanProcessor} will be
   * called each time a {@link io.opentelemetry.api.trace.Span} is started or ended.
   *
   * <p>The {@code spanProcessor} must be thread-safe and return immediately (no remote calls, as
   * contention free as possible).
   *
   * @param spanProcessor the processor to be added to the processing pipeline.
   * @return this
   */
  public SdkTracerProviderBuilder addSpanProcessor(SpanProcessor spanProcessor) {
    spanProcessors.add(spanProcessor);
    return this;
  }

  /**
   * Create a new TraceSdkProvider instance.
   *
   * @return An initialized TraceSdkProvider.
   */
  public SdkTracerProvider build() {
    return new SdkTracerProvider(
        clock, idsGenerator, resource, spanLimitsSupplier, sampler, spanProcessors);
  }

  SdkTracerProviderBuilder() {}
}
