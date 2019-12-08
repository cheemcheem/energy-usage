package com.cheemcheem.springprojects.energyusage.repository;

import com.cheemcheem.springprojects.energyusage.model.SpendingRange;
import com.cheemcheem.springprojects.energyusage.util.comparison.InstantComparison;
import java.util.Collection;
import java.util.Date;
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

}
