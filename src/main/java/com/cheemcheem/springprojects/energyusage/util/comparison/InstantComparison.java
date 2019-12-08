package com.cheemcheem.springprojects.energyusage.util.comparison;

import java.time.Instant;

public class InstantComparison {

  public static boolean isBeforeOrEqual(Instant a, Instant b) {
    return a.isBefore(b) || a.equals(b);
  }

  public static boolean isAfterOrEqual(Instant a, Instant b) {
    return a.isAfter(b) || a.equals(b);
  }
}
