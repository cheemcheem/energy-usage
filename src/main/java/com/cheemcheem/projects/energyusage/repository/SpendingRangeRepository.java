package com.cheemcheem.projects.energyusage.repository;

import com.cheemcheem.projects.energyusage.exception.EmptyRepositoryException;
import com.cheemcheem.projects.energyusage.model.SpendingRange;
import java.time.LocalDateTime;
import java.util.Collection;
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

  public Collection<SpendingRange> getBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {

    return spendingRanges.stream()
        .filter(s -> !s.getEndDate().isBefore(startDate))
        .filter(s -> !s.getStartDate().isAfter(endDate))
        .collect(Collectors.toList());
  }

  public LocalDateTime earliest() throws EmptyRepositoryException {
    return this.spendingRanges.stream()
        .map(SpendingRange::getStartDate)
        .min(LocalDateTime::compareTo)
        .orElseThrow(throwBecauseNothingInStream());
  }

  public LocalDateTime latest() throws EmptyRepositoryException {
    return this.spendingRanges.stream()
        .map(SpendingRange::getEndDate)
        .max(LocalDateTime::compareTo)
        .orElseThrow(throwBecauseNothingInStream());
  }

  private Supplier<EmptyRepositoryException> throwBecauseNothingInStream() {
    return () -> new EmptyRepositoryException(
        "No readings in repository."
    );
  }

}
