package com.cheemcheem.springprojects.energyusage.service;

import com.cheemcheem.springprojects.energyusage.model.SpendingRange;
import com.cheemcheem.springprojects.energyusage.repository.EnergyReadingRepository;
import com.cheemcheem.springprojects.energyusage.repository.SpendingRangeRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
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

  private SpendingRange getSpending(Date startDate, Date endDate) {
    var spendingRanges = new ArrayList<>(spendingRangeRepository.getSpendingRanges());
    spendingRanges.sort(Comparator.comparing(SpendingRange::getStartDate));

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

    var sublist = spendingRanges.subList(firstIndex, lastIndex + 1);
    logger.debug("Sublist of spending ranges: " + sublist);

    // First spending range
    var first = sublist.get(0);
    var portionFirst = first.getEndDate().getTime() - startDate.getTime();
    var totalFirst = first.getEndDate().getTime() - first.getStartDate().getTime();
    var amountOfFirst = ((double) portionFirst) / ((double) totalFirst);
    logger.debug("Amount of first: " + amountOfFirst);
    var firstSpending = first.getUsage().multiply(BigDecimal.valueOf(amountOfFirst));
    logger.debug("First spending: " + firstSpending);

    // Middle spending range(s)
    var middleSpending = BigDecimal.ZERO;
    for (int i = 1; i < sublist.size() - 1; i++) {
      var spendingRange = sublist.get(i);
      middleSpending = middleSpending.add(spendingRange.getUsage());
    }
    logger.debug("Middle spending: " + middleSpending);

    // Last spending range
    var last = sublist.get(sublist.size() - 1);
    var portionLast = endDate.getTime() - last.getStartDate().getTime();
    var totalLast = last.getEndDate().getTime() - last.getStartDate().getTime();
    var amountOfLast = ((double) portionLast) / ((double) totalLast);
    logger.debug("Amount of last: " + amountOfLast);
    var lastSpending = last.getUsage().multiply(BigDecimal.valueOf(amountOfLast));
    logger.debug("Last spending: " + lastSpending);

    var totalSpending = firstSpending.add(middleSpending).add(lastSpending);
    logger.debug("Total spending: " + totalSpending);

    return new SpendingRange(startDate, endDate, totalSpending);
  }

}
