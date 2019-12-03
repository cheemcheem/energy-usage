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

    var spendingRange = this.calculatorService.generateForAllReadings();
    return this.spendingRangeMapper.toDTO(spendingRange);

  }

  public SpendingRangeDTO getSpendingFrom(Date startDate) {
    logger.info("Get spending from '" + startDate + "'.");

    var spendingRange = this.calculatorService.generateForReadingsAfter(startDate);
    return this.spendingRangeMapper.toDTO(spendingRange);

  }

  public SpendingRangeDTO getSpendingTo(Date endDate) {
    logger.info("Get spending to '" + endDate + "'.");

    var spendingRange = this.calculatorService.generateForReadingsUntil(endDate);
    return this.spendingRangeMapper.toDTO(spendingRange);

  }

  public SpendingRangeDTO getSpendingBetween(Date startDate, Date endDate) {
    logger.info("Get spending from '" + startDate + "' to '" + endDate + "'.");

    var spendingRange = this.calculatorService
        .generateForReadingsBetween(startDate, endDate);
    return this.spendingRangeMapper.toDTO(spendingRange);

  }

  public List<SpendingRangeDTO> getAverageSpending(int dayGap) {
    logger.info("Get average spending over all days over '" + dayGap
        + "' day periods.");

    var averageSpendings = this.calculatorService
        .getAverageSpending(dayGap);

    return averageSpendings.stream()
        .map(this.spendingRangeMapper::toDTO)
        .collect(Collectors.toList());
  }

  public List<SpendingRangeDTO> getAverageSpending(Date startDate, Date endDate, int dayGap) {
    logger.info("Get average spending from '" + startDate + "' to '" + endDate + "' over '" + dayGap
        + "' day periods.");

    var averageSpendings = this.calculatorService
        .getAverageSpending(startDate, endDate, dayGap);

    return averageSpendings.stream()
        .map(this.spendingRangeMapper::toDTO)
        .collect(Collectors.toList());

  }

  public List<SpendingRangeDTO> getAverageSpendingBetweenDaily() {
    logger.info("Get average daily spending over all days.");

    var averageSpendings = this.calculatorService.getAverageSpendingDaily();

    return averageSpendings.stream()
        .map(this.spendingRangeMapper::toDTO)
        .collect(Collectors.toList());
  }

  public List<SpendingRangeDTO> getAverageSpendingBetweenDaily(Date startDate, Date endDate) {
    logger.info("Get average daily spending from '" + startDate + "' to '" + endDate + "'.");

    var averageSpendings = this.calculatorService.getAverageSpendingDaily(startDate, endDate);

    return averageSpendings.stream()
        .map(this.spendingRangeMapper::toDTO)
        .collect(Collectors.toList());

  }
}
