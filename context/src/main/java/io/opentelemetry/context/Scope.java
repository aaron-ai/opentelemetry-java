/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.context;

import io.opentelemetry.context.ThreadLocalContextStorage.NoopScope;

public abstract class Scope  {

  /**
   * Returns a {@link Scope} that does nothing. Represents attaching a {@link Context} when it is
   * already attached.
   */
  public static Scope noop() {
    return NoopScope.INSTANCE;
  }

  public abstract void close();
}
