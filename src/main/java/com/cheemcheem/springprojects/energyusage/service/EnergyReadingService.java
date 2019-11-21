package com.cheemcheem.springprojects.energyusage.service;

import com.cheemcheem.springprojects.energyusage.dto.SpendingRangeDTO;
import com.cheemcheem.springprojects.energyusage.exception.EmptyRepositoryException;
import com.cheemcheem.springprojects.energyusage.model.SpendingRange;
import com.cheemcheem.springprojects.energyusage.util.mappers.SpendingRangeMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.List;
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
  private final CalculatorService calculatorService;


  public SpendingRangeDTO getAllSpending() {
    logger.info("Get all spending.");

    try {
      var spendingRange = this.calculatorService.generateForAllReadings();
      return this.spendingRangeMapper.toDTO(spendingRange);
    } catch (EmptyRepositoryException e) {
      logger.warn(e.getMessage());
      return getEmptySpendingAll();
    }
  }

  public SpendingRangeDTO getSpendingFrom(Date startDate) {
    logger.info("Get spending from '" + startDate + "'.");

    try {
      var spendingRange = this.calculatorService.generateForReadingsAfter(startDate);
      return this.spendingRangeMapper.toDTO(spendingRange);
    } catch (EmptyRepositoryException e) {
      logger.warn(e.getMessage());
      return getEmptySpendingFrom(startDate);
    }
  }

  public SpendingRangeDTO getSpendingTo(Date endDate) {
    logger.info("Get spending to '" + endDate + "'.");

    try {
      var spendingRange = this.calculatorService.generateForReadingsUntil(endDate);
      return this.spendingRangeMapper.toDTO(spendingRange);
    } catch (EmptyRepositoryException e) {
      logger.warn(e.getMessage());
      return getEmptySpendingTo(endDate);
    }
  }

  public SpendingRangeDTO getSpendingBetween(Date startDate, Date endDate) {
    logger.info("Get spending from '" + startDate + "' to '" + endDate + "'.");

    try {
      var spendingRange = this.calculatorService
          .generateForReadingsBetween(startDate, endDate);
      return this.spendingRangeMapper.toDTO(spendingRange);
    } catch (EmptyRepositoryException e) {
      logger.warn(e.getMessage());
      return getEmptySpendingBetween(startDate, endDate);
    }
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

  private SpendingRangeDTO getEmptySpendingAll() {
    logger.info("Returning empty spending.");
    var empty = new SpendingRange(
        Date.from(Instant.EPOCH),
        Date.from(Instant.now()),
        BigDecimal.ZERO
    );
    return this.spendingRangeMapper.toDTO(empty);
  }

  private SpendingRangeDTO getEmptySpendingFrom(Date endDate) {
    logger.info("Returning empty spending.");
    var empty = new SpendingRange(
        Date.from(Instant.EPOCH),
        endDate,
        BigDecimal.ZERO
    );
    return this.spendingRangeMapper.toDTO(empty);
  }

  private SpendingRangeDTO getEmptySpendingTo(Date startDate) {
    logger.info("Returning empty spending.");
    var empty = new SpendingRange(
        startDate,
        Date.from(Instant.now()),
        BigDecimal.ZERO
    );
    return this.spendingRangeMapper.toDTO(empty);
  }

  private SpendingRangeDTO getEmptySpendingBetween(Date startDate, Date endDate) {
    logger.info("Returning empty spending.");
    var empty = new SpendingRange(
        startDate,
        endDate,
        BigDecimal.ZERO
    );
    return this.spendingRangeMapper.toDTO(empty);
  }


}
