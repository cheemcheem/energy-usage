package com.cheemcheem.springprojects.energyusage.model;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@Data
public class SpendingRange {

  private final Date startDate;
  private final Date endDate;
  private final BigDecimal usage;
}
