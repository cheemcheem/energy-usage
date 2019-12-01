package com.cheemcheem.springprojects.energyusage.repository;

import com.cheemcheem.springprojects.energyusage.exception.EmptyRepositoryException;
import com.cheemcheem.springprojects.energyusage.model.EnergyReading;
import java.util.Collection;
import java.util.Date;
import java.util.function.Supplier;
import lombok.Data;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

@Data
@Repository
public class EnergyReadingRepository {

  @NonNull
  private Collection<EnergyReading> readings;

  public Date earliest() {
    return this.readings.stream()
        .map(EnergyReading::getDate)
        .min(Date::compareTo)
        .orElseThrow(throwBecauseNothingInStream());
  }

  public Date latest() {
    return this.readings.stream()
        .map(EnergyReading::getDate)
        .max(Date::compareTo)
        .orElseThrow(throwBecauseNothingInStream());
  }

  private Supplier<EmptyRepositoryException> throwBecauseNothingInStream() {
    return () -> new EmptyRepositoryException(
        "Cannot calculate spending over range because no readings found in that range."
    );
  }
}
