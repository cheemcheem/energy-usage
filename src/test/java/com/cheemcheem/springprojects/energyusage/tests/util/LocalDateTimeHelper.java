package com.cheemcheem.springprojects.energyusage.tests.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class LocalDateTimeHelper {

  public static final long DAY = 86400000L;
  public static final long HALF_DAY = DAY / 2;
  public static final long QUARTER_DAY = HALF_DAY / 2;

  public static LocalDateTime toLocalDateTime(long day) {
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(day), ZoneOffset.UTC.normalized());
  }
}
