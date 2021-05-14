/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.sdk.trace.data;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.sdk.common.InstrumentationLibraryInfo;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SpanLimits;
import java.util.List;
import javax.annotation.concurrent.Immutable;

/**
 * Immutable representation of all data collected by the {@link io.opentelemetry.api.trace.Span}
 * class.
 */
@Immutable
public abstract class SpanData {

  /** Returns the {@link SpanContext} of the Span. */
  public abstract SpanContext getSpanContext();

  /**
   * Gets the trace id for this span.
   *
   * @return the trace id.
   */
  public String getTraceId() {
    return getSpanContext().getTraceId();
  }

  /**
   * Gets the span id for this span.
   *
   * @return the span id.
   */
  public String getSpanId() {
    return getSpanContext().getSpanId();
  }

  /**
   * Returns the parent {@link SpanContext}. If the span is a root span, the {@link SpanContext}
   * returned will be invalid.
   */
  public abstract SpanContext getParentSpanContext();

  /**
   * Returns the parent {@code SpanId}. If the {@code Span} is a root {@code Span}, the SpanId
   * returned will be invalid.
   *
   * @return the parent {@code SpanId} or an invalid SpanId if this is a root {@code Span}.
   */
  public String getParentSpanId() {
    return getParentSpanContext().getSpanId();
  }

  /**
   * Returns the resource of this {@code Span}.
   *
   * @return the resource of this {@code Span}.
   */
  public abstract Resource getResource();

  /**
   * Returns the instrumentation library specified when creating the tracer which produced this
   * {@code Span}.
   *
   * @return an instance of {@link InstrumentationLibraryInfo}
   */
  public abstract InstrumentationLibraryInfo getInstrumentationLibraryInfo();

  /**
   * Returns the name of this {@code Span}.
   *
   * @return the name of this {@code Span}.
   */
  public abstract String getName();

  /**
   * Returns the kind of this {@code Span}.
   *
   * @return the kind of this {@code Span}.
   */
  public abstract SpanKind getKind();

  /**
   * Returns the start epoch timestamp in nanos of this {@code Span}.
   *
   * @return the start epoch timestamp in nanos of this {@code Span}.
   */
  public abstract long getStartEpochNanos();

  /**
   * Returns the attributes recorded for this {@code Span}.
   *
   * @return the attributes recorded for this {@code Span}.
   */
  public abstract Attributes getAttributes();

  /**
   * Returns the timed events recorded for this {@code Span}.
   *
   * @return the timed events recorded for this {@code Span}.
   */
  public abstract List<EventData> getEvents();

  /**
   * Returns links recorded for this {@code Span}.
   *
   * @return links recorded for this {@code Span}.
   */
  public abstract List<LinkData> getLinks();

  /**
   * Returns the {@code Status}.
   *
   * @return the {@code Status}.
   */
  public abstract StatusData getStatus();

  /**
   * Returns the end epoch timestamp in nanos of this {@code Span}.
   *
   * @return the end epoch timestamp in nanos of this {@code Span}.
   */
  public abstract long getEndEpochNanos();

  /**
   * Returns whether this Span has already been ended.
   *
   * @return {@code true} if the span has already been ended, {@code false} if not.
   */
  public abstract boolean hasEnded();

  /**
   * The total number of {@link EventData} events that were recorded on this span. This number may
   * be larger than the number of events that are attached to this span, if the total number
   * recorded was greater than the configured maximum value. See: {@link
   * SpanLimits#getMaxNumberOfEvents()}
   *
   * @return The total number of events recorded on this span.
   */
  public abstract int getTotalRecordedEvents();

  /**
   * The total number of {@link LinkData} links that were recorded on this span. This number may be
   * larger than the number of links that are attached to this span, if the total number recorded
   * was greater than the configured maximum value. See: {@link SpanLimits#getMaxNumberOfLinks()}
   *
   * @return The total number of links recorded on this span.
   */
  public abstract int getTotalRecordedLinks();

  /**
   * The total number of attributes that were recorded on this span. This number may be larger than
   * the number of attributes that are attached to this span, if the total number recorded was
   * greater than the configured maximum value. See: {@link SpanLimits#getMaxNumberOfAttributes()}
   *
   * @return The total number of attributes on this span.
   */
  public abstract int getTotalAttributeCount();
}
