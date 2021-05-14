/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.sdk.trace;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;
import io.opentelemetry.sdk.common.CompletableResultCode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.concurrent.ThreadSafe;

/**
 * SpanProcessor is the interface {@code TracerSdk} uses to allow synchronous hooks for when a
 * {@code Span} is started or when a {@code Span} is ended.
 */
@ThreadSafe
public abstract class SpanProcessor {

  /**
   * Returns a {@link SpanProcessor} which simply delegates all processing to the {@code processors}
   * in order.
   */
  public static SpanProcessor composite(SpanProcessor... processors) {
    return composite(Arrays.asList(processors));
  }

  /**
   * Returns a {@link SpanProcessor} which simply delegates all processing to the {@code processors}
   * in order.
   */
  public static SpanProcessor composite(Iterable<SpanProcessor> processors) {
    List<SpanProcessor> processorsList = new ArrayList<SpanProcessor>();
    for (SpanProcessor processor : processors) {
      processorsList.add(processor);
    }
    if (processorsList.isEmpty()) {
      return NoopSpanProcessor.getInstance();
    }
    if (processorsList.size() == 1) {
      return processorsList.get(0);
    }
    return MultiSpanProcessor.create(processorsList);
  }

  /**
   * Called when a {@link io.opentelemetry.api.trace.Span} is started, if the {@link
   * Span#isRecording()} returns true.
   *
   * <p>This method is called synchronously on the execution thread, should not throw or block the
   * execution thread.
   *
   * @param parentContext the parent {@code Context} of the span that just started.
   * @param span the {@code ReadableSpan} that just started.
   */
  public abstract void onStart(Context parentContext, ReadWriteSpan span);

  /**
   * Returns {@code true} if this {@link SpanProcessor} requires start events.
   *
   * @return {@code true} if this {@link SpanProcessor} requires start events.
   */
  public abstract boolean isStartRequired();

  /**
   * Called when a {@link io.opentelemetry.api.trace.Span} is ended, if the {@link
   * Span#isRecording()} returns true.
   *
   * <p>This method is called synchronously on the execution thread, should not throw or block the
   * execution thread.
   *
   * @param span the {@code ReadableSpan} that just ended.
   */
  public abstract void onEnd(ReadableSpan span);

  /**
   * Returns {@code true} if this {@link SpanProcessor} requires end events.
   *
   * @return {@code true} if this {@link SpanProcessor} requires end events.
   */
  public abstract boolean isEndRequired();

  /**
   * Processes all span events that have not yet been processed and closes used resources.
   *
   * @return a {@link CompletableResultCode} which completes when shutdown is finished.
   */
  public CompletableResultCode shutdown() {
    return forceFlush();
  }

  /**
   * Processes all span events that have not yet been processed.
   *
   * @return a {@link CompletableResultCode} which completes when currently queued spans are
   *     finished processing.
   */
  public CompletableResultCode forceFlush() {
    return CompletableResultCode.ofSuccess();
  }

  /**
   * Closes this {@link SpanProcessor} after processing any remaining spans, releasing any
   * resources.
   */
  public void close() {
    shutdown().join(10, TimeUnit.SECONDS);
  }
}
