package com.cheemcheem.springprojects.energyusage.service;

import com.cheemcheem.springprojects.energyusage.model.SpendingRange;
import com.cheemcheem.springprojects.energyusage.repository.EnergyReadingRepository;
import com.cheemcheem.springprojects.energyusage.repository.SpendingRangeRepository;
import com.cheemcheem.springprojects.energyusage.util.comparison.InstantComparison;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.function.BiFunction;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Performs calculations to determine spending between given dates.
 *
 * Uses {@link SpendingRangeRepository} for its spending ranges between dates. Uses {@link
 * EnergyReadingRepository} for its knowledge of earliest and latest dates.
 *
 * @see SpendingRangeRepository
 * @see EnergyReadingRepository
 */
@Service
@RequiredArgsConstructor
public class CalculatorService {

  private final Logger logger = LoggerFactory.getLogger(CalculatorService.class);

  @NonNull
  private final SpendingRangeRepository spendingRangeRepository;

  @NonNull
  private final EnergyReadingRepository energyReadingRepository;

  SpendingRange generateForAllReadings() {
    logger.info("Get all spending.");
    return getSpending(energyReadingRepository.earliest(), energyReadingRepository.latest());
  }

  SpendingRange generateForReadingsAfter(Date startDate) {
    logger.info("Get spending from '" + startDate + "'.");
    return getSpending(startDate, energyReadingRepository.latest());
  }

  SpendingRange generateForReadingsUntil(Date endDate) {
    logger.info("Get spending to '" + endDate + "'.");
    return getSpending(energyReadingRepository.earliest(), endDate);
  }

  SpendingRange generateForReadingsBetween(Date startDate, Date endDate) {
    logger.info("Get spending from '" + startDate + "' to '" + endDate + "'.");
    return getSpending(startDate, endDate);
  }

  List<SpendingRange> getAverageSpending(int dayGap) {
    return getAverageSpending(energyReadingRepository.earliest(), energyReadingRepository.latest(),
        dayGap);

  }

  List<SpendingRange> getAverageSpending(Date startDate, Date endDate, int dayGap) {
    logger.info("Get average spending from '" + startDate + "' to '" + endDate + "' over '" + dayGap
        + "' day periods.");

    BiFunction<Date, Date, Long> daysBetween =
        (start, end) -> ChronoUnit.DAYS.between(start.toInstant(), end.toInstant());

    // Exceptional case: date range too small for gap
    if (daysBetween.apply(startDate, endDate) < dayGap) {
      return List.of();
    }

    var averageSpendingList = new ArrayList<SpendingRange>();
    var lastEndDate = startDate;
    var calendar = Calendar.getInstance();

    // Loop while there are day gap days between the last end date and the overall end date
    while (daysBetween.apply(lastEndDate, endDate) >= dayGap) {

      // Use calendar to get next end date (last end date + day gap)
      calendar.setTime(lastEndDate);
      calendar.add(Calendar.DAY_OF_MONTH, dayGap);
      var newEndDate = calendar.getTime();

      // Get total spending between two dates that are day gap days apart
      var totalSpending = getSpending(lastEndDate, newEndDate);

      // Average the total spending between those dates by dividing by day gap
      var averageReading = totalSpending.getUsage()
          .divide(BigDecimal.valueOf(dayGap), RoundingMode.HALF_UP);

      averageSpendingList.add(
          new SpendingRange(
              lastEndDate,
              newEndDate,
              averageReading
          )
      );

      lastEndDate = newEndDate;
    }

    return averageSpendingList;

  }

  List<SpendingRange> getAverageSpendingDaily() {
    return getAverageSpendingDaily(energyReadingRepository.earliest(),
        energyReadingRepository.latest());
  }

