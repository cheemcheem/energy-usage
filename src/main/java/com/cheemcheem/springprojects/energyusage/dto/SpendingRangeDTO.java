package com.cheemcheem.springprojects.energyusage.dto;

import lombok.Data;

@Data
public class SpendingRangeDTO {

  private final String startDate;
  private final String endDate;
  private final String usage;
}
