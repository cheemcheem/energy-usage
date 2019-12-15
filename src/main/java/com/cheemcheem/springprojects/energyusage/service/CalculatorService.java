package com.cheemcheem.springprojects.energyusage.service;

import com.cheemcheem.springprojects.energyusage.exception.EmptyRepositoryException;
import com.cheemcheem.springprojects.energyusage.exception.InternalStateException;
import com.cheemcheem.springprojects.energyusage.exception.InvalidDateException;
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
    try {
      return calculateSpending(spendingRangeRepository.earliest(),
          spendingRangeRepository.latest());
    } catch (EmptyRepositoryException e) {
      logger.warn(e.getMessage());
      return new SpendingRange(Date.from(Instant.EPOCH), Date.from(Instant.now()), BigDecimal.ZERO);
    } catch (InvalidDateException e) {
      logger.error("Should never have InvalidDateReception thrown here.", e);
      throw new InternalStateException("Should never have InvalidDateReception thrown here.", e);
    }
  }

  SpendingRange calculateSpendingAfterDate(Date startDate) {
    logger.info("Get spending from '" + startDate + "'.");
    Date latest;

    // handle usual case of repository being empty
    try {
      latest = spendingRangeRepository.latest();
    } catch (EmptyRepositoryException e) {
      logger.warn(e.getMessage());
      return new SpendingRange(startDate, Date.from(Instant.now()), BigDecimal.ZERO);
    }

    // handle case where start date is after latest repository date
    try {
      return calculateSpending(startDate, latest);
    } catch (InvalidDateException ignored) {
      logger.warn("No spending from start date '" + startDate
          + "' as that is after latest repository date '" + latest + "'. ");
      return new SpendingRange(startDate, latest, BigDecimal.ZERO);
    }
  }

  SpendingRange calculateSpendingUntilDate(Date endDate) {
    logger.info("Get spending to '" + endDate + "'.");
    Date earliest;

    // handle usual case of repository being empty
    try {
      earliest = spendingRangeRepository.earliest();
    } catch (EmptyRepositoryException e) {
      logger.warn(e.getMessage());
      return new SpendingRange(Date.from(Instant.EPOCH), endDate, BigDecimal.ZERO);
    }

    // handle case where end date is before earliest repository date
    try {
      return calculateSpending(earliest, endDate);
    } catch (InvalidDateException ignored) {
      logger.warn("No spending before end date '" + endDate
          + "' as that is before earliest repository date '" + earliest + "'. ");
      return new SpendingRange(earliest, endDate, BigDecimal.ZERO);
    }

  }

  SpendingRange calculateSpendingBetweenDates(Date startDate, Date endDate)
      throws InvalidDateException {
    logger.info("Get spending from '" + startDate + "' to '" + endDate + "'.");
    return calculateSpending(startDate, endDate);
  }

  List<SpendingRange> calculateAverageDailySpending() {
    try {
      return calculateAverageDailySpending(spendingRangeRepository.earliest(),
          spendingRangeRepository.latest());
    } catch (EmptyRepositoryException e) {
      logger.warn(e.getMessage());
      return List.of();
    } catch (InvalidDateException e) {
      logger.error("Should never have InvalidDateReception thrown here.", e);
      throw new InternalStateException("Should never have InvalidDateReception thrown here.", e);
    }
  }

  List<SpendingRange> calculateAverageSpending(int dayGap) {
    try {
      return calculateAverageSpending(spendingRangeRepository.earliest(),
          spendingRangeRepository.latest(),
          dayGap);
    } catch (EmptyRepositoryException e) {
      logger.warn(e.getMessage());
      return List.of();
    } catch (InvalidDateException e) {
      logger.error("Should never have InvalidDateReception thrown here.", e);
      throw new InternalStateException("Should never have InvalidDateReception thrown here.", e);
    }

  }

  List<SpendingRange> calculateAverageDailySpending(Date startDate, Date endDate)
      throws InvalidDateException {
    logger.info("Get average daily spending from '" + startDate + "' to '" + endDate + "'.");
    var calendar = Calendar.getInstance();

    calendar.setTime(startDate);
    resetToStartOfDay(calendar);
    startDate = calendar.getTime();

    calendar.setTime(endDate);
    resetToStartOfDay(calendar);
    endDate = calendar.getTime();

    return calculateAverageSpending(startDate, endDate, 1);
  }

  List<SpendingRange> calculateAverageWeeklySpending() {
    try {
      return calculateAverageWeeklySpending(spendingRangeRepository.earliest(),
          spendingRangeRepository.latest());
    } catch (EmptyRepositoryException e) {
      logger.warn(e.getMessage());
      return List.of();
    } catch (InvalidDateException e) {
      logger.error("Should never have InvalidDateReception thrown here.", e);
      throw new InternalStateException("Should never have InvalidDateReception thrown here.", e);
    }
  }

  List<SpendingRange> calculateAverageWeeklySpending(Date startDate, Date endDate)
      throws InvalidDateException {
    var calendar = Calendar.getInstance();

    calendar.setTime(startDate);
    resetToStartOfDay(calendar);
    startDate = calendar.getTime();

    calendar.setTime(endDate);
    resetToStartOfDay(calendar);
    endDate = calendar.getTime();

    return calculateAverageSpending(startDate, endDate, 7);
  }

  List<SpendingRange> calculateAverageMonthlySpending() {
    try {
      return calculateAverageMonthlySpending(spendingRangeRepository.earliest(),
          spendingRangeRepository.latest());
    } catch (EmptyRepositoryException e) {
      logger.warn(e.getMessage());
      return List.of();
    } catch (InvalidDateException e) {
      logger.error("Should never have InvalidDateReception thrown here.", e);
      throw new InternalStateException("Should never have InvalidDateReception thrown here.", e);
    }
  }

  List<SpendingRange> calculateAverageMonthlySpending(Date startDate, Date endDate)
      throws InvalidDateException {
    logger.info("Get average monthly spending from '" + startDate + "' to '" + endDate + ".");

    // instant representations
    var requestStartInstant = startDate.toInstant();
    var requestEndInstant = endDate.toInstant();

    if (requestStartInstant.isAfter(requestEndInstant)) {
      logger.warn("Start date '" + startDate + "' occurs after end date '" + endDate + "'.");
      throw InvalidDateException.wrongWayAround(startDate, endDate);
    }

    var startCalendar = Calendar.getInstance();

    startCalendar.setTime(startDate);
    resetToStartOfDay(startCalendar);
    startCalendar.set(Calendar.DAY_OF_MONTH, 1);
    startDate = startCalendar.getTime();

    var endCalendar = Calendar.getInstance();
    endCalendar.setTime(endDate);
    resetToStartOfDay(endCalendar);
    endCalendar.add(Calendar.MONTH, 1);
    endCalendar.set(Calendar.DAY_OF_MONTH, 1);
    endDate = endCalendar.getTime();

    spendingRangeRepository.getSpendingRanges().stream()
        .sorted(Comparator.comparing(SpendingRange::getStartDate))
        .map(SpendingRange::toString)
        .forEach(logger::debug);

    BiFunction<Date, Date, Long> daysBetween =
        (start, end) -> ChronoUnit.DAYS.between(start.toInstant(), end.toInstant());

    // Exceptional case: date range too small for gap
    if (endCalendar.get(Calendar.MONTH) == startCalendar.get(Calendar.MONTH)
        && endCalendar.get(Calendar.YEAR) == startCalendar.get(Calendar.YEAR)) {
      var spendingRange = calculateSpending(startDate, endDate);
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
      var totalSpending = calculateSpending(lastEndDate, newEndDate);

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

  List<SpendingRange> calculateAverageSpending(Date startDate, Date endDate, int dayGap)
      throws InvalidDateException {
    logger.info("Get average spending from '" + startDate + "' to '" + endDate + "' over '" + dayGap
        + "' day periods.");
    spendingRangeRepository.getSpendingRanges().stream()
        .sorted(Comparator.comparing(SpendingRange::getStartDate))
        .map(SpendingRange::toString)
        .forEach(logger::debug);

    // instant representations
    var requestStartInstant = startDate.toInstant();
    var requestEndInstant = endDate.toInstant();

    if (requestStartInstant.isAfter(requestEndInstant)) {
      logger.warn("Start date '" + startDate + "' occurs after end date '" + endDate + "'.");
      throw InvalidDateException.wrongWayAround(startDate, endDate);
    }

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
      var totalSpending = calculateSpending(lastEndDate, newEndDate);

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

    logger.debug("averageSpendingList = " + averageSpendingList);

    return averageSpendingList;

  }

  private SpendingRange calculateSpending(Date startDate, Date endDate)
      throws InvalidDateException {
    // instant representations
    var requestStartInstant = startDate.toInstant();
    var requestEndInstant = endDate.toInstant();

    if (requestStartInstant.isAfter(requestEndInstant)) {
      logger.warn("Start date '" + startDate + "' occurs after end date '" + endDate + "'.");
      throw InvalidDateException.wrongWayAround(startDate, endDate);
    }
    // get sublist of spending ranges
    var sublist = new ArrayList<>(spendingRangeRepository.getBetweenDates(startDate, endDate));
    sublist.sort(Comparator.comparing(SpendingRange::getStartDate));
    logger.debug(
        "Sublist of spending ranges between '" + startDate + "' and '" + endDate + "': " + sublist);

    if (sublist.size() == 0) {
      logger.debug("No spending ranges found.");
      return new SpendingRange(startDate, endDate, BigDecimal.ZERO);
    }

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
