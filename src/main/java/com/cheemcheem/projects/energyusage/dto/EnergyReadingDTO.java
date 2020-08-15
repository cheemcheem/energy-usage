package com.cheemcheem.projects.energyusage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnergyReadingDTO {

  private String date;

  private String reading;

}
