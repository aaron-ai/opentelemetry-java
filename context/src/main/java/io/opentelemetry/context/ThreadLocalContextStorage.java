/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.context;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;

class ThreadLocalContextStorage extends ContextStorage {
  public static final ThreadLocalContextStorage INSTANCE = new ThreadLocalContextStorage();

  private static final Logger logger = Logger.getLogger(ThreadLocalContextStorage.class.getName());

  private static final ThreadLocal<Context> THREAD_LOCAL_STORAGE = new ThreadLocal<Context>();

  @Override
  public Scope attach(final Context toAttach) {
    if (toAttach == null) {
      // Null context not allowed so ignore it.
      return NoopScope.INSTANCE;
    }

    final Context beforeAttach = current();
    if (toAttach == beforeAttach) {
      return NoopScope.INSTANCE;
    }

    THREAD_LOCAL_STORAGE.set(toAttach);

    return new Scope() {
      @Override
      public void close() {
        if (current() != toAttach) {
          logger.log(
              Level.FINE,
              "Context in storage not the expected context, Scope.close was not called correctly");
        }
        THREAD_LOCAL_STORAGE.set(beforeAttach);
      }
    };
  }

  @Override
  @Nullable
  public Context current() {
    return THREAD_LOCAL_STORAGE.get();
  }

  static class NoopScope extends Scope {
    public static final NoopScope INSTANCE = new NoopScope();

    @Override
    public void close() {}
  }
}
