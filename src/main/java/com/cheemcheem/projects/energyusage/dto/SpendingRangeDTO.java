package com.cheemcheem.projects.energyusage.dto;

import lombok.Data;
import lombok.NonNull;

@Data
public class SpendingRangeDTO {

  @NonNull
  private final String startDateISO;
  @NonNull
  private final String endDateISO;
  @NonNull
  private final String usage;
}
