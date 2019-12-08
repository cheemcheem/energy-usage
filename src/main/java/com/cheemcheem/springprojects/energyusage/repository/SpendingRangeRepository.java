package com.cheemcheem.springprojects.energyusage.repository;

import com.cheemcheem.springprojects.energyusage.exception.EmptyRepositoryException;
import com.cheemcheem.springprojects.energyusage.model.SpendingRange;
import com.cheemcheem.springprojects.energyusage.util.comparison.InstantComparison;
import java.util.Collection;
import java.util.Date;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

@Data
@Repository
public class SpendingRangeRepository {

  @NonNull
  private Collection<SpendingRange> spendingRanges;

  public Collection<SpendingRange> getBetweenDates(Date startDate, Date endDate) {
    var startInstant = startDate.toInstant();
    var endInstant = endDate.toInstant();
    return spendingRanges.stream()
        .filter(s -> InstantComparison.isAfterOrEqual(s.getEndDate().toInstant(), startInstant))
        .filter(s -> InstantComparison.isBeforeOrEqual(s.getStartDate().toInstant(), endInstant))
        .collect(Collectors.toList());
  }

  public Date earliest() {
    return this.spendingRanges.stream()
        .map(SpendingRange::getStartDate)
        .min(Date::compareTo)
        .orElseThrow(throwBecauseNothingInStream());
  }

  public Date latest() {
    return this.spendingRanges.stream()
        .map(SpendingRange::getEndDate)
        .max(Date::compareTo)
        .orElseThrow(throwBecauseNothingInStream());
  }

  private Supplier<EmptyRepositoryException> throwBecauseNothingInStream() {
    return () -> new EmptyRepositoryException(
        "Cannot calculate spending over range because no readings found in that range."
    );
  }

}
