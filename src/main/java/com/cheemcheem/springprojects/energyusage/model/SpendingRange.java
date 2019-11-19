package com.cheemcheem.springprojects.energyusage.model;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import lombok.NonNull;

@Data
public class SpendingRange {

  @NonNull
  private final Date startDate;
  @NonNull
  private final Date endDate;
  @NonNull
  private final BigDecimal usage;
}
