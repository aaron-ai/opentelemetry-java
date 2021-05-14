/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.sdk.trace;

import io.opentelemetry.api.trace.SpanId;
import io.opentelemetry.api.trace.TraceId;
import java.util.Random;

class RandomIdGenerator extends IdGenerator {
  public static final RandomIdGenerator INSTANCE = new RandomIdGenerator();

  private static final ThreadLocal<Random> threadLocalRandom =
      new ThreadLocal<Random>() {
        @Override
        protected Random initialValue() {
          return new Random();
        }
      };

  private static final long INVALID_ID = 0;

  @Override
  public String generateSpanId() {
    long id;
    do {
      id = threadLocalRandom.get().nextLong();
    } while (id == INVALID_ID);
    return SpanId.fromLong(id);
  }

  @Override
  public String generateTraceId() {
    long idHi;
    long idLo;
    do {
      Random random = threadLocalRandom.get();
      idHi = random.nextLong();
      idLo = random.nextLong();
    } while (idHi == INVALID_ID && idLo == INVALID_ID);
    return TraceId.fromLongs(idHi, idLo);
  }
}
