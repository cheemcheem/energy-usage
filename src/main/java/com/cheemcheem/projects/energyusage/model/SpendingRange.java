package com.cheemcheem.projects.energyusage.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NonNull;

@Data
public class SpendingRange implements Comparable<SpendingRange> {

  @NonNull
  private final LocalDateTime startDate;
  @NonNull
  private final LocalDateTime endDate;
  @NonNull
  private final BigDecimal usage;

  @Override
  public int compareTo(SpendingRange o) {
    return getStartDate().compareTo(o.getStartDate());
  }

}
