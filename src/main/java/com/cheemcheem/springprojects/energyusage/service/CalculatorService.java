package com.cheemcheem.springprojects.energyusage.service;

import com.cheemcheem.springprojects.energyusage.model.SpendingRange;
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
 * Uses {@link SpendingRangeRepository} for its spending ranges between dates.
 *
 * @see SpendingRangeRepository
 */
@Service
@RequiredArgsConstructor
public class CalculatorService {

  private final Logger logger = LoggerFactory.getLogger(CalculatorService.class);

  @NonNull
  private final SpendingRangeRepository spendingRangeRepository;

  private static void resetToStartOfDay(Calendar calendar) {
    calendar.set(Calendar.AM_PM, Calendar.AM);
    calendar.set(Calendar.HOUR, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
  }

  private static BigDecimal lastSpending(Instant requestStartInstant, Instant requestEndInstant,
      SpendingRange last) {

    double portionOfLast = getPortionOfLast(requestStartInstant, requestEndInstant, last);

    return last.getUsage().multiply(BigDecimal.valueOf(portionOfLast));
  }

  private static BigDecimal firstSpending(Instant requestStartInstant, Instant requestEndInstant,
      SpendingRange first) {
    double portionOfFirst = getPortionOfFirst(first, requestStartInstant, requestEndInstant);

    return first.getUsage().multiply(BigDecimal.valueOf(portionOfFirst));
  }

  private static BigDecimal middleSpending(List<SpendingRange> sublist) {
    // Middle spending range(s)
    var middleSpending = BigDecimal.ZERO;
    for (int i = 1; i < sublist.size() - 1; i++) {
      middleSpending = middleSpending.add(sublist.get(i).getUsage());
    }
    return middleSpending;
  }

  private static double getPortionOfLast(Instant requestStartInstant, Instant requestEndInstant,
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

  private static double getPortionOfFirst(SpendingRange first, Instant requestStartInstant,
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

  SpendingRange calculateAllSpending() {
    logger.info("Get all spending.");
    return getSpending(spendingRangeRepository.earliest(), spendingRangeRepository.latest());
  }

  SpendingRange calculateSpendingAfterDate(Date startDate) {
    logger.info("Get spending from '" + startDate + "'.");
    return getSpending(startDate, spendingRangeRepository.latest());
  }

  SpendingRange calculateSpendingUntilDate(Date endDate) {
    logger.info("Get spending to '" + endDate + "'.");
    return getSpending(spendingRangeRepository.earliest(), endDate);
  }

  SpendingRange calculateSpendingBetweenDates(Date startDate, Date endDate) {
    logger.info("Get spending from '" + startDate + "' to '" + endDate + "'.");
    return getSpending(startDate, endDate);
  }

  List<SpendingRange> getAverageDailySpending() {
    return getAverageDailySpending(spendingRangeRepository.earliest(),
        spendingRangeRepository.latest());
  }

  List<SpendingRange> getAverageSpending(int dayGap) {
    return getAverageSpending(spendingRangeRepository.earliest(), spendingRangeRepository.latest(),
        dayGap);

  }

  List<SpendingRange> getAverageDailySpending(Date startDate, Date endDate) {
    var calendar = Calendar.getInstance();

    calendar.setTime(startDate);
    resetToStartOfDay(calendar);
    startDate = calendar.getTime();

    calendar.setTime(endDate);
    resetToStartOfDay(calendar);
    endDate = calendar.getTime();

    return getAverageSpending(startDate, endDate, 1);
  }

  List<SpendingRange> getAverageWeeklySpending() {
    return getAverageWeeklySpending(spendingRangeRepository.earliest(),
        spendingRangeRepository.latest());
  }

  List<SpendingRange> getAverageWeeklySpending(Date startDate, Date endDate) {
    var calendar = Calendar.getInstance();

    calendar.setTime(startDate);
    resetToStartOfDay(calendar);
    startDate = calendar.getTime();

    calendar.setTime(endDate);
    resetToStartOfDay(calendar);
    endDate = calendar.getTime();

    return getAverageSpending(startDate, endDate, 7);
  }

  List<SpendingRange> getAverageMonthlySpending() {
    return getAverageMonthlySpending(spendingRangeRepository.earliest(),
        spendingRangeRepository.latest());
  }

  List<SpendingRange> getAverageMonthlySpending(Date startDate, Date endDate) {
    var startCalendar = Calendar.getInstance();

    startCalendar.setTime(startDate);
    resetToStartOfDay(startCalendar);
    startCalendar.set(Calendar.DAY_OF_MONTH, 1);
    startDate = startCalendar.getTime();

    var endCalendar = Calendar.getInstance();
    endCalendar.setTime(endDate);
    resetToStartOfDay(endCalendar);
    endCalendar.set(Calendar.MONTH, endCalendar.getActualMaximum(Calendar.MONTH));
    endDate = endCalendar.getTime();

    logger.info("Get average monthly spending from '" + startDate + "' to '" + endDate + ".");

    spendingRangeRepository.getSpendingRanges().stream()
        .sorted(Comparator.comparing(SpendingRange::getStartDate))
        .map(SpendingRange::toString)
        .forEach(logger::debug);

    BiFunction<Date, Date, Long> daysBetween =
        (start, end) -> ChronoUnit.DAYS.between(start.toInstant(), end.toInstant());

    // Exceptional case: date range too small for gap
    if (endCalendar.get(Calendar.MONTH) == startCalendar.get(Calendar.MONTH)
        && endCalendar.get(Calendar.YEAR) == startCalendar.get(Calendar.YEAR)) {
      var spendingRange = getSpending(startDate, endDate);
      var averageSpending = spendingRange.getUsage()
          .divide(BigDecimal.valueOf(endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)),
              RoundingMode.HALF_UP);
      return List.of(new SpendingRange(startDate, endDate, averageSpending));
    }

    var averageSpendingList = new ArrayList<SpendingRange>();
    var lastEndDate = startDate;
    var lastEndCalendar = Calendar.getInstance();
    lastEndCalendar.setTime(lastEndDate);

    // Loop while there is at least a month between the last end date and the overall end date
    while (!(lastEndCalendar.get(Calendar.MONTH) == endCalendar.get(Calendar.MONTH)
        && lastEndCalendar.get(Calendar.YEAR) == endCalendar.get(Calendar.YEAR))) {

      // Use calendar to get next end date (last end date + day gap)
      lastEndCalendar.setTime(lastEndDate);
      lastEndCalendar.add(Calendar.MONTH, 1);
      var newEndDate = lastEndCalendar.getTime();

      // Get total spending between two dates that are day gap days apart
      var totalSpending = getSpending(lastEndDate, newEndDate);

      // Average the total spending between those dates by dividing by day gap

      var diff = daysBetween.apply(lastEndDate, newEndDate);
      var averageReading = totalSpending.getUsage()
          .divide(BigDecimal.valueOf(diff), RoundingMode.HALF_UP);

      averageSpendingList.add(
          new SpendingRange(
              lastEndDate,
              newEndDate,
              averageReading
          )
      );

      logger.debug(averageSpendingList.toString());

      lastEndDate = newEndDate;
    }

    return averageSpendingList;


  }

  List<SpendingRange> getAverageSpending(Date startDate, Date endDate, int dayGap) {
    logger.info("Get average spending from '" + startDate + "' to '" + endDate + "' over '" + dayGap
        + "' day periods.");
    spendingRangeRepository.getSpendingRanges().stream()
        .sorted(Comparator.comparing(SpendingRange::getStartDate))
        .map(SpendingRange::toString)
        .forEach(logger::debug);

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

  private SpendingRange getSpending(Date startDate, Date endDate) {
    // instant representations
    var requestStartInstant = startDate.toInstant();
    var requestEndInstant = endDate.toInstant();

    // get sublist of spending ranges
    var sublist = new ArrayList<>(spendingRangeRepository.getBetweenDates(startDate, endDate));
    sublist.sort(Comparator.comparing(SpendingRange::getStartDate));
    logger.debug(
        "Sublist of spending ranges between " + startDate + " and " + endDate + ": " + sublist);

    // handle first case
    var firstSpending = firstSpending(requestStartInstant, requestEndInstant, sublist.get(0));
    logger.debug("First spending: " + firstSpending);

    if (sublist.size() == 1) {
      return new SpendingRange(startDate, endDate, firstSpending);
    }

    // handle cases where sublist is not size 1
    var middleSpending = middleSpending(sublist);
    logger.debug("Middle spending: " + middleSpending);

    // handle final case
    var lastSpending = lastSpending(requestStartInstant, requestEndInstant,
        sublist.get(sublist.size() - 1));
    logger.debug("Last spending: " + lastSpending);

    var totalSpending = firstSpending.add(middleSpending).add(lastSpending);
    logger.debug("Total spending: " + totalSpending);

    return new SpendingRange(startDate, endDate, totalSpending);
  }

}
