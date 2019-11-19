package com.cheemcheem.springprojects.energyusage.service;

import com.cheemcheem.springprojects.energyusage.dto.SpendingRangeDTO;
import com.cheemcheem.springprojects.energyusage.exception.EmptyRepositoryException;
import com.cheemcheem.springprojects.energyusage.model.SpendingRange;
import com.cheemcheem.springprojects.energyusage.repository.EnergyReadingRepository;
import com.cheemcheem.springprojects.energyusage.util.SpendingRangeCalculator;
import com.cheemcheem.springprojects.energyusage.util.converters.BigDecimalConverter;
import com.cheemcheem.springprojects.energyusage.util.converters.DateConverter;
import com.cheemcheem.springprojects.energyusage.util.mappers.SpendingRangeMapper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnergyReadingService {

  private final Logger logger = LoggerFactory.getLogger(EnergyReadingService.class);

  @NonNull
  private final SpendingRangeMapper spendingRangeMapper;
  @NonNull
  private final SpendingRangeCalculator spendingRangeCalculator;
  @NonNull
  private final EnergyReadingRepository energyReadingRepository;

  public SpendingRangeDTO getAllSpending() {
    logger.info("Get all spending.");

    @NonNull var readings = energyReadingRepository.getReadings();
    try {
      var spendingRange = this.spendingRangeCalculator.generateForReadings(readings);
      return this.spendingRangeMapper.toDTO(spendingRange);
    } catch (EmptyRepositoryException e) {
      logger.warn(e.getMessage());
      return getEmptySpendingAll();
    }
  }

  public SpendingRangeDTO getSpendingFrom(Date startDate) {
    logger.info("Get spending from '" + startDate + "'.");

    @NonNull var readings = this.spendingRangeCalculator.filterForRange(
        energyReadingRepository.getReadings(),
        startDate,
        Date.from(Instant.now())
    );

    try {
      var spendingRange = this.spendingRangeCalculator.generateForReadings(readings);
      return this.spendingRangeMapper.toDTO(spendingRange);
    } catch (EmptyRepositoryException e) {
      logger.warn(e.getMessage());
      return getEmptySpendingFrom(startDate);
    }
  }

  public SpendingRangeDTO getSpendingTo(Date endDate) {
    logger.info("Get spending to '" + endDate + "'.");
    @NonNull var readings = this.spendingRangeCalculator.filterForRange(
        energyReadingRepository.getReadings(),
        Date.from(Instant.EPOCH),
        endDate
    );
    try {
      var spendingRange = this.spendingRangeCalculator.generateForReadings(readings);
      return this.spendingRangeMapper.toDTO(spendingRange);
    } catch (EmptyRepositoryException e) {
      logger.warn(e.getMessage());
      return getEmptySpendingTo(endDate);
    }
  }

  public SpendingRangeDTO getSpendingBetween(Date startDate, Date endDate) {
    logger.info("Get spending from '" + startDate + "' to '" + endDate + "'.");

    @NonNull var readings = this.spendingRangeCalculator.filterForRange(
        energyReadingRepository.getReadings(),
        startDate,
        endDate
    );

    try {
      var spendingRange = this.spendingRangeCalculator.generateForReadings(readings);
      return this.spendingRangeMapper.toDTO(spendingRange);
    } catch (EmptyRepositoryException e) {
      logger.warn(e.getMessage());
      return getEmptySpendingBetween(startDate, endDate);
    }
  }

  public List<SpendingRangeDTO> getAverageSpending(Date startDate, Date endDate, int dayGap) {
    logger.info("Get average spending from '" + startDate + "' to '" + endDate + "' over '" + dayGap
        + "' day periods.");
    BiFunction<Date, Date, Long> daysBetween =
        (start, end) -> ChronoUnit.DAYS.between(start.toInstant(), end.toInstant());

    if (daysBetween.apply(startDate, endDate) < dayGap) {
      return List.of();
    }

    @NonNull var allReadings = this.spendingRangeCalculator.filterForRange(
        energyReadingRepository.getReadings(),
        startDate,
        endDate
    );

    @NonNull var averageSpendings = new ArrayList<SpendingRange>();

    var lastDate = startDate;
    var calendar = Calendar.getInstance();
    System.out
        .println("daysBetween.apply(lastDate, endDate) = " + daysBetween.apply(lastDate, endDate));
    while (daysBetween.apply(lastDate, endDate) >= dayGap) {
      calendar.setTime(lastDate);
      calendar.add(Calendar.DAY_OF_MONTH, dayGap);
      var newEndDate = calendar.getTime();

      logger.debug("lastDate = " + lastDate);
      logger.debug("newEndDate = " + newEndDate);
      @NonNull var subReadings = this.spendingRangeCalculator.filterForRange(
          allReadings,
          lastDate,
          newEndDate
      );

      BigDecimal averageReading;

      try {
        var averageSpending = this.spendingRangeCalculator.generateForReadings(subReadings);
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

    return averageSpendings.stream()
        .map(this.spendingRangeMapper::toDTO)
        .collect(Collectors.toList());

  }

  private SpendingRangeDTO getEmptySpendingAll() {
    return new SpendingRangeDTO(
        DateConverter.format(Date.from(Instant.EPOCH)),
        DateConverter.format(Date.from(Instant.now())),
        BigDecimalConverter.format(BigDecimal.ZERO)
    );
  }

  private SpendingRangeDTO getEmptySpendingFrom(Date endDate) {
    return new SpendingRangeDTO(
        DateConverter.format(Date.from(Instant.EPOCH)),
        DateConverter.format(endDate),
        BigDecimalConverter.format(BigDecimal.ZERO)
    );
  }

  private SpendingRangeDTO getEmptySpendingTo(Date startDate) {
    return new SpendingRangeDTO(
        DateConverter.format(startDate),
        DateConverter.format(Date.from(Instant.now())),
        BigDecimalConverter.format(BigDecimal.ZERO)
    );
  }

  private SpendingRangeDTO getEmptySpendingBetween(Date startDate, Date endDate) {
    return new SpendingRangeDTO(
        DateConverter.format(startDate),
        DateConverter.format(endDate),
        BigDecimalConverter.format(BigDecimal.ZERO)
    );
  }


}
