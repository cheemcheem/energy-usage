package com.cheemcheem.projects.energyusage.service;

import com.cheemcheem.projects.energyusage.exception.EmptyRepositoryException;
import com.cheemcheem.projects.energyusage.exception.InternalStateException;
import com.cheemcheem.projects.energyusage.exception.InvalidDateException;
import com.cheemcheem.projects.energyusage.model.SpendingRange;
import com.cheemcheem.projects.energyusage.repository.SpendingRangeRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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

  private static BigDecimal lastSpending(LocalDateTime requestStart, LocalDateTime requestEnd,
      SpendingRange last) {

    double portionOfLast = getPortionOfLast(requestStart, requestEnd, last);

    return last.getUsage().multiply(BigDecimal.valueOf(portionOfLast));
  }

  private static BigDecimal firstSpending(LocalDateTime requestStartInstant,
      LocalDateTime requestEndInstant,
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

  private static double getPortionOfLast(LocalDateTime requestStart, LocalDateTime requestEnd,
      SpendingRange last) {
    // Will not end up as 0 as long as last.end is after last.start
    long portionLast = 0;

    var totalLast = last.getStartDate().until(last.getEndDate(), ChronoUnit.MILLIS);

    if (!requestStart.isAfter(last.getStartDate()) && !requestEnd.isAfter(last.getEndDate())) {
      // request starts before last range and ends within last range
      portionLast = last.getStartDate().until(requestEnd, ChronoUnit.MILLIS);
    } else if (!requestStart.isAfter(last.getStartDate())
        && !last.getEndDate().isAfter(requestEnd)) {
      // last range is fully within request
      portionLast = totalLast;
    }

    return ((double) portionLast) / ((double) totalLast);
  }

  private static double getPortionOfFirst(SpendingRange first, LocalDateTime requestStart,
      LocalDateTime requestEnd) {
    // Will not end up as 0 as long as first.end is after first.start
    long portionFirst = 0;

    var totalFirst = first.getStartDate().until(first.getEndDate(), ChronoUnit.MILLIS);

    if (!first.getStartDate().isAfter(requestStart) && !requestEnd.isAfter(first.getEndDate())) {
      // Request is fully inside first range
      portionFirst = requestStart.until(requestEnd, ChronoUnit.MILLIS);

    } else if (!first.getStartDate().isAfter(requestStart)
        && !first.getEndDate().isAfter(requestEnd)) {
      // Request starts after first range starts and ends after first range ends
      portionFirst = requestStart.until(first.getEndDate(), ChronoUnit.MILLIS);

    } else if (!requestStart.isAfter(first.getStartDate())
        && !requestEnd.isAfter(first.getEndDate())) {
      // Request starts before first range and ends within first range
      portionFirst = first.getStartDate().until(requestEnd, ChronoUnit.MILLIS);

    } else if (!requestStart.isAfter(first.getStartDate())
        && !first.getEndDate().isAfter(requestEnd)) {
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
      return new SpendingRange(LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC),
          LocalDateTime.now(), BigDecimal.ZERO);
    } catch (InvalidDateException e) {
      logger.error("Should never have InvalidDateReception thrown here.", e);
      throw new InternalStateException("Should never have InvalidDateReception thrown here.", e);
    }
  }

  SpendingRange calculateSpendingAfterDate(LocalDateTime startDate) {
    logger.info("Get spending from '" + startDate + "'.");
    LocalDateTime latest;

    // handle usual case of repository being empty
    try {
      latest = spendingRangeRepository.latest();
    } catch (EmptyRepositoryException e) {
      logger.warn(e.getMessage());
      return new SpendingRange(startDate, LocalDateTime.now(), BigDecimal.ZERO);
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

  SpendingRange calculateSpendingUntilDate(LocalDateTime endDate) {
    logger.info("Get spending to '" + endDate + "'.");
    LocalDateTime earliest;

    // handle usual case of repository being empty
    try {
      earliest = spendingRangeRepository.earliest();
    } catch (EmptyRepositoryException e) {
      logger.warn(e.getMessage());
      return new SpendingRange(LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC), endDate,
          BigDecimal.ZERO);
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

  SpendingRange calculateSpendingBetweenDates(LocalDateTime startDate, LocalDateTime endDate)
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

  List<SpendingRange> calculateAverageDailySpending(LocalDateTime startDate, LocalDateTime endDate)
      throws InvalidDateException {
    logger.info("Get average daily spending from '" + startDate + "' to '" + endDate + "'.");
    startDate = startDate.withNano(0).withSecond(0).withMinute(0).withHour(0);
    endDate = endDate.withNano(0).withSecond(0).withMinute(0).withHour(0);

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

  List<SpendingRange> calculateAverageWeeklySpending(LocalDateTime startDate, LocalDateTime endDate)
      throws InvalidDateException {
    startDate = startDate.withNano(0).withSecond(0).withMinute(0).withHour(0);
    endDate = endDate.withNano(0).withSecond(0).withMinute(0).withHour(0);

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

  List<SpendingRange> calculateAverageMonthlySpending(LocalDateTime startDate,
      LocalDateTime endDate)
      throws InvalidDateException {
    logger.info("Get average monthly spending from '" + startDate + "' to '" + endDate + ".");

    // Exceptional case: dates in wrong order
    if (startDate.isAfter(endDate)) {
      logger.warn("Start date '" + startDate + "' occurs after end date '" + endDate + "'.");
      throw InvalidDateException.wrongWayAround(startDate, endDate);
    }

    // Exceptional case: date range too small for gap
    if (ChronoUnit.MONTHS.between(startDate, endDate) == 0
        && startDate.getMonthValue() == endDate.getMonthValue()) {
      var spendingRange = calculateSpending(startDate, endDate);
      var averageSpending = spendingRange.getUsage()
          .divide(BigDecimal.valueOf((endDate.getDayOfMonth() - startDate.getDayOfMonth())),
              RoundingMode.HALF_UP);
      return List.of(new SpendingRange(startDate, endDate, averageSpending));
    }

    // Normal case
    var averageSpendingList = new ArrayList<SpendingRange>();
    var startOfEndMonth = endDate.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)
        .withNano(0);
    var endOfStartMonth = startDate.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)
        .withNano(0).plusMonths(1).minusNanos(1);
    var currentMonth = startDate;

    while (ChronoUnit.MONTHS.between(currentMonth, startOfEndMonth) > -1) {
      logger.debug("Current Month = {}. Average Monthly Spending so far {}.", currentMonth,
          averageSpendingList);
      var startOfMonth = currentMonth.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)
          .withNano(0);

      if (ChronoUnit.MONTHS.between(startOfMonth, startDate) == 0 && startOfMonth.getMonth()
          .equals(startDate.getMonth())) {
        logger.debug("Calculating the first month's average.");
        averageSpendingList.add(calculateAverageOfFractionOfMonth(startDate, endOfStartMonth));
        currentMonth = currentMonth.plusMonths(1);
        continue;
      }

      if (ChronoUnit.MONTHS.between(startOfMonth, endDate) == 0) {
        logger.debug("Calculating the final month's average.");
        averageSpendingList.add(calculateAverageOfFractionOfMonth(startOfEndMonth, endDate));
        currentMonth = currentMonth.plusMonths(1);
        continue;
      }

      logger.debug("Calculating a middle month's average.");

      var endOfMonth = startOfMonth.plusMonths(1).minusNanos(1);
      averageSpendingList.add(calculateAverageOfFractionOfMonth(startOfMonth, endOfMonth));
      currentMonth = currentMonth.plusMonths(1);
    }

    logger.debug("Final average monthly spending list:");
    averageSpendingList.stream().map(SpendingRange::toString).forEach(logger::debug);
    return averageSpendingList;

  }

  private SpendingRange calculateAverageOfFractionOfMonth(LocalDateTime startOfMonth,
      LocalDateTime endOfMonth) throws InvalidDateException {
    var spending = calculateSpending(startOfMonth, endOfMonth);
    logger.debug("Total spending for this month {}.", spending);
    var differenceInSeconds = ChronoUnit.NANOS.between(startOfMonth, endOfMonth);
    var secondDay = ChronoUnit.DAYS.getDuration().toNanos();
    var average = spending.getUsage()
        .multiply(BigDecimal.valueOf(secondDay))
        .divide(BigDecimal.valueOf(differenceInSeconds), RoundingMode.HALF_UP)
        .setScale(2, RoundingMode.HALF_UP);
    var spendingRange = new SpendingRange(
        startOfMonth,
        endOfMonth,
        average
    );
    logger.debug("Average spending for this month {}.", spendingRange);
    return spendingRange;
  }

  List<SpendingRange> calculateAverageSpending(LocalDateTime startDate, LocalDateTime endDate,
      int dayGap)
      throws InvalidDateException {
    logger.info("Get average spending from '{}' to '{}' over '{}' day periods.", startDate,
        endDate, dayGap);

    if (startDate.isAfter(endDate)) {
      logger.warn("Start date '" + startDate + "' occurs after end date '" + endDate + "'.");
      throw InvalidDateException.wrongWayAround(startDate, endDate);
    }

    // Exceptional case: date range too small for gap
    if (ChronoUnit.DAYS.between(startDate, endDate) < dayGap) {
      return List.of();
    }

    var averageSpendingList = new ArrayList<SpendingRange>();
    var lastEndDate = startDate;

    // Loop while there are day gap days between the last end date and the overall end date
    while (ChronoUnit.DAYS.between(lastEndDate, endDate) >= dayGap) {

      var newEndDate = lastEndDate.plusDays(dayGap);

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

    logger.debug("Final average spending list over {} day periods:", dayGap);
    averageSpendingList.stream().map(SpendingRange::toString).forEach(logger::debug);

    return averageSpendingList;

  }

  private SpendingRange calculateSpending(LocalDateTime startDate, LocalDateTime endDate)
      throws InvalidDateException {

    if (startDate.isAfter(endDate)) {
      logger.warn("Start date '" + startDate + "' occurs after end date '" + endDate + "'.");
      throw InvalidDateException.wrongWayAround(startDate, endDate);
    }

    // get sublist of spending ranges
    var sublist = new ArrayList<>(spendingRangeRepository.getBetweenDates(startDate, endDate));
    sublist.sort(Comparator.comparing(SpendingRange::getStartDate));
    logger.debug("Sublist of spending ranges between '{}' and '{}':", startDate, endDate);
    sublist.stream().map(SpendingRange::toString).forEach(logger::debug);

    if (sublist.size() == 0) {
      logger.debug("No spending ranges found.");
      return new SpendingRange(startDate, endDate, BigDecimal.ZERO);
    }

    // handle first case
    var firstSpending = firstSpending(startDate, endDate, sublist.get(0));
    logger.debug("First spending: " + firstSpending);

    if (sublist.size() == 1) {
      return new SpendingRange(startDate, endDate, firstSpending);
    }

    // handle cases where sublist is not size 1
    var middleSpending = middleSpending(sublist);
    logger.debug("Middle spending: " + middleSpending);

    // handle final case
    var lastSpending = lastSpending(startDate, endDate, sublist.get(sublist.size() - 1));
    logger.debug("Last spending: " + lastSpending);

    var totalSpending = firstSpending.add(middleSpending).add(lastSpending);
    logger.debug("Total spending: " + totalSpending);

    return new SpendingRange(startDate, endDate, totalSpending);
  }

}
