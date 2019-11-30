package com.cheemcheem.springprojects.energyusage.service;

import com.cheemcheem.springprojects.energyusage.exception.EmptyRepositoryException;
import com.cheemcheem.springprojects.energyusage.model.EnergyReading;
import com.cheemcheem.springprojects.energyusage.model.SpendingRange;
import com.cheemcheem.springprojects.energyusage.repository.EnergyReadingRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CalculatorService {

  private final Logger logger = LoggerFactory.getLogger(CalculatorService.class);

  @NonNull
  private final EnergyReadingRepository energyReadingRepository;

  public Collection<EnergyReading> filterForRange(Collection<EnergyReading> readings,
      Date startDate,
      Date endDate) {
    return readings.stream()
        .filter(readingWithinDates(startDate, endDate))
        .collect(Collectors.toCollection(TreeSet::new));
  }

  private Predicate<EnergyReading> readingWithinDates(Date startDate, Date endDate) {
    return r -> (r.getDate().after(startDate) || r.getDate().equals(startDate))
        && (r.getDate().before(endDate) || r.getDate().equals(endDate));
  }

  SpendingRange generateForAllReadings() throws EmptyRepositoryException {
    return generateForReadings(energyReadingRepository.getReadings());
  }

  SpendingRange generateForReadingsAfter(Date startDate) throws EmptyRepositoryException {
    @NonNull var readings = filterForRange(
        energyReadingRepository.getReadings(),
        startDate,
        Date.from(Instant.now())
    );
    return generateForReadings(readings);
  }

  SpendingRange generateForReadingsUntil(Date endDate) throws EmptyRepositoryException {
    @NonNull var readings = filterForRange(
        energyReadingRepository.getReadings(),
        Date.from(Instant.EPOCH),
        endDate
    );
    return generateForReadings(readings);
  }

  SpendingRange generateForReadingsBetween(Date startDate, Date endDate)
      throws EmptyRepositoryException {
    @NonNull var readings = filterForRange(
        energyReadingRepository.getReadings(),
        startDate,
        endDate
    );
    return generateForReadings(readings);
  }

  @NonNull List<SpendingRange> getAverageSpending(Date startDate, Date endDate, int dayGap) {
    BiFunction<Date, Date, Long> daysBetween =
        (start, end) -> ChronoUnit.DAYS.between(start.toInstant(), end.toInstant());

    // Exceptional case: date range too small for gap
    if (daysBetween.apply(startDate, endDate) < dayGap) {
      return List.of();
    }

    @NonNull var allReadings = filterForRange(
        energyReadingRepository.getReadings(),
        startDate,
        endDate
    );

    @NonNull var averageSpendings = new ArrayList<SpendingRange>();

    var lastDate = startDate;
    var calendar = Calendar.getInstance();

    while (daysBetween.apply(lastDate, endDate) >= dayGap) {
      calendar.setTime(lastDate);
      calendar.add(Calendar.DAY_OF_MONTH, dayGap);
      var newEndDate = calendar.getTime();

      @NonNull var subReadings = filterForRange(
          allReadings,
          lastDate,
          newEndDate
      );

      BigDecimal averageReading;

      try {
        var averageSpending = generateForReadings(subReadings);
        averageReading = averageSpending.getUsage()
            .divide(BigDecimal.valueOf(dayGap), RoundingMode.HALF_UP);
      } catch (EmptyRepositoryException e) {
        logger.warn(e.getMessage());
        averageReading = BigDecimal.ZERO;
      }
      averageSpendings.add(
          new SpendingRange(
              lastDate,
              newEndDate,
              averageReading
          )
      );

      lastDate = newEndDate;
    }

    return averageSpendings;

  }

  public SpendingRange generateForReadings(Collection<EnergyReading> readings)
      throws EmptyRepositoryException {

    var startDate = readings.stream()
        .map(EnergyReading::getDate)
        .min(Date::compareTo)
        .orElseThrow(throwBecauseNothingInStream());

    var endDate = readings.stream()
        .map(EnergyReading::getDate)
        .max(Date::compareTo)
        .orElseThrow(throwBecauseNothingInStream());

    var sortedUsages = readings.stream()
        .sorted()
        .map(EnergyReading::getReading)
        .collect(Collectors.toList());

    var totalUsage = BigDecimal.ZERO;
    var lastUsage = BigDecimal.ZERO;

    for (BigDecimal thisUsage : sortedUsages) {
      totalUsage = getNewUsage(totalUsage, lastUsage, thisUsage);
      lastUsage = thisUsage;
    }

    return new SpendingRange(startDate, endDate, totalUsage);
  }

  private BigDecimal getNewUsage(BigDecimal totalUsage, BigDecimal lastUsage,
      BigDecimal thisUsage) {
    // Handle negative usage values
    // Otherwise would have been much fewer lines
    if (thisUsage.compareTo(BigDecimal.ZERO) > 0) {
      // current +
      if (lastUsage.compareTo(BigDecimal.ZERO) > 0) {
        // earlier -
        if (lastUsage.compareTo(thisUsage) > 0) {
          totalUsage = totalUsage.add(lastUsage.subtract(thisUsage));
        }
      }
    }

    if (thisUsage.compareTo(BigDecimal.ZERO) < 0) {
      // current -
      if (lastUsage.compareTo(BigDecimal.ZERO) > 0) {
        // earlier +
        totalUsage = totalUsage.add(lastUsage.subtract(thisUsage));
      }

      if (lastUsage.compareTo(BigDecimal.ZERO) < 0) {
        // earlier -
        if (lastUsage.compareTo(thisUsage) > 0) {
          totalUsage = totalUsage.add(lastUsage.subtract(thisUsage));
        }
      }
    }
    return totalUsage;
  }

  private Supplier<EmptyRepositoryException> throwBecauseNothingInStream() {
    return () -> new EmptyRepositoryException(
        "Cannot calculate spending over range because no readings found in that range."
    );
  }

}
