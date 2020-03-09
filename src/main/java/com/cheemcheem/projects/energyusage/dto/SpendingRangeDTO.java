package com.cheemcheem.projects.energyusage.dto;

import lombok.Data;
import lombok.NonNull;

@Data
public class SpendingRangeDTO {

  @NonNull
  private final String startDate;
  @NonNull
  private final String endDate;
  @NonNull
  private final String usage;
}
