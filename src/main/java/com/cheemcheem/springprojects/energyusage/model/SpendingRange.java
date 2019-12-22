package com.cheemcheem.springprojects.energyusage.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NonNull;

@Data
public class SpendingRange {

  @NonNull
  private final LocalDateTime startDate;
  @NonNull
  private final LocalDateTime endDate;
  @NonNull
  private final BigDecimal usage;
}
