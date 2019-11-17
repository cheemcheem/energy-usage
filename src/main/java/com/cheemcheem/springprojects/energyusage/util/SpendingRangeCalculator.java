package com.cheemcheem.springprojects.energyusage.util;

import com.cheemcheem.springprojects.energyusage.exception.EmptyRepositoryException;
import com.cheemcheem.springprojects.energyusage.model.EnergyReading;
import com.cheemcheem.springprojects.energyusage.model.SpendingRange;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SpendingRangeCalculator {

  public static Collection<EnergyReading> filterForRange(Collection<EnergyReading> readings,
      Date startDate,
      Date endDate) {
    return readings.stream()
        .filter(readingWithinDates(startDate, endDate))
        .collect(Collectors.toCollection(TreeSet::new));
  }


  private static Predicate<EnergyReading> readingWithinDates(Date startDate, Date endDate) {
    return r -> (r.getDate().after(startDate) || r.getDate().equals(startDate))
        && (r.getDate().before(endDate) || r.getDate().equals(endDate));
  }

  public static SpendingRange generateForReadings(Collection<EnergyReading> readings)
      throws EmptyRepositoryException {
    var startDate = readings.stream()
        .map(EnergyReading::getDate)
        .min(Date::compareTo)
        .orElseThrow(throwBecauseNothingInStream("startDate, readings = " + readings));

    var endDate = readings.stream()
        .map(EnergyReading::getDate)
        .max(Date::compareTo)
        .orElseThrow(throwBecauseNothingInStream("endDate = "));

    var sortedUsages = readings.stream()
        .sorted()
        .map(EnergyReading::getReading)
        .collect(Collectors.toList());

    var usage = BigDecimal.ZERO;
    var earlierUsage = BigDecimal.ZERO;
    for (BigDecimal laterUsage : sortedUsages) {
      if (earlierUsage.compareTo(laterUsage) < 0) {
        earlierUsage = laterUsage;
        continue;
      }
      usage = usage.add(earlierUsage.subtract(laterUsage));
      earlierUsage = laterUsage;
    }

    return new SpendingRange(startDate, endDate, usage);
  }

  private static Supplier<EmptyRepositoryException> throwBecauseNothingInStream(String source) {
    return () -> new EmptyRepositoryException(
        "Cannot calculate spending over range because no readings found in that range. Source: '"
            + source + "'"
    );
  }


}
