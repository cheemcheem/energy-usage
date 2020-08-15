package com.cheemcheem.projects.energyusage.service;

import com.cheemcheem.projects.energyusage.dto.SpendingRangeDTO;
import com.cheemcheem.projects.energyusage.exception.InvalidDateException;
import com.cheemcheem.projects.energyusage.model.EnergyReading;
import com.cheemcheem.projects.energyusage.model.SpendingRange;
import com.cheemcheem.projects.energyusage.model.User;
import com.cheemcheem.projects.energyusage.repository.SpendingRangeRepository;
import com.cheemcheem.projects.energyusage.util.Calculator;
import com.cheemcheem.projects.energyusage.util.mapper.DTOMapper;
import com.cheemcheem.projects.energyusage.util.mapper.EnergyReadingsMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Calls required calculations and converts between DTO and Model objects.
 *
 * The DTO used is {@link SpendingRangeDTO}. The Models used are {@link EnergyReading} and {@link
 * SpendingRange}
 *
 * @see Calculator for calculations
 * @see DTOMapper for DTO/Model mapping
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EnergyUsageService {


  private Calculator getCalculatorService(User user) {
    var energyReadings = new ArrayList<>(user.getEnergyReadings());
    var spendingRanges = new EnergyReadingsMapper(energyReadings).getEnergyReadingsRange();
    var spendingRangeRepository = new SpendingRangeRepository(spendingRanges);
    return new Calculator(spendingRangeRepository);
  }


  public SpendingRangeDTO getAllSpending(User user) {
    log.info("Get all spending.");
    var spendingRange = getCalculatorService(user).calculateAllSpending();
    return DTOMapper.toSpendingRangeDTO(spendingRange);

  }

  public SpendingRangeDTO getSpendingFrom(LocalDateTime startDate, User user) {
    log.info("Get spending from '" + startDate + "'.");

    var spendingRange = getCalculatorService(user).calculateSpendingAfterDate(startDate);
    return DTOMapper.toSpendingRangeDTO(spendingRange);

  }

  public SpendingRangeDTO getSpendingTo(LocalDateTime endDate, User user) {
    log.info("Get spending to '" + endDate + "'.");

    var spendingRange = getCalculatorService(user).calculateSpendingUntilDate(endDate);
    return DTOMapper.toSpendingRangeDTO(spendingRange);

  }

  public SpendingRangeDTO getSpendingBetween(LocalDateTime startDate, LocalDateTime endDate,
      User user)
      throws InvalidDateException {
    log.info("Get spending from '" + startDate + "' to '" + endDate + "'.");

    var spendingRange = getCalculatorService(user)
        .calculateSpendingBetweenDates(startDate, endDate);
    return DTOMapper.toSpendingRangeDTO(spendingRange);

  }

  public List<SpendingRangeDTO> getAverageSpending(int dayGap, User user) {
    log.info("Get average spending over all days over '" + dayGap
        + "' day periods.");

    var averageSpending = getCalculatorService(user)
        .calculateAverageSpending(dayGap);

    return averageSpending.stream()
        .map(DTOMapper::toSpendingRangeDTO)
        .collect(Collectors.toList());
  }

  public List<SpendingRangeDTO> getAverageSpending(LocalDateTime startDate, LocalDateTime endDate,
      int dayGap, User user)
      throws InvalidDateException {
    log.info("Get average spending from '" + startDate + "' to '" + endDate + "' over '" + dayGap
        + "' day periods.");

    var averageSpending = getCalculatorService(user)
        .calculateAverageSpending(startDate, endDate, dayGap);

    return averageSpending.stream()
        .map(DTOMapper::toSpendingRangeDTO)
        .collect(Collectors.toList());

  }

  public List<SpendingRangeDTO> getAverageSpendingBetweenDaily(User user) {
    log.info("Get average daily spending over all days.");

    var averageDailySpending = getCalculatorService(user).calculateAverageDailySpending();

    return averageDailySpending.stream()
        .map(DTOMapper::toSpendingRangeDTO)
        .collect(Collectors.toList());
  }

  public List<SpendingRangeDTO> getAverageSpendingBetweenDaily(LocalDateTime startDate,
      LocalDateTime endDate, User user)
      throws InvalidDateException {
    log.info("Get average daily spending from '" + startDate + "' to '" + endDate + "'.");

    var averageDailySpending = getCalculatorService(user)
        .calculateAverageDailySpending(startDate, endDate);

    return averageDailySpending.stream()
        .map(DTOMapper::toSpendingRangeDTO)
        .collect(Collectors.toList());

  }

  public List<SpendingRangeDTO> getAverageSpendingBetweenWeekly(User user) {
    log.info("Get average weekly spending over all days.");

    var averageWeeklySpending = getCalculatorService(user).calculateAverageWeeklySpending();

    return averageWeeklySpending.stream()
        .map(DTOMapper::toSpendingRangeDTO)
        .collect(Collectors.toList());
  }

  public List<SpendingRangeDTO> getAverageSpendingBetweenWeekly(LocalDateTime startDate,
      LocalDateTime endDate, User user)
      throws InvalidDateException {
    log.info("Get average weekly spending from '" + startDate + "' to '" + endDate + "'.");

    var averageWeeklySpending = getCalculatorService(user)
        .calculateAverageWeeklySpending(startDate, endDate);

    return averageWeeklySpending.stream()
        .map(DTOMapper::toSpendingRangeDTO)
        .collect(Collectors.toList());

  }

  public List<SpendingRangeDTO> getAverageSpendingBetweenMonthly(User user) {
    log.info("Get average monthly spending over all days.");

    var averageMonthlySpending = getCalculatorService(user).calculateAverageMonthlySpending();

    return averageMonthlySpending.stream()
        .map(DTOMapper::toSpendingRangeDTO)
        .collect(Collectors.toList());
  }

  public List<SpendingRangeDTO> getAverageSpendingBetweenMonthly(LocalDateTime startDate,
      LocalDateTime endDate, User user)
      throws InvalidDateException {
    log.info("Get average weekly monthly from '" + startDate + "' to '" + endDate + "'.");

    var averageMonthlySpending = getCalculatorService(user)
        .calculateAverageMonthlySpending(startDate, endDate);

    return averageMonthlySpending.stream()
        .map(DTOMapper::toSpendingRangeDTO)
        .collect(Collectors.toList());

  }
}
