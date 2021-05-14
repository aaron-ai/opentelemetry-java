/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.sdk.metrics.view;

import com.google.auto.value.AutoValue;
import io.opentelemetry.sdk.metrics.common.InstrumentType;
import java.util.regex.Pattern;
import javax.annotation.concurrent.Immutable;

/**
 * Provides means for selecting one ore more {@link io.opentelemetry.api.metrics.Instrument}s. Used
 * for configuring aggregations for the specified instruments.
 */
@AutoValue
@Immutable
public abstract class InstrumentSelector {
  private static final Pattern MATCH_ALL = Pattern.compile(".*");

  /**
   * Returns a new {@link Builder} for {@link InstrumentSelector}.
   *
   * @return a new {@link Builder} for {@link InstrumentSelector}.
   */
  public static Builder builder() {
    return new AutoValue_InstrumentSelector.Builder().setInstrumentNamePattern(MATCH_ALL);
  }

  /**
   * Returns {@link InstrumentType} that should be selected. If null, then this specifier will not
   * be used.
   */
  public abstract InstrumentType getInstrumentType();

  /**
   * Returns the {@link Pattern} generated by the provided {@code regex} in the {@link Builder}, or
   * {@code Pattern.compile(".*")} if none was specified.
   */
  public abstract Pattern getInstrumentNamePattern();

  /** Builder for {@link InstrumentSelector} instances. */
  @AutoValue.Builder
  public abstract static class Builder {
    /** Sets a specifier for {@link InstrumentType}. */
    public abstract Builder setInstrumentType(InstrumentType instrumentType);

    abstract Builder setInstrumentNamePattern(Pattern instrumentNamePattern);

    /** Sets a specifier for selecting Instruments by name. */
    public final Builder setInstrumentNameRegex(String regex) {
      if (regex == null) {
        throw new NullPointerException("regex");
      }
      return setInstrumentNamePattern(Pattern.compile(regex));
    }

    /** Returns an InstrumentSelector instance with the content of this builder. */
    public abstract InstrumentSelector build();
  }
}
