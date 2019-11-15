package com.cheemcheem.springprojects.energyusage.service;

import com.cheemcheem.springprojects.energyusage.dto.SpendingRangeDTO;
import com.cheemcheem.springprojects.energyusage.repository.EnergyReadingRepository;
import com.cheemcheem.springprojects.energyusage.util.SpendingRangeCalculator;
import com.cheemcheem.springprojects.energyusage.util.converters.BigDecimalConverter;
import com.cheemcheem.springprojects.energyusage.util.converters.DateConverter;
import com.cheemcheem.springprojects.energyusage.util.mappers.SpendingRangeMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnergyReadingService {

  private final SpendingRangeMapper spendingRangeMapper;

  @NonNull
  private final EnergyReadingRepository energyReadingRepository;

  public SpendingRangeDTO getAllSpending() {
    @NonNull var readings = energyReadingRepository.getReadings();
    var spendingRange = SpendingRangeCalculator.generateForReadings(readings);
    return spendingRangeMapper.toDTO(spendingRange);
  }

  public SpendingRangeDTO getSpendingFrom(Date startDate) {
    @NonNull var readings = SpendingRangeCalculator.filterForRange(
        energyReadingRepository.getReadings(),
        startDate,
        Date.from(Instant.now())
    );

    var spendingRange = SpendingRangeCalculator.generateForReadings(readings);
    return spendingRangeMapper.toDTO(spendingRange);
  }

  public SpendingRangeDTO getSpendingTo(Date endDate) {
    @NonNull var readings = SpendingRangeCalculator.filterForRange(
        energyReadingRepository.getReadings(),
        Date.from(Instant.EPOCH),
        endDate
    );

    var spendingRange = SpendingRangeCalculator.generateForReadings(readings);
    return spendingRangeMapper.toDTO(spendingRange);
  }

  public SpendingRangeDTO getSpendingBetween(Date startDate, Date endDate) {
    @NonNull var readings = SpendingRangeCalculator.filterForRange(
        energyReadingRepository.getReadings(),
        startDate,
        endDate
    );

    var spendingRange = SpendingRangeCalculator.generateForReadings(readings);
    return spendingRangeMapper.toDTO(spendingRange);
  }

  public SpendingRangeDTO getEmptySpendingFrom(Date endDate) {
    return new SpendingRangeDTO(
        DateConverter.format(Date.from(Instant.EPOCH)),
        DateConverter.format(endDate),
        BigDecimalConverter.format(BigDecimal.ZERO)
    );
  }

  public SpendingRangeDTO getEmptySpendingTo(Date startDate) {
    return new SpendingRangeDTO(
        DateConverter.format(startDate),
        DateConverter.format(Date.from(Instant.now())),
        BigDecimalConverter.format(BigDecimal.ZERO)
    );
  }

  public SpendingRangeDTO getEmptySpendingBetween(Date startDate, Date endDate) {
    return new SpendingRangeDTO(
        DateConverter.format(startDate),
        DateConverter.format(endDate),
        BigDecimalConverter.format(BigDecimal.ZERO)
    );
  }

}
