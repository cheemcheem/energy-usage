package com.cheemcheem.springprojects.energyusage.util;

import com.cheemcheem.springprojects.energyusage.exception.EmptyRepositoryException;
import com.cheemcheem.springprojects.energyusage.model.EnergyReading;
import com.cheemcheem.springprojects.energyusage.model.SpendingRange;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SpendingRangeCalculator {

  public static List<EnergyReading> filterForRange(List<EnergyReading> readings, Date startDate,
      Date endDate) {
    return readings.stream()
        .filter(readingWithinDates(startDate, endDate))
        .collect(Collectors.toList());
  }

  private static Predicate<EnergyReading> readingWithinDates(Date startDate, Date endDate) {
    return r -> (r.getDate().after(startDate) || r.getDate().equals(startDate))
        && (r.getDate().before(endDate) || r.getDate().equals(endDate));
  }

  public static SpendingRange generateForReadings(List<EnergyReading> readings) {
    var startDate = readings.stream()
        .map(EnergyReading::getDate)
        .min(Date::compareTo)
        .orElseThrow(throwBecauseNothingInStream());

    var endDate = readings.stream()
        .map(EnergyReading::getDate)
        .max(Date::compareTo)
        .orElseThrow(throwBecauseNothingInStream());

    var usages = readings.stream()
        .sorted(sortDateAscending())
        .map(EnergyReading::getReading)
        .collect(Collectors.toList());

    var usage = BigDecimal.ZERO;
    var earlierUsage = BigDecimal.ZERO;
    for (BigDecimal laterUsage : usages) {
      if (earlierUsage.compareTo(laterUsage) < 0) {
        earlierUsage = laterUsage;
        continue;
      }
      usage = usage.add(earlierUsage.subtract(laterUsage));
      earlierUsage = laterUsage;
    }

    return new SpendingRange(startDate, endDate, usage);
  }

  private static Supplier<EmptyRepositoryException> throwBecauseNothingInStream() {
    return () -> new EmptyRepositoryException(
        "Cannot calculate spending over range because no readings found in that range."
    );
  }

  private static Comparator<EnergyReading> sortDateAscending() {
    return Comparator.comparing(EnergyReading::getDate, Date::compareTo);
  }

}
