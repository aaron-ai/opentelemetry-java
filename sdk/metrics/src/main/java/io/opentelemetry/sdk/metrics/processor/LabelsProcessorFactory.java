/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.sdk.metrics.processor;

import io.opentelemetry.sdk.common.InstrumentationLibraryInfo;
import io.opentelemetry.sdk.metrics.common.InstrumentDescriptor;
import io.opentelemetry.sdk.resources.Resource;

public abstract class LabelsProcessorFactory {
  public static LabelsProcessorFactory noop() {
    return new LabelsProcessorFactory() {
      @Override
      public LabelsProcessor create(Resource resource,
          InstrumentationLibraryInfo instrumentationLibraryInfo, InstrumentDescriptor descriptor) {
        return new NoopLabelsProcessor();
      }
    };
  }

  /**
   * Returns a new {@link LabelsProcessorFactory}.
   *
   * @return new {@link LabelsProcessorFactory}
   */
  public abstract LabelsProcessor create(
      Resource resource,
      InstrumentationLibraryInfo instrumentationLibraryInfo,
      InstrumentDescriptor descriptor);
}
