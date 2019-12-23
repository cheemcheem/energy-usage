package com.cheemcheem.springprojects.energyusage.service;

import com.cheemcheem.springprojects.energyusage.dto.SpendingRangeDTO;
import com.cheemcheem.springprojects.energyusage.exception.InvalidDateException;
import com.cheemcheem.springprojects.energyusage.util.mapper.SpendingRangeMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Calls required calculations and converts between DTO and Model objects.
 *
 * The DTO used is {@link SpendingRangeDTO}. The Models used are {@link
 * com.cheemcheem.springprojects.energyusage.model.EnergyReading} and {@link
 * com.cheemcheem.springprojects.energyusage.model.SpendingRange}
 *
 * @see CalculatorService for calculations
 * @see SpendingRangeMapper for DTO/Model mapping
 */
@Service
@RequiredArgsConstructor
public class EnergyUsageService {

  private final Logger logger = LoggerFactory.getLogger(EnergyUsageService.class);

  @NonNull
  private final SpendingRangeMapper spendingRangeMapper;
  @NonNull
  private final CalculatorService calculatorService;


  public SpendingRangeDTO getAllSpending() {
    logger.info("Get all spending.");

    var spendingRange = this.calculatorService.calculateAllSpending();
    return this.spendingRangeMapper.toDTO(spendingRange);

  }

  public SpendingRangeDTO getSpendingFrom(LocalDateTime startDate) {
    logger.info("Get spending from '" + startDate + "'.");

    var spendingRange = this.calculatorService.calculateSpendingAfterDate(startDate);
    return this.spendingRangeMapper.toDTO(spendingRange);

  }

  public SpendingRangeDTO getSpendingTo(LocalDateTime endDate) {
    logger.info("Get spending to '" + endDate + "'.");

    var spendingRange = this.calculatorService.calculateSpendingUntilDate(endDate);
    return this.spendingRangeMapper.toDTO(spendingRange);

  }

  public SpendingRangeDTO getSpendingBetween(LocalDateTime startDate, LocalDateTime endDate)
      throws InvalidDateException {
    logger.info("Get spending from '" + startDate + "' to '" + endDate + "'.");

    var spendingRange = this.calculatorService
        .calculateSpendingBetweenDates(startDate, endDate);
    return this.spendingRangeMapper.toDTO(spendingRange);

  }

  public List<SpendingRangeDTO> getAverageSpending(int dayGap) {
    logger.info("Get average spending over all days over '" + dayGap
        + "' day periods.");

    var averageSpending = this.calculatorService
        .calculateAverageSpending(dayGap);

    return averageSpending.stream()
        .map(this.spendingRangeMapper::toDTO)
        .collect(Collectors.toList());
  }

  public List<SpendingRangeDTO> getAverageSpending(LocalDateTime startDate, LocalDateTime endDate,
      int dayGap)
      throws InvalidDateException {
    logger.info("Get average spending from '" + startDate + "' to '" + endDate + "' over '" + dayGap
        + "' day periods.");

    var averageSpending = this.calculatorService
        .calculateAverageSpending(startDate, endDate, dayGap);

    return averageSpending.stream()
        .map(this.spendingRangeMapper::toDTO)
        .collect(Collectors.toList());

  }

  public List<SpendingRangeDTO> getAverageSpendingBetweenDaily() {
    logger.info("Get average daily spending over all days.");

    var averageDailySpending = this.calculatorService.calculateAverageDailySpending();

    return averageDailySpending.stream()
        .map(this.spendingRangeMapper::toDTO)
        .collect(Collectors.toList());
  }

  public List<SpendingRangeDTO> getAverageSpendingBetweenDaily(LocalDateTime startDate,
      LocalDateTime endDate)
      throws InvalidDateException {
    logger.info("Get average daily spending from '" + startDate + "' to '" + endDate + "'.");

    var averageDailySpending = this.calculatorService
        .calculateAverageDailySpending(startDate, endDate);

    return averageDailySpending.stream()
        .map(this.spendingRangeMapper::toDTO)
        .collect(Collectors.toList());

  }

  public List<SpendingRangeDTO> getAverageSpendingBetweenWeekly() {
    logger.info("Get average weekly spending over all days.");

    var averageWeeklySpending = this.calculatorService.calculateAverageWeeklySpending();

    return averageWeeklySpending.stream()
        .map(this.spendingRangeMapper::toDTO)
        .collect(Collectors.toList());
  }

  public List<SpendingRangeDTO> getAverageSpendingBetweenWeekly(LocalDateTime startDate,
      LocalDateTime endDate)
      throws InvalidDateException {
    logger.info("Get average weekly spending from '" + startDate + "' to '" + endDate + "'.");

    var averageWeeklySpending = this.calculatorService
        .calculateAverageWeeklySpending(startDate, endDate);

    return averageWeeklySpending.stream()
        .map(this.spendingRangeMapper::toDTO)
        .collect(Collectors.toList());

  }

  public List<SpendingRangeDTO> getAverageSpendingBetweenMonthly() {
    logger.info("Get average monthly spending over all days.");

    var averageMonthlySpending = this.calculatorService.calculateAverageMonthlySpending();

    return averageMonthlySpending.stream()
        .map(this.spendingRangeMapper::toDTO)
        .collect(Collectors.toList());
  }

  public List<SpendingRangeDTO> getAverageSpendingBetweenMonthly(LocalDateTime startDate,
      LocalDateTime endDate)
      throws InvalidDateException {
    logger.info("Get average weekly monthly from '" + startDate + "' to '" + endDate + "'.");

    var averageMonthlySpending = this.calculatorService
        .calculateAverageMonthlySpending(startDate, endDate);

    return averageMonthlySpending.stream()
        .map(this.spendingRangeMapper::toDTO)
        .collect(Collectors.toList());

  }
}