package com.cheemcheem.springprojects.energyusage.repository;

import com.cheemcheem.springprojects.energyusage.model.EnergyReading;
import java.util.List;
import lombok.Data;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

@Data
@Repository
public class EnergyReadingRepository {

  @NonNull
  private List<EnergyReading> readings;
}