  List<SpendingRange> getAverageSpendingDaily(Date startDate, Date endDate) {
    var calendar = Calendar.getInstance();

    calendar.setTime(startDate);
    calendar.set(Calendar.HOUR, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    startDate = calendar.getTime();

    calendar.setTime(endDate);
    calendar.set(Calendar.HOUR, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    endDate = calendar.getTime();

    return getAverageSpending(startDate, endDate, 1);
  }

  private SpendingRange getSpending(Date startDate, Date endDate) {
    // instant representations
    var requestStartInstant = startDate.toInstant();
    var requestEndInstant = endDate.toInstant();

    // get sublist of spending ranges
    var allSpendingRanges = new ArrayList<>(spendingRangeRepository.getSpendingRanges());
    allSpendingRanges.sort(Comparator.comparing(SpendingRange::getStartDate));
    var sublist = getSpendingRanges(startDate, endDate, allSpendingRanges);
    logger.debug("Sublist of spending ranges: " + sublist);

    // handle first case
    var first = sublist.get(0);

    double amountOfFirst = getAmountOfFirst(first, requestStartInstant, requestEndInstant);
    logger.debug("Amount of first: " + amountOfFirst);

    var firstSpending = first.getUsage().multiply(BigDecimal.valueOf(amountOfFirst));
    var totalSpending = firstSpending;
    logger.debug("First spending: " + firstSpending);

    if (sublist.size() == 1) {
      return new SpendingRange(startDate, endDate, totalSpending);
    }

    // handle cases where sublist is not size 1
    var middleSpending = getMiddleSpending(sublist);
    logger.debug("Middle spending: " + middleSpending);

    var last = sublist.get(sublist.size() - 1);

    double amountOfLast = getAmountOfLast(requestStartInstant, requestEndInstant, last);
    logger.debug("Amount of last: " + amountOfLast);

    var lastSpending = last.getUsage().multiply(BigDecimal.valueOf(amountOfLast));
    logger.debug("Last spending: " + lastSpending);

    totalSpending = firstSpending.add(middleSpending).add(lastSpending);
    logger.debug("Total spending: " + totalSpending);

    return new SpendingRange(startDate, endDate, totalSpending);
  }

  private List<SpendingRange> getSpendingRanges(Date startDate, Date endDate,
      ArrayList<SpendingRange> spendingRanges) {
    var indices = new HashSet<Integer>();

    for (int i = 0; i < spendingRanges.size(); i++) {
      var spendingRange = spendingRanges.get(i);
      var endAfterStart = spendingRange.getEndDate().compareTo(startDate) >= 0;
      var startBeforeEnd = spendingRange.getStartDate().compareTo(endDate) <= 0;
      if (endAfterStart && startBeforeEnd) {
        indices.add(i);
      }
    }

    var firstIndex = indices.stream().mapToInt(i -> i).min().orElseThrow();
    var lastIndex = indices.stream().mapToInt(i -> i).max().orElseThrow();

    return spendingRanges.subList(firstIndex, lastIndex + 1);
  }

  private BigDecimal getMiddleSpending(List<SpendingRange> sublist) {
    // Middle spending range(s)
    var middleSpending = BigDecimal.ZERO;
    for (int i = 1; i < sublist.size() - 1; i++) {
      middleSpending = middleSpending.add(sublist.get(i).getUsage());
    }
    return middleSpending;
  }

  private double getAmountOfLast(Instant requestStartInstant, Instant requestEndInstant,
      SpendingRange last) {
    // Will not end up as 0 as long as last.end is after last.start
    long portionLast = 0;

    var lastStartInstant = last.getStartDate().toInstant();
    var lastEndInstant = last.getEndDate().toInstant();
    var totalLast = last.getEndDate().getTime() - last.getStartDate().getTime();

    if (InstantComparison.isBeforeOrEqual(requestStartInstant, lastStartInstant)
        && InstantComparison.isBeforeOrEqual(requestEndInstant, lastEndInstant)) {
      // request starts before last range and ends within last range
      portionLast = requestEndInstant.toEpochMilli() - last.getStartDate().getTime();
    } else if (InstantComparison.isBeforeOrEqual(requestStartInstant, lastStartInstant)
        && InstantComparison.isBeforeOrEqual(lastEndInstant, requestEndInstant)) {
      // last range is fully within request
      portionLast = totalLast;
    }

    return ((double) portionLast) / ((double) totalLast);
  }

  private double getAmountOfFirst(SpendingRange first, Instant requestStartInstant,
      Instant requestEndInstant) {
    // Will not end up as 0 as long as first.end is after first.start
    long portionFirst = 0;

    var firstStartInstant = first.getStartDate().toInstant();
    var firstEndInstant = first.getEndDate().toInstant();

    var totalFirst = first.getEndDate().getTime() - first.getStartDate().getTime();

    if (InstantComparison.isBeforeOrEqual(firstStartInstant, requestStartInstant)
        && InstantComparison.isBeforeOrEqual(requestEndInstant, firstEndInstant)) {
      // Request is fully inside first range
      portionFirst = requestEndInstant.toEpochMilli() - requestStartInstant.toEpochMilli();

    } else if (InstantComparison.isBeforeOrEqual(firstStartInstant, requestStartInstant)
        && InstantComparison.isBeforeOrEqual(firstEndInstant, requestEndInstant)) {
      // Request starts after first range starts and ends after first range ends
      portionFirst = first.getEndDate().getTime() - requestStartInstant.toEpochMilli();

    } else if (InstantComparison.isBeforeOrEqual(requestStartInstant, firstStartInstant)
        && InstantComparison.isBeforeOrEqual(requestEndInstant, firstEndInstant)) {
      // Request starts before first range and ends within first range
      portionFirst = requestEndInstant.toEpochMilli() - first.getStartDate().getTime();

    } else if (InstantComparison.isBeforeOrEqual(requestStartInstant, firstStartInstant)
        && InstantComparison.isBeforeOrEqual(firstEndInstant, requestEndInstant)) {
      // Request starts before first and ends after first
      portionFirst = totalFirst;
    }

    return ((double) portionFirst) / ((double) totalFirst);
  }

}
