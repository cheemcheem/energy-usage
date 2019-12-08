package com.cheemcheem.springprojects.energyusage.service;

import com.cheemcheem.springprojects.energyusage.dto.SpendingRangeDTO;
import com.cheemcheem.springprojects.energyusage.util.mapper.SpendingRangeMapper;
import java.util.Date;
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

  public SpendingRangeDTO getSpendingFrom(Date startDate) {
    logger.info("Get spending from '" + startDate + "'.");

    var spendingRange = this.calculatorService.calculateSpendingAfterDate(startDate);
    return this.spendingRangeMapper.toDTO(spendingRange);

  }

  public SpendingRangeDTO getSpendingTo(Date endDate) {
    logger.info("Get spending to '" + endDate + "'.");

    var spendingRange = this.calculatorService.calculateSpendingUntilDate(endDate);
    return this.spendingRangeMapper.toDTO(spendingRange);

  }

  public SpendingRangeDTO getSpendingBetween(Date startDate, Date endDate) {
    logger.info("Get spending from '" + startDate + "' to '" + endDate + "'.");

    var spendingRange = this.calculatorService
        .calculateSpendingBetweenDates(startDate, endDate);
    return this.spendingRangeMapper.toDTO(spendingRange);

  }

  public List<SpendingRangeDTO> getAverageSpending(int dayGap) {
    logger.info("Get average spending over all days over '" + dayGap
        + "' day periods.");

    var averageSpending = this.calculatorService
        .getAverageSpending(dayGap);

    return averageSpending.stream()
        .map(this.spendingRangeMapper::toDTO)
        .collect(Collectors.toList());
  }

  public List<SpendingRangeDTO> getAverageSpending(Date startDate, Date endDate, int dayGap) {
    logger.info("Get average spending from '" + startDate + "' to '" + endDate + "' over '" + dayGap
        + "' day periods.");

    var averageSpending = this.calculatorService
        .getAverageSpending(startDate, endDate, dayGap);

    return averageSpending.stream()
        .map(this.spendingRangeMapper::toDTO)
        .collect(Collectors.toList());

  }

  public List<SpendingRangeDTO> getAverageSpendingBetweenDaily() {
    logger.info("Get average daily spending over all days.");

    var averageDailySpending = this.calculatorService.getAverageDailySpending();

    return averageDailySpending.stream()
        .map(this.spendingRangeMapper::toDTO)
        .collect(Collectors.toList());
  }

  public List<SpendingRangeDTO> getAverageSpendingBetweenDaily(Date startDate, Date endDate) {
    logger.info("Get average daily spending from '" + startDate + "' to '" + endDate + "'.");

    var averageDailySpending = this.calculatorService.getAverageDailySpending(startDate, endDate);

    return averageDailySpending.stream()
        .map(this.spendingRangeMapper::toDTO)
        .collect(Collectors.toList());

  }

  public List<SpendingRangeDTO> getAverageSpendingBetweenWeekly() {
    logger.info("Get average weekly spending over all days.");

    var averageWeeklySpending = this.calculatorService.getAverageWeeklySpending();

    return averageWeeklySpending.stream()
        .map(this.spendingRangeMapper::toDTO)
        .collect(Collectors.toList());
  }

  public List<SpendingRangeDTO> getAverageSpendingBetweenWeekly(Date startDate, Date endDate) {
    logger.info("Get average weekly spending from '" + startDate + "' to '" + endDate + "'.");

    var averageWeeklySpending = this.calculatorService.getAverageWeeklySpending(startDate, endDate);

    return averageWeeklySpending.stream()
        .map(this.spendingRangeMapper::toDTO)
        .collect(Collectors.toList());

  }

  public List<SpendingRangeDTO> getAverageSpendingBetweenMonthly() {
    logger.info("Get average monthly spending over all days.");

    var averageMonthlySpending = this.calculatorService.getAverageMonthlySpending();

    return averageMonthlySpending.stream()
        .map(this.spendingRangeMapper::toDTO)
        .collect(Collectors.toList());
  }

  public List<SpendingRangeDTO> getAverageSpendingBetweenMonthly(Date startDate, Date endDate) {
    logger.info("Get average weekly monthly from '" + startDate + "' to '" + endDate + "'.");

    var averageMonthlySpending = this.calculatorService
        .getAverageMonthlySpending(startDate, endDate);

    return averageMonthlySpending.stream()
        .map(this.spendingRangeMapper::toDTO)
        .collect(Collectors.toList());

  }
}
